CREATE TABLE availprojects (
       id                  NUMBER		NOT NULL,
       createdby           NUMBER,
       createddatetime     DATE,
       changedby           NUMBER,
       changeddatetime     DATE,
       name                VARCHAR2(255),
       description         VARCHAR2(2000)
);
