create or replace procedure sp_createcustomer(
v_gender in number default 0,
v_lastname in varchar2,
v_firstname in varchar2,
v_street in varchar2,
v_zipcode in varchar2,
v_city in varchar2,
v_phone in varchar2,
v_email in varchar2,
v_birthday in date,
v_accountnumber in number,
v_accountowner in varchar2,
v_bankcode in number,
v_bank in varchar2,
v_tacread in number,
v_rowread in number,
v_desiredstartdate in date
)
as
	v_customercount		number;
begin

	select count(*) into v_customercount
	  from t_customer
	 where lastname = v_lastname
	   and firstname = v_firstname
	   and city = v_city
	   and email = v_email;
	   
	if v_customercount = 0
	then
		insert into t_customer (id, gender, lastname, firstname, street,
		zipcode, city, phone, email, birthday, accountnumber,
		accountowner, bankcode, bank, tacread, rowread, desiredstartdate)
		values (t_customer_seq.nextval, v_gender, v_lastname, v_firstname,
		v_street, v_zipcode, v_city, v_phone, v_email, v_birthday,
		v_accountnumber, v_accountowner, v_bankcode, v_bank, v_tacread,
		v_rowread, v_desiredstartdate);
	end if;

end;
/