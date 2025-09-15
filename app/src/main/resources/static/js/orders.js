// app/src/main/resources/static/js/orders.js

import { orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const ordersContainer = document.getElementById('orders-container');
    const token = localStorage.getItem('token');

    if (!token) {
        ordersContainer.innerHTML = '<p>لطفاً ابتدا وارد شوید!</p>';
        return;
    }

    try {
        const ordersRes = await orderAPI.getUserOrders(token);
        const orders = ordersRes.data || ordersRes;

        if (!Array.isArray(orders) || orders.length === 0) {
            ordersContainer.innerHTML = '<p>هیچ سفارشی یافت نشد!</p>';
            return;
        }

        ordersContainer.innerHTML = orders.map(order => {
            const items = order.items || order.products || [];
            return `
                <div class="card mb-3">
                    <div class="card-header d-flex justify-content-between">
                        <span>سفارش #${order.id || order._id}</span>
                        <span class="badge bg-${(order.status === 'paid' || order.status === 'completed') ? 'success' : 'warning'}">
                            ${(order.status === 'paid' || order.status === 'completed') ? 'پرداخت شده' : 'در حال پردازش'}
                        </span>
                    </div>
                    <div class="card-body">
                        <h5>تاریخ: ${new Date(order.createDate || order.createdAt).toLocaleDateString('fa-IR')}</h5>
                        <h6>محصولات:</h6>
                        <ul>
                            ${items.map(item => `
                                <li>${item.product?.title || item.product?.name || ''} - تعداد: ${item.quantity || 1} - قیمت: ${item.price || item.product?.price || 0} تومان</li>
                            `).join('')}
                        </ul>
                        <h5 class="text-primary">جمع کل: ${order.totalAmount || order.amount} تومان</h5>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        ordersContainer.innerHTML = '<p>خطا در دریافت سفارش‌ها!</p>';
        console.error(error);
    }
});

// خروج کاربر
document.getElementById('logout-btn')?.addEventListener('click', () => {
    localStorage.removeItem('token');
    window.location.href = 'index.html';
});
