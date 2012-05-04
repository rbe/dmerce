SET heading OFF
SET feedback OFF
SET echo OFF
SET serveroutput ON SIZE 1000000
SET linesize 132

SPOOL drop-foreign-keys.sql

SELECT 'ALTER TABLE ' || LOWER(table_name) || ' DROP CONSTRAINT ' || LOWER(constraint_name) || ';' FROM user_constraints WHERE constraint_name LIKE '%_FK';

SPOOL OFF

@drop-foreign-keys.sql

EXIT;
