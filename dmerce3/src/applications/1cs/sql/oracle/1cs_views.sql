create or replace view v_links as
select lc.id as linkcategoriesid, l.createddatetime, l.realname, l.email,
       l.name, l.description, l.url, l.hits
  from t_links l, t_linkcategories lc
 where lc.active = 1
   and l.active = 1
   and l.linkcategoriesid = lc.id
 order by name;

create or replace view v_linkcategories as
select lc.id, lc.name, lc.description,
       (select count(*)
          from v_links
         where linkcategoriesid = lc.id) entries,
       (select createddatetime
          from v_links
         where linkcategoriesid = lc.id
           and rownum = 1) dateoflastlink,
       (select url
          from v_links
         where linkcategoriesid = lc.id
           and rownum = 1) urloflastlink
  from t_linkcategories lc
 where lc.active = 1
 order by position;

create or replace view v_adverts as
select ac.id advertcategoriesid, a.id, a.createddatetime,
       a.realname, a.email, a.url, a.description, a.text,
       a.foto_original, a.foto_server
  from t_advertcategories ac, t_adverts a
 where ac.active = 1
   and a.active = 1
   and ac.id = a.advertcategoriesid
   and a.validuntil >= sysdate
 order by createddatetime desc;

create or replace view v_advertcategories as
select ac.id, ac.name, ac.description,
       (select count(*)
          from t_adverts
         where advertcategoriesid = ac.id
           and validuntil >= sysdate) entries,
       (select createddatetime
          from v_adverts
         where advertcategoriesid = ac.id
           and rownum = 1) dateoflastentry
  from t_advertcategories ac
 where ac.active = 1
 order by position;

create or replace view v_advertpics as
select ap.picurl
  from t_adverts a, t_advertpics ap
 where a.active = 1
   and ap.active = 1
   and a.validuntil >= sysdate;