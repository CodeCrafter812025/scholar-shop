// main.js
import { productAPI, cartAPI, updateCartBadge } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
  updateCartBadge();
  loadProductsForHome();
});

async function loadProductsForHome() {
  const container =
    document.getElementById('home-products') ||
    document.getElementById('products-container') ||
    document.querySelector('#home .row') ||
    document.querySelector('.home-products');

  if (!container) return;

  try {
    const res = await productAPI.getAll(0, 100);
    const list = res?.data?.content ?? res?.content ?? res?.data ?? res;
    const products = Array.isArray(list) ? list : [];

    if (!products.length) {
      container.innerHTML = '<p class="text-muted">محصولی یافت نشد.</p>';
      return;
    }
    container.innerHTML = products.map(cardTemplate).join('');
  } catch (e) {
    console.error('Error loading products:', e);
    container.innerHTML = '<p class="text-danger">خطا در بارگذاری محصولات!</p>';
  }
}

function cardTemplate(p) {
  const img = p?.image?.path || 'images/tshirt1.webp';
  const price = (p?.price ?? 0).toLocaleString('fa-IR');
  return `
    <div class="col-md-4 mb-3">
      <div class="card h-100">
        <img src="${img}" class="card-img-top" alt="${p?.title || ''}" onerror="this.src='images/tshirt1.webp'">
        <div class="card-body d-flex flex-column">
          <h5 class="card-title">${p?.title || '-'}</h5>
          <p class="card-text small text-muted">${p?.description ?? ''}</p>
          <div class="mt-auto d-flex justify-content-between align-items-center">
            <span class="fw-bold">${price} تومان</span>
            <button class="btn btn-sm btn-primary" onclick="addToCart(${p.id}, '${(p?.title||'').replace(/'/g,'\\\'')}', ${Number(p?.price||0)})">افزودن</button>
          </div>
        </div>
      </div>
    </div>
  `;
}

window.addToCart = function(id, title, price) {
  cartAPI.add({ id, title, price: Number(price||0) }, 1);
  updateCartBadge();
  alert('به سبد اضافه شد.');
};
