--liquibase formatted sql

--changeset master:data

insert into item (id, name, url, selector, break_selector, created_at, deleted_at)
values (1, 'name 1', 'url 1', 'selector 1', 'break selector 1', '2000-01-01 10:10:10', null),
       (2, 'name 2', 'url 2', 'selector 2', 'break selector 2', '2000-01-01 10:10:11', null),
       (3, 'name 3', 'url 3', 'selector 3', 'break selector 3', '2000-01-01 10:10:12', null),
       (4, 'name 4', 'url 4', 'selector 4', 'break selector 4', '2000-01-01 10:10:13', null),
       (5, 'name 5', 'url 5', 'selector 5', 'break selector 5', '2000-01-01 10:10:14', '2000-01-01 10:10:14');

insert into item_detail (id, item_id, value, created_at)
values (1, 1, '123.5', '2000-01-01 10:10:10'),
       (2, 1, '123.3', '2000-01-01 10:10:11'),
       (3, 2, '123.5', '2000-01-01 10:10:12'),
       (4, 2, '123.6', '2000-01-01 10:10:13'),
       (5, 2, '123.4', '2000-01-01 10:10:14'),
       (6, 3, '124.4', '2000-01-01 10:10:15'),
       (7, 3, '125.4', '2000-01-01 10:10:16'),
       (8, 3, '126.4', '2000-01-01 10:10:17'),
       (9, 4, '100', '2000-01-01 10:10:17'),
       (10, 4, '101', '2000-01-02 10:10:17'),
       (11, 4, '102', '2000-01-03 10:10:17'),
       (12, 4, '103', '2000-01-04 10:10:17'),
       (13, 4, '103', '2000-01-05 10:10:17'),
       (14, 4, '102', '2000-01-06 10:10:17'),
       (15, 4, '102', '2000-01-07 10:10:17'),
       (16, 4, '101', '2000-01-08 10:10:17'),
       (17, 4, '101', '2000-01-09 10:10:17');

-- rollback