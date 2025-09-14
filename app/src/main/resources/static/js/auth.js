import { userAPI } from './api.js';

// مدیریت فرم ورود
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // گرفتن نام کاربری و رمز عبور از فرم
    const username = document.getElementById('loginUsername')?.value.trim();
    const password = document.getElementById('loginPassword')?.value.trim();

    if (!username || !password) {
        alert('لطفاً همهٔ فیلدها را پر کنید.');
        return;
    }

    try {
        // ارسال درخواست ورود
        const response = await userAPI.login({ username, password });

        // بررسی پاسخ سرور
        if (!response || response.status === 'Error' || !response.data) {
            alert(response?.message || 'نام کاربری یا رمز عبور اشتباه است.');
            return;
        }

        // ذخیره توکن در localStorage
        const token = response.data.token || response.token;
        localStorage.setItem('token', token);

        // هدایت بر اساس نام کاربری
        const loggedInUsername = response.data.username || username;
        if (loggedInUsername.toLowerCase() === 'admin') {
            window.location.href = '/admin.html';
        } else {
            window.location.href = '/';
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('خطا در ورود: ' + error.message);
    }
});

// مدیریت فرم ثبت‌نام (در بک‌اند پیاده‌سازی نشده است)
document.getElementById('registerForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    alert('امکان ثبت‌نام از این طریق وجود ندارد. لطفاً با مدیر سیستم تماس بگیرید.');
});
