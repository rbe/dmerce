CREATE TABLE configuration (
       fqhn		   VARCHAR2(250)	NOT NULL,
       hostserial	   NUMBER(10)		NOT NULL,
       ownerconnectstring  VARCHAR2(250)	NOT NULL,
       dmercesysconnectstring VARCHAR2(200)	NOT NULL,
       saminactivetimeout  NUMBER		DEFAULT 900	NOT NULL,
       uxsroleprefix       VARCHAR(27),
       fileuploaddir       VARCHAR2(250),
       debug		   NUMBER(1)		DEFAULT 0	NOT NULL
);
