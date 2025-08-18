insert into permission (id, description, name, parent_id)values
 (1, 'دسترسی‌ها', 'root', null),
 (2, 'مدیریت لینک‌ها', 'nav_management', 1),
 (3, 'مدیریت محتوا', 'content_management', 1),
 (4, 'مدیریت اسلایدر', 'slider_management', 1),
 (5, 'مدیریت بلاگ', 'blog_management', 1),
 (6, 'مدیریت محصولات', 'product_management', 1),
 (7, 'مدیریت کاربران', 'user_management', 1),
 (8, 'مدیریت مشتریان', 'customer_management', 1),
 (9, 'مدیریت فایل', 'file_management', 1),
 (10, 'مدیریت سفارشات', 'invoice_management', 10),
 (11, 'داشبورد کاربر', 'user_dashboard', 1),

 (200, 'ایجاد لینک جدید', 'add_nav', 2),
 (201, 'ویرایش لینک', 'edit_nav', 2),
 (202, 'حذف لینک', 'delete_nav', 2),
 (203, 'لیست لینک‌ها', 'list_nav', 2),

 (300, 'ایجاد محتوا جدید', 'add_content', 3),
 (301, 'ویرایش محتوا', 'edit_content', 3),
 (302, 'حذف محتوا', 'delete_content', 3),
 (303, 'لیست محتوا', 'list_content', 3),

 (400, 'ایجاد اسلایدر جدید', 'add_slider', 4),
 (401, 'ویرایش اسلایدر', 'edit_slider', 4),
 (402, 'حذف اسلایدر', 'delete_slider', 4),
 (403, 'لیست اسلایدر', 'list_slider', 4),

 (500, 'ایجاد بلاگ جدید', 'add_blog', 5),
 (501, 'ویرایش بلاگ', 'edit_blog', 5),
 (502, 'حذف بلاگ', 'delete_blog', 5),
 (503, 'لیست بلاگ', 'list_blog', 5),

 (600, 'ایجاد محصول جدید', 'add_product', 6),
 (601, 'ویرایش محصول', 'edit_product', 6),
 (602, 'حذف محصول', 'delete_product', 6),
 (603, 'لیست محصولات', 'list_product', 6),
 (610, 'ایجاد رنگ جدید', 'add_color', 6),
 (611, 'ویرایش رنگ', 'edit_color', 6),
 (612, 'لیست رنگ‌ها', 'list_color', 6),
 (620, 'ایجاد سایز جدید', 'add_size', 6),
 (621, 'ویرایش سایز', 'edit_size', 6),
 (622, 'لیست سایزها', 'list_size', 6),
 (630, 'ایجاد دسته‌بندی محصول جدید', 'add_product_category', 6),
 (631, 'ویرایش دسته‌بندی محصول', 'edit_product_category', 6),
 (632, 'لیست دسته‌بندی محصولات', 'list_product_category', 6),

 (700, 'ایجاد کاربر جدید', 'add_user', 7),
 (701, 'ویرایش کاربر', 'edit_user', 7),
 (702, 'حذف کاربر', 'delete_user', 7),
 (703, 'لیست کاربران', 'list_user', 7),

 (800, 'ایجاد مشتری جدید', 'add_customer', 8),
 (801, 'ویرایش مشتری', 'edit_customer', 8),
 (802, 'حذف مشتری', 'delete_customer', 8),
 (803, 'لیست مشتریان', 'list_customer', 8),

 (900, 'آپلود فایل جدید', 'add_file', 9),
 (901, 'حذف فایل', 'delete_file', 9),
 (902, 'لیست فایل‌ها', 'list_file', 9),

 (1000, 'لیست سفارشات', 'list_invoice', 10),
 (1001, 'مشاهده سفارش', 'info_invoice', 10),
 (1100, 'لیست فاکتورها', 'list_my_invoice', 10),
 (1101, 'مشاهده فاکتور', 'info_my_invoice', 10),
 (1102, 'ویرایش اطلاعات کاربری', 'edit_my_user', 10);

select * from permission;

insert into permission (id, description, name, parent_id) values
(704, 'تغییر رمز عبور کاربر توسط ادمین', 'change_password_by_admin', 7);
