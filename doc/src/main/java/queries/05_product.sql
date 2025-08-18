insert into product (id,
                     add_date,
                     description,
                     enable,
                     exist,
                     price,
                     title,
                     visit_count,
                     image_id,
                     category_id) values
(66, '2020-09-20 10:13:19', '', TRUE, TRUE, 1400000, 'Mens 624', 116, 5, 1),
       (115, '2020-09-20 11:30:48', '', TRUE, TRUE, 1850000, 'Minimus 20v7', 41, 6, 1),
       (86, '2020-09-20 10:20:02', '', TRUE, TRUE, 1100000, 'Minimus Prevail', 23, 7, 1),
       (76, '2020-09-20 10:16:57', '', TRUE, TRUE, 1600000, 'Mens New Balance 857v2 Suede', 19, 8, 1),
       (71, '2020-09-20 10:15:23', '', TRUE, TRUE, 2000000, 'Mens New Balance 857v2', 12, 9, 1),
       (61, '2020-09-20 09:34:06', '', TRUE, TRUE, 58000, 'T-Shirt X', 11, 10, 2)
;

select * from product;