package tourbears.tourbearsmap;

import com.google.android.gms.maps.model.Polygon;

/**
 * Created by drealiu on 6/25/16.
 */
public class Place {

    public Polygon polygon;
    public String description;
    public String title;
    public final String layoutID = "place_dialogue";
    public String imgResource;

    public Place(Polygon polygon, String title, String description, String imgResource) {
        this.polygon = polygon;
        this.description = description;
        this.title = title;
        this.imgResource = imgResource;
        //this.layoutID = layoutID;
    }



}
