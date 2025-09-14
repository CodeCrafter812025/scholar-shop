// آدرس پایه API
const API_URL = '/api';

// تابع عمومی برای درخواست‌های GET
async function fetchData(endpoint) {
    const response = await fetch(`${API_URL}/${endpoint}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
}

// توابع مربوط به محصولات (مثال ساده، بسته به پیاده‌سازی خود تغییر دهید)
const productAPI = {
    getAllProducts: async (page = 0, size = 20) => {
        const res = await fetchData(`product/search?page=${page}&size=${size}`);
        const items = res.data?.content || res.content || res;
        return Array.isArray(items) ? items : [];
    },
    getProductById: async (id) => {
        const res = await fetchData(`product/${id}`);
        return res.data || res;
    },
    createProduct: async (productData, token) => {
        const response = await fetch(`${API_URL}/product`, {
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
    deleteProduct: async (productId, token) => {
        const response = await fetch(`${API_URL}/product/${productId}`, {
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
    // ثبت‌نام عمومی وجود ندارد؛ در صورت نیاز می‌توانید این متد را به API مدیریتی متصل کنید
    register: async (/* userData */) => {
        throw new Error('Registration via public API is not supported.');
    },
    // مسیر صحیح ورود
    login: async (credentials) => {
        const response = await fetch(`${API_URL}/user/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
        return await response.json();
    },
    // دریافت همه کاربران برای مدیر
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

// توابع سبد خرید (همانند قبل)
const cartAPI = {
    getCart: () => JSON.parse(localStorage.getItem('cart')) || [],
    addToCart: (product) => {
        const cart = cartAPI.getCart();
        const productId = product.id || product._id;
        const existingItem = cart.find(item => item.id === productId || item._id === productId);
        if (existingItem) {
            existingItem.quantity += 1;
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
        const cart = cartAPI.getCart().filter(item =>
            (item.id !== productId) && (item._id !== productId)
        );
        localStorage.setItem('cart', JSON.stringify(cart));
        return cart;
    }
};

// توابع مربوط به سفارش‌ها (همانند قبل)
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

// خروجی گرفتن برای استفاده در فایل‌های دیگر
export { API_URL, productAPI, userAPI, cartAPI, orderAPI };
