package com.jamesdpeters.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.Main;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.builders.JPLInfo;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import javafx.geometry.Point3D;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class JsonConverter {

    public JsonConverter() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

//        try {
//            String jsonFile = getClass().getResource("Body.json").getFile();
//            UniverseBuilder builder = UniverseBuilder.getInstance().fromFile(new Gson(), new File(jsonFile));
//            System.out.println(builder.getName());
//        } catch (Exception e){e.printStackTrace();}

//        BodyBuilder builder = BodyBuilder.getInstance()
//                .setName("Test")
//                .setInitPos(new Point3D(0,0,0))
//                .setInitVelocity(new Point3D(-1.12*Math.pow(10,1),2.77*Math.pow(10,1),-1.66*Math.pow(10,-3)))
//                .setMass(1.32*Math.pow(10,24))
//                .setRadius(6000);
//
//        String Universe = UniverseBuilder.getInstance()
//                .setName("Universe")
//                .setDt(0.1)
//                .setG(6.67e-11)
//                .addBody(builder)
//                .addBody(builder)
//                .serialise(gson);

        JPLInfo info = new JPLInfo();
        info.setName("Earth");
        info.setFilename("horizon_data/earth.txt");
        info.setMass(123456);
        info.setRadius(54321);

                String Universe = UniverseBuilderJPL.getInstance()
                .setName("Universe")
                .setDt(0.1)
                .setG(6.67e-11)
                        .addBodyInfo(info)
                        .addBodyInfo(info)
                .serialise(gson);

                System.out.println(Universe);

        String file = Main.class.getResource("").getFile();    // Resource Folder
        File f = new File(file+"/BodyTest.json");
        System.out.println(f.getPath());
        FileUtils.writeStringToFile(f,Universe, Charset.defaultCharset());

        UniverseBuilderJPL universeBuilderJPL = UniverseBuilderJPL.getInstance().fromFile(gson,f);
        for(JPLInfo jplinfo : universeBuilderJPL.getJPLInfo()){
            System.out.println(jplinfo.getBodyBuilder().serialise(gson));
        }

//
//        UniverseBuilder universeBuilder = UniverseBuilder.getInstance().fromFile(gson,f);
//        System.out.println(universeBuilder.getName());
    }

    public static void main(String[] args) throws IOException {
        new JsonConverter();
    }
}
