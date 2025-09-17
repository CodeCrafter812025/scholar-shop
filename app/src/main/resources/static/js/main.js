// static/js/main.js
import { productAPI, cart, ui } from './api.js';

document.addEventListener('DOMContentLoaded', loadProductsForHome);

async function loadProductsForHome() {
  const host = document.getElementById('products-container');
  host.innerHTML = '<p class="text-muted">در حال بارگذاری…</p>';
  try {
    const list = await productAPI.getAllProducts(0, 12);
    if (!Array.isArray(list) || list.length === 0) {
      host.innerHTML = '<p class="text-muted">محصولی یافت نشد.</p>';
      return;
    }
    host.innerHTML = list.map(renderCard).join('');
    ui.updateCartBadge();

    window.addToCart = (id, title, price, imgPath) => {
      cart.add({ productId:id, title, price, img: imgPath, quantity:1 });
      ui.toast('به سبد اضافه شد.');
      ui.updateCartBadge(); // تضمین آپدیت فوری
    };
  } catch (e) {
    console.error('Error loading products:', e);
    host.innerHTML = '<p class="text-danger">خطا در بارگذاری محصولات!</p>';
  }
}

function renderCard(p) {
  const imgPath = p?.image?.path || '';
  const img = imgPath ? `/${imgPath}` :
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="300" height="200"><rect width="100%" height="100%" fill="%23ddd"/></svg>';
  const price = (p.price || 0).toLocaleString('fa-IR');
  const safeTitle = (p.title||'').replace(/'/g,'\\\'');
  return `
    <div class="col-md-3 mb-4">
      <div class="card h-100">
        <img src="${img}" class="card-img-top" alt="">
        <div class="card-body">
          <h6 class="card-title">${p.title}</h6>
          <p class="card-text text-muted">${price} تومان</p>
          <button class="btn btn-sm btn-primary"
            onclick="addToCart(${p.id}, '${safeTitle}', ${p.price||0}, '${imgPath}')">
            افزودن به سبد
          </button>
        </div>
      </div>
    </div>`;
}
