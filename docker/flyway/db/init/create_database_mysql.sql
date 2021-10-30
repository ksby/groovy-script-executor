create database if not exists testdb character set utf8mb4;

create user 'testdb_user'@'%' identified by 'xxxxxxxx';
grant all privileges ON testdb.* to 'testdb_user'@'%' with grant option;
grant select ON performance_schema.user_variables_by_thread to 'testdb_user'@'%';
flush privileges;
