package com.example.straviatec;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GPX {
    private static final String TAG = GPX.class.getName();

    public static String writePath(File file, String name, List<Location> points) throws Exception {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";
        String toRet = new String();
        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Location location : points) {
            segments += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><ele>" + location.getAltitude() + "</ele>" + "\"><time>" + df.format(new Date(location.getTime())) + "</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            FileInputStream out = new FileInputStream(file);
            try {
                out.read(bytes);
            } finally {
                out.close();
            }
            toRet = new String(bytes);

        } catch (IOException e) {
            Log.e("generateGfx", "Error Writting Path", e);
        }
        return toRet;
    }

}