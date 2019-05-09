/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package mapgen;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.OffsetComparator;
import utils.MapsUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class AIW2019_P3_G10 {

    public static void main(String[] args) {

        try {
            Gate.init();
            String encoding = "UTF-8";
            File inDir = new File("./data/analysed/newyork");
            File[] flist = inDir.listFiles();
            String floc;

            Document d;

            Annotation tweet;
            AnnotationSet tweets;
            FeatureMap fm;

            Map<Object, Object> mp;
            Map<Object, Object> mp_usr;
            ArrayList<Double> coordinates;
            Double lati, longi;
            String usrName;
            String id;
            String creation;
            String text;
            String app;
            int num_locs = 0;
            int num_orgs = 0;
            int num_urls = 0;
            int num_pers = 0;
            String newTextHeat;
            String newTextCircle;
            String color;
            String posColor = "#008000";
            String negColor = "#FF0000";
            String neuColor = "#0000FF";
            String textToWriteHeat = "";
            String textToWriteCircle = "";
            String sentiLabel;
            for (int f = 0; f < flist.length; f++) {
                newTextHeat = "";
                newTextCircle = "";
                floc = flist[f].getAbsolutePath();
                System.out.println(floc);
                d = Factory.newDocument(new URL("file:///" + floc), encoding);
                tweets = d.getAnnotations("Original markups").get("Tweet");
                tweet = tweets.iterator().next();
                fm = tweet.getFeatures();
                mp = (Map<Object, Object>) fm.get("geo");
                mp_usr = (Map<Object, Object>) fm.get("user");
                id = (String) fm.get("id");
                creation = (String) fm.get("created_at");
                usrName = (String) mp_usr.get("screen_name");
                app = (String) fm.get("source");

                // TO COMPLETE: extract app used to tweet!!!


                text = d.getContent().toString();
                if (mp != null) {
                    coordinates = (ArrayList<Double>) mp.get("coordinates");
                    lati = coordinates.get(0);
                    longi = coordinates.get(1);

                    num_locs = getNumLocs(d);
                    num_orgs = getNumOrgs(d);
                    num_urls = getNumLinks(d);
                    num_pers = getNumPeople(d);
                    sentiLabel = getSentiment(d);

                    //---- HEAT MAP ----
                    newTextHeat = "new google.maps.LatLng(" + lati + "," + longi + "),";

                    //---- CRCL MAP ----

                    String newtext = "";
                    text = text.replace("\n", " ").replace("'", " ");
                    for (String token : text.split(" ")) {
                        if (token.contains("http")) {
                            newtext += "<a href=\"" + token + "\" target=\"_blank\"> link </a>";
                        } else newtext += token + " ";
                    }


                    if (sentiLabel.equals("POS")) {
                        color = posColor;
                    } else if (sentiLabel.equals("NEG")) {
                        color = negColor;
                    } else {
                        color = neuColor;
                    }

                    newTextCircle = "  id" + id + ": {center: {lat: " + lati + ", lng: " + longi + "}," +

                            "color: '" + color + "'," +
                            "user: '" + usrName + " " + id + " ·+'," +
                            "device: '" + app + "'," +
                            "time: '" + creation + "'," +
                            "locations: '" + num_locs + "'," +
                            "orgs: '" + num_orgs + "'," +
                            "people: '" + num_pers + "'," +
                            "links: '" + num_urls + "'," +
                            "sentiment: '" + sentiLabel + "'," +
                            "text: '" + newtext + "'," +
                            "},";

                }
                textToWriteHeat = textToWriteHeat + newTextHeat + "\n";
                textToWriteCircle = textToWriteCircle + newTextCircle + "\n";
            }

            String fs = System.getProperty("file.separator");
            String inputFileHeat = "maps" + fs + "heat-map-nyc.html";
            String inputFileCircle = "maps" + fs + "circle-map-nyc.html";


            //other if here to decide which kind of map to create
            MapsUtils.createNewMap(inputFileHeat, textToWriteHeat);
            MapsUtils.createNewMap(inputFileCircle, textToWriteCircle);

        } catch (ResourceInstantiationException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (GateException ex) {
            ex.printStackTrace();
        }
    }

    public static int getNumLocs(Document doc) {
        return doc.getAnnotations().get("Location").size();
    }

    public static int getNumOrgs(Document doc) {
        return doc.getAnnotations().get("Organization").size();
    }

    public static int getNumPeople(Document doc) {
        return doc.getAnnotations().get("Person").size();
    }

    public static int getNumLinks(Document doc) {
        return doc.getAnnotations().get("URL").size();
    }

    public static String getSentiment(Document doc) {
        int sumPos = 0;
        int sumNeg = 0;

        AnnotationSet tweets = doc.getAnnotations("Original markups").get("Tweet");
        Annotation tweet = tweets.iterator().next();
        FeatureMap fm = tweet.getFeatures();

        String tweetLang = (String) fm.get("lang");
        if (tweetLang.equals("en")) {
            tweetLang = "english";
        } else if (tweetLang.equals("es")) {
            tweetLang = "spanish";
        }

        AnnotationSet all = doc.getAnnotations();
        AnnotationSet myAnnotations = all.get("Senti");
        Iterator ite = myAnnotations.iterator();

        while (ite.hasNext()) {
            Annotation ann = (Annotation) ite.next();
            fm = ann.getFeatures();
            String value = (String) fm.get("majorType");
            String sentiLang = (String) fm.get("language");
            if (tweetLang.equals(sentiLang)) {
                if (value.equals("NEGATIVE")) {
                    sumNeg++;
                } else {
                    sumPos++;
                }
            }

        }
        if (sumPos > sumNeg) {
            return "POS";
        } else if (sumNeg > sumPos) {
            return "NEG";
        } else {
            return "NEU";
        }
    }
}