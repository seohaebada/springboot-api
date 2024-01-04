postgres=# create database sampleapidb;
CREATE DATABASE
postgres=# create user sampleapi with encrypted password '1234';
CREATE ROLE
postgres=# grant all privileges on database sampleapidb to sampleapi;
GRANT