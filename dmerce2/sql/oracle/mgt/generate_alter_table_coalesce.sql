-- #############################################################################################
--
-- %Purpose: Generate Script to coalesce free Space in cluttered Tablespaces
--
-- #############################################################################################
--
SELECT  a1.tablespace_name,COUNT(*) nbr_cont_wholes
    FROM    sys.dba_free_space a1, sys.dba_free_space a2
    WHERE   a1.tablespace_name=a2.tablespace_name
    AND     a1.block_id+a1.blocks = a2.block_id
    GROUP BY A1.tablespace_name
/
set heading off
spool alter_ts_coal.sql
SELECT 'ALTER TABLESPACE '||a1.tablespace_name||' COALESCE;'
    FROM    sys.dba_free_space a1, sys.dba_free_space a2
    WHERE   a1.tablespace_name=a2.tablespace_name
    AND     a1.block_id+a1.blocks = a2.block_id
    GROUP BY A1.tablespace_name
/
SPOOL OFF
set heading on
@alter_ts_coal.sql
