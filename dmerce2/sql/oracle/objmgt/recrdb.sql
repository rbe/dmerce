rem Script Description: This PL/SQL procedure replaces all of the individual SQL scripts 
rem                     necessary to recreate a database. Please see the notes included
rem                     below.
rem
rem                     Every DBA seems to maintain a series of script to re-create the
rem                     database. This script saves the hassle of doing it from Sever Manager.
rem
rem                     Here are some expectations about the script:
rem                     o Create this package under the SYS account. Grant execute privleges
rem                       to appropriate users.
rem                     o Run from the sys account of your Oracle instance OR 
rem                       an account with access to the DBA_ system views
rem                     o Have the serveroutput on size 1000000; 
rem                     o The first call in the procedure is a command to create an ASCII
rem                       dump of the controlfile. By default, this trace file is put 
rem                       in the /trace sub-directory, as defined by the Oracle instance. 
rem                     o Setting up an dbms_job so that a regular weekly copy of
rem                       all of the database.
rem
rem                     NOTE: This script has been used with Oracle v7 Only. It is not
rem                     certified against an Oracle v8 database.
rem
rem Output file:        See Below
rem
rem Prepared By:        Oracle Resource Stop
rem
rem Usage Information:  SQLPLUS SYS/{PASSWD} @recreatedb.sql
rem

