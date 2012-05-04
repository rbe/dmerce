-- #############################################################################################
--
-- %Purpose: Displays database resource usage statistics (Whole instance or Session)
--
-- #############################################################################################
--
set serveroutput on size 100000 verify off feedback off
--
accept sid number default 0 prompt 'Enter SID, or press Return for System Stats: '
accept interval number default 10 prompt 'Time interval in seconds [10]: '
prompt
prompt Statistic                                               Change
prompt ---------                                               ------;
--
declare
   max_statistic# number;
   current_second integer;
   type  stats_table is table of number index by binary_integer;
   first_stat stats_table;
   second_stat stats_table;
   stat_name varchar2(64);
   stat_class number;
begin
select max(statistic#) into max_statistic# from v$statname;
current_second := to_number(to_char(sysdate,'SSSSS'));
while to_number(to_char(sysdate,'SSSSS')) = current_second
   loop null; end loop;
current_second := to_number(to_char(sysdate,'SSSSS'));
for i in 0 .. max_statistic# loop
   if &&sid = 0 then
    select value into first_stat(i)
       from v$sysstat v
       where v.statistic# = i;
   else
    select value into first_stat(i)
       from v$sesstat v
       where v.sid = &&sid and v.statistic# = i;
    end if;
   end loop;
while to_number(to_char(sysdate,'SSSSS')) < current_second + &&interval
   loop null; end loop;
for i in 0 .. max_statistic# loop
   if &&sid = 0 then
    select value into second_stat(i)
       from v$sysstat v
       where v.statistic# = i;
   else
    select value into second_stat(i)
       from v$sesstat v
       where v.sid = &&sid and v.statistic# = i;
    end if;
   end loop;
for i in 0 .. max_statistic# loop
  if (second_stat(i) - first_stat(i)) > 0 then
    select v.name, v.class into stat_name, stat_class
       from v$statname v where v.statistic# = i;
      if stat_class in (1,8,64,128) then
       dbms_output.put(rpad(stat_name,52));
         dbms_output.put_line( to_char(second_stat(i) - first_stat(i),'9,999,990'));
       end if;
      end if;
   end loop;
end;
/
prompt
undef sid interval
set feedback on
