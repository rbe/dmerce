CREATE TABLE samsessions (
       sessionid	   VARCHAR2(200)	NOT NULL,
       firsttime	   NUMBER(16,6)		NOT NULL,
       lasttime		   NUMBER(16,6)		NOT NULL,
       disabledtime	   NUMBER(16,6),
       servername          VARCHAR2(200),
       serveraddr	   VARCHAR2(15)		NOT NULL,
       remoteaddr	   VARCHAR2(15)		NOT NULL,
       httpxforwardedfor   VARCHAR2(15),
       useragent	   VARCHAR2(200),
       fqun		   VARCHAR2(200),
       httpreferer	   VARCHAR2(200),
       uxsi		   VARCHAR2(10)
);
