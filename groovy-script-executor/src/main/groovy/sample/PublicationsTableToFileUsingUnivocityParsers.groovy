package sample

import com.univocity.parsers.annotations.Parsed
import com.univocity.parsers.common.processor.BeanWriterProcessor
import com.univocity.parsers.csv.CsvWriter
import com.univocity.parsers.csv.CsvWriterSettings
import groovy.sql.Sql

class PublicationsTableToFileUsingUnivocityParsers {

    static class CsvRecord {
        @Parsed(index = 0, field = "isbm")
        String isbm
        @Parsed(index = 1, field = "title_author")
        String title_author
    }

    static void main(args) {
        def sql = Sql.newInstance("jdbc:postgresql://localhost:5432/sampledb",
                "sampledb_user",
                "xxxxxxxx",
                "org.postgresql.Driver")

        CsvWriterSettings settings = new CsvWriterSettings()
        settings.setQuoteAllFields(true)
        BeanWriterProcessor<CsvRecord> writerProcessor = new BeanWriterProcessor<>(CsvRecord)
        settings.setRowWriterProcessor(writerProcessor)
        CsvWriter writer = new CsvWriter(new File("publications.csv"), settings)

        writer.writeHeaders()

        CsvRecord publications = new CsvRecord()
        sql.eachRow("select * from publications") { row ->
            publications.isbm = row.isbm
            publications.title_author = row.title_author
            writer.processRecord(publications)
        }

        writer.close()
        sql.close()
    }

}
