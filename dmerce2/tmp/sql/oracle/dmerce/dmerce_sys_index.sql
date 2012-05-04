CONNECT dmerce_sys/dmerce_sys;
CREATE INDEX i_templates ON templates (fqhn, fqtn);
--ALTER INDEX i_templates INITRANS 4 STORAGE (NEXT 8192 PCTINCREASE 0);
CREATE INDEX i_tabperms ON tabperms (table_schema, table_name);
--ALTER INDEX i_tabperms INITRANS 4 STORAGE (NEXT 8192 PCTINCREASE 0);
CREATE INDEX i_colperms ON colperms (table_schema, table_name);
--ALTER INDEX i_colperms INITRANS 4 STORAGE (NEXT 8192 PCTINCREASE 0);
