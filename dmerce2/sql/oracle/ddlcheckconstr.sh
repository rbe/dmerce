set -a

echo; read USRID?"Enter the DB schema name for generating the check constraints DDL: "; echo
read PASSWD?"Supply the password for the above DB schema name: "; echo
read ORACLE_SID?"Supply a value for the ORACLE_SID variable: "; echo
read ORACLE_HOME?"Enter the path to the ORACLE_HOME: "
echo; echo
#
if [[ -z "$USRID" || -z "$PASSWD" || -z "$ORACLE_SID" || -z "$ORACLE_HOME" ]]; then
   echo "Missing value(s) for one or more of the required inputs..."
   echo "Terminating script!!!"; echo
   exit 1
fi
#
# Start PL/SQL anonymous block
#
sqlplus -s ${USRID}/${PASSWD}@${ORACLE_SID}<<-%%
set serveroutput on size 2000 
rem
declare
   v_tname                varchar2(100);
   v_srch_cond            varchar2(4000);
   chekfile               utl_file.file_type;
   v_tblname              varchar2(30);
   file_name              varchar2(60) := 'chek_cons.sql';
   counter                number := 0;
   c_tname                varchar2(100) := ' ';
   no_rows                exception;
cursor chex is
   select table_name, search_condition
     from user_constraints
    where constraint_type = 'C'
    order by table_name;
begin
/*
Open the check constraint cursor
*/
   chekfile := utl_file.fopen('directory_name', file_name, 'W'); 
   open chex;
   utl_file.put_line(chekfile, 'spool' || substr(file_name, 1, instr(file_name, '.', 1)) || 'lst');
   utl_file.new_line(chekfile);
      loop
        fetch chex into v_tname, v_srch_cond;
          if chex%rowcount = 0 then
             raise no_rows;
          end if;
        exit when chex%notfound;
          if v_srch_cond like '%IS NOT NULL%' then
              null;
          else
              if c_tname <> v_tname then
                 counter := 1; 
                 utl_file.put_line(chekfile, 'alter table '||v_tname||chr(10)||'  add constraint '||
                                   v_tname||'_C'||counter||chr(10)||' check ('||v_srch_cond||')'||chr(10)||'/');
                 c_tname := v_tname;
              else
                 counter := counter + 1;
                 utl_file.put_line(chekfile, 'alter table '||v_tname||chr(10)||'  add constraint '||
                                   v_tname||'_C'||counter||chr(10)||' check ('||v_srch_cond||')'||chr(10)||'/');
                 c_tname := v_tname; 
              end if;
          end if;
      end loop;
   close chex;
   utl_file.new_line(chekfile);
   utl_file.put_line(chekfile, 'spool off');
   utl_file.fclose(chekfile);
exception
   when no_rows then
      dbms_output.put_line('No data found - Tablename incorrect or does not exist');
   when others then
      dbms_output.put_line('Error in PL/SQL construct');
end;
/
%%
