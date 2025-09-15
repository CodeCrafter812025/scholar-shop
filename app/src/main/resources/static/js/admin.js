// app/src/main/resources/static/js/admin.js

import { productAPI, userAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('لطفاً وارد شوید!');
        window.location.href = 'auth.html';
        return;
    }

    // بارگذاری بخش‌ها
    await loadProducts();
    await loadUsers();
    await loadOrders();
    await loadReports();

    // رویداد ثبت محصول
    document.getElementById('addProductForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const raw = Object.fromEntries(new FormData(e.target).entries());
        // تبدیل فیلدها به فرمت مورد انتظار
        const productData = {
            title: raw.name,
            description: raw.description,
            price: parseInt(raw.price, 10),
            image: raw.image ? { path: raw.image } : null
        };
        try {
            await productAPI.createProduct(productData, token);
            alert('محصول جدید با موفقیت افزوده شد');
            e.target.reset();
            bootstrap.Modal.getInstance(document.getElementById('addProductModal')).hide();
            await loadProducts();
        } catch (err) {
            console.error('createProduct', err);
            alert('خطا در ایجاد محصول!');
        }
    });

    // رویداد دانلود گزارش سفارش‌ها
    document.getElementById('download-report-btn')?.addEventListener('click', async () => {
        try {
            const invoices = await collectAllInvoices();
            if (!invoices.length) {
                alert('هیچ سفارشی برای گزارش وجود ندارد.');
                return;
            }
            const header = ['OrderID','UserName','Status','Total','Products'];
            const rows = invoices.map(order => {
                const id = order.id || order._id || '';
                const userName = order.user?.firstname || order.user?.name || order.user?.username || '';
                const status = order.status || '';
                const total = order.totalAmount || order.amount || 0;
                const products = (order.items || order.products || []).map(item => {
                    const name = item.product?.title || item.product?.name || '';
                    const qty = item.quantity || 1;
                    return `${name} (x${qty})`;
                }).join('; ');
                return [id, userName, status, total, products].join(',');
            });
            const csv = [header.join(','), ...rows].join('\n');
            const blob = new Blob([csv], { type:'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = 'orders_report.csv';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        } catch (err) {
            console.error('download-report', err);
            alert('خطا در دریافت گزارش سفارش‌ها!');
        }
    });
});

// جمع‌آوری همه فاکتورهای کاربران برای پنل
async function collectAllInvoices() {
    const token = localStorage.getItem('token');
    const users = await userAPI.getAllUsers(token);
    const invoices = [];
    for (const user of users) {
        try {
            const res = await orderAPI.getOrdersByUser(user.id, token);
            const orders = res.data || res;
            orders.forEach(o => invoices.push(o));
        } catch (err) {
            console.error('collectAllInvoices', err);
        }
    }
    return invoices;
}