CREATE OR REPLACE PROCEDURE recreatedb AS

        cursor cur1 is 
	   select 'create tablespace '||dt.tablespace_name||' datafile  '''||file_name 
          ||''' size  '||bytes||' default storage ( initial '||initial_extent|| 
          ' next  '||next_extent||' pctincrease '||pct_increase|| 
          ' minextents '||min_extents||' maxextents '||max_extents||');' line
	   from dba_tablespaces dt,dba_data_files ddf
         where ddf.tablespace_name=dt.tablespace_name; 

	   tbsp_row cur1%ROWTYPE; 

	   CURSOR cur2 IS 
	   select 'create rollback segment '||segment_name||' tablespace  "'||tablespace_name|| 
      	    '" storage (initial '||initial_extent||' next  '||next_extent||
	          ' minextents '||min_extents|| ' maxextents '||max_extents||');' rbs 
	   from dba_rollback_segs; 
--
	   rbs_row cur2%ROWTYPE; 
-- 
-- turn rollback segments online 
-- 
         CURSOR cur3 IS 
   select 'alter rollback segment '||segment_name||' online;' arbs
   from dba_rollback_segs; 
--
   alt_rbs_row cur3%ROWTYPE; 
-- 
-- create profiles 
-- 
   CURSOR cur4 IS 
   select 'create profile '||profile||' limit cpu_per_session default
          cpu_per_call default connect_time unlimited idle_time default 
          sessions_per_user default logical_reads_per_session default 
          logical_reads_per_call default private_sga default composite_limit
          default;' crt_pro,profile 
   from dba_profiles group by profile; 
--
   prof_row cur4%ROWTYPE; 
-- 
-- alter profiles as they are in the database 
-- 
   CURSOR cur5(prof IN VARCHAR2) IS 
   select 'alter profile '||prof||' '||resource_name||' '||limit||';' apl 
   from dba_profiles where profile=prof; 
-- 
-- create users as defined in database 
-- 
   CURSOR cur6 IS 
   select 'create user '||username||' identified by '||username 
          ||' default tablespace "'||default_tablespace||'" temporary tablespace "'|| 
          temporary_tablespace||'" profile '||profile||');' usr 
   from dba_users 
   where username not in ('SYSTEM','SYS'); 
--
   user_row cur6%ROWTYPE; 
-- 
-- create roles 
-- 
   CURSOR cur7 IS 
   select 'create '||role||' not identified;'  crr from dba_roles; 
--
   role_row cur7%ROWTYPE; 
-- 
-- add priv's to roles 
-- 
   CURSOR cur8 IS 
   select 'grant '||privilege||' to '||grantee||'  
          '||decode(admin_option,'NO',';','YES','WITH ADMIN OPTION;') adr 
   from dba_sys_privs; 
--
   add_role_row cur8%ROWTYPE; 
-- 
-- grant roles to users 
-- 
   CURSOR cur9 IS 
   select 'grant "'||granted_role||'" to '||grantee||';' rle 
   from dba_role_privs; 
--
   get_role_row cur9%ROWTYPE; 
-- 
-- alter roles to users 
-- 
   CURSOR cur10 IS 
   select 'alter user '||grantee||' default role all;' arl 
   from dba_role_privs; 
--
   alt_role_row cur10%ROWTYPE; 
-- 
-- assign quotas to users 
-- 
   CURSOR cur11 IS 
   select 'alter user '||username||' quota unlimited on  "'||tablespace_name||'";' qut 
   from dba_ts_quotas; 
--
   quota_row cur11%ROWTYPE; 
-- 
   cid INTEGER;            -- the cursor 
   rtn INTEGER;            -- the return or calling variable 
   s1 VARCHAR2(2000);      -- the dump site for the dynamic SQL 

BEGIN 
-- 
-- dynamic sql to create the trace of the controlfile 
-- 
   s1 := 'alter database backup controlfile to trace'; 
   cid := dbms_sql.open_cursor; 
   dbms_sql.parse(cid, s1 , dbms_sql.v7); 
   rtn := dbms_sql.execute(cid); 
   dbms_sql.close_cursor(cid); 
-- 
   dbms_output.put_line('******************* create control file dump to the $ORACLE_HOME/instance/trace directory'); 
-- 
-- lets print out all datafiles 
-- 
   OPEN cur1; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  CREATING THE DATAFILES'); 
   LOOP 
   FETCH cur1 INTO tbsp_row; 
   EXIT WHEN cur1%NOTFOUND; 
      dbms_output.put_line(tbsp_row.line); 
   END LOOP; 
   CLOSE cur1; 
-- 
-- lets print out all rollback segments 
-- 
   OPEN cur2; 
   dbms_output.put_line(' '); 
  
   dbms_output.put_line('************************************************* CREATE ROLLBACK SEGMENTS'); 
   LOOP 
   FETCH cur2 INTO rbs_row; 
   EXIT WHEN cur2%NOTFOUND; 
      dbms_output.put_line(rbs_row.rbs); 
   END LOOP; 
   CLOSE cur2; 
-- 
-- lets alter rollback segments online 
-- 
   OPEN cur3; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  PUT ROLLBACK SEGMENTS ONLINE'); 
   LOOP 
   FETCH cur3 INTO alt_rbs_row; 
   EXIT WHEN cur3%NOTFOUND; 
      dbms_output.put_line(alt_rbs_row.arbs); 
   END LOOP; 
   CLOSE cur3; 
-- 
-- lets create all profiles 
-- 
   OPEN cur4; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  CREATE PROFILES'); 
   LOOP 
   FETCH cur4 INTO prof_row; 
   EXIT WHEN cur4%NOTFOUND; 
        dbms_output.put_line(substr(prof_row.crt_pro,1,250)); 
        dbms_output.put_line(substr(prof_row.crt_pro,251,500)); 
        FOR ap IN cur5(prof_row.profile) LOOP 
            dbms_output.put_line(substr(ap.apl,1,250)); 
            dbms_output.put_line(substr(ap.apl,251,500)); 
        END LOOP; 
   END LOOP; 
   CLOSE cur4; 
-- 
-- lets create all users 
-- 
   OPEN cur6; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  CREATE USERS'); 
   LOOP 
   FETCH cur6 INTO user_row; 
   EXIT WHEN cur6%NOTFOUND; 
      dbms_output.put_line(user_row.usr); 
   END LOOP; 
   CLOSE cur6; 
-- 
-- lets create the roles 
-- 
   OPEN cur7; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  CREATE ROLES'); 
   LOOP 
   FETCH cur7 INTO role_row; 
   EXIT WHEN cur7%NOTFOUND; 
      dbms_output.put_line(role_row.crr); 
   END LOOP; 
   CLOSE cur7; 
-- 
-- lets add to the roles 
-- 
   OPEN cur8; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  ADD TO ROLES'); 
   LOOP 
   FETCH cur8 INTO add_role_row; 
   EXIT WHEN cur8%NOTFOUND; 
      dbms_output.put_line(add_role_row.adr); 
   END LOOP; 
   CLOSE cur8; 
-- 
-- lets assign roles to users 
-- 
   OPEN cur9; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  ASSIGN ROLES TO USERS'); 
   LOOP 
   FETCH cur9 INTO get_role_row; 
   EXIT WHEN cur9%NOTFOUND; 
      dbms_output.put_line(get_role_row.rle); 
   END LOOP; 
   CLOSE cur9; 
-- 
-- lets assign default roles to users 
-- 
   OPEN cur10; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  ASSIGN DEFAULTS TO USERS'); 
   LOOP 
   FETCH cur10 INTO alt_role_row; 
   EXIT WHEN cur10%NOTFOUND; 
      dbms_output.put_line(alt_role_row.arl); 
   END LOOP; 
   CLOSE cur10; 
-- 
-- lets assign quotas to users 
-- 
   OPEN cur11; 
   dbms_output.put_line(' '); 
   dbms_output.put_line('**************************************************  ASSIGN QUOTAS TO USERS'); 
   LOOP 
   FETCH cur11 INTO quota_row; 
   EXIT WHEN cur11%NOTFOUND; 
      dbms_output.put_line(quota_row.qut); 
   END LOOP; 
   CLOSE cur11; 
END; 
/
