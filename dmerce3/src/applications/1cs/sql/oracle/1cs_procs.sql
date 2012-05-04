create or replace procedure sp_createlink(
v_category in varchar2,
v_realname in varchar2 default null,
v_email in varchar2 default null,
v_name in varchar2,
v_description in varchar2,
v_url in varchar2
)
as
        --v_categoryid        number;
begin

        update t_links
           set new = 0
         where new = 1;

        insert into t_links (id, active, createddatetime, new, realname, email,
        name, description, url, linkcategoriesid)
        values (t_links_seq.nextval, 1, sysdate, 1, v_realname, v_email, 
        v_name, v_description, v_url, v_category);

end;
/

create or replace procedure sp_createadvert(
v_category in varchar2,
v_realname in varchar2,
v_email in varchar2,
v_url in varchar2 default null,
v_description in varchar2,
v_text in varchar2,
v_fotooriginal in varchar2 default null,
v_fotoserver in varchar2 default null
)
as
        v_categoryid        number;
        v_url2              varchar2(50);
begin

        if v_url is null
        then
                v_url2 := '';
        else
                v_url2 := v_url;
        end if;

/*
        select id into v_categoryid
          from t_advertcategories
         where name = v_category;
*/

        insert into t_adverts (id, active, createddatetime, validuntil,
        realname, email, url, description, text, advertcategoriesid,
        foto_original, foto_server)
        values (t_adverts_seq.nextval, 1, sysdate, sysdate + 14,
        v_realname, v_email, v_url2, v_description, v_text, v_category,
        v_fotooriginal, v_fotoserver);

end;
/