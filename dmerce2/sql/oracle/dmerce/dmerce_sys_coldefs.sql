CREATE TABLE coldefs AS
       SELECT owner, table_name, column_name
         FROM all_tab_columns
        WHERE 1 = 0;
