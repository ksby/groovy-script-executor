package ksby.cmdapp.groovyscriptexecutor.script

import com.univocity.parsers.annotations.Parsed
import com.univocity.parsers.common.processor.BeanListProcessor
import com.univocity.parsers.csv.CsvParserSettings
import com.univocity.parsers.csv.CsvRoutines
import groovy.sql.Sql
import groovy.util.logging.Slf4j

@Slf4j
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
        log.info("bookテーブルをtruncateしました。")

        new File("publications.csv").withReader { reader ->
            CsvRoutines csvRoutines = new CsvRoutines(settings)
            for (CsvRecord csvRecord : csvRoutines.iterate(CsvRecord, reader)) {
                String[] titleAndAuthor = csvRecord.title_author.split(" / ")
                def title = titleAndAuthor[0]
                def author = null
                if (titleAndAuthor.size() == 1) {
                    log.warn("title_authorカラムにはauthorが記載されていません。")
                } else {
                    author = titleAndAuthor[1]
                }

                sql.execute("""
                                insert into book (isbm, title, author)
                                values (:isbm, :title, :author)
                            """,
                        isbm: csvRecord.isbm,
                        title: title,
                        author: author)
                log.info("bookテーブルに登録しました (isbm = {}, title = {}, author = {})",
                        csvRecord.isbm, title, author)
            }
        }

        sql.commit()
        sql.close()
    }

}
