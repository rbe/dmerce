CREATE TABLE log (
       id                  NUMBER		NOT NULL,
       createddatetime     DATE,
       serveraddr          VARCHAR2(15),
       remoteaddr          VARCHAR2(15),
       httpxforwardedfor   VARCHAR2(15),
       template            VARCHAR2(200),
       handler             VARCHAR2(200),
       msgtype             VARCHAR2(20),
       msg                 VARCHAR2(500)
);
