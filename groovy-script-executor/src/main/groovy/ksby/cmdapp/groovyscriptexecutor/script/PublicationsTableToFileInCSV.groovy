package ksby.cmdapp.groovyscriptexecutor.script

import groovy.sql.Sql

class PublicationsTableToFileInCSV {

    static void main(String[] args) {
        def sql = Sql.newInstance("jdbc:postgresql://localhost:5432/sampledb",
                "sampledb_user",
                "xxxxxxxx",
                "org.postgresql.Driver")

        new File("publications.csv").withWriter { writer ->
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
