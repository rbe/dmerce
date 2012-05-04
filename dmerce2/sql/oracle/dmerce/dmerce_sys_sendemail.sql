CREATE TABLE sendemail (
       id                  NUMBER		DEFAULT 0	NOT NULL,
       createdby           NUMBER		DEFAULT 0	NOT NULL,
       createddatetime     NUMBER		DEFAULT 0	NOT NULL,
       changedby           NUMBER		DEFAULT 0	NOT NULL,
       changeddatetime     NUMBER		DEFAULT 0	NOT NULL,
       fqhn                VARCHAR2(200)	DEFAULT ''	NOT NULL,
       prjdocroot          VARCHAR2(200)	DEFAULT ''	NOT NULL,
       template            VARCHAR2(200)	DEFAULT ''	NOT NULL,
       query               VARCHAR2(4000)	DEFAULT ''	NOT NULL,
       htmlformatted       NUMBER(1)		DEFAULT 0	NOT NULL,
       todo                NUMBER(1)		DEFAULT 0	NOT NULL
);
