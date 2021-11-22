drop schema read CASCADE;
drop schema write CASCADE;
drop schema public CASCADE;

create schema read;
create schema write;
create schema public;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

delete from write.service;
delete from read.service;
delete from read.connection;
delete from read.statistic;

Drop table read.statistic;
delete from write.databasechangelog where orderexecuted = 8
delete from read.statistic
create table read.statistic (
id    integer PRIMARY KEY
);
