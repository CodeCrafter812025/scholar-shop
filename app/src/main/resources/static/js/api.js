// static/js/api.js
const API_BASE = ''; // همان origin فعلی (https://127.0.0.1)

async function http(method, url, { body, headers } = {}) {
  const res = await fetch(API_BASE + url, {
    method,
    headers: {
      ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...(headers || {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);

  const ct = res.headers.get('content-type') || '';
  if (!ct.includes('application/json')) return null;

  const json = await res.json(); // بک‌اند: {status, message, data}
  return json?.data ?? json;
}

/** ---- auth helper ---- */
export const auth = {
  getToken() { return localStorage.getItem('token') || ''; },
  setToken(t) { localStorage.setItem('token', t || ''); },
  clear() { localStorage.removeItem('token'); },
  bearer() { const t = this.getToken(); return t ? `Bearer ${t}` : ''; },
  headers(json = true) {
    const h = {};
    if (json) h['Content-Type'] = 'application/json';
    const b = this.bearer();
    if (b) h['Authorization'] = b;
    return h;
  },
};

/** ---- UI helpers ---- */
export const ui = {
  toast(msg) {
    try {
      const toastEl = document.getElementById('liveToast');
      const body = toastEl?.querySelector('.toast-body');
      if (body) body.textContent = msg;
      if (toastEl) new bootstrap.Toast(toastEl).show();
    } catch { alert(msg); }
  },
  setCartBadge(n) {
    const el = document.querySelector('.cart-icon + .badge');
    if (el) el.textContent = String(n ?? 0);
  },
};

/** ---- Public Product APIs ---- */
export const productAPI = {
  async getAllProducts(page = 0, size = 12, q = '') {
    const qs = new URLSearchParams({ page, size, ...(q ? { q } : {}) }).toString();
    const data = await http('GET', `/api/product/search?${qs}`);
    // اگر Page بود -> content
    if (data && typeof data === 'object' && 'content' in data && Array.isArray(data.content)) {
      return data.content;
    }
    // در غیر این صورت اگر آرایه بود همان را برگردان
    if (Array.isArray(data)) return data;
    // هیچ
    return [];
  },
  getTop(type = 'Popular') {
    return http('GET', `/api/product/top/${type}`);
  },
  getById(id) {
    return http('GET', `/api/product/${id}`);
  },
};

/** ---- Cart (localStorage) ---- */
const CART_KEY = 'cart';
function readCart() { try { return JSON.parse(localStorage.getItem(CART_KEY) || '[]'); } catch { return []; } }
function writeCart(items) { localStorage.setItem(CART_KEY, JSON.stringify(items || [])); }

export const cartAPI = {
  get() { return readCart(); },
  add(product, qty = 1) {
    const cart = readCart();
    const i = cart.findIndex(x => x.id === product.id);
    if (i >= 0) cart[i].qty += qty;
    else cart.push({ id: product.id, title: product.title, price: product.price, image: product.image, qty });
    writeCart(cart);
    return cart;
  },
  setQty(id, qty) {
    const cart = readCart().map(x => x.id === id ? { ...x, qty } : x).filter(x => x.qty > 0);
    writeCart(cart);
    return cart;
  },
  remove(id) {
    writeCart(readCart().filter(x => x.id !== id));
    return readCart();
  },
  clear() { writeCart([]); },
  count() { return readCart().reduce((s, x) => s + (x.qty || 0), 0); },
  total() { return readCart().reduce((s, x) => s + (x.price || 0) * (x.qty || 0), 0); },
};

// برای سازگاری قدیمی
export { cartAPI as cart };

/** ---- Orders (user) ---- */
export const orderAPI = {
  createInvoice(body) {
    return http('POST', '/api/invoice', { body, headers: auth.headers() });
  },
  getUserInvoices() {
    return http('GET', '/api/invoice', { headers: auth.headers(false) });
  },
};

/** ---- User (login) ---- */
export const userAPI = {
  async login(username, password) {
    const out = await http('POST', '/api/user/login', {
      body: { username, password },
      headers: { 'Content-Type': 'application/json' },
    });
    // پاسخ login شما شبیه {id, username, token} است
    const token = out?.token || out?.data?.token || null;
    if (token) auth.setToken(token);
    return out;
  },
  logout() { auth.clear(); },
};

/** ---- Admin Panel APIs ---- */
export const panelAPI = {
  listProducts(page = 0, size = 20) {
    const qs = new URLSearchParams({ page, size }).toString();
    return http('GET', `/api/panel/product?${qs}`, { headers: auth.headers(false) });
  },
  createProduct(dto) {
    return http('POST', '/api/panel/product', { body: dto, headers: auth.headers() });
  },
  updateProduct(dto) {
    return http('PUT', '/api/panel/product', { body: dto, headers: auth.headers() });
  },
  deleteProduct(id) {
    return http('DELETE', `/api/panel/product/${id}`, { headers: auth.headers(false) });
  },

  listUsers(page = 0, size = 50) {
    const qs = new URLSearchParams({ page, size }).toString();
    return http('GET', `/api/panel/user?${qs}`, { headers: auth.headers(false) });
  },

  listInvoicesAllFallback() {
    return orderAPI.getUserInvoices();
  },
};
