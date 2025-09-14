-- insert into customer (id, address, firstname, lastname, postal_code, tel)
-- values (23, 'tehran', 'Hossein', 'Badrnezhad', '1234567890', '02112345678');
insert into user (id, email, enable, mobile, password, register_date, username, customer_id)
values (24, 'hossein@gmail.com', 1, '09121234567', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', now(), 'admin', 22);

--select * from customer;

select * from `user`;
