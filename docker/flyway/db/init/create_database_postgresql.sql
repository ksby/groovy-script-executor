CREATE USER sampledb_user PASSWORD 'xxxxxxxx';
GRANT pg_read_server_files TO sampledb_user;
CREATE DATABASE sampledb OWNER sampledb_user ENCODING 'UTF8' LC_COLLATE 'ja_JP.UTF-8' LC_CTYPE 'ja_JP.UTF-8';
