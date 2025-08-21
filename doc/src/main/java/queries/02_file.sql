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

UPDATE file SET content_type = 'image/webp' WHERE extension = 'webp' OR name LIKE '%.webp';
