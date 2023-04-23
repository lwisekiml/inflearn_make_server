# 11강. MySQL에서 테이블 만들기
create database library;

show databases;

# drop database library;

use library;

show tables;

create table fruit
(
    id           bigint auto_increment,
    name         varchar(20),
    price        int,
    stocked_date date,
    primary key (id)
);

show tables;

# drop table fruit;

# 12강. 테이블의 데이터를 조작하기

insert into fruit (name, price, stocked_date) values ('사과', 1000, '2023-01-01');

select * from fruit;

select name, price from fruit;

select * from fruit where name = '사과';
select * from fruit where name = '바나나' or price = 1000;
select * from fruit where price between  1000 and 2000; # 1000 <= price <= 2000
select * from fruit where name in ('사과', '수박');
select * from fruit where name not in ('사과', '수박');

update fruit set price = 1500 where name = '사과';
select * from fruit;

delete from fruit where name = '사과'
select * from fruit;

