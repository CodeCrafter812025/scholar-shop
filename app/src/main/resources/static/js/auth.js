import { userAPI } from './api.js';

// مدیریت فرم ورود
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // در این پروژه فیلد loginEmail همان نام کاربری است
    const username = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    try {
        const response = await userAPI.login({ username, password });

        // اگر بک‌اند وضعیت خطا برگرداند
        if (!response || response.status === 'Error' || !response.token) {
            alert('نام کاربری یا رمز عبور اشتباه است.');
            return;
        }

        // ذخیرهٔ توکن در LocalStorage
        localStorage.setItem('token', response.token);

        // اگر نام کاربری admin بود، به پنل ادمین منتقل شو
        if ((response.username || username).toLowerCase() === 'admin') {
            window.location.href = '/admin.html';
        } else {
            window.location.href = '/';
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('خطا در ورود: ' + error.message);
    }
});

// مدیریت فرم ثبت‌نام
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('registerName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    try {
        const response = await userAPI.register({ name, email, password });
        alert('ثبت‌نام با موفقیت انجام شد!');
        // هدایت به صفحه ورود
        document.getElementById('register-tab').classList.remove('active');
        document.getElementById('login-tab').classList.add('active');
        document.getElementById('register').classList.remove('show', 'active');
        document.getElementById('login').classList.add('show', 'active');
    } catch (error) {
        alert('خطا در ثبت‌نام: ' + error.message);
    }
});