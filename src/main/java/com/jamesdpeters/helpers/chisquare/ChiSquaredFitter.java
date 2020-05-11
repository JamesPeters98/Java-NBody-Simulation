package com.jamesdpeters.helpers.chisquare;

import com.jamesdpeters.eclipse.TransitInfo;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.sun.source.tree.Tree;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChiSquaredFitter {

    TreeMap<Double, Value> experimentalData;
    double TDBoffset = 0;

    public void load(String experimentalDataPath, double TDBoffset){
        this.TDBoffset = TDBoffset;
        try {
            URI pathname = Utils.getResource(experimentalDataPath);
            Path path = Paths.get(pathname);
            String content = Files.readString(path, StandardCharsets.US_ASCII);
            CSVParser parser = CSVParser.parse(content, CSVFormat.DEFAULT);
            List<CSVRecord> records = parser.getRecords();
            records.remove(0);

            experimentalData = new TreeMap<>();
            records.forEach(record -> {
                double time = toDouble(record,Header.JDTDB)-TDBoffset;
                if(time >= 0) {
                    Value value = getValue(record);
                    experimentalData.put(time, value);
                }
            });


        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public double chiSquare(TreeMap<Double,Double> modelData){
        double chi2 = 0;
        double maxKey = modelData.keySet().stream().max(Double::compareTo).get();
        double minKey = modelData.keySet().stream().min(Double::compareTo).get();
        int vals = 0;
        Map<Double,Value> filteredData = experimentalData.entrySet().stream().filter(entry -> (entry.getKey() >= minKey && entry.getKey() <= maxKey)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for(Map.Entry<Double,Value> entry : filteredData.entrySet()){
            double time = entry.getKey();
            if(time > maxKey){
                break;
            }
            vals++;
            Value value = entry.getValue();
            Map.Entry<Double,Double> low = modelData.floorEntry(time);
            Map.Entry<Double,Double> high = modelData.ceilingEntry(time);
            double modelVal = 0;
            if (low != null && high != null) {
                modelVal = Math.abs(time-low.getKey()) < Math.abs(time-high.getKey())
                        ?   low.getValue()
                        :   high.getValue();
            } else if (low != null || high != null) {
                modelVal = low != null ? low.getValue() : high.getValue();
            }
            double chi = Math.pow(value.value-modelVal,2)/Math.pow(value.error,2);
            chi2 += chi;
        }
        return chi2/(vals-1);
    }

    public void outputData(String folderpath, TreeMap<Double,Double> modelData){
        TreeMap<Double,ChiSquareValue> values = new TreeMap<>();
        experimentalData.forEach((time, observation) -> {
            Map.Entry<Double,Double> low = modelData.floorEntry(time);
            Map.Entry<Double,Double> high = modelData.ceilingEntry(time);
            double modelVal = 0;
            if (low != null && high != null) {
                modelVal = Math.abs(time-low.getKey()) < Math.abs(time-high.getKey())
                        ?   low.getValue()
                        :   high.getValue();
            } else if (low != null || high != null) {
                modelVal = low != null ? low.getValue() : high.getValue();
            }
            values.put(time,new ChiSquareValue(modelVal,observation.value,observation.error));
        });
        CSVWriter.writeChi2Data(folderpath,values);
    }

    public void plot(TransitInfo transitInfo){
        Graph.plotEclipseWithObservations("Trappist Observations",transitInfo,experimentalData);
    }


    private enum Header {
        JDTDB(0),
        FLUX(1),
        ERROR(2);

        public final int index;

        private Header(int index) {
            this.index = index;
        }
    }

    private static double toDouble(CSVRecord record, Header header){
        return Double.parseDouble(record.get(header.index));
    }

    private static Value getValue(CSVRecord record){
        double val = toDouble(record, Header.FLUX);
        double error = toDouble(record, Header.ERROR);
        return new Value(val,error);
    }
}
