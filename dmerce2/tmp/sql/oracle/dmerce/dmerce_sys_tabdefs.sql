CREATE TABLE tabdefs AS
       SELECT owner, table_name
         FROM all_tables
        WHERE 1 = 0;