// بارگذاری محصولات پنل ادمین
async function loadProducts() {
    const productsList = document.getElementById('products-list');
    productsList.innerHTML = '<p>در حال بارگذاری محصولات...</p>';
    try {
        const token = localStorage.getItem('token');
        const products = await productAPI.getAllPanelProducts(0, 100, token);
        if (!products || products.length === 0) {
            productsList.innerHTML = '<p>محصولی یافت نشد.</p>';
            return;
        }
        productsList.innerHTML = products.map(product => {
            const imagePath = product.image?.path
                ? product.image.path
                : product.image?.name
                    ? `/images/${product.image.name}`
                    : product.image
                        ? product.image
                        : 'https://via.placeholder.com/100';
            const title = product.title || product.name || '';
            return `
                <div class="card mb-3">
                    <div class="row g-0">
                        <div class="col-md-2">
                            <img src="${imagePath}" class="img-fluid rounded-start" alt="${title}">
                        </div>
                        <div class="col-md-8">
                            <div class="card-body">
                                <h5 class="card-title">${title}</h5>
                                <p class="card-text"><strong>قیمت:</strong> ${product.price} تومان</p>
                            </div>
                        </div>
                        <div class="col-md-2 d-flex flex-column justify-content-center align-items-center">
                            <button class="btn btn-danger" onclick="deleteProduct('${product.id || product._id}')">حذف</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('loadProducts', err);
        productsList.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
    }
}

// بارگذاری کاربران در پنل
async function loadUsers() {
    const usersList = document.getElementById('users-list');
    usersList.innerHTML = '<p>در حال بارگذاری کاربران...</p>';
    try {
        const token = localStorage.getItem('token');
        const users = await userAPI.getAllUsers(token);
        if (!users || users.length === 0) {
            usersList.innerHTML = '<p>کاربری یافت نشد.</p>';
            return;
        }
        usersList.innerHTML = users.map(user => `
            <div class="card mb-2">
                <div class="card-body">
                    <h5>${user.firstname || user.username || user.email}</h5>
                    <p><strong>ایمیل:</strong> ${user.email || ''}</p>
                </div>
            </div>
        `).join('');
    } catch (err) {
        console.error('loadUsers', err);
        usersList.innerHTML = '<p>خطا در بارگذاری کاربران!</p>';
    }
}

// بارگذاری سفارش‌های همه کاربران
async function loadOrders() {
    const ordersList = document.getElementById('orders-list');
    ordersList.innerHTML = '<p>در حال بارگذاری سفارش‌ها...</p>';
    try {
        const invoices = await collectAllInvoices();
        if (!invoices.length) {
            ordersList.innerHTML = '<p>سفارشی ثبت نشده است.</p>';
            return;
        }
        ordersList.innerHTML = invoices.map(order => {
            const status = order.status || '';
            const badgeClass = (status === 'paid' || status === 'completed') ? 'success' : 'warning';
            const user = order.user || {};
            const total = order.totalAmount || order.amount || 0;
            const items = order.items || order.products || [];
            return `
                <div class="card mb-3">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span>سفارش #${order.id || order._id}</span>
                        <span class="badge bg-${badgeClass}">${status}</span>
                    </div>
                    <div class="card-body">
                        <h6>کاربر: ${user.firstname || user.name || user.username || ''}</h6>
                        <h6>محصولات:</h6>
                        <ul>
                            ${items.map(item => {
                                const pName = item.product?.title || item.product?.name || '';
                                const qty = item.quantity || 1;
                                const price = item.price || item.product?.price || 0;
                                return `<li>${pName} - تعداد: ${qty} - قیمت: ${price} تومان</li>`;
                            }).join('')}
                        </ul>
                        <h5 class="text-primary">جمع کل: ${total} تومان</h5>
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('loadOrders', err);
        ordersList.innerHTML = '<p>خطا در بارگذاری سفارش‌ها!</p>';
    }
}

// محاسبه‌ی پرفروش‌ترین محصول و فروش ماهانه
async function loadReports() {
    const bestContainer = document.getElementById('best-selling-container');
    const monthlyContainer = document.getElementById('monthly-sales-container');
    bestContainer.innerHTML = '<p>در حال محاسبه گزارش...</p>';
    monthlyContainer.innerHTML = '<p>در حال محاسبه گزارش...</p>';
    try {
        const invoices = await collectAllInvoices();
        if (!invoices.length) {
            bestContainer.innerHTML = '<p>سفارشی برای محاسبه وجود ندارد.</p>';
            monthlyContainer.innerHTML = '<p>سفارشی برای محاسبه وجود ندارد.</p>';
            return;
        }
        // پرفروش‌ترین محصول
        const productMap = {};
        invoices.forEach(inv => {
            const items = inv.items || inv.products || [];
            items.forEach(item => {
                const prod = item.product;
                const id = prod.id || prod._id;
                if (!productMap[id]) productMap[id] = { product: prod, quantity: 0, total: 0 };
                const qty = item.quantity || 1;
                const price = item.price || prod.price || 0;
                productMap[id].quantity += qty;
                productMap[id].total += price * qty;
            });
        });
        const best = Object.values(productMap).sort((a, b) => b.quantity - a.quantity)[0];
        if (best) {
            const imgPath = best.product.image?.path
                ? best.product.image.path
                : best.product.image?.name
                    ? `/images/${best.product.image.name}`
                    : best.product.image
                        ? best.product.image
                        : 'https://via.placeholder.com/100';
            const name = best.product.title || best.product.name || '';
            bestContainer.innerHTML = `
                <div class="card">
                    <div class="card-body d-flex align-items-center">
                        <img src="${imgPath}" alt="${name}" class="img-thumbnail me-3" style="width:100px;height:auto;">
                        <div>
                            <h5>${name}</h5>
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
        const monthly = {};
        invoices.forEach(inv => {
            const date = new Date(inv.createDate || inv.createdAt || inv.date);
            if (isNaN(date)) return;
            const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
            if (!monthly[key]) monthly[key] = 0;
            monthly[key] += inv.totalAmount || inv.amount || 0;
        });
        let table = '<table class="table table-bordered"><thead><tr><th>ماه</th><th>مبلغ فروش (تومان)</th></tr></thead><tbody>';
        Object.keys(monthly).sort().forEach(month => {
            table += `<tr><td>${month}</td><td>${monthly[month].toLocaleString()}</td></tr>`;
        });
        table += '</tbody></table>';
        monthlyContainer.innerHTML = table;
    } catch (err) {
        console.error('loadReports', err);
        bestContainer.innerHTML = '<p>خطا در محاسبه گزارش!</p>';
        monthlyContainer.innerHTML = '<p>خطا در محاسبه گزارش!</p>';
    }
}

// حذف محصول
window.deleteProduct = async function(productId) {
    if (!confirm('آیا از حذف این محصول مطمئن هستید؟')) return;
    try {
        const token = localStorage.getItem('token');
        await productAPI.deleteProduct(productId, token);
        alert('محصول با موفقیت حذف شد');
    } catch (err) {
        console.error('deleteProduct', err);
        alert('خطا در حذف محصول!');
    } finally {
        await loadProducts();
    }
};
