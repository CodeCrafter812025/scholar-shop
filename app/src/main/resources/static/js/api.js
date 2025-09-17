// static/js/api.js
const BASE = '';
const JSONH = { 'Content-Type': 'application/json;charset=utf-8' };

export const token = {
  get: () => localStorage.getItem('token') || '',
  set: (t) => localStorage.setItem('token', t || ''),
  clear: () => localStorage.removeItem('token'),
};

export const ui = {
  toast(msg) {
    try {
      const el = document.getElementById('liveToast');
      if (!el) { alert(msg); return; }
      el.querySelector('.toast-body').textContent = msg;
      new bootstrap.Toast(el).show();
    } catch { alert(msg); }
  },
  updateCartBadge() {
    const count = cart.count();
    const sels = [
      'a[href="/cart.html"] .badge',
      'a[href="cart.html"] .badge',
      '.navbar .cart-icon + .badge',
      '.cart-icon ~ .badge'
    ];
    const seen = new Set();
    for (const s of sels) {
      document.querySelectorAll(s).forEach(el => {
        if (!seen.has(el)) { el.textContent = String(count); seen.add(el); }
      });
    }
  }
};

async function http(path, { method='GET', body, headers={} } = {}) {
  const t = token.get();
  const res = await fetch(`${BASE}${path}`, {
    method,
    headers: {
      ...(body ? JSONH : {}),
      ...(t ? { Authorization: `Bearer ${t}` } : {}),
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) {
    const txt = await res.text().catch(()=> '');
    const err = new Error(`HTTP ${res.status}`);
    err.status = res.status;
    err.body = txt;
    throw err;
  }
  const ct = res.headers.get('content-type') || '';
  return ct.includes('application/json') ? res.json() : res.text();
}

/* ------------------ Public APIs ------------------ */
export const productAPI = {
  async getAllProducts(page=0, size=30, q) {
    const r = await http(`/api/product/search?page=${page}&size=${size}${q ? `&q=${encodeURIComponent(q)}` : ''}`);
    const pageObj = r?.data ?? r;
    const arr = Array.isArray(pageObj) ? pageObj : (pageObj?.content ?? []);
    return arr;
  },
  async getProductById(id) {
    const r = await http(`/api/product/${id}`);
    return r?.data ?? r;
  },
};

export const userAPI = {
  async login(username, password) {
    const tryOne = async (cands) => {
      let last;
      for (const c of cands) {
        try { return await http(c.url, c); }
        catch (e) { last = e; if (e.status === 401) throw e; }
      }
      throw last || new Error('login failed');
    };
    const r = await tryOne([
      { url:'/api/user/login', method:'POST', body:{ username, password } },
      { url:'/api/users/login', method:'POST', body:{ username, password } },
      { url:'/api/login',      method:'POST', body:{ username, password } },
    ]);
    const tok = r?.data?.token || r?.token;
    if (!tok) throw new Error('No token returned');
    token.set(tok);
    return r;
  }
};

/* سبد خرید روی LocalStorage */
export const cart = {
  key: 'cart',
  read() { try { return JSON.parse(localStorage.getItem(this.key)||'[]'); } catch { return []; } },
  write(v){ localStorage.setItem(this.key, JSON.stringify(v)); },
  add(p){ const a=this.read(); const i=a.find(x=>x.productId===p.productId); if(i) i.quantity+=p.quantity||1; else a.push({...p,quantity:p.quantity||1}); this.write(a); ui.updateCartBadge(); },
  setQty(id,q){ const all=this.read(); const i=all.find(x=>x.productId===id); const rest=all.filter(x=>x.productId!==id); if(q>0 && i){ this.write([...rest,{...i,quantity:q}]); } else { this.write(rest); } ui.updateCartBadge(); },
  remove(id){ this.write(this.read().filter(x=>x.productId!==id)); ui.updateCartBadge(); },
  clear(){ this.write([]); ui.updateCartBadge(); },
  count(){ return this.read().reduce((s,x)=>s+(x.quantity||0),0); },
  total(){ return this.read().reduce((s,x)=>s+(x.quantity||0)*(x.price||0),0); },
};

export const orderAPI = {
  async createOrder(items) {
    return http('/api/invoice', { method:'POST', body:{ items } });
  },
  async getUserOrders() {
    const r = await http('/api/invoice', { method:'GET' });
    return r?.data ?? [];
  }
};

/* ------------------ Admin ------------------ */
export const panelProductAPI = {
  async list() { return productAPI.getAllProducts(0, 100); },

  async create({ title, price, categoryId, imageId, description }) {
    // اعتبارسنجی سمت کلاینت برای جلوگیری از خطای DB
    if (!title || price == null || isNaN(price)) throw new Error('title/price required');
    if (!imageId) throw new Error('imageId is required (Product.image nullable=false)');
    if (!description) throw new Error('description is required (not null)');

    const nested = {
      title, price: Number(price),
      description,
      enable: true, exist: true,
      category: categoryId ? { id: Number(categoryId) } : null,
      image:    { id: Number(imageId) }  // اجباری
    };

    const tryOne = async (cands) => {
      let last;
      for (const c of cands) {
        try { return await http(c.url, c); } catch (e) { last = e; }
      }
      throw last || new Error('create failed');
    };

    return tryOne([
      { url:'/api/panel/product/add', method:'POST', body:nested },
      { url:'/api/panel/product',     method:'PUT',  body:nested },
      { url:'/api/panel/product',     method:'POST', body:nested },
    ]);
  },

    async remove(id) {
      id = Number(id);

      // اول حذف نرم صریح
      try {
        return await http(`/api/panel/product/disable/${id}`, { method: 'PATCH' });
      } catch (_) { /* fallback */ }

      // سپس حذف سخت با چند مسیر رایج + fallback
      const tries = [
        { url: `/api/panel/product/${id}`, method: 'DELETE' },
        { url: `/api/panel/product/delete/${id}`, method: 'POST' },
        { url: `/api/panel/product/delete?id=${id}`, method: 'POST' },
        { url: '/api/panel/product/delete', method: 'POST', body: { id } },
      ];
      let last;
      for (const t of tries) {
        try { return await http(t.url, t); } catch (e) { last = e; }
      }
      throw last || new Error('delete failed');
    },
};

export const panelReportAPI = {
  async listInvoicesAll() {
    try {
      const r = await http('/api/invoice', { method:'GET' });
      return r?.data ?? [];
    } catch {
      return [];
    }
  }
};
