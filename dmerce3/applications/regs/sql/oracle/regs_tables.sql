--drop table t_blproducts;
create table t_blproducts (
     id number primary key,
     code varchar2(10) not null,
     name varchar2(100) not null
);
create sequence t_blproducts_seq start with 1 increment by 1 nomaxvalue nocycle nocache order;

--drop table t_blpreg;
create table t_blpreg (
     id				number primary key,
     partnerid		number not null,
     productid		number not null,
     company		varchar2(100),
     lastname		varchar2(30) not null,
     firstname		varchar2(20),
     street			varchar2(100),
     zipcode		char(5),
     city			varchar2(20),
     country		varchar2(20),
     phone			varchar2(20),
     fax				varchar2(20),
     email			varchar2(50) not null,
     remarks		varchar2(500),
     tacread		char(1)
);
create sequence t_dmerceregs_seq start with 1 increment by 1 nomaxvalue nocycle nocache order;

--drop table t_dmercereg;
create table t_dmercereg (
     id				number primary key,
     partnerid		number not null,
     company		varchar2(100),
     companytype	varchar2(4),
     lastname		varchar2(30) not null,
     firstname		varchar2(20),
     street			varchar2(100),
     zipcode		char(5),
     city			varchar2(20),
     country		varchar2(20),
     phone			varchar2(20),
     fax				varchar2(20),
     email			varchar2(50) not null,
     remarks		varchar2(500),
     licread		char(1)
);
create sequence t_dmercereg_seq start with 1 increment by 1 nomaxvalue nocycle nocache order;
