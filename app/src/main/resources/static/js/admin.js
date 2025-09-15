import { productAPI, userAPI, orderAPI } from './api.js';

// بارگذاری محصولات در تب محصولات
async function loadProducts() {
    const productsList = document.getElementById('products-list');
    productsList.innerHTML = '<p>در حال بارگذاری محصولات...</p>';
    try {
        const products = await productAPI.getAllProducts();
        productsList.innerHTML = products.map(product => `
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-2">
                            <img src="${product.image?.path || product.image ? `/images/${product.image.name || product.image}` : 'https://via.placeholder.com/100'}"
                                 class="img-fluid">
                        </div>
                        <div class="col-md-8">
                            <h5>${product.title || product.name}</h5>
                            <p>${(product.description || '').substring(0, 100)}...</p>
                            <p><strong>قیمت: ${product.price} تومان</strong></p>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-danger btn-sm" onclick="deleteProduct('${product.id || product._id}')">حذف</button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        productsList.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
        console.error('loadProducts', error);
    }
}

// بارگذاری کاربران
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
                    <h5 class="card-title">${user.firstname || user.username || user.email}</h5>
                    <p class="card-text"><strong>ایمیل:</strong> ${user.email || ''}</p>
                </div>
            </div>
        `).join('');
    } catch (error) {
        usersList.innerHTML = '<p>خطا در بارگذاری کاربران!</p>';
        console.error('loadUsers', error);
    }
}

// بارگذاری سفارش‌ها (فاکتورها) برای همهٔ کاربران
async function loadOrders() {
    const ordersList = document.getElementById('orders-list');
    ordersList.innerHTML = '<p>در حال بارگذاری سفارش‌ها...</p>';
    try {
        const token = localStorage.getItem('token');
        const users = await userAPI.getAllUsers(token);
        const allInvoices = [];
        for (const user of users) {
            const invoicesRes = await orderAPI.getOrdersByUser(user.id, token);
            const invoices = invoicesRes.data || invoicesRes;
            invoices.forEach(inv => {
                allInvoices.push({ ...inv, user });
            });
        }
        if (allInvoices.length === 0) {
            ordersList.innerHTML = '<p>سفارشی ثبت نشده است!</p>';
            return;
        }
        ordersList.innerHTML = allInvoices.map(order => {
            const status = order.status || '';
            const badgeClass = (status === 'paid' || status === 'completed') ? 'success' : 'warning';
            return `
                <div class="card mb-3">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span>سفارش #${order.id}</span>
                        <span class="badge bg-${badgeClass}">${status}</span>
                    </div>
                    <div class="card-body">
                        <h6>کاربر: ${order.user.firstname || order.user.username || ''}</h6>
                        <p><strong>قیمت کل:</strong> ${order.totalAmount || 0} تومان</p>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        ordersList.innerHTML = '<p>خطا در بارگذاری سفارش‌ها!</p>';
        console.error('loadOrders', error);
    }
}

// بارگذاری گزارش پرفروش‌ترین محصول و فروش ماهانه
async function loadReports() {
    const bestContainer = document.getElementById('best-selling-container');
    const monthlyContainer = document.getElementById('monthly-sales-container');
    bestContainer.innerHTML = '<p>در حال محاسبه گزارش...</p>';
    monthlyContainer.innerHTML = '<p>در حال محاسبه گزارش...</p>';

    try {
        const token = localStorage.getItem('token');
        const users = await userAPI.getAllUsers(token);
        const allInvoices = [];
        for (const user of users) {
            const invoicesRes = await orderAPI.getOrdersByUser(user.id, token);
            const invoices = invoicesRes.data || invoicesRes;
            invoices.forEach(inv => {
                allInvoices.push(inv);
            });
        }
        if (allInvoices.length === 0) {
            bestContainer.innerHTML = '<p>سفارشی برای محاسبه وجود ندارد.</p>';
            monthlyContainer.innerHTML = '<p>سفارشی برای محاسبه وجود ندارد.</p>';
            return;
        }

        // محاسبه پرفروش‌ترین محصول
        const productMap = {};
        allInvoices.forEach(inv => {
            const items = inv.items || inv.products || [];
            items.forEach(item => {
                const prod = item.product;
                const id = prod.id || prod._id;
                if (!productMap[id]) {
                    productMap[id] = { product: prod, quantity: 0, total: 0 };
                }
                const qty = item.quantity || 1;
                const price = item.price || prod.price || 0;
                productMap[id].quantity += qty;
                productMap[id].total += price * qty;
            });
        });
        const best = Object.values(productMap).sort((a, b) => b.quantity - a.quantity)[0];
        if (best) {
            bestContainer.innerHTML = `
                <div class="card">
                    <div class="card-body d-flex align-items-center">
                        <img src="${best.product.image?.path || (best.product.image ? `/images/${best.product.image}` : 'https://via.placeholder.com/100')}"
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

        // محاسبه فروش ماهانه
        const monthly = {};
        allInvoices.forEach(inv => {
            const date = new Date(inv.createDate || inv.createdAt);
            if (isNaN(date)) return;
            const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
            if (!monthly[key]) monthly[key] = 0;
            monthly[key] += inv.totalAmount || 0;
        });
        let tableHtml = '<table class="table table-bordered"><thead><tr><th>ماه</th><th>مبلغ فروش (تومان)</th></tr></thead><tbody>';
        Object.keys(monthly).sort().forEach(month => {
            tableHtml += `<tr><td>${month}</td><td>${monthly[month].toLocaleString()}</td></tr>`;
        });
        tableHtml += '</tbody></table>';
        monthlyContainer.innerHTML = tableHtml;
    } catch (error) {
        console.error('loadReports', error);
        bestContainer.innerHTML = '<p>خطا در محاسبه گزارش!</p>';
        monthlyContainer.innerHTML = '<p>خطا در محاسبه گزارش!</p>';
    }
}

// تابع حذف محصول
window.deleteProduct = async function(productId) {
    if (!confirm('آیا از حذف این محصول مطمئن هستید؟')) return;
    try {
        const token = localStorage.getItem('token');
        await productAPI.deleteProduct(productId, token);
        alert('محصول با موفقیت حذف شد');
        loadProducts();
    } catch (error) {
        console.error('deleteProduct', error);
        alert('خطا در حذف محصول!');
    }
};

// بارگذاری همه بخش‌ها پس از آماده شدن DOM
document.addEventListener('DOMContentLoaded', async () => {
    await loadProducts();
    await loadUsers();
    await loadOrders();
    await loadReports();

    // می‌توانید رویدادهای دیگری مانند افزودن محصول را نیز در اینجا تنظیم کنید
});
