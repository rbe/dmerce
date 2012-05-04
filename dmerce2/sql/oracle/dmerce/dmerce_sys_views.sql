CREATE OR REPLACE VIEW v_coldefcolperm
    AS
	SELECT cd.table_name, cd.column_name, cpc.s, cpc.i, cpc.u, cpc.uxsrole
	  FROM coldefs cd, colpermcounts cpc
	 WHERE cd.table_name = cpc.table_name
	   AND cd.column_name = cpc.column_name;
