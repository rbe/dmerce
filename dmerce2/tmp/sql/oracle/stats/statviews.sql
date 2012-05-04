--
-- INSTANCE
--
CREATE OR REPLACE VIEW dbinfo AS
	SELECT dbid, name, instance_name, host_name, created, log_mode, banner
	  FROM v$database, v$instance, v$version;

CREATE OR REPLACE VIEW dboptions AS
	SELECT *
	  FROM v$option
      ORDER BY value, parameter;

CREATE OR REPLACE VIEW dbparameter AS
	SELECT name, value
	  FROM v$parameter
	 WHERE value NOT LIKE '%/%'
      ORDER BY name;

CREATE OR REPLACE VIEW dbparameterfiles AS
	SELECT name, value
	  FROM v$parameter
	 WHERE value LIKE '%/%'
         ORDER BY name;

CREATE OR REPLACE VIEW dblistinitoraparams AS
	SELECT a.ksppinm name, b.ksppstvl value, b.ksppstdf isdefault,
	       DECODE(a.ksppity, 1, 'boolean', 2, 'string', 3, 'number', 4, 'file', 
	              a.ksppity) type,
	       a.ksppdesc description
	  FROM sys.x$ksppi a, sys.x$ksppcv b
	 WHERE a.indx = b.indx
	   AND a.ksppinm not like '\_%' escape '\'
	 ORDER BY name;

CREATE OR REPLACE VIEW dblistinitoraparams2 AS
	SELECT a.ksppinm name, b.ksppstvl value, b.ksppstdf isdefault,
	       DECODE(a.ksppity, 1, 'boolean', 2, 'string', 3, 'number', 4, 'file',
	              a.ksppity) type,
	       a.ksppdesc description
	  FROM sys.x$ksppi a, sys.x$ksppcv b
	 WHERE a.indx = b.indx
	   AND a.ksppinm like '\_%' escape '\'
	 ORDER BY name;

CREATE OR REPLACE VIEW dbuptime AS
	SELECT SYSDATE-logon_time "Uptime/Days", (SYSDATE-logon_time)*24 "Uptime/Hours"
	  FROM sys.v_$session
	 WHERE sid = 1;

CREATE OR REPLACE VIEW dbbgproc AS
	SELECT name, status, description
	  FROM v$session, v$bgprocess
	 WHERE v$session.paddr = v$bgprocess.paddr;

CREATE OR REPLACE VIEW dbsga AS
	SELECT name, value
	  FROM v$sga;

CREATE OR REPLACE VIEW dbsgamemstat AS
	SELECT name,
	       sgasize/1024/1024 "Allocated (M)",
	       bytes/1024/1024 "Free (M)",
	       ROUND(bytes/sgasize*100, 2) "% Free"
	  FROM (SELECT SUM(bytes) sgasize FROM sys.v_$sgastat), sys.v_$sgastat
      ORDER BY name;

CREATE OR REPLACE VIEW dbsgalibcachehr AS
	SELECT 100*(SUM(pins)-SUM(reloads))/SUM(pins) "SQL Cache Hit Ratio"
          FROM v$librarycache;

CREATE OR REPLACE VIEW dbsgalibcachepins AS
	SELECT sum(pins) "PINS",
	       sum(reloads) "RELOADS",
	       round((sum(reloads) / sum(pins) * 100),2) "% RELOADS"
 	  FROM v$librarycache;

CREATE OR REPLACE VIEW dbsgarowcachehr AS
	SELECT 100*(SUM(gets)-SUM(getmisses))/SUM(gets) "Row Cache Hit Ratio"
          FROM v$rowcache;

CREATE OR REPLACE VIEW dbsgabufcachehr AS
	SELECT 100 * (cur.value + con.value - phys.value) / (cur.value + con.value)
	       "Buffer Cache Hit Ratio"
	  FROM v$sysstat cur, v$sysstat con, v$sysstat phys, v$statname ncu, v$statname nco,
               v$statname nph
	 WHERE cur.statistic# = ncu.statistic#
	   AND ncu.name = 'db block gets'
           AND con.statistic# = nco.statistic#
           AND nco.name = 'consistent gets'
           AND phys.statistic# = nph.statistic#
           AND nph.name = 'physical reads';

CREATE OR REPLACE VIEW dbblocksize AS
	SELECT to_number(value) "Block size in bytes"
	  FROM sys.v_$parameter
	 WHERE name = 'db_block_size';

