SET termout OFF
SET feedback OFF
SET linesize 132
SET serveroutput ON SIZE 100000

DECLARE
	libcac   NUMBER(10,2);
	rowcac   NUMBER(10,2);
	bufcac   NUMBER(10,2);
	redlog   NUMBER(10,2);
	spsize   NUMBER;
	blkbuf   NUMBER;
	logbuf   NUMBER;

BEGIN
	SELECT value INTO redlog
          FROM v$sysstat
	 WHERE name = 'redo log space requests';

	SELECT 100 * (SUM(pins) - SUM(reloads)) / SUM(pins) INTO libcac
          FROM v$librarycache;

	SELECT 100 * (SUM(gets) - SUM(getmisses)) / SUM(gets) INTO rowcac
          FROM v$rowcache;

	SELECT 100 * (cur.value + con.value - phys.value) / (cur.value + con.value) INTO bufcac
	  FROM v$sysstat cur, v$sysstat con, v$sysstat phys, v$statname ncu, v$statname nco,
               v$statname nph
	 WHERE cur.statistic# = ncu.statistic#
	   AND ncu.name = 'db block gets'
           AND con.statistic# = nco.statistic#
           AND nco.name = 'consistent gets'
           AND phys.statistic# = nph.statistic#
           AND nph.name = 'physical reads';

	SELECT value INTO spsize FROM v$parameter WHERE name = 'shared_pool_size';
	SELECT value INTO blkbuf FROM v$parameter WHERE name = 'db_block_buffers';
	SELECT value INTO logbuf FROM v$parameter WHERE name = 'log_buffer';

	--DBMS_OUTPUT.PUT_LINE('SGA MEMORY STATISTICS');
	--DBMS_OUTPUT.PUT_LINE('********************');
	--DBMS_OUTPUT.PUT_LINE('NAME='||sganame);
	--DBMS_OUTPUT.PUT_LINE('USED M='||sgaallocmem);
	--DBMS_OUTPUT.PUT_LINE('FREE K='||sgafreek);
	--DBMS_OUTPUT.PUT_LINE('FREE %='||sgafreep);
	DBMS_OUTPUT.PUT_LINE('');
	DBMS_OUTPUT.PUT_LINE('SGA CACHE STATISTICS');
	DBMS_OUTPUT.PUT_LINE('********************');
	DBMS_OUTPUT.PUT_LINE('SQL Cache Hit rate = '||libcac);
	DBMS_OUTPUT.PUT_LINE('Dict Cache Hit rate = '||rowcac);
	DBMS_OUTPUT.PUT_LINE('Buffer Cache Hit rate = '||bufcac);
	DBMS_OUTPUT.PUT_LINE('Redo Log space requests = '||redlog);
	DBMS_OUTPUT.PUT_LINE('');
	DBMS_OUTPUT.PUT_LINE('INIT.ORA SETTING');
	DBMS_OUTPUT.PUT_LINE('****************');
	DBMS_OUTPUT.PUT_LINE('Shared Pool Size = '||spsize||' Bytes');
	DBMS_OUTPUT.PUT_LINE('DB Block Buffer = '||blkbuf||' Blocks');
	DBMS_OUTPUT.PUT_LINE('Log Buffer  = '||logbuf||' Bytes');
	DBMS_OUTPUT.PUT_LINE('');

	IF libcac < 99 THEN
		DBMS_OUTPUT.PUT_LINE('*** HINT: Library Cache too low! Increase the Shared Pool Size.');
	END IF;
	IF rowcac < 85 THEN
		DBMS_OUTPUT.PUT_LINE('*** HINT: Row Cache too low! Increase the Shared Pool Size.');
	END IF;
	IF bufcac < 90 THEN
		DBMS_OUTPUT.PUT_LINE('*** HINT: Buffer Cache too low! Increase the DB Block Buffer value.');
	END IF;
	IF redlog > 100 THEN
		DBMS_OUTPUT.PUT_LINE('*** HINT: Log Buffer value is rather low!');
	END IF;
END;
/
