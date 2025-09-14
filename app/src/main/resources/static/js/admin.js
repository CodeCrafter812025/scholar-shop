import { productAPI, userAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('لطفاً وارد شوید!');
        window.location.href = 'auth.html';
        return;
    }

    // بارگذاری محصولات
    await loadProducts();

    // بارگذاری کاربران
    await loadUsers();

    // بارگذاری سفارش‌ها
    await loadOrders();

    // بارگذاری گزارش‌ها
    await loadReports();

    // مدیریت فرم افزودن محصول
    document.getElementById('addProductForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const productData = Object.fromEntries(formData);

        try {
            await productAPI.createProduct(productData, token);
            alert('محصول با موفقیت افزوده شد!');
            e.target.reset();
            bootstrap.Modal.getInstance(document.getElementById('addProductModal')).hide();
            await loadProducts();
        } catch (error) {
            alert('خطا در افزودن محصول!');
        }
    });

    document.getElementById('download-report-btn')?.addEventListener('click', async () => {
        try {
            const token = localStorage.getItem('token');
            const orders = await orderAPI.getAllOrders(token);
            if (!orders || orders.length === 0) {
                alert('هیچ سفارشی برای گزارش وجود ندارد.');
                return;
            }
            const header = ['OrderID', 'UserName', 'Status', 'Total', 'Products'];
            const rows = orders.map(order => {
                const id = order._id || order.id || '';
                const user = order.user?.name || order.userName || order.customer?.name || order.customer?.email || '';
                const status = order.status || '';
                const total = order.totalAmount || order.total || order.amount || 0;
                const products = (order.products || order.items || []).map(item => {
                    const name = item.product?.name || item.name || '';
                    const quantity = item.quantity || item.count || 1;
                    return `${name} (x${quantity})`;
                }).join('; ');
                return [id, user, status, total, products].join(',');
            });
            const csvContent = [header.join(','), ...rows].join('\n');
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = 'orders_report.csv';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        } catch (error) {
            console.error(error);
            alert('خطا در دریافت گزارش سفارش‌ها!');
        }
    });

});

