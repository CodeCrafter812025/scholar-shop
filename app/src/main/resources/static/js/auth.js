import { userAPI } from './api.js';

// مدیریت فرم ورود
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await userAPI.login({ email, password });
        alert('ورود با موفقیت انجام شد!');
        // ذخیره توکن در LocalStorage
        localStorage.setItem('token', response.token);
        // هدایت به صفحه اصلی
        window.location.href = '/';
    } catch (error) {
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