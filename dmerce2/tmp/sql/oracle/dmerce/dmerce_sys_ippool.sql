CREATE TABLE ippool (
       id                  NUMBER		NOT NULL,
       net                 VARCHAR2(200)	NOT NULL,
       poolstart           NUMBER		NOT NULL,
       poolend             NUMBER		NOT NULL,
       highestip           NUMBER
);
