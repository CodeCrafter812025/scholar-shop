// static/js/cart.js
import { cartAPI, ui, orderAPI, auth } from './api.js';

const NOIMG = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect width="100%" height="100%" fill="%23ddd"/><text x="50%" y="55%" font-size="10" text-anchor="middle" fill="%23666">No image</text></svg>';

document.addEventListener('DOMContentLoaded', () => {
  renderCart();
  ui.setCartBadge(cartAPI.count());
});

function row(i) {
  const imgPath = i?.image?.path || '';
  const imgSrc = imgPath ? `/${imgPath}` : NOIMG;
  const price = (i.price || 0).toLocaleString('fa-IR');
  return `
    <div class="d-flex align-items-center border rounded p-2 mb-2">
      <img src="${imgSrc}" class="me-3" width="80" height="80" alt="">
      <div class="flex-grow-1">
        <div class="fw-bold">${i.title || 'محصول'}</div>
        <div class="text-muted small">${price} تومان</div>
      </div>
      <div class="d-flex align-items-center">
        <button class="btn btn-outline-secondary btn-sm" onclick="decr(${i.id})">−</button>
        <span class="px-3">${i.qty || 0}</span>
        <button class="btn btn-outline-secondary btn-sm" onclick="incr(${i.id})">+</button>
        <button class="btn btn-outline-danger btn-sm ms-2" onclick="removeItem(${i.id})">حذف</button>
      </div>
    </div>
  `;
}

export function renderCart() {
  const host = document.getElementById('cart-items');
  const totalEl = document.getElementById('cart-total');
  const items = cartAPI.get();

  if (!items.length) {
    host.innerHTML = '<p class="text-muted">سبد شما خالی است.</p>';
    totalEl.textContent = '0';
    ui.setCartBadge(0);
    return;
  }

  host.innerHTML = items.map(row).join('');
  totalEl.textContent = cartAPI.total().toLocaleString('fa-IR');
  ui.setCartBadge(cartAPI.count());
}

window.incr = (id) => {
  const it = cartAPI.get().find(x => x.id === id);
  cartAPI.setQty(id, (it?.qty || 0) + 1);
  renderCart();
};

window.decr = (id) => {
  const it = cartAPI.get().find(x => x.id === id);
  const q = (it?.qty || 0) - 1;
  cartAPI.setQty(id, q);
  renderCart();
};

window.removeItem = (id) => {
  cartAPI.remove(id);
  renderCart();
};

document.getElementById('checkoutBtn')?.addEventListener('click', async () => {
  if (!auth.getToken()) {
    ui.toast('لطفاً ابتدا وارد شوید.');
    location.href = '/auth.html';
    return;
  }
  const items = cartAPI.get().map(x => ({ productId: x.id, quantity: x.qty || 1 }));
  if (!items.length) { ui.toast('سبد خرید خالی است.'); return; }

  try {
    await orderAPI.createInvoice({ items });
    ui.toast('سفارش شما ثبت شد!');
    cartAPI.clear();
    renderCart();
  } catch (e) {
    console.error(e);
    ui.toast('خطا در ثبت سفارش');
  }
});
