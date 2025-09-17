// static/js/admin.js
import { panelProductAPI, panelReportAPI, ui } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
  loadProducts();
  loadOrders();
  loadReports();

  document.getElementById('addProductForm')?.addEventListener('submit', onCreate);
  document.getElementById('download-report-btn')?.addEventListener('click', downloadOrdersCsv);

  // تلاش برای تب کاربران (اگر endpointی جواب داد، لیست می‌سازیم)
  loadUsers();
});

/* ------------------ Products ------------------ */
async function loadProducts() {
  const host = document.getElementById('products-list');
  host.innerHTML = '<p class="text-muted">در حال بارگذاری…</p>';
  try {
    const list = await panelProductAPI.list();
    if (!Array.isArray(list) || list.length === 0) {
      host.innerHTML = '<p class="text-muted">محصولی یافت نشد.</p>';
      return;
    }
    host.innerHTML = list.map(p => card(p)).join('');
    // expose delete globally
    window.deleteProduct = async (id) => {
      try {
        await panelProductAPI.remove(id);
        ui.toast('محصول حذف شد.');
        loadProducts();
      } catch (e) {
        console.error('deleteProduct error:', e);
        ui.toast('خطا در حذف محصول!');
      }
    };
  } catch (e) {
    console.error(e);
    host.innerHTML = '<p class="text-danger">خطا در دریافت محصولات.</p>';
  }
}

function card(p) {
  const img = p?.image?.path ? `/${p.image.path}` :
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="300" height="200"><rect width="100%" height="100%" fill="%23eee"/></svg>';
  const price = (p.price || 0).toLocaleString('fa-IR');
  return `
    <div class="card mb-2">
      <div class="row g-0 align-items-center">
        <div class="col-auto"><img src="${img}" width="110" height="80" class="rounded-start" alt=""></div>
        <div class="col">
          <div class="card-body py-2">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <div class="fw-bold">${p.title}</div>
                <div class="text-muted small">${price} تومان</div>
              </div>
              <div>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteProduct(${p.id})">حذف</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>`;
}

async function onCreate(e) {
  e.preventDefault();
  const title = document.getElementById('prodTitle')?.value?.trim();
  const price = Number(document.getElementById('prodPrice')?.value || 0);
  const categoryId = document.getElementById('prodCategoryId')?.value?.trim();
  const imageId = document.getElementById('prodImageId')?.value?.trim();
  const description = document.getElementById('prodDesc')?.value?.trim();

  if (!title || !price || !imageId || !description) {
    ui.toast('عنوان، قیمت، تصویر و توضیحات الزامی است.');
    return;
  }

  try {
    await panelProductAPI.create({ title, price, categoryId, imageId, description });
    ui.toast('محصول ثبت شد.');
    const modalEl = document.getElementById('addProductModal');
    if (modalEl) bootstrap.Modal.getOrCreateInstance(modalEl).hide();
    e.target.reset();
    loadProducts();
  } catch (err) {
    console.error('createProduct error', err);
    ui.toast('خطا در ثبت محصول! (imageId/description را بررسی کنید)');
  }
}


/* ------------------ Orders (Admin view – از /api/invoice) ------------------ */
async function loadOrders() {
  const host = document.getElementById('orders-list');
  if (!host) return;
  host.innerHTML = '<p class="text-muted">در حال بارگذاری سفارش‌ها…</p>';
  try {
    const invoices = await panelReportAPI.listInvoicesAll(); // از /api/invoice
    if (!Array.isArray(invoices) || invoices.length === 0) {
      host.innerHTML = '<p class="text-muted">سفارشی یافت نشد.</p>';
      return;
    }
    host.innerHTML = invoices.map(invCard).join('');
  } catch (e) {
    console.error(e);
    host.innerHTML = '<p class="text-danger">خطا در دریافت سفارش‌ها.</p>';
  }
}

