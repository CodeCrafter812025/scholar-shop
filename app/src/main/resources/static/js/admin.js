// static/js/admin.js
import { auth, ui, panelAPI, productAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
  // اگر لاگین نیست:
  if (!auth.getToken()) {
    ui.toast('برای ورود به پنل مدیریت باید وارد شوید.');
    // می‌تونی ریدایرکت کنی:
    // location.href = '/auth.html';
  }

  await loadProducts();
  await loadOrders();
  await loadReports();
  await loadUsers();


  // ایجاد محصول
  document.getElementById('addProductForm')?.addEventListener('submit', onCreate);
  // دانلود گزارش سفارش‌ها (CSV سمت کلاینت)
  document.getElementById('download-report-btn')?.addEventListener('click', downloadOrdersCsv);
});

async function loadProducts() {
  const host = document.getElementById('products-list');
  if (!host) return;
  host.innerHTML = '<p class="text-muted">در حال بارگذاری محصولات…</p>';
  try {
    const res = await panelAPI.listProducts(0, 50);
    const items = Array.isArray(res?.data) ? res.data : (res || []);
    if (!items.length) {
      host.innerHTML = '<p class="text-muted">محصولی یافت نشد.</p>';
      return;
    }
    host.innerHTML = items.map(p => `
      <div class="card mb-2">
        <div class="card-body d-flex justify-content-between align-items-center">
          <div>
            <div class="fw-bold">${p.title}</div>
            <div class="text-muted">${(p.price ?? 0).toLocaleString('fa-IR')} تومان</div>
          </div>
          <div>
            <button class="btn btn-sm btn-outline-danger" data-id="${p.id}" data-action="delete">حذف</button>
          </div>
        </div>
      </div>
    `).join('');

    host.querySelectorAll('button[data-action="delete"]').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = Number(btn.dataset.id);
        if (!confirm('حذف این محصول؟')) return;
        try {
          await panelAPI.deleteProduct(id);
          ui.toast('حذف شد.');
          await loadProducts();
        } catch (e) {
          console.error('deleteProduct error', e);
          ui.toast('حذف انجام نشد (احتمالاً به‌صورت نرم غیرفعال شد یا مجوز ندارید).');
          await loadProducts();
        }
      });
    });
  } catch (e) {
    console.error('loadProducts error', e);
    host.innerHTML = '<p class="text-danger">عدم دسترسی یا خطای سرور.</p>';
  }
}

async function onCreate(e) {
  e.preventDefault();
  const title = document.getElementById('prodTitle').value.trim();
  const price = Number(document.getElementById('prodPrice').value);
  const categoryId = Number(document.getElementById('prodCategoryId').value);
  const imageId = document.getElementById('prodImageId').value ? Number(document.getElementById('prodImageId').value) : 10; // پیش‌فرض نمونه
  const desc = document.getElementById('prodDesc').value ?? '';

  if (!title || !price || !categoryId) {
    ui.toast('عنوان، قیمت و شناسه دسته‌بندی الزامی است.');
    return;
  }

  const dto = {
    title,
    price,
    description: desc,
    category: { id: categoryId },
    image: { id: imageId },
    enable: true,
    exist: true,
  };

  try {
    await panelAPI.createProduct(dto);
    ui.toast('محصول ایجاد شد.');
    e.target.reset();
    const modal = bootstrap.Modal.getInstance(document.getElementById('addProductModal'));
    modal?.hide();
    await loadProducts();
  } catch (err) {
    console.error('createProduct error', err);
    ui.toast('ایجاد محصول انجام نشد. شناسهٔ تصویر/دسته‌بندی باید معتبر باشد.');
  }
}

