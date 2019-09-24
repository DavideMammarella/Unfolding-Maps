package module3;

//Java utilities libraries

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.CartoDB;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 *
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Davide Mammarella
 *
 * */
public class EarthquakeCityMap extends PApplet {

    // You can ignore this.  It's to keep eclipse from generating a warning.
    private static final long serialVersionUID = 1L;

    // IF YOU ARE WORKING OFFLINE, change the value of this variable to true
    private static final boolean offline = false;

    // Less than this threshold is a light earthquake
    public static final float THRESHOLD_MODERATE = 5;
    // Less than this threshold is a minor earthquake
    public static final float THRESHOLD_LIGHT = 4;

    /** This is where to find the local tiles, for working without an Internet connection */
    public static String mbTilesString = "blankLight-1-3.mbtiles";

    // The map
    private UnfoldingMap map;

    //feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";


    public void setup() {
        size(950, 600, OPENGL);

        if (offline) {
            map = new UnfoldingMap(this, 180, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
            earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
        }
        else {
            map = new UnfoldingMap(this, 180, 50, 700, 500, new CartoDB.Positron());
            // IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
            //earthquakesURL = "2.5_week.atom";
        }

        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map);

        // The List you will populate with new SimplePointMarkers
        List<Marker> markers = new ArrayList<Marker>();

        //Use provided parser to collect properties for each earthquake
        //PointFeatures have a getLocation method
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);

        // Loop that calls createMarker (see below)
        // to create a new SimplePointMarker for each PointFeature in
        // earthquakes.  Then add each new SimplePointMarker to the
        // List markers (so that it will be added to the map in the line below)
        for (PointFeature feature: earthquakes) {
            Marker marker = createMarker(feature);
            markers.add(marker);
        }

        // Add the markers to the map so that they are displayed
        map.addMarkers(markers);
    }

    /**
     * createMarker: A suggested helper method that takes in an earthquake
     * feature and returns a SimplePointMarker for that earthquake
     *
     * This method adds the proper styling to each marker
     * based on the magnitude of the earthquake.
     */
    private SimplePointMarker createMarker(PointFeature feature)
    {
        int color = color(219, 22, 47, 204);
        int radius = 13;

        Object magObj = feature.getProperty("magnitude");
        float mag = Float.parseFloat(magObj.toString());

        if (mag < THRESHOLD_LIGHT) {
            color = color(95, 117, 142, 204);
            radius = 5;


        } else if (mag < THRESHOLD_MODERATE) {
            color = color(230, 242, 92, 204);
            radius = 10;
        }

        // To print all of the features in a PointFeature (so you can see what they are)
        // uncomment the line below.  Note this will only print if you call createMarker
        // from setup
        //System.out.println(feature.getProperties());

        // Create a new SimplePointMarker at the location given by the PointFeature
        SimplePointMarker marker = new SimplePointMarker(feature.getLocation());

        // according to the magnitude of the earthquake.
        // Don't forget about the constants THRESHOLD_MODERATE and
        // THRESHOLD_LIGHT, which are declared above.
        // Rather than comparing the magnitude to a number directly, compare
        // the magnitude to these variables (and change their value in the code
        // above if you want to change what you mean by "moderate" and "light")

        marker.setRadius(radius);
        marker.setColor(color);

        // Finally return the marker
        return marker;
    }

    public void draw() {
        background(43,43,43);
        map.draw();
        addKey();
    }


    // helper method to draw key in GUI using Processing's graphics methods
    private void addKey()
    {
        // Key canvas
        fill(color(250, 250, 240));
        float keyX = 65, keyY = 120;
        float keyWidth = 180, keyHeight = 250;
        rect(keyX, keyY, keyWidth, keyHeight);

        // Key title
        fill(0);
        textSize(16);
        text("Earthquake Key", 85, 160);

        // Legend for red markers
        fill(219, 22, 47, 204);
        ellipse(100, 217, 20, 20);
        textSize(12);
        text("5.0+ Magnitude", 125, 220);

        // Legend for yellow markers
        fill(219, 223, 172, 204);
        ellipse(100, 266, 15, 15);
        textSize(12);
        text("4.0+ Magnitude", 125, 270);

        // Legend for brown markers
        fill(95, 117, 142, 204);
        ellipse(100, 315, 10, 10);
        textSize(12);
        text("< 4.0 Magnitude", 125, 320);
    }

}