CREATE OR REPLACE VIEW dbmaxpossibleextents AS
	SELECT to_number(value)/16-7 "Maximum Possible Extents"
	  FROM sys.v_$parameter
	 WHERE name = 'db_block_size';

CREATE OR REPLACE VIEW dbrecommendedminextsize AS
	SELECT to_number(a.value) * to_number(b.value) / 1024 "Min extent size in K"
	  FROM sys.v_$parameter a, sys.v_$parameter b
	 WHERE a.name = 'db_block_size'
	   AND b.name = 'db_file_multiblock_read_count';

CREATE OR REPLACE VIEW dbctrlfiles AS
	SELECT name, status
	  FROM v$controlfile;

CREATE OR REPLACE VIEW dbctrlfilestat AS
	SELECT type, records_used, records_total,
	       records_used/records_total*100 "PCT_USED"
	  FROM sys.v_$controlfile_record_section;

CREATE OR REPLACE VIEW dblogfiles AS
	SELECT v$logfile.group#, member, v$logfile.status, type, bytes/1048576 MB
	  FROM v$logfile, v$log
	 WHERE v$log.group# = v$logfile.group#;

CREATE OR REPLACE VIEW dblogfilecontention AS
	SELECT value "Redo Log Contention"
	  FROM v$sysstat
	 WHERE name = 'redo log space requests';

CREATE OR REPLACE VIEW dbrbs AS
	SELECT segment_name, status, owner, tablespace_name, initial_extent, next_extent,
	       min_extents, max_extents, pct_increase
	  FROM dba_rollback_segs;

CREATE OR REPLACE VIEW dbrbsstats AS
	SELECT rn.name "Rollback Segment", rs.rssize/1024 "Size (KB)", rs.gets "Gets",
	       rs.waits "Waits", (rs.waits/rs.gets)*100 "% Waits",
	       rs.shrinks "# Shrinks", rs.extends "# Extends",
	       round((sum(rs.waits) / sum(rs.gets)) * 100, 2) "% AVG WAITS/GETS"
	  FROM sys.v_$rollname rn, sys.v_$rollstat rs
	 WHERE rn.usn = rs.usn;

--
-- STATS
--
CREATE OR REPLACE VIEW dbtop10cputime AS
	SELECT rownum as rank, a.*
	  FROM (SELECT v.sid, program, v.value / (100 * 60) CPUMins
		  FROM v$statname s , v$sesstat v, v$session sess
		 WHERE s.name = 'CPU used by this session'
		   AND sess.sid = v.sid
		   AND v.statistic# = s.statistic#
		   AND v.value > 0
		 ORDER BY v.value DESC) a
	 WHERE rownum < 11;

