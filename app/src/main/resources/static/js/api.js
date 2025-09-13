// آدرس پایه API
const API_URL = '/api';

// تابع عمومی برای درخواست‌های GET
async function fetchData(endpoint) {
    const response = await fetch(`${API_URL}/${endpoint}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
}

// توابع مربوط به محصولات
const productAPI = {
    // دریافت همه محصولات از endpoint search و استخراج آرایه‌ی محصولات
    getAllProducts: async (page = 0, size = 20) => {
        const res = await fetchData(`product/search?page=${page}&size=${size}`);
        // اگر پاسخ به شکل { data: { content: [...] } } یا { content: [...] } باشد
        const items = res.data?.content || res.content || res;
        return Array.isArray(items) ? items : [];
    },

    // دریافت محصول بر اساس شناسه
    getProductById: async (id) => {
        const res = await fetchData(`product/${id}`);
        // در پاسخ‌های API معمولا داده در فیلد data قرار می‌گیرد
        return res.data || res;
    },

    // ایجاد محصول جدید (ممکن است مسیر back-end شما متفاوت باشد)
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

    // حذف محصول
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
    // خواندن سبد از localStorage
    getCart: () => JSON.parse(localStorage.getItem('cart')) || [],

    // افزودن محصول به سبد
    addToCart: (product) => {
        const cart = cartAPI.getCart();
        const productId = product.id || product._id;
        let existingItem = cart.find(item =>
            (item.id === productId) || (item._id === productId)
        );
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            // ذخیره‌ی id و title برای نمایش در سبد
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

    // حذف کامل یک محصول از سبد
    removeFromCart: (productId) => {
        const cart = cartAPI.getCart().filter(item =>
            (item.id !== productId) && (item._id !== productId)
        );
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

// خروجی گرفتن برای ماژول‌های دیگر
export { API_URL, productAPI, userAPI, cartAPI, orderAPI };
