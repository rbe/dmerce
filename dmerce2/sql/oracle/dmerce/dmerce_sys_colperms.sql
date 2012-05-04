CREATE TABLE colperms (
       table_schema        VARCHAR2(30) 	NOT NULL,
       table_name          VARCHAR2(30) 	NOT NULL,
       column_name         VARCHAR2(30) 	NOT NULL,
       privilege           VARCHAR2(40) 	NOT NULL,
       uxsrole             VARCHAR2(30)		NOT NULL
);