--
-- LOCKS
--
CREATE OR REPLACE VIEW dblockslatches AS
	SELECT s.sid, s.serial#,
	       DECODE(s.process, NULL,
	          DECODE(substr(p.username,1,1), '?',   UPPER(s.osuser), p.username),
	          DECODE(       p.username, 'ORACUSR ', UPPER(s.osuser), s.process)
	       ) process,
	       nvl(s.username, 'SYS ('||SUBSTR(p.username,1,4)||')') username,
	       DECODE(s.terminal, NULL, RTRIM(p.terminal, chr(0)),
	              UPPER(s.terminal)) terminal,
	       DECODE(l.type,
	          -- Long locks
	                      'TM', 'DML/DATA ENQ',   'TX', 'TRANSAC ENQ',
	                      'UL', 'PLS USR LOCK',
	          -- Short locks
	                      'BL', 'BUF HASH TBL',  'CF', 'CONTROL FILE',
	                      'CI', 'CROSS INST F',  'DF', 'DATA FILE   ',
	                      'CU', 'CURSOR BIND ',
	                      'DL', 'DIRECT LOAD ',  'DM', 'MOUNT/STRTUP',
	                      'DR', 'RECO LOCK   ',  'DX', 'DISTRIB TRAN',
	                      'FS', 'FILE SET    ',  'IN', 'INSTANCE NUM',
	                      'FI', 'SGA OPN FILE',
	                      'IR', 'INSTCE RECVR',  'IS', 'GET STATE   ',
	                      'IV', 'LIBCACHE INV',  'KK', 'LOG SW KICK ',
	                      'LS', 'LOG SWITCH  ',
	                      'MM', 'MOUNT DEF   ',  'MR', 'MEDIA RECVRY',
	                      'PF', 'PWFILE ENQ  ',  'PR', 'PROCESS STRT',
	                      'RT', 'REDO THREAD ',  'SC', 'SCN ENQ     ',
	                      'RW', 'ROW WAIT    ',
	                      'SM', 'SMON LOCK   ',  'SN', 'SEQNO INSTCE',
	                      'SQ', 'SEQNO ENQ   ',  'ST', 'SPACE TRANSC',
	                      'SV', 'SEQNO VALUE ',  'TA', 'GENERIC ENQ ',
	                      'TD', 'DLL ENQ     ',  'TE', 'EXTEND SEG  ',
	                      'TS', 'TEMP SEGMENT',  'TT', 'TEMP TABLE  ',
	                      'UN', 'USER NAME   ',  'WL', 'WRITE REDO  ',
	                      'TYPE='||l.type) type,
	       DECODE(l.lmode, 0, 'NONE', 1, 'NULL', 2, 'RS', 3, 'RX',
	                       4, 'S',    5, 'RSX',  6, 'X',
	                       to_char(l.lmode)) lmode,
	       DECODE(l.request, 0, 'NONE', 1, 'NULL', 2, 'RS', 3, 'RX',
	                         4, 'S', 5, 'RSX', 6, 'X',
	                         to_char(l.request)) lrequest,
	       DECODE(l.type, 'MR', DECODE(u.name, NULL,
	                            'DICTIONARY OBJECT', u.name||'.'||o.name),
	                      'TD', u.name||'.'||o.name,
	                      'TM', u.name||'.'||o.name,
	                      'RW', 'FILE#='||SUBSTR(l.id1,1,3)||
	                      ' BLOCK#='||SUBSTR(l.id1,4,5)||' ROW='||l.id2,
	                      'TX', 'RS+SLOT#'||l.id1||' WRP#'||l.id2,
	                      'WL', 'REDO LOG FILE#='||l.id1,
	                      'RT', 'THREAD='||l.id1,
	                      'TS', DECODE(l.id2, 0, 'ENQUEUE',
	                                             'NEW BLOCK ALLOCATION'),
	                      'ID1='||l.id1||' ID2='||l.id2) object
	  FROM sys.v_$lock l, sys.v_$session s, sys.obj$ o, sys.user$ u,
	       sys.v_$process p
	 WHERE s.paddr  = p.addr(+)
	   AND l.sid    = s.sid
	   AND l.id1    = o.obj#(+)
	   AND o.owner# = u.user#(+)
	   AND l.type   <> 'MR'
	UNION ALL                          /*** LATCH HOLDERS ***/
	SELECT s.sid, s.serial#, s.process, s.username, s.terminal,
	       'LATCH', 'X', 'NONE', h.name||' ADDR='||RAWTOHEX(laddr)
	  FROM sys.v_$process p, sys.v_$session s, sys.v_$latchholder h
	 WHERE h.pid  = p.pid
	   AND p.addr = s.paddr
	UNION ALL                         /*** LATCH WAITERS ***/
	SELECT s.sid, s.serial#, s.process, s.username, s.terminal,
	       'LATCH', 'NONE', 'X', name||' LATCH='||p.latchwait
	  FROM sys.v_$session s, sys.v_$process p, sys.v_$latch l
	 WHERE latchwait IS NOT NULL
	   AND p.addr      = s.paddr
	   AND p.latchwait = l.addr;

