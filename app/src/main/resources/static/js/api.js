// app/src/main/resources/static/js/api.js

const API_URL = '/api';

// تابع عمومی GET
async function fetchData(endpoint) {
    const response = await fetch(`${API_URL}/${endpoint}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
}

// ---------- Product API ----------
const productAPI = {
    // محصولات عمومی (صفحه اصلی و محصولات)
    getAllProducts: async (page = 0, size = 20) => {
        const res = await fetchData(`product/search?page=${page}&size=${size}`);
        const items = res.data?.content || res.content || res;
        return Array.isArray(items) ? items : [];
    },
    // محصولات پنل ادمین
    getAllPanelProducts: async (page = 0, size = 50, token) => {
        const response = await fetch(`${API_URL}/panel/product?page=${page}&size=${size}`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch products');
        const data = await response.json();
        return data.data || data;
    },
    // ایجاد محصول
    createProduct: async (productData, token) => {
        const response = await fetch(`${API_URL}/panel/product/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify(productData)
        });
        if (!response.ok) throw new Error('Failed to create product');
        return await response.json();
    },
    // حذف محصول
    deleteProduct: async (productId, token) => {
        const response = await fetch(`${API_URL}/panel/product/${productId}`, {
            method: 'DELETE',
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to delete product');
        return await response.json();
    }
};

// ---------- User API ----------
const userAPI = {
    login: async (credentials) => {
        const response = await fetch(`${API_URL}/user/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
        return await response.json();
    },
    getAllUsers: async (token, page = 0, size = 50) => {
        const response = await fetch(`${API_URL}/panel/user?page=${page}&size=${size}`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch users');
        const data = await response.json();
        return data.data || data;
    }
};

// ---------- Cart API ----------
const cartAPI = {
    getCart: () => JSON.parse(localStorage.getItem('cart')) || [],
    addToCart: (product) => {
        const cart = cartAPI.getCart();
        const productId = product.id || product._id;
        const existing = cart.find(item => item.id === productId || item._id === productId);
        if (existing) {
            existing.quantity += 1;
        } else {
            cart.push({
                id: productId,
                title: product.title || product.name,
                image: product.image,
                price: product.price,
                quantity: 1
            });
        }
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    },
    removeFromCart: (productId) => {
        const cart = cartAPI.getCart().filter(item => item.id !== productId && item._id !== productId);
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    }
};

// ---------- Order API ----------
const orderAPI = {
    // دریافت سفارش‌های همه کاربران (برای پنل)
    getOrdersByUser: async (userId, token) => {
        const response = await fetch(`${API_URL}/panel/invoice/user/${userId}`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch orders for user');
        return await response.json();
    },
    // ایجاد سفارش توسط کاربر
    createOrder: async (orderData, token) => {
        const response = await fetch(`${API_URL}/invoice`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify(orderData)
        });
        if (!response.ok) throw new Error('Failed to create order');
        return await response.json();
    },
    // دریافت سفارش‌های کاربر جاری (برای orders.js)
    getUserOrders: async (token) => {
        const response = await fetch(`${API_URL}/invoice`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch user orders');
        const res = await response.json();
        return res.data || res;
    }
};

export { API_URL, productAPI, userAPI, cartAPI, orderAPI };
