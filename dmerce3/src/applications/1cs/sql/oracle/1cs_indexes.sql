create unique index uq_t_adverts on t_adverts (realname, email, url, description, text);
create unique index uq_t_advertcategories on t_advertcategories (name);
-- create unique index uq_t_advertpics on t_adverpics ();
create unique index uq_t_appointments on t_appointments (description);
create unique index uq_t_guestbook on t_guestbook (realname, email, url, text);
create unique index uq_t_links on t_links (url);
create unique index uq_t_linkcategories on t_linkcategories (name);