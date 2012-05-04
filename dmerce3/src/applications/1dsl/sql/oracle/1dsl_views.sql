create or replace view v_products as
select productid, p.originalname,
       resellerid, r.company resellername,
       rp.name, rp.description, rp.installationcost,
       rp.price
  from t_resellerproduct rp,
       t_reseller r,
       t_product p
 where rp.resellerid = r.id
   and rp.productid = p.id
 order by name;
