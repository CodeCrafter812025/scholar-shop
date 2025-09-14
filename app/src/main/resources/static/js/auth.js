import { userAPI } from './api.js';

// مدیریت فرم ورود
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('loginUsername')?.value.trim();
    const password = document.getElementById('loginPassword')?.value.trim();

    if (!username || !password) {
        alert('لطفاً همهٔ فیلدها را پر کنید.');
        return;
    }

    try {
        const response = await userAPI.login({ username, password });

        if (!response || response.status === 'Error' || !response.data) {
            alert(response?.message || 'نام کاربری یا رمز عبور اشتباه است.');
            return;
        }

        const token = response.data.token || response.token;
        localStorage.setItem('token', token);

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

// مدیریت فرم ثبت‌نام (غیرفعال)
document.getElementById('registerForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    alert('امکان ثبت‌نام از این طریق وجود ندارد. لطفاً با مدیر سیستم تماس بگیرید.');
});
