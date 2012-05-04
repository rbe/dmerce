CREATE TABLE templates (
       fqhn                VARCHAR2(200)	NOT NULL,
       fqtn		   VARCHAR2(200)	NOT NULL,
       sam		   NUMBER(1)		DEFAULT 0	NOT NULL,
       samfailurl	   VARCHAR2(200)	DEFAULT '/',
       givenfields	   VARCHAR2(2000),
       ignoreemptyfields   VARCHAR2(2000),
       emailfrom	   VARCHAR2(200),
       emailsubject	   VARCHAR2(200)
);
