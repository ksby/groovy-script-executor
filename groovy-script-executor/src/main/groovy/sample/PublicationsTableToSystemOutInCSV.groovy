package sample

import groovy.sql.Sql

class PublicationsTableToSystemOutInCSV {

    static void main(args) {
        def sql = Sql.newInstance("jdbc:postgresql://localhost:5432/sampledb",
                "sampledb_user",
                "xxxxxxxx",
                "org.postgresql.Driver")

        try (Writer writer = System.out.newWriter("windows-31j")) {
            def rows = sql.rows("""\
                select coalesce(isbm, ''),
                       title_author
                from publications
            """)
            rows.each { row ->
                writer.println(row.values().join(","))
            }
        }

        sql.close()
    }

}
