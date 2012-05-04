create table t_adverts (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        advertcategoriesid        number(1) not null,
        validuntil                date default sysdate + 14,
        realname                  varchar2(50) not null,
        email                     varchar2(50) not null,
        url                       varchar2(50) null,
        description               varchar2(50),
        text                      varchar2(250) not null,
        foto_original             varchar2(100),
        foto_server               varchar2(100)
);

create table t_advertcategories (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        name                      varchar2(50) not null,
        description               varchar2(250) not null,
        position                  number
);

/*
create table t_advertpics (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        advertsid                 number not null,
        pic_original              varchar2(50) not null,
        pic_server                varchar2(50) not null
);
*/

create table t_appointments (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        dateofappointment         date not null,
        realname                  varchar2(100) not null,
        description               varchar2(250) not null,
        url                       varchar2(50),
        email                     varchar2(50) not null,
        phone                     varchar2(50)
);

create table t_guestbook (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        realname                  varchar2(100) not null,
        email                     varchar2(50) not null,
        url                       varchar2(100),
        text                      varchar2(1500) not null
);

create table t_links (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        linkcategoriesid          number not null,
        new                       number(1) default 0 not null,
        realname                  varchar2(100),
        email                     varchar2(50),
        name                      varchar2(50) not null,
        description               varchar2(250) not null,
        url                       varchar2(50) not null,
        hits                      number
);

create table t_linkcategories (
        id                        number not null,
        active                    number(1) default 1 not null,
        createddatetime           date default sysdate,
        name                      varchar2(50) not null,
        description               varchar2(250) not null,
        position                  number
);
