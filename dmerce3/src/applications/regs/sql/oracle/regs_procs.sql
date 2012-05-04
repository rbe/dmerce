create or replace procedure sp_createrequest_bl(
v_partnerid in number,
v_product in varchar2,
v_company in varchar2,
v_lastname in varchar2,
v_firstname in varchar2,
v_street in varchar2,
v_zipcode in varchar2,
v_city in varchar2,
v_country in varchar2,
v_phone in varchar2,
v_fax in varchar2,
v_email in varchar2,
v_remarks in varchar2,
v_tacread in varchar2)
as
        v_productid number;
        v_tac number;
begin

        select id into v_productid
          from t_blproducts
         where code = v_product;

         if v_tacread = 'true'
         then
                 v_tac := 1;
         else
                 v_tac := 0;
         end if;

        insert into t_blpreg (id, partnerid, productid, company, lastname,
        firstname, street, zipcode, city, country, phone, fax, email,
        remarks, tacread)
        values (t_blpreg_seq.nextval, v_partnerid, v_productid, v_company,
        v_lastname, v_firstname, v_street, v_zipcode, v_city, v_country,
        v_phone, v_fax, v_email, v_remarks, v_tac);

end;
/

create or replace procedure sp_createrequest_dmerce(
v_partnerid in number,
--v_product in varchar2,
v_company in varchar2,
v_companytype in varchar2,
v_lastname in varchar2,
v_firstname in varchar2,
v_street in varchar2,
v_zipcode in varchar2,
v_city in varchar2,
v_country in varchar2,
v_phone in varchar2,
v_fax in varchar2,
v_email in varchar2,
v_remarks in varchar2,
v_licread in varchar2)
as
        --v_productid number;
        v_lic number;
begin

         if v_licread = 'true'
         then
                 v_lic := 1;
         else
                 v_lic := 0;
         end if;

        insert into t_dmercereg (id, partnerid, company, lastname,
        firstname, street, zipcode, city, country, phone, fax, email,
        remarks, licread)
        values (t_dmercereg_seq.nextval, v_partnerid, v_company,
        v_lastname, v_firstname, v_street, v_zipcode, v_city, v_country,
        v_phone, v_fax, v_email, v_remarks, v_lic);

end;
/
