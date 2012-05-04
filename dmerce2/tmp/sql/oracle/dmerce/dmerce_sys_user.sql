DROP USER dmerce_sys CASCADE;
CREATE USER dmerce_sys
       IDENTIFIED BY dmerce_sys
       DEFAULT TABLESPACE dmerce_sys
       TEMPORARY TABLESPACE TEMP
       QUOTA unlimited ON dmerce_sys
       QUOTA unlimited ON TEMP;
