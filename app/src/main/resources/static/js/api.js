// آدرس پایه API
const API_URL = '/api';

// تابع عمومی برای درخواست‌های GET
async function fetchData(endpoint) {
    const response = await fetch(`${API_URL}/${endpoint}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
}

// توابع مربوط به محصولات (مثال ساده)
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
    // متد ورود (login) – از مسیر صحیح استفاده می‌کند
    login: async (credentials) => {
        const response = await fetch(`${API_URL}/user/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
        return await response.json();
    },
    // متد دریافت همه کاربران برای مدیر
    getAllUsers: async (token, page = 0, size = 50) => {
        const response = await fetch(`${API_URL}/panel/user?page=${page}&size=${size}`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        const data = await response.json();
        return data.data || data;
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

// توابع مربوط به فاکتورها (سفارش‌ها)
const orderAPI = {
    // دریافت فاکتورهای یک کاربر خاص
    getOrdersByUser: async (userId, token) => {
        const response = await fetch(`${API_URL}/panel/invoice/user/${userId}`, {
            headers: {
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch orders for user');
        }
        return await response.json();
    }
};

export { API_URL, productAPI, userAPI, cartAPI, orderAPI };
