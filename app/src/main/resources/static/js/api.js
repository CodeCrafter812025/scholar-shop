// api.js

const authHeader = () => {
  const t = localStorage.getItem('token');
  return t ? { Authorization: `Bearer ${t}` } : {};
};

async function http(method, url, body = null, withAuth = false) {
  const res = await fetch(url, {
    method,
    headers: {
      ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...(withAuth ? authHeader() : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  let data = null;
  try { data = await res.json(); } catch (_) {}

  if (!res.ok) throw new Error((data && data.message) || `HTTP ${res.status}`);
  if (data && data.status === 'Error') throw new Error(data.message || 'Server Error');

  return data;
}

// -------- Products ----------
export const productAPI = {
  getAll: (page = 0, size = 50) => http('GET', `/api/product/search?page=${page}&size=${size}`),
  getById: (id) => http('GET', `/api/product/${id}`),
  getProductById: (id) => http('GET', `/api/product/${id}`),
};

// -------- Orders (Invoices) ----------
export const orderAPI = {
  getUserOrders: () => http('GET', '/api/invoice', null, true),
  createOrder: (items) => http('POST', '/api/invoice', { items }, true),
};

// -------- Users ----------
export const userAPI = {
  login: ({ username, password }) => http('POST', '/api/user/login', { username, password }),
};

// -------- Admin: Products ----------
export const adminProductAPI = {
  create: (payload) => http('POST', `/api/panel/product/add`, payload, true),
  delete: (id) => http('POST', `/api/panel/product/delete/${id}`, null, true),
};

// -------- Cart (localStorage) --------
function cartRead() {
  try { return JSON.parse(localStorage.getItem('cart') || '[]'); } catch { return []; }
}
function cartWrite(items) { localStorage.setItem('cart', JSON.stringify(items)); }
function cartAdd(product, qty = 1) {
  const cart = cartRead();
  const i = cart.findIndex(x => x.id === product.id);
  if (i >= 0) cart[i].quantity += qty;
  else cart.push({ id: product.id, title: product.title, price: product.price, quantity: qty });
  cartWrite(cart); return cart;
}
function cartRemove(id) {
  const cart = cartRead().filter(x => x.id !== id);
  cartWrite(cart); return cart;
}
function cartUpdate(id, qty) {
  const cart = cartRead();
  const i = cart.findIndex(x => x.id === id);
  if (i >= 0) { cart[i].quantity = Math.max(1, qty); cartWrite(cart); }
  return cart;
}
function cartCount() {
  return cartRead().reduce((s, it) => s + (Number(it.quantity) || 0), 0);
}
export function updateCartBadge() {
  const n = String(cartCount());
  const els = [document.getElementById('cart-count'), ...document.querySelectorAll('.cart-count')];
  els.forEach(el => { if (el) el.textContent = n; });
}

export const cartAPI = {
  get: cartRead, set: cartWrite, add: cartAdd, remove: cartRemove, update: cartUpdate, count: cartCount,
  getCart: cartRead, setCart: cartWrite, addItem: cartAdd, removeItem: cartRemove, updateQty: cartUpdate,
};
