SELECT * FROM cms4.user;
insert into user (username, password)
values ("kai","123456"),
("sena","123456");

insert into role (id, name)
values (1, "ROLE_ADMIN"),
(2, "ROLE_USER");

insert into users_roles (user_id, role_id)