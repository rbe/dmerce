CREATE TABLE uxsgroups (
       id                  NUMBER		NOT NULL,
       createdby           NUMBER,
       createddatetime     DATE,
       changedby           NUMBER,
       changeddatetime     DATE,
       active              NUMBER(1)		DEFAULT 1	NOT NULL,
       uxsgroup            VARCHAR2(200)	NOT NULL,
       schema              VARCHAR2(30)		NOT NULL,
       description         VARCHAR2(2000)	NULL
);