async function loadProducts() {
    const productsList = document.getElementById('products-list');
    try {
        const products = await productAPI.getAllProducts();
        productsList.innerHTML = products.map(product => `
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-2">
                            <img src="${product.image?.path || product.image || 'https://via.placeholder.com/100'}" class="img-fluid">
                        </div>
                        <div class="col-md-8">
                            <h5>${product.title || product.name}</h5>
                            <p>${(product.description || '').substring(0, 100)}...</p>
                            <p><strong>قیمت: ${product.price} تومان</strong></p>
                        </div>
                        <div class="col-md-2">
                            <!-- دکمه‌های ویرایش و حذف -->
                            <button class="btn btn-warning btn-sm mb-2">ویرایش</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteProduct('${product.id || product._id}')">حذف</button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        productsList.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
    }
}


async function loadUsers() {
    const usersList = document.getElementById('users-list');
    usersList.innerHTML = '<p>در حال بارگذاری کاربران...</p>';
    try {
        const token = localStorage.getItem('token');
        const users = await userAPI.getAllUsers(token);
        if (!users || users.length === 0) {
            usersList.innerHTML = '<p>کاربری یافت نشد!</p>';
            return;
        }
        usersList.innerHTML = users.map(user => `
            <div class="card mb-2">
                <div class="card-body">
                    <h5 class="card-title">${user.name || user.username || user.email}</h5>
                    <p class="card-text"><strong>ایمیل:</strong> ${user.email || ''}</p>
                    ${user.role ? `<p class="card-text"><strong>نقش:</strong> ${user.role}</p>` : ''}
                </div>
            </div>
        `).join('');
    } catch (error) {
        usersList.innerHTML = '<p>خطا در بارگذاری کاربران!</p>';
        console.error(error);
    }
}

async function loadOrders() {
    const ordersList = document.getElementById('orders-list');
    ordersList.innerHTML = '<p>در حال بارگذاری سفارش‌ها...</p>';
    try {
        const token = localStorage.getItem('token');
        const orders = await orderAPI.getAllOrders(token);
        if (!orders || orders.length === 0) {
            ordersList.innerHTML = '<p>سفارشی ثبت نشده است!</p>';
            return;
        }
        ordersList.innerHTML = orders.map(order => {
            const status = order.status || '';
            const statusBadge = (status === 'completed' || status === 'paid') ? 'success' : 'warning';
            const products = order.products || order.items || [];
            const user = order.user || order.customer || {};
            const total = order.totalAmount || order.total || order.amount || 0;
            return `
                <div class="card mb-3">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span>سفارش #${order._id || order.id || ''}</span>
                        <span class="badge bg-${statusBadge}">${status}</span>
                    </div>
                    <div class="card-body">
                        <h6>کاربر: ${user.name || user.username || user.email || ''}</h6>
                        <h6>محصولات:</h6>
                        <ul>
                            ${products.map(item => {
                                const name = item.product?.name || item.name || '';
                                const quantity = item.quantity || item.count || 1;
                                const price = item.price || item.product?.price || 0;
                                return `<li>${name} - تعداد: ${quantity} - قیمت: ${price} تومان</li>`;
                            }).join('')}
                        </ul>
                        <h5 class="text-primary">جمع کل: ${total} تومان</h5>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        ordersList.innerHTML = '<p>خطا در بارگذاری سفارش‌ها!</p>';
        console.error(error);
    }
}

async function loadReports() {
    const bestContainer = document.getElementById('best-selling-container');
    const monthlyContainer = document.getElementById('monthly-sales-container');

    try {
        const token = localStorage.getItem('token');
        const orders = await orderAPI.getAllOrders(token);

        if (!orders || orders.length === 0) {
            bestContainer.innerHTML = '<p>هیچ سفارشی برای محاسبه وجود ندارد!</p>';
            monthlyContainer.innerHTML = '<p>هیچ سفارشی برای محاسبه وجود ندارد!</p>';
            return;
        }

        // پرفروش‌ترین محصول
        const best = computeBestSellingProduct(orders);
        if (best) {
            bestContainer.innerHTML = `
                <div class="card">
                    <div class="card-body d-flex align-items-center">
                        <img src="${best.product.image?.path
                            || (best.product.image ? `/images/${best.product.image}` : 'https://via.placeholder.com/100')}"
                             alt="${best.product.title || best.product.name}"
                             class="img-thumbnail me-3" style="width:100px;height:auto;">
                        <div>
                            <h5>${best.product.title || best.product.name}</h5>
                            <p>تعداد فروش: <strong>${best.quantity}</strong></p>
                            <p>مبلغ کل فروش: <strong>${best.total.toLocaleString()}</strong> تومان</p>
                        </div>
                    </div>
                </div>
            `;
        } else {
            bestContainer.innerHTML = '<p>پرفروش‌ترین محصول یافت نشد.</p>';
        }

        // فروش ماهانه
        const monthly = computeMonthlySales(orders);
        // تولید جدول ماهانه
        let tableHtml = '<table class="table table-bordered"><thead><tr><th>ماه</th><th>مبلغ فروش (تومان)</th></tr></thead><tbody>';
        Object.keys(monthly).sort().forEach(month => {
            tableHtml += `<tr><td>${month}</td><td>${monthly[month].toLocaleString()}</td></tr>`;
        });
        tableHtml += '</tbody></table>';
        monthlyContainer.innerHTML = tableHtml;
    } catch (error) {
        console.error('خطا در بارگذاری گزارشات:', error);
        bestContainer.innerHTML = '<p>خطا در بارگذاری گزارش پرفروش‌ترین محصول!</p>';
        monthlyContainer.innerHTML = '<p>خطا در بارگذاری گزارش فروش ماهانه!</p>';
    }
}

// محاسبه پرفروش‌ترین محصول
function computeBestSellingProduct(orders) {
    const productMap = {};
    orders.forEach(order => {
        // بعضی مدل‌ها از items استفاده می‌کنند، بعضی از products
        const items = order.items || order.products || [];
        items.forEach(item => {
            const prod = item.product;
            const id = prod.id || prod._id;
            if (!productMap[id]) {
                productMap[id] = { product: prod, quantity: 0, total: 0 };
            }
            productMap[id].quantity += item.quantity || 1;
            // اگر price در item موجود باشد، استفاده کنیم، وگرنه از محصول بخوانیم
            const price = item.price || prod.price || 0;
            productMap[id].total += price * (item.quantity || 1);
        });
    });
    const list = Object.values(productMap);
    return list.sort((a, b) => b.quantity - a.quantity)[0];
}

// محاسبه فروش ماهانه (براساس createDate)
function computeMonthlySales(orders) {
    const monthly = {};
    orders.forEach(order => {
        const date = new Date(order.createDate || order.createdAt);
        if (isNaN(date)) return;
        const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
        if (!monthly[key]) monthly[key] = 0;
        monthly[key] += order.totalAmount || order.amount || 0;
    });
    return monthly;
}

async function deleteProduct(productId) {
    if (confirm('آیا از حذف این محصول مطمئن هستید؟')) {
        const token = localStorage.getItem('token');
        try {
            await productAPI.deleteProduct(productId, token);
            alert('محصول با موفقیت حذف شد!');
            await loadProducts();
        } catch (error) {
            alert('خطا در حذف محصول!');
        }
    }
}