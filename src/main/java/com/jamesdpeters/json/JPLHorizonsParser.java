package com.jamesdpeters.json;

import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.helpers.CONSTANTS;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.vectors.Vector3D;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.DAYS;


public class JPLHorizonsParser {

    public static void main(String[] args) throws IOException, URISyntaxException {
        parse("horizon_data/earth.txt", "Earth");
    }

    public static BodyBuilder parse(String filename, String bodyName){
        try {
            URI pathname = Utils.getResource(filename);
            Path path = Paths.get(pathname);
            String content = Files.readString(path, StandardCharsets.US_ASCII);
            String csvString = StringUtils.substringBetween(content, "$$SOE", "$$EOE");

            CSVParser parser = CSVParser.parse(csvString, CSVFormat.DEFAULT);
            List<CSVRecord> records = parser.getRecords();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MMM-dd HH:mm:ss.SSSS", Locale.ENGLISH);

            // Initial values.
            CSVRecord initial = records.get(0);
            Vector3D initialPos = new Vector3D(toDouble(initial,JPLHeader.X),toDouble(initial,JPLHeader.Y),toDouble(initial,JPLHeader.Z));
            Vector3D initialVelocity = new Vector3D(toDouble(initial,JPLHeader.VX),toDouble(initial,JPLHeader.VY),toDouble(initial,JPLHeader.VZ));
            LocalDateTime initDateTime = LocalDateTime.parse(initial.get(JPLHeader.DATE.index).replace(" A.D. ", ""), formatter);

            TreeMap<Double, Vector3D> positions = new TreeMap<>();
            TreeMap<Double, Vector3D> velocities = new TreeMap<>();

            for (CSVRecord record : records) {
                LocalDateTime dateTime = LocalDateTime.parse(record.get(JPLHeader.DATE.index).replace(" A.D. ", ""), formatter);
                Duration duration = Duration.between(initDateTime,dateTime);
                double day = duration.getSeconds()/CONSTANTS.SECONDS.DAY;
                Vector3D Pos = new Vector3D(toDouble(record,JPLHeader.X),toDouble(record,JPLHeader.Y),toDouble(record,JPLHeader.Z));
                Vector3D Velocity = new Vector3D(toDouble(record,JPLHeader.VX),toDouble(record,JPLHeader.VY),toDouble(record,JPLHeader.VZ));
                positions.put(day,Pos);
                velocities.put(day,Velocity);
            }

            return BodyBuilder.getInstance()
                    .setInitPos(initialPos)
                    .setInitVelocity(initialVelocity)
                    .setName(bodyName)
                    .setPositions(positions)
                    .setVelocities(velocities);
        } catch (Exception e){
            e.printStackTrace();
        }
        return BodyBuilder.getInstance();
    }

    private enum JPLHeader {
        JDTDB(0),
        DATE(1),
        X(2),
        Y(3),
        Z(4),
        VX(5),
        VY(6),
        VZ(7);

        public final int index;

        private JPLHeader(int index) {
            this.index = index;
        }
    }

    private static double toDouble(CSVRecord record, JPLHeader header){
        return Double.parseDouble(record.get(header.index));
    }
}
