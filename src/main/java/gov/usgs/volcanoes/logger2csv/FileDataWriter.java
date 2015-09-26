/*
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.logger2csv;

import gov.usgs.volcanoes.logger2csv.logger.LoggerRecord;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A class to write CSV data to a file.
 *
 * @author Tom Parker
 *
 */
public class FileDataWriter {
  private static final Logger LOGGER = LoggerFactory.getLogger(Logger2csv.class);

  private final CSVFormat csvFormat;
  private final SimpleDateFormat fileNamePattern;
  private final List<CSVRecord> headers;

  /**
   * Constructor.
   * 
   * @param csvFormat CSVFormat of file on disk
   *
   */
  public FileDataWriter(CSVFormat csvFormat, String fileNamePattern) {
    headers = new ArrayList<CSVRecord>();
    this.csvFormat = csvFormat;
    this.fileNamePattern = new SimpleDateFormat(fileNamePattern);
  }


  /**
   * Add line to the headers.
   *
   * @param header header row
   */
  public final void addHeader(CSVRecord header) {
    headers.add(header);
  }

  /**
   * Add a list of rows to the header.
   *
   * @param headerList List of header rows to add
   */
  public final void addHeaders(List<CSVRecord> headerList) {
    headers.addAll(headerList);
  }

  /**
   * Write records to daily CSV files
   *
   * @param records records to write
   * @throws ParseException file pattern cannot be parsed
   * @throws IOException file cannot be accessed
   */
  public final void write(Iterator<LoggerRecord> records) throws ParseException, IOException {
    File workingFile = null;
    CSVPrinter printer = null;

    while (records.hasNext()) {
      final LoggerRecord record = records.next();
      File thisFile = null;
      thisFile = new File(fileNamePattern.format(new Date(record.date)));
      LOGGER.debug("working file: {}", thisFile);

      if (!thisFile.equals(workingFile)) {
        workingFile = thisFile;
        if (printer != null) {
          close(printer);
        }

        try {
          printer = getPrinter(workingFile);
        } catch (final IOException e) {
          close(printer);
          throw e;
        }
      }

      printer.printRecord(record.record);
    }

  }

  private void close(Closeable open) {
    try {
      open.close();
    } catch (final IOException ignore) {
    }
  }


  private CSVPrinter getPrinter(File file) throws IOException {
    CSVPrinter printer;

    if (file.exists()) {
      final FileWriter writer = new FileWriter(file, true);
      printer = new CSVPrinter(writer, csvFormat);
    } else {
      file.getParentFile().mkdirs();
      final FileWriter writer = new FileWriter(file);
      printer = new CSVPrinter(writer, csvFormat);
      printer.printRecords(headers);
    }

    return printer;
  }


}
