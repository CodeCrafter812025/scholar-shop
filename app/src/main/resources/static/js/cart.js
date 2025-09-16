// cart.js
import { cartAPI, orderAPI, updateCartBadge } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
  updateCartBadge();
  renderCart();
});

function fmt(n) { return Number(n || 0).toLocaleString('fa-IR'); }

function renderCart() {
  const listEl = document.getElementById('cart-items');
  const totalEl = document.getElementById('cart-total');
  if (!listEl || !totalEl) return;

  const cart = cartAPI.getCart();
  if (!Array.isArray(cart) || cart.length === 0) {
    listEl.innerHTML = '<p class="text-muted">سبد خرید شما خالی است.</p>';
    totalEl.textContent = '۰';
    return;
  }

  listEl.innerHTML = cart.map(it => rowTemplate(it)).join('');
  const total = cart.reduce((s, it) => s + (Number(it.price) || 0) * (Number(it.quantity) || 0), 0);
  totalEl.textContent = fmt(total);
}

function rowTemplate(it) {
  const itemTotal = Number(it.price) * Number(it.quantity);
  return `
    <div class="d-flex align-items-center justify-content-between border rounded p-2 mb-2">
      <div class="me-2">${it.title || '-'}</div>
      <div class="d-flex align-items-center gap-2">
        <button class="btn btn-sm btn-outline-secondary" onclick="decQty(${it.id})">−</button>
        <span id="qty-${it.id}" class="mx-1">${it.quantity}</span>
        <button class="btn btn-sm btn-outline-secondary" onclick="incQty(${it.id})">+</button>
      </div>
      <div class="min-w-100 text-end">${fmt(it.price)} × ${fmt(it.quantity)} = <b>${fmt(itemTotal)}</b></div>
      <button class="btn btn-sm btn-outline-danger" onclick="removeItem(${it.id})">حذف</button>
    </div>
  `;
}

window.incQty = function(id) {
  const current = cartAPI.getCart().find(x => x.id === id)?.quantity || 1;
  cartAPI.updateQty(id, current + 1);
  updateCartBadge(); renderCart();
};
window.decQty = function(id) {
  const current = cartAPI.getCart().find(x => x.id === id)?.quantity || 1;
  cartAPI.updateQty(id, Math.max(1, current - 1));
  updateCartBadge(); renderCart();
};
window.removeItem = function(id) {
  cartAPI.removeItem(id);
  updateCartBadge(); renderCart();
};

document.getElementById('checkoutBtn')?.addEventListener('click', async (e) => {
  e.preventDefault();
  const token = localStorage.getItem('token');
  if (!token) { alert('لطفاً ابتدا وارد شوید.'); location.href = 'auth.html'; return; }

  const cart = cartAPI.getCart();
  if (!cart.length) { alert('سبد خرید خالی است.'); return; }

  try {
    const items = cart.map(it => ({ productId: it.id, quantity: it.quantity }));
    await orderAPI.createOrder(items);
    alert('سفارش شما ثبت شد!');
    cartAPI.setCart([]); updateCartBadge(); renderCart();
    location.href = 'orders.html';
  } catch (err) {
    console.error('Checkout error:', err);
    alert('خطا در ثبت سفارش: ' + (err.message || ''));
  }
});
