rem -----------------------------------------------------------------------
rem Filename:   sec-parms.sql
rem Purpose:    Security related database initialization parameters and
rem             password file users.
rem Date:       04-Nov-2001
rem Author:     Frank Naude (frank@ibi.co.za)
rem -----------------------------------------------------------------------

conn / as sysdba

tti "Security related initialization parameters:"
select name || '=' || value "PARAMTER"
from   sys.v_$parameter 
where  name in ('remote_login_passwordfile', 'remote_os_authent', 
                'os_authent_prefix', 'dblink_encrypt_login',
                'audit_trail', 'transaction_auditing')
/

tti "Password file users:"
select * from sys.v_$pwfile_users
/
