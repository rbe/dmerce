create unique index i_addrcomtype_name on t_addrcomtype (name asc);

create unique index i_dnsns_name on t_dnsns (name asc);
--create unique index i_dnsns_ipaddress on t_dnsns (ipaddress asc);

create unique index i_dnszones_name on t_dnszones (name asc);

create unique index i_dnszonesns_uq on t_dnszonesns (position asc, dnszonesid asc, dnsnsid asc);

--create unique index i_dnsrecords_uq on t_dnsrecords (dnszoneid asc, type asc, value asc);

create index i_lpcomm_legalpersonid on t_lpcomm (legalpersonid asc);
create index i_lpcomm_addrcommtypeid on t_lpcomm (addrcommtypeid asc);

create unique index i_lptypes_uq on t_lptypes (lptype asc);

create unique index i_legalperson_login on t_legalperson (login asc);