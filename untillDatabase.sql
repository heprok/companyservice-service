drop schema read CASCADE;
drop schema write CASCADE;

create schema read;
create schema write;

drop schema public CASCADE;
create schema public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

delete from write.service;
delete from read.service;
