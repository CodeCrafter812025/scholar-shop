--insert into file (id, name, path) values
--(1, 'slider3.jpg', 'slider3.jpg'),
--(2, 'slider2.jpg', 'slider2.jpg'),
--(3, 'shoes_cat.png', 'shoes_cat.png'),
--(4, 'Blue_Tshirt.png', 'Blue_Tshirt.png'),
--(5, 'new-balance-1.webp', 'new-balance-1.webp'),
--(6, 'NEW-BALANCE-7.webp', 'NEW-BALANCE-7.webp'),
--(7, 'New-Balance-6.webp', 'New-Balance-6.webp'),
--(8, 'New-Balance-4.webp', 'New-Balance-4.webp'),
--(9, 'new-balance-3.webp','new-balance-3.webp'),
--(10, 'white-t-shirt.jpg', 'white-t-shirt.jpg'),
--(11, '1_Djv53DbJjRwELNN0z1Fl3Q.jpeg', '1_Djv53DbJjRwELNN0z1Fl3Q.jpeg'),
--(12, '1_qWiyMxttCwdDYm7YsVMiQQ.jpeg', '1_qWiyMxttCwdDYm7YsVMiQQ.jpeg'),
--(13, '1_QO7ucc8NsW2FeS_eHaCjAA.jpeg', '1_QO7ucc8NsW2FeS_eHaCjAA.jpeg')
--;
--
select * from file;

--UPDATE file SET content_type = 'image/webp' WHERE extension = 'webp' OR name LIKE '%.webp';

-- همه‌ی مسیرهایی که هنوز با 'images/' شروع نمی‌شوند، به‌روزرسانی کن
--UPDATE file
--SET path = CONCAT('images/', path)
--WHERE path NOT LIKE 'images/%';

-- ردیف 5: new-balance-1.webp -> jacket1.webp
UPDATE file
SET name = 'jacket1.webp',
    path = 'images/jacket1.webp'
WHERE name = 'new-balance-1.webp';

-- ردیف 6: NEW-BALANCE-7.webp -> jeans1.webp
UPDATE file
SET name = 'jeans1.webp',
    path = 'images/jeans1.webp'
WHERE name = 'NEW-BALANCE-7.webp';

-- ردیف 7: New-Balance-6.webp -> polo1.webp
UPDATE file
SET name = 'polo1.webp',
    path = 'images/polo1.webp'
WHERE name = 'New-Balance-6.webp';

-- ردیف 8: New-Balance-4.webp -> shirt1.webp
UPDATE file
SET name = 'shirt1.webp',
    path = 'images/shirt1.webp'
WHERE name = 'New-Balance-4.webp';

-- ردیف 9: new-balance-3.webp -> shorts1.webp
UPDATE file
SET name = 'shorts1.webp',
    path = 'images/shorts1.webp'
WHERE name = 'new-balance-3.webp';

-- ردیف 10: white-t-shirt.jpg -> tshirt1.webp
UPDATE file
SET name = 'tshirt1.webp',
    path = 'images/tshirt1.webp'
WHERE name = 'white-t-shirt.jpg';


