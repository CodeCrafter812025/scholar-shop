// static/js/cart.js
import { cart, ui, orderAPI, token } from './api.js';

const NOIMG = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect width="100%" height="100%" fill="%23ddd"/><text x="50%" y="55%" font-size="10" text-anchor="middle" fill="%23666">No image</text></svg>';

document.addEventListener('DOMContentLoaded', () => {
  renderCart();
  ui.updateCartBadge();
});

function row(i) {
  const imgSrc = i.img ? `/${i.img}` : NOIMG;
  const price = (i.price || 0).toLocaleString('fa-IR');
  return `
    <div class="d-flex align-items-center border rounded p-2 mb-2">
      <img src="${imgSrc}" class="me-3" width="80" height="80" alt="">
      <div class="flex-grow-1">
        <div class="fw-bold">${i.title || 'محصول'}</div>
        <div class="text-muted small">${price} تومان</div>
      </div>
      <div class="d-flex align-items-center">
        <button class="btn btn-outline-secondary btn-sm" onclick="decr(${i.productId})">−</button>
        <span class="px-3">${i.quantity}</span>
        <button class="btn btn-outline-secondary btn-sm" onclick="incr(${i.productId})">+</button>
        <button class="btn btn-outline-danger btn-sm ms-2" onclick="removeItem(${i.productId})">حذف</button>
      </div>
    </div>
  `;
}

export async function renderCart() {
  const host = document.getElementById('cart-items');
  const totalEl = document.getElementById('cart-total');
  const items = cart.read();

  if (!items.length) {
    host.innerHTML = '<p class="text-muted">سبد شما خالی است.</p>';
    totalEl.textContent = '0';
    return;
  }

  host.innerHTML = items.map(row).join('');
  totalEl.textContent = cart.total().toLocaleString('fa-IR');
}

window.incr = (id) => { const it = cart.read().find(x=>x.productId===id); cart.setQty(id, (it?.quantity||0)+1); renderCart(); };
window.decr = (id) => { const it = cart.read().find(x=>x.productId===id); const q = (it?.quantity||0)-1; cart.setQty(id, q); renderCart(); };
window.removeItem = (id) => { cart.remove(id); renderCart(); };

document.getElementById('checkoutBtn')?.addEventListener('click', async () => {
  if (!token.get()) { ui.toast('لطفاً ابتدا وارد شوید.'); location.href = '/auth.html'; return; }
  const items = cart.read().map(x => ({ productId: x.productId, quantity: x.quantity || 1 }));
  if (!items.length) { ui.toast('سبد خرید خالی است.'); return; }
  try {
    await orderAPI.createOrder(items);
    ui.toast('سفارش شما ثبت شد!');
    cart.clear();
    renderCart();
  } catch (e) {
    console.error(e);
    ui.toast('خطا در ثبت سفارش');
  }
});