async function loadOrders() {
  const host = document.getElementById('orders-list');
  if (!host) return;
  host.innerHTML = '<p class="text-muted">در حال بارگذاری سفارش‌ها…</p>';
  try {
    // اگر API ادمین همه سفارش‌ها ندارید، از سفارش‌های کاربر جاری استفاده می‌کنیم
    const data = await orderAPI.getUserInvoices();
    const orders = Array.isArray(data) ? data : (data?.data || []);
    if (!orders?.length) {
      host.innerHTML = '<p class="text-muted">سفارشی یافت نشد.</p>';
      return;
    }
    host.innerHTML = orders.map(o => `
      <div class="card mb-2">
        <div class="card-header d-flex justify-content-between">
          <span>سفارش #${o.id}</span>
          <span class="badge ${o.status === 'paid' ? 'bg-success' : 'bg-warning'}">${o.status || 'InProgress'}</span>
        </div>
        <div class="card-body">
          <div>تاریخ: ${new Date(o.createDate).toLocaleDateString('fa-IR')}</div>
          <div>جمع کل: ${(o.totalAmount ?? 0).toLocaleString('fa-IR')} تومان</div>
        </div>
      </div>
    `).join('');
  } catch (e) {
    console.error('loadOrders error', e);
    host.innerHTML = '<p class="text-danger">عدم دسترسی یا خطای سرور.</p>';
  }
}

async function downloadOrdersCsv() {
  try {
    const data = await orderAPI.getUserInvoices();
    const orders = Array.isArray(data) ? data : (data?.data || []);
    if (!orders?.length) { ui.toast('سفارشی برای دانلود وجود ندارد.'); return; }

    const rows = [
      ['id','date','status','total'],
      ...orders.map(o => [o.id, o.createDate, o.status, o.totalAmount]),
    ];
    const csv = rows.map(r => r.map(x => `"${(x ?? '').toString().replace(/"/g,'""')}"`).join(',')).join('\r\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'orders.csv';
    a.click();
    URL.revokeObjectURL(a.href);
  } catch (e) {
    console.error('downloadOrdersCsv error', e);
    ui.toast('دانلود گزارش انجام نشد.');
  }
}

async function loadReports() {
  const bestEl = document.getElementById('best-selling-container');
  const monthEl = document.getElementById('monthly-sales-container');
  if (bestEl) bestEl.innerHTML = '<p>در حال بارگذاری…</p>';
  if (monthEl) monthEl.innerHTML = '<p>در حال بارگذاری…</p>';
  try {
    const tops = await productAPI.getTop('Popular');
    const list = Array.isArray(tops) ? tops : (tops?.data || []);
    bestEl.innerHTML = list.length
      ? '<ol>' + list.map((p, i) => `<li>${p.title} — تعداد ${p.visitCount ?? '-'} </li>`).join('') + '</ol>'
      : '<p class="text-muted">یافت نشد.</p>';
  } catch (e) {
    bestEl.innerHTML = '<p class="text-danger">خطا در بارگذاری.</p>';
  }

  try {
    // فروش ماهانه ساده از سفارش‌های کاربر جاری (دمو)
    const data = await orderAPI.getUserInvoices();
    const orders = Array.isArray(data) ? data : (data?.data || []);
    const sum = orders.reduce((s, o) => s + (o.totalAmount || 0), 0);
    const now = new Date();
    monthEl.innerHTML = `<p class="fs-5"> ${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')} — ${sum.toLocaleString('fa-IR')} تومان</p>`;
  } catch (e) {
    monthEl.innerHTML = '<p class="text-danger">خطا در بارگذاری.</p>';
  }
}


async function loadUsers() {
  const host = document.getElementById('users-list');
  if (!host) return;
  host.innerHTML = '<p class="text-muted">در حال بارگذاری کاربران…</p>';
  try {
    const res = await panelAPI.listUsers(0, 50);
    const items = Array.isArray(res?.data) ? res.data : (res || []);
    if (!items.length) {
      host.innerHTML = '<p class="text-muted">کاربری یافت نشد.</p>';
      return;
    }
    host.innerHTML = items.map(u => `
      <div class="d-flex justify-content-between align-items-center border rounded p-2 mb-2">
        <div>
          <div class="fw-bold">${u.username ?? '-'}</div>
          <div class="text-muted">${u.email ?? ''}</div>
        </div>
        <span class="badge bg-secondary">#${u.id}</span>
      </div>
    `).join('');
  } catch (e) {
    console.error('loadUsers error', e);
    host.innerHTML = '<p class="text-danger">عدم دسترسی یا خطای سرور.</p>';
  }
}