function invCard(o) {
  const d = (o.createDate ? new Date(o.createDate) : new Date());
  const total = (o.totalAmount||0).toLocaleString('fa-IR');
  const items = Array.isArray(o.items) ? o.items : [];
  return `
    <div class="card mb-3">
      <div class="card-header d-flex justify-content-between">
        <span>سفارش #${o.id}</span>
        <span class="badge bg-${o.status==='InProgress' ? 'warning' : 'success'}">${o.status||''}</span>
      </div>
      <div class="card-body">
        <div class="mb-2">تاریخ: ${d.toLocaleDateString('fa-IR')}</div>
        <ul class="mb-2">
          ${items.map(i => `<li>${i.product?.title || ''} × ${i.quantity} — ${ (i.price||0).toLocaleString('fa-IR') } تومان</li>`).join('')}
        </ul>
        <div class="fw-bold">جمع کل: ${total} تومان</div>
      </div>
    </div>`;
}

/* ------------------ Reports (Best sellers & monthly) ------------------ */
async function loadReports() {
  const bestHost = document.getElementById('best-selling-container');
  const monthHost = document.getElementById('monthly-sales-container');
  if (bestHost) bestHost.textContent = 'در حال بارگذاری...';
  if (monthHost) monthHost.textContent = 'در حال بارگذاری...';

  try {
    const invoices = await panelReportAPI.listInvoicesAll();
    // Best sellers
    const map = new Map();
    invoices.forEach(inv => (inv.items||[]).forEach(it => {
      const name = it.product?.title || `#${it.product?.id||''}`;
      map.set(name, (map.get(name)||0) + (it.quantity||0));
    }));
    const top = [...map.entries()].sort((a,b)=>b[1]-a[1]).slice(0,5);
    if (bestHost) {
      bestHost.innerHTML = top.length
        ? `<ol class="mb-0">${top.map(([n,c])=>`<li class="mb-1">${c} تعداد — ${n}</li>`).join('')}</ol>`
        : '<p class="text-muted mb-0">داده‌ای نیست.</p>';
    }

    // Monthly
    const monthMap = new Map();
    invoices.forEach(inv => {
      const d = inv.createDate ? new Date(inv.createDate) : null;
      if (!d) return;
      const key = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}`;
      monthMap.set(key, (monthMap.get(key)||0) + (inv.totalAmount||0));
    });
    const months = [...monthMap.entries()].sort((a,b)=>a[0].localeCompare(b[0]));
    if (monthHost) {
      monthHost.innerHTML = months.length
        ? months.map(([m,sum])=>`<div>${m.replace('-',' / ')} — ${sum.toLocaleString('fa-IR')} تومان</div>`).join('')
        : '<p class="text-muted mb-0">داده‌ای نیست.</p>';
    }
  } catch (e) {
    console.error('reports', e);
    if (bestHost) bestHost.innerHTML = '<p class="text-danger">API گزارش سمت سرور در دسترس نیست.</p>';
    if (monthHost) monthHost.innerHTML = '<p class="text-danger">API گزارش سمت سرور در دسترس نیست.</p>';
  }
}

/* ------------------ Download CSV ------------------ */
async function downloadOrdersCsv() {
  try {
    const invoices = await panelReportAPI.listInvoicesAll();
    if (!invoices.length) { ui.toast('سفارشی برای گزارش وجود ندارد.'); return; }
    const rows = [['id','createDate','status','totalAmount','itemsCount']];
    invoices.forEach(o => rows.push([
      o.id,
      o.createDate || '',
      o.status || '',
      o.totalAmount || 0,
      Array.isArray(o.items) ? o.items.length : 0
    ]));
    const csv = rows.map(r => r.map(cell => `"${String(cell).replace(/"/g,'""')}"`).join(',')).join('\r\n');
    const blob = new Blob([csv], {type:'text/csv;charset=utf-8;'});
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `orders-report-${new Date().toISOString().slice(0,10)}.csv`;
    document.body.appendChild(a); a.click(); a.remove();
  } catch (e) {
    console.error(e);
    ui.toast('خطا در تولید گزارش');
  }
}