CREATE OR REPLACE VIEW dbshowlocks AS
	 SELECT /*+ ordered */
		 --b.kaddr,
		 c.sid,
		 lock_waiter.waiting_session,
		 lock_blocker.holding_session,
		 c.program,
		 c.osuser,
		 c.machine,
		 c.process,
		 DECODE(u.name,
			NULL,'',
			u.name||'.'||o.name
		 ) object,
		 c.username,
		 DECODE
		 (
			b.type,
			'BL', 'Buffer hash table instance lock',
			'CF', 'Control file schema global enqueue lock',
			'CI', 'Cross-instance function invocation instance lock',
			'CU', 'Cursor bind lock',
			'DF', 'Data file instance lock',
			'DL', 'direct loader parallel index create lock',
			'DM', 'Mount/startup db primary/secondary instance lock',
			'DR', 'Distributed recovery process lock',
			'DX', 'Distributed transaction entry lock',
			'FS', 'File set lock',
			'IN', 'Instance number lock',
			'IR', 'Instance recovery serialization global enqueue lock',
			'IS', 'Instance state lock',
			'IV', 'Library cache invalidation instance lock',
			'JQ', 'Job queue lock',
			'KK', 'Thread kick lock',
			'LA','Library cache lock instance lock (A..P=namespace);',
			'LB','Library cache lock instance lock (A..P=namespace);',
			'LC','Library cache lock instance lock (A..P=namespace);',
			'LD','Library cache lock instance lock (A..P=namespace);',
			'LE','Library cache lock instance lock (A..P=namespace);',
			'LF','Library cache lock instance lock (A..P=namespace);',
			'LG','Library cache lock instance lock (A..P=namespace);',
			'LH','Library cache lock instance lock (A..P=namespace);',
			'LI','Library cache lock instance lock (A..P=namespace);',
			'LJ','Library cache lock instance lock (A..P=namespace);',
			'LK','Library cache lock instance lock (A..P=namespace);',
			'LL','Library cache lock instance lock (A..P=namespace);',
			'LM','Library cache lock instance lock (A..P=namespace);',
			'LN','Library cache lock instance lock (A..P=namespace);',
			'LO','Library cache lock instance lock (A..P=namespace);',
			'LP','Library cache lock instance lock (A..P=namespace);',
			'MM', 'Mount definition global enqueue lock',
			'MR', 'Media recovery lock',
			'NA', 'Library cache pin instance lock (A..Z=namespace)',
			'NB', 'Library cache pin instance lock (A..Z=namespace)',
			'NC', 'Library cache pin instance lock (A..Z=namespace)',
			'ND', 'Library cache pin instance lock (A..Z=namespace)',
			'NE', 'Library cache pin instance lock (A..Z=namespace)',
			'NF', 'Library cache pin instance lock (A..Z=namespace)',
			'NG', 'Library cache pin instance lock (A..Z=namespace)',
			'NH', 'Library cache pin instance lock (A..Z=namespace)',
			'NI', 'Library cache pin instance lock (A..Z=namespace)',
			'NJ', 'Library cache pin instance lock (A..Z=namespace)',
			'NK', 'Library cache pin instance lock (A..Z=namespace)',
			'NL', 'Library cache pin instance lock (A..Z=namespace)',
			'NM', 'Library cache pin instance lock (A..Z=namespace)',
			'NN', 'Library cache pin instance lock (A..Z=namespace)',
			'NO', 'Library cache pin instance lock (A..Z=namespace)',
			'NP', 'Library cache pin instance lock (A..Z=namespace)',
			'NQ', 'Library cache pin instance lock (A..Z=namespace)',
			'NR', 'Library cache pin instance lock (A..Z=namespace)',
			'NS', 'Library cache pin instance lock (A..Z=namespace)',
			'NT', 'Library cache pin instance lock (A..Z=namespace)',
			'NU', 'Library cache pin instance lock (A..Z=namespace)',
			'NV', 'Library cache pin instance lock (A..Z=namespace)',
			'NW', 'Library cache pin instance lock (A..Z=namespace)',
			'NX', 'Library cache pin instance lock (A..Z=namespace)',
			'NY', 'Library cache pin instance lock (A..Z=namespace)',
			'NZ', 'Library cache pin instance lock (A..Z=namespace)',
			'PF', 'Password File lock',
			'PI', 'Parallel operation locks',
			'PS', 'Parallel operation locks',
			'PR', 'Process startup lock',
			'QA','Row cache instance lock (A..Z=cache)',
			'QB','Row cache instance lock (A..Z=cache)',
			'QC','Row cache instance lock (A..Z=cache)',
			'QD','Row cache instance lock (A..Z=cache)',
			'QE','Row cache instance lock (A..Z=cache)',
			'QF','Row cache instance lock (A..Z=cache)',
			'QG','Row cache instance lock (A..Z=cache)',
			'QH','Row cache instance lock (A..Z=cache)',
			'QI','Row cache instance lock (A..Z=cache)',
			'QJ','Row cache instance lock (A..Z=cache)',
			'QK','Row cache instance lock (A..Z=cache)',
			'QL','Row cache instance lock (A..Z=cache)',
			'QM','Row cache instance lock (A..Z=cache)',
			'QN','Row cache instance lock (A..Z=cache)',
			'QP','Row cache instance lock (A..Z=cache)',
			'QQ','Row cache instance lock (A..Z=cache)',
			'QR','Row cache instance lock (A..Z=cache)',
			'QS','Row cache instance lock (A..Z=cache)',
			'QT','Row cache instance lock (A..Z=cache)',
			'QU','Row cache instance lock (A..Z=cache)',
			'QV','Row cache instance lock (A..Z=cache)',
			'QW','Row cache instance lock (A..Z=cache)',
			'QX','Row cache instance lock (A..Z=cache)',
			'QY','Row cache instance lock (A..Z=cache)',
			'QZ','Row cache instance lock (A..Z=cache)',
			'RT', 'Redo thread global enqueue lock',
			'SC', 'System commit number instance lock',
			'SM', 'SMON lock',
			'SN', 'Sequence number instance lock',
			'SQ', 'Sequence number enqueue lock',
			'SS', 'Sort segment locks',
			'ST', 'Space transaction enqueue lock',
			'SV', 'Sequence number value lock',
			'TA', 'Generic enqueue lock',
			'TS', 'Temporary segment enqueue lock (ID2=0)',
			'TS', 'New block allocation enqueue lock (ID2=1)',
			'TT', 'Temporary table enqueue lock',
			'UN', 'User name lock',
			'US', 'Undo segment DDL lock',
			'WL', 'Being-written redo log instance lock',
			b.type
		 ) lock_type,
		 DECODE
		 (
			b.lmode,
			0, 'None',           /* Mon Lock equivalent */
			1, 'NULL',           /* N */
			2, 'Row-S (SS)',     /* L */
			3, 'Row-X (SX)',     /* R */
			4, 'Share',          /* S */
			5, 'S/Row-X (SRX)',  /* C */
			6, 'Exclusive',      /* X */
			to_char(b.lmode)
		 ) mode_held,
		 DECODE
		 (
			b.request,
			0, 'None',           /* Mon Lock equivalent */
			1, 'NULL',           /* N */
			2, 'Row-S (SS)',     /* L */
			3, 'Row-X (SX)',     /* R */
			4, 'Share',          /* S */
			5, 'S/Row-X (SSX)',  /* C */
			6, 'Exclusive',      /* X */
			to_char(b.request)
		 ) mode_requested
	    FROM v$lock b,
		 v$session c,
		 sys.user$ u,
		 sys.obj$ o,
		 (SELECT * FROM sys.dba_waiters) lock_blocker,
		 (SELECT * FROM sys.dba_waiters) lock_waiter
	   WHERE b.sid = c.sid
	     AND u.user# = c.user#
	     AND o.obj#(+) = b.id1
	     AND lock_blocker.waiting_session(+) = c.sid
	     AND lock_waiter.holding_session(+) = c.sid
	     AND c.username != 'SYS'
	ORDER BY kaddr, lockwait;

