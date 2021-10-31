create table publications
(
    id                bigserial constraint publications_pk primary key,
    url               text,
    fmt               text,
    book_number       text,
    isbm              text,
    issn              text,
    title_author      text,
    edition_indicates text,
    series            text,
    publication       text,
    remarks           text
);

COPY publications
FROM '/docker-entrypoint-initdb.d/jm2020.csv'
WITH CSV;
