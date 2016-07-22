package com.andrealiu.maptest;

import android.*;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    final Context context = this;
    private ArrayList<Place> places = new ArrayList<Place>();
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private LocationRequest mLocationRequest;
    SupportMapFragment mapFrag;
    Location mLastLocation;
    Marker mCurrLocationMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        LatLng statue = new LatLng(37.873046331455676 , -122.26484343409538);
        mMap.addMarker(new MarkerOptions().position(statue).title("Li Ka Shing Statue"));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(statue));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.873046331455676, -122.26484343409538), 18));
        //OLD ONE = mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.87514292161614, -122.25852448493242), 18));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

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
        String LKSDesc = new StringBuilder()
                .append("• It was the best of times, it was the worst of times,\n")
                .append("• It was the age of wisdom, it was the age of foolishness,\n")
                .append("• It was the epoch of belief, it was the epoch of incredulity,\n")
                .append("• It was the season of Light, it was the season of Darkness,\n")
                .append("• It was the spring of hope, it was the winter of despair,\n")
                .append("• We had everything before us, we had nothing before us")
                .toString();
        places.add(new Place(LKSpolygon, "Li Ka Shing", LKSDesc, "lks_image"));

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
        String moffitDesc = new StringBuilder()
                .append("• First research library to be open to both Graduate and Undergraduate " +
                        "students\n")
                .append("• Outside, you can find news panels of various countries newspapers " +
                        "translated into english and updated every day!\n")
                .append("• There will be a nap center here!\n")
                .toString();
        places.add(new Place(moffitPolygon, "Moffit Library", moffitDesc, "moffitt"));

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
        String doeDesc = new StringBuilder()
                .append("• One of our 27 librairies. \n")
                .append("• Open to the public, so take a look!\n")
                .toString();
        places.add(new Place(doePolygon, "Doe Library", doeDesc, "doe"));

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
        String MLKDesc = new StringBuilder()
                .append("• Renovated and opened August 2015. \n")
                .append("• Has a food court, a student store, and a huge ballroom where events such " +
                        "as career fairs, often take place.\n")
                .toString();
        places.add(new Place(MLKPolygon, "Martin Luther King, Jr. Student Union", MLKDesc, "mlk"));

        Polygon southHallPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87152135677233, -122.25868809968233),
                        new LatLng(37.871110068712774, -122.25852716714144),
                        new LatLng(37.87115876703273, -122.25833337754011),
                        new LatLng(37.871554175025665, -122.25850034505127))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        String southHallDesc = new StringBuilder()
                .append("• The first building ever built on campus, it was once the college of " +
                        "Agriculture but now it holds one of the 9 graduate schools--the school " +
                        "of information.. \n")
                .append("• Try finding a small bear statue hidden on the preface of the building!\n")
                .toString();
        places.add(new Place(southHallPolygon, "South Hall", southHallDesc, "south"));

        Polygon campanilePolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87251224984933, -122.25800111889838),
                        new LatLng(37.872014158360564, -122.2578465566039),
                        new LatLng(37.87203294933334, -122.25773323327302),
                        new LatLng(37.8725493022166, -122.25793842226267))
                .strokeColor(0xFF00AA00)
                .fillColor(0x2200FFFF)
                .strokeWidth(2)
        );
        String campanileDesc = new StringBuilder()
                .append("• Over 600 ft tall! (Taller than Stanford's tower) \n")
                .append("• Plays concerts Mon-Fri at 8,12 and 6. Saturday at 12 and Sunday at 2\n")
                .append("• Celebrated it’s 100th birthday last year (2015)\n")
                .toString();
        places.add(new Place(campanilePolygon, "Campanile", campanileDesc, "campanile"));

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
        String sproulDesc = new StringBuilder()
                .append("• Administrative building includes the visitors center, financial aid, and admissions. \n")
                .toString();
        places.add(new Place(sproulPolygon, "Sproul Hall", sproulDesc, "sproulhall"));

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
        String VLSBDesc = new StringBuilder()
                .append("• Largest building on campus and second largest in the nation at 3.5 acres.\n")
                .append("• Holds integrative biology and molecular cell biology majors.\n")
                .append("• Contains Osborne, a life-size cast of the most fully intact t-rex ever found!\n")
                .toString();
        places.add(new Place(VLSBPolygon, "Valley Life Sciences Building", VLSBDesc, "vlsb"));

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
        String memGladeDesc = new StringBuilder()
                .append("• Once a memorial for the students and faculty who served in WWII, " +
                        "it now is a central meeting place for students.\n")
                .append("• Our quidditch team practices here!\n")
                .append("• Surrounded by three university seals. The seal was originally designed" +
                        " by Tiffany and Co.!\n")
                .toString();
        places.add(new Place(gladePolygon, "Memorial Glade", memGladeDesc, "memglade"));

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
        String memStadiumDesc = new StringBuilder()
                .append("• Newly renovated.\n")
                .toString();
        places.add(new Place(memStadiumPolygon, "Memorial Stadium", memStadiumDesc, "memstadium"));

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
        String sutardjaDesc = new StringBuilder()
                .append("• CITRUS center holds a display of various research projects for" +
                        " the betterment of society.\n")
                .toString();
        places.add(new Place(sutardjaPolygon, "Sutardja Daj Hall", sutardjaDesc, "sutardja"));

        for (Place place: places) {
            place.polygon.setClickable(true);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions().position( latLng );
                options.title(latLng.latitude + ", " + latLng.longitude);

                options.icon( BitmapDescriptorFactory.defaultMarker() );
                mMap.addMarker(options);

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {

                for (Place place: places)  {
                    if (place.polygon.equals(polygon)) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Done",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        LayoutInflater factory = LayoutInflater.from(context);
                        final View view = factory.inflate(R.layout.place_dialogue, null);
                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        int resId = getResources().getIdentifier(place.imgResource, "drawable", getPackageName());
                        image.setImageResource(resId);
                        TextView description = (TextView) view.findViewById(R.id.text);
                        description.setText(place.description);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText(place.title);
                        builder1.setView(view);
                        AlertDialog alert11 = builder1.create();
                        alert11.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                        alert11.show();
                        break;
                    }

                }


            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {

        Log.i("DEBUG", "ON LOCATION CHANGED");
//        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
//
//        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

//        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
