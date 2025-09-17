// static/js/auth.js
import { userAPI, ui } from './api.js';

document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  // توجه: اینجا با idهای واقعی فرم در auth.html می‌خوانیم
  const username = document.getElementById('loginUsername')?.value?.trim();
  const password = document.getElementById('loginPassword')?.value?.trim();
  if (!username || !password) { alert('لطفاً همهٔ فیلدها را پر کنید.'); return; }

  try {
    await userAPI.login(username, password); // /api/user/login و ذخیرهٔ توکن در localStorage
    ui.toast('ورود موفق!');
    if (username.toLowerCase() === 'admin') location.href = '/admin.html';
    else location.href = '/';
  } catch (err) {
    console.error('Login error:', err);
    alert('ورود ناموفق بود.');
  }
});

// ثبت‌نام غیرفعال
document.getElementById('registerForm')?.addEventListener('submit', (e) => {
  e.preventDefault();
  alert('امکان ثبت‌نام از این طریق وجود ندارد. لطفاً با مدیر سیستم تماس بگیرید.');
});