CREATE OR REPLACE VIEW dbsessionwait AS
	SELECT sid, event, p1text, p1, wait_time, seconds_in_wait, state
	  FROM v$session_wait;

CREATE OR REPLACE VIEW dbactivetransactions AS
	SELECT username, terminal, osuser,
	       t.start_time, r.name, t.used_ublk "ROLLB BLKS",
	       DECOCE(t.space, 'YES', 'SPACE TX',
	          DECODE(t.recursive, 'YES', 'RECURSIVE TX',
	             DECODE(t.noundo, 'YES', 'NO UNDO TX', t.status)
	       )) status
	 FROM sys.v_$transaction t, sys.v_$rollname r, sys.v_$session s
	WHERE t.xidusn = r.usn
	  AND t.ses_addr = s.saddr;

--
-- TABLESPACES
--
CREATE OR REPLACE VIEW dbtbs AS
	SELECT tablespace_name, initial_extent/1024 "Ini KB", next_extent/1024 "Next KB",
	       min_extents Min, replace(max_extents, 2147483645, 'Unlimited') Max, pct_increase PCT,
	       status, extent_management Management
	  FROM dba_tablespaces;

CREATE OR REPLACE VIEW dbtbsusage AS
	SELECT total.name "Tablespace Name",
	       free_space, (total_space-free_space) used_space, total_space
	FROM
	  (  SELECT tablespace_name, SUM(bytes/1024/1024) free_space
	       FROM sys.dba_free_space
	   GROUP BY tablespace_name
	  ) free,
	  (  SELECT b.name, SUM(bytes/1024/1024) TOTAL_SPACE
	       FROM sys.v_$datafile a, sys.v_$tablespace b
	      WHERE a.ts# = b.ts#
	   GROUP BY b.name
	  ) total
	WHERE free.tablespace_name = total.name;

