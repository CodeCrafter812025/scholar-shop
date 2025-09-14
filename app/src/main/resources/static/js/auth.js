import { userAPI } from './api.js';

// مدیریت فرم ورود
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // در فرم فعلی، فیلد نام کاربری با id="loginEmail" تعریف شده است
    const username = document.getElementById('loginEmail')?.value.trim();
    const password = document.getElementById('loginPassword')?.value.trim();

    if (!username || !password) {
        alert('لطفاً همهٔ فیلدها را پر کنید.');
        return;
    }

    try {
        const response = await userAPI.login({ username, password });

        // پاسخ سرور؛ در صورت خطا
        if (!response || response.status === 'Error' || !response.data) {
            alert(response?.message || 'نام کاربری یا رمز عبور اشتباه است.');
            return;
        }

        // ذخیره توکن دریافتی در localStorage
        const token = response.data.token || response.token;
        localStorage.setItem('token', token);

        // هدایت ادمین به پنل مدیریت
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

// مدیریت فرم ثبت‌نام (در حال حاضر غیرقابل استفاده است)
document.getElementById('registerForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    alert('امکان ثبت‌نام از این طریق وجود ندارد. لطفاً با مدیر سیستم تماس بگیرید.');
});
