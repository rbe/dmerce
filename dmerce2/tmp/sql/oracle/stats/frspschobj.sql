SET VERIFY OFF
SET HEADING OFF
SET SERVEROUTPUT ON
SET LINESIZE 132
SET PAGESIZE 5000
SET FEEDBACK OFF

UNDEF Owner
UNDEF Grpid
UNDEF Inst
UNDEF DBs
COLUMN Inst new_val Grpid
COLUMN B new_val DBs
SELECT value Inst FROM v$parameter WHERE name = 'instance_number';
SELECT value B FROM v$parameter WHERE name = 'db_block_size';

ACCEPT Owner CHAR PROMPT 'Please enter schema owner: '

DECLARE
  CURSOR C1 IS
    SELECT table_name object_name, 'TABLE' object_type FROM dba_tables WHERE owner = UPPER('&Owner')
    UNION
    SELECT index_name object_name, 'INDEX' object_type FROM dba_indexes WHERE owner = UPPER('&Owner')
    UNION
    SELECT cluster_name object_name, 'CLUSTER' object_type FROM dba_clusters WHERE owner = UPPER('&Owner')
    ORDER BY object_type, object_name;
  F_blks INTEGER := 0;
  Sz Number;
BEGIN
  DBMS_OUTPUT.ENABLE(9999999999999);
  DBMS_OUTPUT.PUT_LINE('===================================================================================');
  DBMS_OUTPUT.PUT_LINE('-                     Free Space Report For &Owner');
  DBMS_OUTPUT.PUT_LINE('===================================================================================');
  DBMS_OUTPUT.PUT_LINE(Rpad('OBJECT NAME',40)||'  '||Rpad('OBJECT TYPE',15)||'  '||RPAD('FREE SPACE in KB',25));
  DBMS_OUTPUT.PUT_LINE('===================================================================================');
  FOR I IN C1 LOOP
      F_blks := 0;
      DBMS_SPACE.FREE_BLOCKS(SEGMENT_OWNER => UPPER('&Owner'),
                             SEGMENT_NAME => RTRIM(I.Object_name),
                             SEGMENT_TYPE => RTRIM(I.Object_Type),
                             FREELIST_GROUP_ID => &Grpid , FREE_BLKS  => F_blks);
      Sz := NVL(F_blks, 0) * (&DBs / 1024);
      IF Sz > 0 Then
          DBMS_OUTPUT.PUT_LINE(Rpad(I.object_name,40)||'  '||Rpad(I.object_type,15)||'  '||Rpad(Sz,10));
      END IF;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('===================================================================================');
END;
/
EXIT;
