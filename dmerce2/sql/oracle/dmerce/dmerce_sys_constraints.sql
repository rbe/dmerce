ALTER TABLE configuration ADD (CONSTRAINT c_configuration_pk PRIMARY KEY (fqhn));
ALTER TABLE samsessions ADD (CONSTRAINT c_samsessions_pk PRIMARY KEY (sessionid));
ALTER TABLE log ADD (CONSTRAINT c_log_pk PRIMARY KEY (id));
ALTER TABLE availprojects ADD (CONSTRAINT c_availprojects_pk PRIMARY KEY (id));
ALTER TABLE manageprojects ADD (CONSTRAINT c_manageprojects_pk PRIMARY KEY (id));
ALTER TABLE projectnames ADD (CONSTRAINT c_projectnames_pk PRIMARY KEY (id))
ALTER TABLE projectnames ADD (CONSTRAINT c_projectnames_uq UNIQUE (projectname));
ALTER TABLE ippool ADD (CONSTRAINT c_ippool_pk PRIMARY KEY (id));
ALTER TABLE ippool ADD (CONSTRAINT c_ippool_uq UNIQUE (net));
ALTER TABLE mclg ADD (CONSTRAINT c_mclg_pk PRIMARY KEY (id));
ALTER TABLE mclg ADD (CONSTRAINT c_mclg_uq UNIQUE (stage));
ALTER TABLE uxsgroups ADD (CONSTRAINT c_uxsgroups_pk PRIMARY KEY (id));
