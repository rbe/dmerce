DROP USER dmerce_web CASCADE;
CREATE USER dmerce_web
       IDENTIFIED BY dmerce_web
       DEFAULT TABLESPACE dmerce_sys
       TEMPORARY TABLESPACE TEMP
       QUOTA unlimited ON dmerce_sys
       QUOTA unlimited ON TEMP;
GRANT CREATE SESSION TO dmerce_web;
GRANT ALTER SESSION TO dmerce_web;
GRANT CREATE ANY SEQUENCE TO dmerce_web;
GRANT SELECT ANY SEQUENCE TO dmerce_web;
GRANT DROP ANY SEQUENCE TO dmerce_web;
GRANT CREATE PROCEDURE TO dmerce_web;
