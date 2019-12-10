package com.jamesdpeters.json;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.helpers.Utils;
import javafx.geometry.Point3D;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-d H:m:s.A", Locale.ENGLISH);

            // Initial values.
            CSVRecord initial = records.get(0);
            Point3D initialPos = new Point3D(toDouble(initial,JPLHeader.X),toDouble(initial,JPLHeader.Y),toDouble(initial,JPLHeader.Z));
            Point3D initialVelocity = new Point3D(toDouble(initial,JPLHeader.VX),toDouble(initial,JPLHeader.VY),toDouble(initial,JPLHeader.VZ));
            LocalDate initDate = LocalDate.parse(initial.get(JPLHeader.DATE.index).replace(" A.D. ", ""), formatter);

            HashMap<Long, Point3D> positions = new HashMap<>();
            HashMap<Long, Point3D> velocities = new HashMap<>();

            for (CSVRecord record : records) {
                LocalDate date = LocalDate.parse(record.get(JPLHeader.DATE.index).replace(" A.D. ", ""), formatter);
                long day = DAYS.between(initDate,date);
                Point3D Pos = new Point3D(toDouble(record,JPLHeader.X),toDouble(record,JPLHeader.Y),toDouble(record,JPLHeader.Z));
                Point3D Velocity = new Point3D(toDouble(record,JPLHeader.VX),toDouble(record,JPLHeader.VY),toDouble(record,JPLHeader.VZ));
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