CREATE OR REPLACE VIEW dbtbscannotextend AS
	SELECT a.owner, a.segment_name, b.tablespace_name,
	       decode(ext.extents,1,b.next_extent,
	       a.bytes*(1+b.pct_increase/100)) nextext,
	       freesp.largest
	  FROM dba_extents a,
	       dba_segments b,
	       (  SELECT owner, segment_name, max(extent_id) extent_id,
	                 count(*) extents
	            FROM dba_extents
	        GROUP BY owner, segment_name
	       ) ext,
	       (  SELECT tablespace_name, max(bytes) largest
	            FROM dba_free_space
	        GROUP BY tablespace_name
	       ) freesp
	 WHERE a.owner = b.owner
	   AND a.segment_name = b.segment_name
	   AND a.owner = ext.owner
	   AND a.segment_name = ext.segment_name
	   AND a.extent_id = ext.extent_id
	   AND b.tablespace_name = freesp.tablespace_name
	   AND DECODE(ext.extents, 1, b.next_extent, a.bytes*(1+b.pct_increase/100)) > freesp.largest;

CREATE OR REPLACE VIEW dbdatafiles AS
	SELECT tablespace_name, file_name, bytes/1048576 MB, autoextensible, status
	  FROM dba_data_files
      ORDER BY tablespace_name; 

CREATE OR REPLACE VIEW dbgrowth AS
	SELECT to_char(creation_time, 'RRRR Month') "Month",
	       sum(bytes)/1024/1024 "Growth in MB"
	  FROM sys.v_$datafile
	 WHERE creation_time > SYSDATE-365
	 GROUP BY to_char(creation_time, 'RRRR Month');

CREATE OR REPLACE VIEW dbspaceusage AS
	SELECT SUM(bytes)/1024/1024 "Total Used",
	       SUM(DECODE(SUBSTR(segment_type,1,5), 'TABLE',     bytes/1024/1024, 0))
	                "Data part",
	       SUM(DECODE(SUBSTR(segment_type,1,5), 'INDEX',     bytes/1024/1024, 0))
	                "Index part",
	       SUM(DECODE(SUBSTR(segment_type,1,3), 'LOB',       bytes/1024/1024, 0))
	                "LOB part",
	       SUM(DECODE(segment_type,             'ROLLBACK',  bytes/1024/1024, 0))
	                "RBS part",
	       SUM(DECODE(segment_type,             'TEMPORARY', bytes/1024/1024, 0))
	                "TEMP part"
	  FROM sys.dba_segments;

CREATE OR REPLACE VIEW dbspacetotal AS
	SELECT SUM(bytes)/1024/1024 "Total DB size in MB"
	  FROM sys.v_$datafile;

CREATE OR REPLACE VIEW dbtablefragmentation AS
	SELECT a.owner,
	       segment_name,
	       segment_type,
	       sum(bytes),
	       max_extents,
	       count(*)
	FROM   dba_extents a, dba_tables b
	WHERE  segment_name = b.table_name
	HAVING count(*) > 3
	GROUP BY a.owner, segment_name, segment_type, max_extents
	ORDER BY a.owner, segment_name, segment_type, max_extents;

CREATE OR REPLACE VIEW dbindexfragmentation AS
	SELECT a.owner,
	       segment_name,
	       segment_type,
	       sum(bytes),
	       max_extents,
	       count(*)
	FROM   dba_extents a, dba_indexes b
	WHERE  segment_name = index_name
	HAVING count(*) > 3
	GROUP BY a.owner, segment_name, segment_type, max_extents
	ORDER BY a.owner, segment_name, segment_type, max_extents;

--
-- USERS / ROLES
--
CREATE OR REPLACE VIEW dbusers AS
	SELECT username, created, default_tablespace, temporary_tablespace, profile
	  FROM dba_users;

CREATE OR REPLACE VIEW dbroles AS
	SELECT grantee, granted_role, admin_option
	  FROM dba_role_privs
	 WHERE grantee NOT IN ('SYS','SYSTEM','DBA','ORDSYS')
      ORDER BY grantee, granted_role;
