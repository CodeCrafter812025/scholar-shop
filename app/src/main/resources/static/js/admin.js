// admin.js
import { productAPI, adminProductAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
  // بارگذاری اولیه
  await loadProducts();
  await loadOrdersForMe();

  // افزودن محصول
  const form = document.getElementById('addProductForm');
  form?.addEventListener('submit', onCreateProduct);

  // دانلود گزارش سفارش‌ها
  document.getElementById('download-report-btn')?.addEventListener('click', downloadOrdersCsv);
});

async function loadProducts() {
  const host = document.getElementById('products-list');
  if (!host) return;

  try {
    const res = await productAPI.getAll(0, 200);
    const list = res?.data?.content ?? res?.content ?? res?.data ?? res;
    const products = Array.isArray(list) ? list : [];

    if (!products.length) {
      host.innerHTML = `<div class="alert alert-secondary">محصولی یافت نشد.</div>`;
      return;
    }

    host.innerHTML = `
      <div class="row">
        ${products.map(p => productCard(p)).join('')}
      </div>
    `;
  } catch (e) {
    console.error('loadProducts error:', e);
    host.innerHTML = `<div class="alert alert-danger">خطا در دریافت محصولات</div>`;
  }
}

function productCard(p) {
  const img = p?.image?.path || 'images/tshirt1.webp';
  const price = (p?.price ?? 0).toLocaleString('fa-IR');
  return `
    <div class="col-md-4 mb-3">
      <div class="card h-100">
        <img src="${img}" class="card-img-top" alt="${p?.title || ''}" onerror="this.src='images/tshirt1.webp'">
        <div class="card-body d-flex flex-column">
          <h6 class="card-title">${p?.title || '-'}</h6>
          <div class="mt-auto d-flex justify-content-between align-items-center">
            <span class="fw-bold">${price}</span>
            <button class="btn btn-sm btn-outline-danger" onclick="deleteProduct(${p.id})">حذف</button>
          </div>
        </div>
      </div>
    </div>
  `;
}

async function onCreateProduct(e) {
  e.preventDefault();
  try {
    const title = document.getElementById('prodTitle')?.value?.trim();
    const description = document.getElementById('prodDesc')?.value?.trim() || '';
    const price = Number(document.getElementById('prodPrice')?.value);
    const categoryId = Number(document.getElementById('prodCategoryId')?.value);
    const imageIdRaw = document.getElementById('prodImageId')?.value?.trim();

    if (!title || !price || !categoryId) {
      alert('عنوان، قیمت و شناسه دسته‌بندی الزامی است.');
      return;
    }

    const payload = { title, description, price, enable: true, exist: true, category: { id: categoryId } };
    const imageId = Number(imageIdRaw);
    if (!Number.isNaN(imageId) && imageId > 0) payload.image = { id: imageId };

    await adminProductAPI.create(payload);

    // بستن مودال اگر bootstrap وجود دارد
    const modalEl = document.getElementById('addProductModal');
    if (modalEl && window.bootstrap) window.bootstrap.Modal.getInstance(modalEl)?.hide();
    e.target.reset();

    alert('محصول با موفقیت ثبت شد.');
    await loadProducts();
  } catch (err) {
    console.error('createProduct error:', err);
    alert('خطا در ایجاد محصول: ' + (err.message || ''));
  }
}

window.deleteProduct = async function(id) {
  if (!confirm('محصول حذف شود؟')) return;
  try {
    await adminProductAPI.delete(id);
    alert('محصول حذف شد.');
    await loadProducts();
  } catch (err) {
    console.error('deleteProduct error:', err);
    alert('خطا در حذف محصول: ' + (err.message || ''));
  }
};

// سفارش‌های کاربر فعلی (به‌صورت نمونه)
async function loadOrdersForMe() {
  const host = document.getElementById('orders-list');
  if (!host) return;

  try {
    const res = await orderAPI.getUserOrders();
    const orders = res?.data || [];

    if (!orders.length) {
      host.innerHTML = `<div class="alert alert-secondary">سفارشی یافت نشد.</div>
        <button class="btn btn-secondary mt-2" id="download-report-btn">دانلود گزارش سفارش‌ها</button>`;
      return;
    }

    host.innerHTML = `
      <div class="list-group mb-3">
        ${orders.map(o => orderItem(o)).join('')}
      </div>
      <button class="btn btn-secondary" id="download-report-btn">دانلود گزارش سفارش‌ها</button>
    `;

    document.getElementById('download-report-btn')?.addEventListener('click', downloadOrdersCsv);
  } catch (e) {
    console.error('loadOrdersForMe error:', e);
    host.innerHTML = `<div class="alert alert-danger">خطا در دریافت سفارش‌ها</div>`;
  }
}

function orderItem(o) {
  const when = new Date(o.createDate).toLocaleString('fa-IR');
  const sum = (o.totalAmount || 0).toLocaleString('fa-IR');
  return `
    <a class="list-group-item list-group-item-action">
      <div class="d-flex w-100 justify-content-between">
        <h6 class="mb-1">سفارش #${o.id}</h6>
        <small>${when}</small>
      </div>
      <p class="mb-1">وضعیت: ${o.status}</p>
      <small>جمع کل: ${sum} تومان</small>
    </a>
  `;
}

// دانلود CSV سفارش‌ها
async function downloadOrdersCsv() {
  try {
    const res = await orderAPI.getUserOrders();
    const orders = res?.data || [];
    const rows = [
      ['id', 'createDate', 'status', 'totalAmount'],
      ...orders.map(o => [o.id, o.createDate, o.status, o.totalAmount]),
    ].map(r => r.join(',')).join('\n');

    const blob = new Blob([rows], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'orders.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  } catch (e) {
    alert('خطا در ساخت گزارش');
    console.error(e);
  }
}
