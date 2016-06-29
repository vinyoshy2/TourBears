package com.andrealiu.maptest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng statue = new LatLng(37.873046331455676 , -122.26484343409538);
        mMap.addMarker(new MarkerOptions().position(statue).title("Li Ka Shing Statue"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(statue));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.873046331455676, -122.26484343409538), 18));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.87514292161614, -122.25852448493242), 18));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions().position( latLng );
                options.title(latLng.latitude + ", " + latLng.longitude);

                options.icon( BitmapDescriptorFactory.defaultMarker() );
                mMap.addMarker(options);
            }
        });

        ArrayList<Polygon> places = new ArrayList<Polygon>();

        Polygon LKSpolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.872539774466794, -122.26504124701023),
                        new LatLng(37.87252283624188, -122.2653030976653),
                        new LatLng(37.872801257819695, -122.26532623171806),
                        new LatLng(37.87277293932327, -122.26570978760721),
                        new LatLng(37.87284704377689, -122.26572453975679),
                        new LatLng(37.87284704377689, -122.2658194220068),
                        new LatLng(37.87307200326887, -122.2658321633935),
                        new LatLng(37.87309741041538, -122.26538121700287),
                        new LatLng(37.8734480810737, -122.26543016731738),
                        new LatLng(37.87343961206614, -122.26523134857416),
                        new LatLng(37.873386416090206, -122.26520821452141),
                        new LatLng(37.87339117991053, -122.26515322923659))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(LKSpolygon);

        Polygon moffitPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.8726869251318, -122.26111315190792),
                        new LatLng(37.87264352100939, -122.2612791135907),
                        new LatLng(37.8722579113116, -122.26113930344582),
                        new LatLng(37.87227167366869, -122.2611128166318),
                        new LatLng(37.87223912039578, -122.26109873503448),
                        new LatLng(37.87231798927608, -122.2608258202672),
                        new LatLng(37.872346837268246, -122.26082514971495),
                        new LatLng(37.87240717982086, -122.2605860978365),
                        new LatLng(37.8723772731104, -122.26057671010496),
                        new LatLng(37.87240373923805, -122.26045299321413),
                        new LatLng(37.87241591360728, -122.26046338677405),
                        new LatLng(37.87243311651689, -122.26041108369829),
                        new LatLng(37.8724950469582, -122.26043321192263),
                        new LatLng(37.872553007452275, -122.26021997630598),
                        new LatLng(37.87279887589052, -122.26031418889762),
                        new LatLng(37.87274753206485, -122.26054519414902),
                        new LatLng(37.87282507710734, -122.26057570427658),
                        new LatLng(37.87278034977197, -122.26074032485486),
                        new LatLng(37.8728383100416, -122.26076647639275),
                        new LatLng(37.872781408407434, -122.26101022213696),
                        new LatLng(37.872725300706556, -122.26098205894232),
                        new LatLng(37.87270306934155, -122.26109940558672))

                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(moffitPolygon);

        Polygon doePolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.8724572005836, -122.25999869406225),
                        new LatLng(37.872251559452984, -122.25993767380713),
                        new LatLng(37.87226108724004, -122.25988302379847),
                        new LatLng(37.87173996841001, -122.25971069186927),
                        new LatLng(37.8719019417474, -122.25899554789066),
                        new LatLng(37.871934495169285, -122.25901030004026),
                        new LatLng(37.8720652380359, -122.2585254907608),
                        new LatLng(37.87254533232099, -122.25870922207832),
                        new LatLng(37.87245667126352, -122.25908808410169),
                        new LatLng(37.872599058223294, -122.25914373993874),
                        new LatLng(37.87258450194822, -122.25920207798481),
                        new LatLng(37.87263505190932, -122.259228564779883))

                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(doePolygon);

        Polygon MLKPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.86933361669133, -122.2599919885397),
                        new LatLng(37.868922581091915, -122.25990917533638),
                        new LatLng(37.86893872612652, -122.25979954004286),
                        new LatLng(37.868816711924836, -122.25977204740049),
                        new LatLng(37.86886011830173, -122.259416654706),
                        new LatLng(37.86882888688677, -122.25941196084021),
                        new LatLng(37.86884000315463, -122.25932445377111),
                        new LatLng(37.8688685878357, -122.25933384150267),
                        new LatLng(37.86887705736871, -122.25928958505392),
                        new LatLng(37.869450336620616, -122.25941196084021),
                        new LatLng(37.86939369704083, -122.25982703268527),
                        new LatLng(37.86935690775774, -122.25982636213303))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(MLKPolygon);

        Polygon southHallPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87152135677233, -122.25868809968233),
                        new LatLng(37.871110068712774, -122.25852716714144),
                        new LatLng(37.87115876703273, -122.25833337754011),
                        new LatLng(37.871554175025665, -122.25850034505127))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(southHallPolygon);

        Polygon campanilePolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87251224984933, -122.25800111889838),
                        new LatLng(37.872014158360564, -122.2578465566039),
                        new LatLng(37.87203294933334, -122.25773323327302),
                        new LatLng(37.8725493022166, -122.25793842226267))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(campanilePolygon);

        Polygon sproulPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.86998047038954, -122.25865390151738),
                        new LatLng(37.8699272719159, -122.259081043303),
                        new LatLng(37.8698131994375, -122.25906830281019),
                        new LatLng(37.86981849282787, -122.25895866751671),
                        new LatLng(37.86963878201186, -122.258919775486),
                        new LatLng(37.86962978322606, -122.258964702487),
                        new LatLng(37.869490566576054, -122.25892648100852),
                        new LatLng(37.869494271965586, -122.2588926181197),
                        new LatLng(37.86931032561756, -122.25885439664125),
                        new LatLng(37.86930185613435, -122.25895866751671),
                        new LatLng(37.869175078441366, -122.2589308395982),
                        new LatLng(37.86924521644044, -122.25849900394678))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(sproulPolygon);

        Polygon VLSBPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87192681997355, -122.26166836917401),
                        new LatLng(37.87165951093357, -122.26284317672253),
                        new LatLng(37.87151077023589, -122.26279556751251),
                        new LatLng(37.8714716000378, -122.26299304515123),
                        new LatLng(37.87130539112773, -122.26292230188847),
                        new LatLng(37.87129268724657, -122.26296856999399),
                        new LatLng(37.8712304911301, -122.26295080035925),
                        new LatLng(37.87123948972041, -122.26289480924605),
                        new LatLng(37.871067722321456, -122.26282741874456),
                        new LatLng(37.871104246085416, -122.2626544162631),
                        new LatLng(37.87099388074337, -122.2625984251499),
                        new LatLng(37.87127495474212, -122.26142026484013),
                        new LatLng(37.87140014083898, -122.26148027926682),
                        new LatLng(37.871471070710655, -122.26121373474598),
                        new LatLng(37.87185192060769, -122.26137433201075),
                        new LatLng(37.871795018211685, -122.26162880659103))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(VLSBPolygon);

        Polygon gladePolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.873373183254415, -122.25983574986458),
                        new LatLng(37.873484868313945, -122.25963022559883),
                        new LatLng(37.87350683478676, -122.25954037159681),
                        new LatLng(37.87351927362992, -122.25944682955742),
                        new LatLng(37.873520067598555, -122.25933618843555),
                        new LatLng(37.87351398050543, -122.25927516818047),
                        new LatLng(37.87350207097394, -122.25922018289566),
                        new LatLng(37.87348725022096, -122.25916922092438),
                        new LatLng(37.87346104923962, -122.25910417735577),
                        new LatLng(37.8734369655011, -122.25905388593674),
                        new LatLng(37.87340573602622, -122.25899990648031),
                        new LatLng(37.8733620676705, -122.2589449211955),
                       new LatLng(37.87331681134725, -122.2588936239481),
                        new LatLng(37.87324350133777, -122.25883428007364),
                        new LatLng(37.87317124988517, -122.25879974663258),
                        new LatLng(37.87294126218891, -122.2587139159441),
                        new LatLng(37.872900240142314, -122.2588926181197),
                        //
                        new LatLng(37.87288409597577, -122.2588859125972),
                        new LatLng(37.87281634336943, -122.25916184484959),
                        new LatLng(37.87269936411338, -122.2591155767441),
                        new LatLng(37.87266548773306, -122.25927248597145),
                        new LatLng(37.87278299636063, -122.25931841880085),
                        new LatLng(37.872729270592444, -122.25954975932838),
                        new LatLng(37.872790406808335, -122.25957456976177),
                        new LatLng(37.87278034977197, -122.2596188262105),
                        new LatLng(37.87277452727662, -122.25969795137645),
                        new LatLng(37.87275891240043, -122.25975025445221),
                        new LatLng(37.872744885474916, -122.25977808237076),
                        new LatLng(37.8727311232058, -122.25983541458845),
                        new LatLng(37.87270703922863, -122.25982770323755),
                        new LatLng(37.872706509910365, -122.25988805294038),
                        new LatLng(37.87271524366131, -122.25994605571032),
                        new LatLng(37.872742503543904, -122.26001009345055),
                        new LatLng(37.87276844012189, -122.26004529744387),
                        new LatLng(37.87284360321464, -122.26004663834837),
                        new LatLng(37.87290685660299, -122.26001545786856),
                        new LatLng(37.873021982923575, -122.25998226553203),
                        new LatLng(37.87308470684321, -122.25996516644953),
                        new LatLng(37.873215976983246, -122.25990783423185),
                        new LatLng(37.87334274772303, -122.25982200354338))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(gladePolygon);

        Polygon memStadiumPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87108016157644, -122.25197654217482),
                        new LatLng(37.87047301761724, -122.25159533321857),
                        new LatLng(37.869986822443536, -122.25089058279991),
                        new LatLng(37.86995056279466, -122.25051440298557),
                        new LatLng(37.8699770296934, -122.25016538053751),
                        new LatLng(37.8701159807558, -122.2499293461442),
                        new LatLng(37.87008951390698, -122.2498894482851),
                        new LatLng(37.8702083499837, -122.2497432678938),
                        new LatLng(37.87026101891855, -122.24978048354387),
                        new LatLng(37.87050530700345, -122.24956423044203),
                        new LatLng(37.8709697961983, -122.2494924813509),
                        new LatLng(37.87108122023635, -122.24955953657629),
                        new LatLng(37.87198689820846, -122.25047383457424),
                        new LatLng(37.87211393572447, -122.25102368742226),
                        new LatLng(37.872025274147866, -122.25144881755112),
                        new LatLng(37.87180639869439, -122.25181963294744),
                        new LatLng(37.87155735098491, -122.25200336426497),
                        new LatLng(37.87118470416825, -122.25205231457947))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(memStadiumPolygon);

        Polygon sutardjaPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87514292161614, -122.25852448493242),
                        new LatLng(37.87522152273764, -122.25815400481224),
                        new LatLng(37.87471286349008, -122.25796557962894),
                        new LatLng(37.87452601899269, -122.2587353736162),
                        new LatLng(37.8748832291339, -122.25886311382057),
                        new LatLng(37.8748917679755, -122.25882958620785),
                        new LatLng(37.87480840277378, -122.25879739969967),
                        new LatLng(37.874891767955, -122.25842524319886))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        places.add(memStadiumPolygon);

        for (Polygon place: places) {
            place.setClickable(true);
        }

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {
                /*LatLng iLatlng = new LatLng(37.872933057781424, -122.26527929306029);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(iLatlng)
                        .title("Li Ka Shing")
                        .snippet("Description Here ")
                        );
                marker.setAlpha(0.0f);
                marker.showInfoWindow();
                */

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//                builder1.setMessage("Li Ka Shing Description.");
//                builder1.setCancelable(true);
                LayoutInflater factory = LayoutInflater.from(context);
                final View view = factory.inflate(R.layout.place_dialogue, null);
                ImageView image = (ImageView) view.findViewById(R.id.image);
                image.setImageResource(R.drawable.lks_image);
                TextView text = (TextView) view.findViewById(R.id.text);
                text.setText("Description herejkn");
                builder1.setView(view);
//                builder1.setNeutralButton(
//                        "Done",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
////                final Dialog alert11 = new Dialog(context);
////                alert11.setContentView(R.layout.place_dialogue);
//                builder1.setTitle("Li Ka Shing");
                AlertDialog alert11 = builder1.create();
//                TextView text = (TextView) alert11.findViewById(R.id.text);
//                text.setText("Description here");
//                ImageView image = (ImageView) alert11.findViewById(R.id.image);
//                image.setImageResource(R.drawable.lks_image);
                alert11.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                alert11.show();

            }
        });


    }

}
