package com.andrealiu.maptest;

import com.google.android.gms.maps.model.Polygon;

/**
 * Created by drealiu on 6/25/16.
 */
public class Place {

    public Polygon polygon;
    public String description;
    public String layoutID;

    public Place(Polygon polygon, String description, String layoutID) {
        this.polygon = polygon;
        this.description = description;
        this.layoutID = layoutID;
    }



}
