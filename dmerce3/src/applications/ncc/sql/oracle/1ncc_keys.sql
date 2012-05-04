/*
 * Primary keys
 *
 */
alter table t_addrcomtype add constraint t_addrcomtype_pk primary key (id);
alter table t_dnsns add constraint t_dnsns_pk primary key (id);
alter table t_dnsrrtypes add constraint t_dnsrrtypes_pk primary key (id);
alter table t_dnszones add constraint t_dnszones_pk primary key (id);
alter table t_dnszonesns add constraint t_dnszonesns_pk primary key (id);
alter table t_dnsrecords add constraint t_dnsrecords_pk primary key (id);
alter table t_dnshandles add constraint t_dnshandles_pk primary key (id);
alter table t_lpcomm add constraint t_lpcomm_pk primary key (id);
alter table t_lptypes add constraint t_lptypes_pk primary key (id);
alter table t_profile add constraint t_profile_pk primary key (id);
alter table t_dnslistenon add constraint t_dnslistenon_pk primary key (id);
alter table t_dnsforwarders add constraint t_dnsforwarders_pk primary key (id);
alter table t_legalperson add constraint t_legalperson_pk primary key (id);

/*
 * Foreign Keys
 *
 */
alter table t_dnslistenon add constraint t_dnslistenon_fk_t_dnsns foreign key (dnsnsid) references t_dnsns (id);
alter table t_dnsforwarders add constraint t_dnsforwarders_fk_t_dnsns foreign key (dnsnsid) references t_dnsns (id);
alter table t_dnszonesns add constraint t_dnszonesns_fk_t_dnsns foreign key (dnsnsid) references t_dnsns (id);
alter table t_dnszonesns add constraint t_dnszonesns_fk_t_dnszones foreign key (dnszonesid) references t_dnszones (id);
alter table t_dnshandles add constraint t_dnshandles_fk_t_dnsns foreign key (dnsnsid) references t_dnsns (id);
alter table t_dnsrecords add constraint t_dnsrecords_fk_t_dnsrrtypes foreign key (type) references t_dnsrrtypes (id);
alter table t_dnsrecords add constraint t_dnsrecords_fk_t_dnszones foreign key (dnszoneid) references t_dnszones (id);
alter table t_lpcomm add constraint t_lpcomm_fk_t_addrcommtype foreign key (addrcommtypeid) references t_addrcomtype (id);
alter table t_legalperson add constraint t_legalperson_fk_t_profile foreign key (profileid) references t_profile (id);
alter table t_legalperson add constraint t_legalperson_fk_t_lptypes foreign key (lptypeid) references t_lptypes (id);
