-- insert into customer (id, address, firstname, lastname, postal_code, tel)
-- values (23, 'tehran', 'Hossein', 'Badrnezhad', '1234567890', '02112345678');
insert into user (id, email, enable, mobile, password, register_date, username, customer_id)
values (24, 'hossein@gmail.com', 1, '09121234567', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', now(), 'admin', 22);

--select * from customer;

select * from `user`;

select p.name
from user u
join user_role ur on ur.user_id = u.id
join role r on r.id = ur.role_id
join role_permission rp on rp.role_id = r.id
join permission p on p.id = rp.permission_id
where u.username = 'admin';

