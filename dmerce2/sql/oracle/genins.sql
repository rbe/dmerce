-- #############################################################################################
--
-- %Purpose:    Generate INSERT statements for existing data in a table
--
-- Author:      christoph.bohl@akadia.com
--
-- #############################################################################################
--
PROMPT ==========================================================================
PROMPT Generate INSERT statements for existing data in a table
PROMPT ==========================================================================
PROMPT
PROMPT You'll be prompted for the following:
PROMPT - table_name: The name of the table to generate statements (case insensitive)
PROMPT - col1:       The name of a column you want to fill with fixed data (case insensitive)
PROMPT .             - [ENTER]: do not use this functionality
PROMPT - col1_value: The value for the column above (case sensitive)
PROMPT .             - Enter String Values within two single quotes: ''example''
PROMPT .             - [ENTER]: do not use this functionality
PROMPT - col2:       The name of a column you want to fill with fixed data (case insensitive)
PROMPT .             - [ENTER]: do not use this functionality
PROMPT - col2_value: The value for the column above (case sensitive)
PROMPT .             - Enter String Values within two single quotes: ''example''
PROMPT .             - [ENTER]: do not use this functionality
PROMPT

set feedback off
set trimspool on
set linesize 255

CREATE OR REPLACE PROCEDURE genins(p_table IN varchar
                                  ,p_default_col1 VARCHAR default null
                                  ,p_default_col1_value VARCHAR default null
                                  ,p_default_col2 VARCHAR default null
                                  ,p_default_col2_value VARCHAR default null)
IS
  --
  l_column_list   VARCHAR(2000);
  l_value_list    VARCHAR(2000);
  l_query         VARCHAR(2000);
  l_cursor        INTEGER;
  ignore NUMBER;
  --
  FUNCTION get_cols(p_table VARCHAR)
  RETURN VARCHAR
  IS
    l_cols VARCHAR(2000);
    CURSOR l_col_cur(c_table VARCHAR) IS
            SELECT column_name
            FROM   user_tab_columns
            WHERE  table_name = upper(c_table)
            ORDER BY column_id;
  BEGIN
    l_cols := null;
    FOR rec IN l_col_cur(p_table)
    LOOP
      l_cols := l_cols || rec.column_name || ',';
    END LOOP;
    RETURN substr(l_cols,1,length(l_cols)-1);
  END;
  --
  FUNCTION get_query(p_table IN VARCHAR
                    ,p_default_col1 VARCHAR
                    ,p_default_col1_value VARCHAR
                    ,p_default_col2 VARCHAR
                    ,p_default_col2_value VARCHAR)
  RETURN VARCHAR
  IS
    l_query VARCHAR(2000);
      CURSOR l_query_cur(c_table VARCHAR
                        ,c_default_col1 VARCHAR
                        ,c_default_col1_value VARCHAR
                        ,c_default_col2 VARCHAR
                        ,c_default_col2_value VARCHAR) IS
        SELECT decode(column_name,c_default_col1,''''||replace(c_default_col1_value,'''','''''')||'''',
               decode(column_name,c_default_col2,''''||replace(c_default_col2_value,'''','''''')||'''',
              'decode('||column_name||',null,''null'','||
               decode(data_type
               ,'VARCHAR2','''''''''||'||column_name ||'||'''''''''
               ,'DATE'    ,'''to_date(''''''||to_char('||column_name||',''YYYY-MM-DD HH24:MI:SS'')||'''''',''''YYYY-MM-DD HH24:MI:SS'''')'''
               ,column_name
               ) || ')' )) column_query
          FROM user_tab_columns
         WHERE table_name = upper(c_table)
        ORDER BY column_id;
  BEGIN
    l_query := 'SELECT ';
    FOR rec IN l_query_cur(p_table, p_default_col1, p_default_col1_value, p_default_col2, p_default_col2_value)
    LOOP
      l_query := l_query || rec.column_query || '||'',''||';
    END LOOP;
    l_query := substr(l_query,1,length(l_query)-7);
    RETURN l_query || ' FROM ' || p_table;
  END;
  --
BEGIN
  l_column_list  := get_cols(p_table);
  l_query        := get_query(p_table,upper(p_default_col1),p_default_col1_value
                                     ,upper(p_default_col2),p_default_col2_value);
  l_cursor := dbms_sql.open_cursor;
  DBMS_SQL.PARSE(l_cursor, l_query, DBMS_SQL.native);
  DBMS_SQL.DEFINE_COLUMN(l_cursor, 1, l_value_list, 2000);
  ignore := DBMS_SQL.EXECUTE(l_cursor);
  --
  LOOP
    IF DBMS_SQL.FETCH_ROWS(l_cursor)>0 THEN
      DBMS_SQL.COLUMN_VALUE(l_cursor, 1, l_value_list);
      DBMS_OUTPUT.PUT_LINE('INSERT INTO '||p_table||' ('||l_column_list||')');
      DBMS_OUTPUT.PUT_LINE('  VALUES ('||l_value_list||');');
    ELSE
      EXIT;
    END IF;
  END LOOP;
END;
/

set serveroutput on size 1000000
exec genins('&table_name','&col1','&col1_value','&col2','&col2_value');
set serveroutput off

drop procedure genins;
set feedback on
