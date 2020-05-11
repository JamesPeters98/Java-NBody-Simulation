package com.jamesdpeters.json;

import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.eclipse.EclipseInfo;
import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.vectors.Vector3D;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class KaggleCSVParser {

    public static HashMap<Long, EclipseInfo> parse(String filename){
        HashMap<Long,EclipseInfo> eclipseInfoList = new HashMap<>();
        try {
            URI pathname = Utils.getResource(filename);
            Path path = Paths.get(pathname);
            String content = Files.readString(path, StandardCharsets.US_ASCII);

            CSVParser parser = CSVParser.parse(content, CSVFormat.DEFAULT.withHeader());
            List<CSVRecord> records = parser.getRecords();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("u MMMM d HH:mm:ss", Locale.ENGLISH);

            for (CSVRecord record : records) {
                String dateTimeString = record.get(EclipseHeader.DATE.index)+" "+record.get(EclipseHeader.TIME.index);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                EclipseInfo info = new EclipseInfo();
                info.setMidpoint(dateTime);
                long catalog = record.getRecordNumber()-1;
                eclipseInfoList.put(catalog,info);
            }
        } catch (Exception e){ e.printStackTrace(); }

        return eclipseInfoList;
    }

    private enum EclipseHeader {
        CATALOG(0),
        DATE(1),
        TIME(2);

        public final int index;

        private EclipseHeader(int index) {
            this.index = index;
        }
    }

    private static double toDouble(CSVRecord record, EclipseHeader header){
        return Double.parseDouble(record.get(header.index));
    }
}
