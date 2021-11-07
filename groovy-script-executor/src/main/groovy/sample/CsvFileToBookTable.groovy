package sample

import com.univocity.parsers.annotations.Parsed
import com.univocity.parsers.common.processor.BeanListProcessor
import com.univocity.parsers.csv.CsvParserSettings
import com.univocity.parsers.csv.CsvRoutines
import groovy.sql.Sql

class CsvFileToBookTable {

    static class CsvRecord {
        @Parsed(index = 0, field = "isbm")
        String isbm
        @Parsed(index = 1, field = "title_author")
        String title_author
    }

    static void main(args) {
        def sql = Sql.newInstance("jdbc:mysql://localhost:3306/testdb?sslMode=DISABLED&characterEncoding=utf8",
                "testdb_user",
                "xxxxxxxx",
                "org.mariadb.jdbc.Driver")
        sql.connection.autoCommit = false

        CsvParserSettings settings = new CsvParserSettings()
        settings.format.lineSeparator = "\r\n"
        settings.headerExtractionEnabled = true
        BeanListProcessor<CsvRecord> rowProcessor = new BeanListProcessor<>(CsvRecord)
        settings.processor = rowProcessor

        sql.execute("truncate table book")

        new File("publications.csv").withReader { reader ->
            CsvRoutines csvRoutines = new CsvRoutines(settings)
            for (CsvRecord csvRecord : csvRoutines.iterate(CsvRecord, reader)) {
                String[] titleAndAuthor = csvRecord.title_author.split(" / ")
                sql.execute("""
                                insert into book (isbm, title, author)
                                values (:isbm, :title, :author)
                            """,
                        isbm: csvRecord.isbm,
                        title: titleAndAuthor[0],
                        author: titleAndAuthor.size() == 2 ? titleAndAuthor[1] : null)
            }
        }

        sql.commit()
        sql.close()
    }

}
