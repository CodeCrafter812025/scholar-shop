// آدرس پایه API
const API_URL = '/api';

// تابع عمومی برای درخواست‌های GET
async function fetchData(endpoint) {
    try {
        const response = await fetch(`${API_URL}/${endpoint}`);
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Error fetching data:', error);
        return [];
    }
}

// توابع مربوط به محصولات
const productAPI = {
    getAllProducts: () => fetchData('products'),
    getProductById: (id) => fetchData(`products/${id}`),

    // متد ایجاد محصول جدید
    createProduct: async (productData, token) => {
        const response = await fetch(`${API_URL}/products`, {
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

    // متد حذف محصول
    deleteProduct: async (productId, token) => {
        const response = await fetch(`${API_URL}/products/${productId}`, {
            method: 'DELETE',
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to delete product');
        return await response.json();
    }
};

// توابع مربوط به کاربران
const userAPI = {
    register: async (userData) => {
        const response = await fetch(`${API_URL}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        return await response.json();
    },

    login: async (credentials) => {
        const response = await fetch(`${API_URL}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
        return await response.json();
    },

    // دریافت فهرست تمام کاربران (برای مدیر)
    getAllUsers: async (token) => {
        const response = await fetch(`${API_URL}/users`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch users');
        return await response.json();
    }
};

// توابع سبد خرید
const cartAPI = {
    getCart: () => JSON.parse(localStorage.getItem('cart')) || [],
    addToCart: (product) => {
        const cart = cartAPI.getCart();
        const existingItem = cart.find(item => item._id === product._id);
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push({ ...product, quantity: 1 });
        }
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    },
    removeFromCart: (productId) => {
        const cart = cartAPI.getCart().filter(item => item._id !== productId);
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    }
};

// توابع مربوط به سفارش‌ها
const orderAPI = {
    getUserOrders: async (token) => {
        const response = await fetch(`${API_URL}/orders`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return await response.json();
    },

    createOrder: async (orderData, token) => {
        const response = await fetch(`${API_URL}/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(orderData)
        });
        return await response.json();
    },

    // دریافت تمام سفارش‌ها (برای مدیر)
    getAllOrders: async (token) => {
        const response = await fetch(`${API_URL}/orders/all`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) throw new Error('Failed to fetch orders');
        return await response.json();
    }
};

// Export توابع برای استفاده در ماژول‌های دیگر
export { API_URL, productAPI, userAPI, cartAPI, orderAPI };
