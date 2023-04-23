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

