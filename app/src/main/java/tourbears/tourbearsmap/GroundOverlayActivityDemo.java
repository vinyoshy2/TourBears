package tourbears.tourbearsmap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.*;
import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to add a ground overlay to a map.
 */
public class GroundOverlayActivityDemo extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnSeekBarChangeListener, OnMapReadyCallback, LocationListener,
        GoogleMap.OnGroundOverlayClickListener {

    private static final int TRANSPARENCY_MAX = 100;

    private static final LatLng UCBSMALL = new LatLng(37.87555698269304, -122.26861585585893394);
    //private static final LatLng UCBBIG = new LatLng(37.875108252407934, -122.27027289569378);
    private static final LatLng NEAR_NEWARK =
            new LatLng(UCBSMALL.latitude - 0.0010, UCBSMALL.longitude - 0.0010);

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GroundOverlay mGroundOverlay;
    private UiSettings mUiSettings;
    private GoogleMap mMap;

    private GroundOverlay mGroundOverlayRotated;
    private DrawerLayout mDrawerLayout;
    private SeekBar mTransparencyBar;

    private int mCurrentEntry = 0;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private final Context context = this;
    private ArrayList<Place> places = new ArrayList<Place>();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    SupportMapFragment mapFrag;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    private Polyline mainTourLine;
    private Polyline chemEngTourLine;
    private Polyline bearTourLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground_overlay);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

//        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
//        mTransparencyBar.setMax(TRANSPARENCY_MAX);
//        mTransparencyBar.setProgress(0);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();

        android.location.LocationListener mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (Math.abs(location.getLatitude() - 37.875435) < 2 && Math.abs(location.getLongitude() + 122.265627) < 2 ) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notificon)
                            .setContentTitle("Wow ur at Vinay's apartment")
                            .setContentText("is rly cool in here");
                    int notID = 001;
                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(notID, mBuilder.build());

                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                return;
            }
            @Override
            public void onProviderEnabled(String provider) {
                return;
            }
            @Override
            public void onProviderDisabled(String provider) {
                return;
            }
        };
        Criteria mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);

        LocationManager mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(mCriteria, true), (long) 3, (float) 3.0, mLocationListener);
    }

    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Tour Toggle" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
//
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 4) {
//                    Toast.makeText(GroundOverlayActivityDemo.this, "Toggling tour!" + position, Toast.LENGTH_SHORT).show();
//                    toggleTour();
//                }
//
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Register a listener to respond to clicks on GroundOverlays.
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
//        mMap.setOnGroundOverlayClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(latLng.latitude + "," + latLng.longitude);

                options.icon( BitmapDescriptorFactory.defaultMarker());
                mMap.addMarker(options);
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.86952973782737, -122.2593318298459), 18));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.86952973782737, -122.2593318298459), 18));
        // OLD
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.8757799323313, -122.26865082979202), 14));
        mImages.clear();
        //mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.ucbmap3));
//        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.ucbplsworkrot));

        // Add a small, rotated overlay that is clickable by default
        // (set by the initial state of the checkbox.)
        float rot = (float)80;
//        mGroundOverlayRotated = map.addGroundOverlay(new GroundOverlayOptions()
//                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
//                .position(UCBSMALL, 1110f, 1310f)
//                .bearing(rot)
//                .clickable(((CheckBox) findViewById(R.id.toggleClickability)).isChecked()));

//        mTransparencyBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with ground overlay.");

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
                .strokeColor(ContextCompat.getColor(context, R.color.CNSStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.CNSFillColor))
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

                .strokeColor(ContextCompat.getColor(context, R.color.LibraryStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LibraryFillColor))
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

                .strokeColor(ContextCompat.getColor(context, R.color.LibraryStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LibraryFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.LSStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LSFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.OtherStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.OtherFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.OtherStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.OtherFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.LSStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LSFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.LSStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LSFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.OtherStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.OtherFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.OtherStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.OtherFillColor))
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
                .strokeColor(ContextCompat.getColor(context, R.color.COEStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.COEFillColor))
                .strokeWidth(2)
        );
        String sutardjaDesc = new StringBuilder()
                .append("• CITRUS center holds a display of various research projects for" +
                        " the betterment of society.\n")
                .toString();
        places.add(new Place(sutardjaPolygon, "Sutardja Daj Hall", sutardjaDesc, "sutardja"));

        Polygon wursterPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87093883034309, -122.25527867674829),
                        new LatLng(37.870615937744034, -122.25521899759768),
                        new LatLng(37.87063605240628, -122.25501280277969),
                        new LatLng(37.869995821185725, -122.25487165153027),
                        new LatLng(37.87008660255303, -122.25426346063614),
                        new LatLng(37.870234816789846, -122.25429095327854),
                        new LatLng(37.870249902865076, -122.25418534129857),
                        new LatLng(37.87067628171431, -122.25426815450193),
                        new LatLng(37.87066304839172, -122.25438214838503),
                        new LatLng(37.870744830287286, -122.25439891219139),
                        new LatLng(37.870760180924215, -122.254284247756),
                        new LatLng(37.87111827332328, -122.25435633212327),
                        new LatLng(37.87108810152533, -122.25461684167385),
                        new LatLng(37.87101002532412, -122.2545977309346))
                .strokeColor(ContextCompat.getColor(context, R.color.EDStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.EDFillColor))
                .strokeWidth(2)
        );
        String wursterDesc = new StringBuilder()
                .append("• Wurster Hall encompasses the College of Environmental Design where you " +
                        "can find majors in Architecture, city planning, and sustainable " +
                        "environmental design.\n")
                .append("• Cal Alumni Irving Morrow designed the Golden Gate Bridge! Go up the " +
                        "Campanile to see his amazing work\n")
                .toString();
        places.add(new Place(wursterPolygon, "Wurster Hall", wursterDesc, "wurster"));

        Polygon bechtelPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87417297158065, -122.25779861211777),
                        new LatLng(37.87446197303607, -122.25789818912745),
                        new LatLng(37.87429153641745, -122.25862741470335),
                        new LatLng(37.873937164608115, -122.25850000977516),
                        new LatLng(37.874012061854025, -122.25819692015648),
                        new LatLng(37.87410045638551, -122.25822810083628),
                        new LatLng(37.87413697864599, -122.25807420909405),
                        new LatLng(37.87410892531709, -122.25806280970573))
                .strokeColor(ContextCompat.getColor(context, R.color.LibraryStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LibraryFillColor))
                .strokeWidth(2)
        );
        String bechtelDesc = new StringBuilder()
                .append("• This unique library is one of the 2 libraries on campus where you are expected to talk in!\n")
                .append("• No need to be quiet while collaborating on projects.\n")
                .toString();
        places.add(new Place(bechtelPolygon, "Bechtel Engineering Library", bechtelDesc, "bechtel"));

        Polygon evansPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87379848540762, -122.25817780941725),
                        new LatLng(37.87390858189349, -122.25771378725769),
                        new LatLng(37.87339700235713, -122.25752670317888),
                        new LatLng(37.87327975936603, -122.25798469036818))
                .strokeColor(ContextCompat.getColor(context, R.color.COEStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.COEFillColor))
                .strokeWidth(2)
        );
        String evansDesc = new StringBuilder()
                .append("• If you ever take a math or statistics class, you will probably go to Evans Hall. \n")
                .append("• This is also where you can find undergraduate college advisers for" +
                        " the College of Letters and Sciences.\n")
                .toString();
        places.add(new Place(evansPolygon, "Evans Hall", evansDesc, "evans"));

        Polygon jacobsPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.876113652812, -122.25903611630201),
                        new LatLng(37.87582756897271, -122.25897040218115),
                        new LatLng(37.87588870261739, -122.258539237082),
                        new LatLng(37.87617716804848, -122.2586103156209))
                .strokeColor(ContextCompat.getColor(context, R.color.COEStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.COEFillColor))
                .strokeWidth(2)
        );
        String jacobsDesc = new StringBuilder()
                .append("• This is a brand new building, opened Fall of 2015. \n")
                .append("• It contains several 3-D printers and is home to various classes specifically for innovative design!\n")
                .toString();
        places.add(new Place(jacobsPolygon, "Jacobs Hall", jacobsDesc, "jacobs"));

        Polygon starrPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87375349400784, -122.26002618670465),
                        new LatLng(37.873472958777754, -122.2599222511053),
                        new LatLng(37.87337821173228, -122.26029206067325),
                        new LatLng(37.87340070755021, -122.2603017836809),
                        new LatLng(37.87334380635043, -122.26061090826987),
                        new LatLng(37.87359390664474, -122.26070042699574))
                .strokeColor(ContextCompat.getColor(context, R.color.LibraryStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LibraryFillColor))
                .strokeWidth(2)
        );
        String starrDesc = new StringBuilder()
                .append("• The first academic building built solely for East Asian studies! \n")
                .toString();
        places.add(new Place(starrPolygon, "Starr East Asian Library", starrDesc, "starr"));

        Polygon bampfaPolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.870925332402116, -122.26678870618345),
                        new LatLng(37.870597411076574, -122.26672902703285),
                        new LatLng(37.87067601704789, -122.26604271680118),
                        new LatLng(37.8710039380236, -122.2660980373621))
                .strokeColor(ContextCompat.getColor(context, R.color.LibraryStrokeColor))
                .fillColor(ContextCompat.getColor(context,R.color.LibraryFillColor))
                .strokeWidth(2)
        );
        String bampfaDesc = new StringBuilder()
                .append("• A newly renovated Museum which opened spring of 2016! \n")
                .append("• Check out unique screenings of films! \n")
                .toString();
        places.add(new Place(bampfaPolygon, "Berkeley Art Museum and Pacific Film Archive", bampfaDesc, "bampfa"));

        Polygon dwinellePolygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(37.87111430335058, -122.26124893873929),
                        new LatLng(37.870606409744205, -122.26106185466051),
                        new LatLng(37.87067204705134, -122.2607570886612),
                        new LatLng(37.8703176578316, -122.26066220551729),
                        new LatLng(37.87029754308242, -122.2607594355941),
                        new LatLng(37.87011121672373, -122.26072892546654),
                        new LatLng(37.87021073199665, -122.25995577871801),
                        new LatLng(37.870407115465376, -122.26001143455507),
                        new LatLng(37.870368209348065, -122.26024881005287),
                        new LatLng(37.87077950154893, -122.26036883890629),
                        new LatLng(37.870827670755595, -122.26013146340846),
                        new LatLng(37.870992557416926, -122.26019985973835),
                        new LatLng(37.87092242108121, -122.2604861855507),
                        new LatLng(37.87113309455287, -122.26056430488823),
                        new LatLng(37.87115426773278, -122.26047310978174),
                        new LatLng(37.87126780880629, -122.26051736623047))
                .strokeColor(ContextCompat.getColor(context, R.color.LSStrokeColor))
                .fillColor(ContextCompat.getColor(context, R.color.LSFillColor))
                .strokeWidth(2)
        );
        String dwinelleDesc = new StringBuilder()
                .append("• Home to many history, humanities, and languages classes.  \n")
                .append("• Every semester, UC Berkeley offers over 60 languages, from Swahili to Yiddish to Farsai! \n")
                .toString();
        places.add(new Place(dwinellePolygon, "Dwinelle Hall", dwinelleDesc, "dwinelle"));

        for (Place place: places) {
            place.polygon.setClickable(true);
        }


        /* POLY LINES */
        ArrayList<LatLng> mainTourPoints = new ArrayList<LatLng>();
        mainTourPoints.add(new LatLng(37.872425970693385, -122.2601877897978));
        mainTourPoints.add(new LatLng(37.87235213047661, -122.26030111312866));
        mainTourPoints.add(new LatLng(37.87226690977598, -122.2604452818632));
        mainTourPoints.add(new LatLng(37.872203126516304, -122.26058777421713));
        mainTourPoints.add(new LatLng(37.87214807701958, -122.26077016443014));
        mainTourPoints.add(new LatLng(37.872115523692045, -122.26098842918871));
        mainTourPoints.add(new LatLng(37.87210043799881, -122.26120501756668));
        mainTourPoints.add(new LatLng(37.87215072363093, -122.26145144551994));
        mainTourPoints.add(new LatLng(37.87219333406076, -122.26156108081342));
        mainTourPoints.add(new LatLng(37.87219571600951, -122.26171195507051));
        mainTourPoints.add(new LatLng(37.872188040841, -122.26188395172358));
        mainTourPoints.add(new LatLng(37.87216713261926, -122.2619630768895));
        mainTourPoints.add(new LatLng(37.87205094631685, -122.26237244904043));
        // right in front of VLSB
        mainTourPoints.add(new LatLng(37.871074338946705, -122.26197615265845));
        mainTourPoints.add(new LatLng(37.87081417279427, -122.26191882044077));
        mainTourPoints.add(new LatLng(37.8705373317086, -122.26191278547047));
        mainTourPoints.add(new LatLng(37.8703367139047, -122.26174615323544));
        mainTourPoints.add(new LatLng(37.8701861178592, -122.26170457899569));
        mainTourPoints.add(new LatLng(37.87008766122722, -122.26184137165546));
        mainTourPoints.add(new LatLng(37.8699770296934, -122.26180147379635));
        // intersection of spieker plaza and cross-sproul path
        mainTourPoints.add(new LatLng(37.869447160570566, -122.26168345659971));
        mainTourPoints.add(new LatLng(37.86952020968707, -122.26105514913796));
        mainTourPoints.add(new LatLng(37.8695196803459, -122.26095356047152));
        mainTourPoints.add(new LatLng(37.86950962286309, -122.26067058742046));
        mainTourPoints.add(new LatLng(37.86945113063309, -122.26062700152399));
        mainTourPoints.add(new LatLng(37.869399255132365, -122.2605401650071));
        mainTourPoints.add(new LatLng(37.869392638356686, -122.26051602512598));
        mainTourPoints.add(new LatLng(37.8693799341457, -122.26035308092833));
        mainTourPoints.add(new LatLng(37.86939396171185, -122.26022031158207));
        mainTourPoints.add(new LatLng(37.86944636655802, -122.2597723826766));
        mainTourPoints.add(new LatLng(37.86947495100386, -122.2596312314272));
        // first point right in front of sproul
        mainTourPoints.add(new LatLng(37.86952973782737, -122.2593318298459));
        mainTourPoints.add(new LatLng(37.87011942144545, -122.25945688784122));
        mainTourPoints.add(new LatLng(37.87070962967669, -122.25961580872534));
        mainTourPoints.add(new LatLng(37.870827141423824, -122.25914172828197));
        mainTourPoints.add(new LatLng(37.871024052579926, -122.25850738584995));
        mainTourPoints.add(new LatLng(37.87126145686272, -122.25803699344397));
        mainTourPoints.add(new LatLng(37.87164945374283, -122.25759945809843));
        mainTourPoints.add(new LatLng(37.87198028166517, -122.25773356854916));
        mainTourPoints.add(new LatLng(37.87290050480075, -122.25813221186401));
        mainTourPoints.add(new LatLng(37.87286424658585, -122.25821904838085));
        mainTourPoints.add(new LatLng(37.872770557393146, -122.25862506777048));
        // edge of memorial glade
        mainTourPoints.add(new LatLng(37.87297831434038, -122.25870653986931));
        mainTourPoints.add(new LatLng(37.87287668553748, -122.25921414792538));
        mainTourPoints.add(new LatLng(37.872868216464255, -122.25927148014307));
        mainTourPoints.add(new LatLng(37.87286689317148, -122.25934121757747));
        mainTourPoints.add(new LatLng(37.872869275098445, -122.2593951970339));
        mainTourPoints.add(new LatLng(37.87279675862005, -122.25946225225925));
//        // break off from memorial glade after below point
        mainTourPoints.add(new LatLng(37.87276605819164, -122.25951053202154));
        // INSERT: CIRCLE DOE LIBRARY
        mainTourPoints.add(new LatLng(37.87241194370449, -122.2593677043915));
        mainTourPoints.add(new LatLng(37.871937935774035, -122.25918900221585));
        mainTourPoints.add(new LatLng(37.871846627363404, -122.25961480289699));
        mainTourPoints.add(new LatLng(37.87231163742307, -122.25981093943119));
        mainTourPoints.add(new LatLng(37.87241194370449, -122.2593677043915));
        // go back to memorial glade point
        mainTourPoints.add(new LatLng(37.87276605819164, -122.25951053202154));
        // resume
        mainTourPoints.add(new LatLng(37.872755736493, -122.2595738992095));
        mainTourPoints.add(new LatLng(37.87274964933671, -122.2596349194646));
        mainTourPoints.add(new LatLng(37.872743297520934, -122.25969560444354));
        mainTourPoints.add(new LatLng(37.87272715331999, -122.2597509250045));
        mainTourPoints.add(new LatLng(37.87269168899734, -122.25982334464787));
        mainTourPoints.add(new LatLng(37.872688248427814, -122.25988168269396));
        mainTourPoints.add(new LatLng(37.8727017460458, -122.2599494084716));
        mainTourPoints.add(new LatLng(37.8727290059334, -122.26002249866725));
        mainTourPoints.add(new LatLng(37.87278987749065, -122.26011101156473));
        mainTourPoints.add(new LatLng(37.87267951467369, -122.26016767323019));
        mainTourPoints.add(new LatLng(37.87251807236542, -122.26019516587259));
        mainTourPoints.add(new LatLng(37.872425970693385, -122.2601877897978));
        PolylineOptions mainOptions = new PolylineOptions().width(7).color(ContextCompat.getColor(context, R.color.MainTourColor)).geodesic(true);
        for (int z = 0; z < mainTourPoints.size(); z++) {
            LatLng point = mainTourPoints.get(z);
            mainOptions.add(point);
        }
        mainOptions.visible(false);
        mainTourLine = mMap.addPolyline(mainOptions);

        ArrayList<LatLng> chemEngTourPoints = new ArrayList<LatLng>();
        // start: in front of Sproul
        chemEngTourPoints.add(new LatLng(37.86952973782737, -122.2593318298459));
        chemEngTourPoints.add(new LatLng(37.87011942144545, -122.25945688784122));
        chemEngTourPoints.add(new LatLng(37.87070962967669, -122.25961580872534));
        chemEngTourPoints.add(new LatLng(37.87157005482043, -122.25992124527693));
        chemEngTourPoints.add(new LatLng(37.87165077705749, -122.25960072129965));
        //
        chemEngTourPoints.add(new LatLng(37.87179713551093, -122.25894022732972));
        chemEngTourPoints.add(new LatLng(37.8717831084023, -122.25880343466997));
        chemEngTourPoints.add(new LatLng(37.87171297281915, -122.25847194658052));
        chemEngTourPoints.add(new LatLng(37.87171509012076, -122.25839976221323));
        chemEngTourPoints.add(new LatLng(37.87175452485195, -122.25825157016514));
        chemEngTourPoints.add(new LatLng(37.87124372435076, -122.25804906338453));
        chemEngTourPoints.add(new LatLng(37.871353030662576, -122.25791595876217));
        chemEngTourPoints.add(new LatLng(37.87183683486045, -122.25739862769842));
        chemEngTourPoints.add(new LatLng(37.87206365006726, -122.25690476596354));
        chemEngTourPoints.add(new LatLng(37.87222377006697, -122.25646991282701));
        chemEngTourPoints.add(new LatLng(37.87264193305319, -122.25662346929313));
        chemEngTourPoints.add(new LatLng(37.87282984096395, -122.25666303187609));
        // above = right next to gilman hall; before loop
        // below = top left of loop (clockwise)
        chemEngTourPoints.add(new LatLng(37.87301880702749, -122.25584529340267));
        chemEngTourPoints.add(new LatLng(37.87305612379786, -122.25585769861937));
        // middle of pimentel below
        chemEngTourPoints.add(new LatLng(37.87342135076531, -122.25601159036158));
        chemEngTourPoints.add(new LatLng(37.873570087605685, -122.25610882043839));
        chemEngTourPoints.add(new LatLng(37.87357273416595, -122.256380058825));
        // resume
        chemEngTourPoints.add(new LatLng(37.87355817808326, -122.25658558309078));
        chemEngTourPoints.add(new LatLng(37.87371061982484, -122.25659564137457));
        chemEngTourPoints.add(new LatLng(37.87380166127009, -122.25661173462868));
        chemEngTourPoints.add(new LatLng(37.873870471590074, -122.25666169077158));
        chemEngTourPoints.add(new LatLng(37.87393689995342, -122.25673478096724));
        chemEngTourPoints.add(new LatLng(37.87398639036843, -122.25680351257324));
        chemEngTourPoints.add(new LatLng(37.874002534293396, -122.25690979510547));
        chemEngTourPoints.add(new LatLng(37.87400465152921, -122.25696913897991));
        chemEngTourPoints.add(new LatLng(37.8738760293439, -122.25751228630543));
        // finished looping memorial glade
        chemEngTourPoints.add(new LatLng(37.87395674905432, -122.25763365626335));
        chemEngTourPoints.add(new LatLng(37.87402873508217, -122.25767489522694));
        chemEngTourPoints.add(new LatLng(37.87459138815545, -122.2578666731715));
        chemEngTourPoints.add(new LatLng(37.874621029214175, -122.2578666731715));
        chemEngTourPoints.add(new LatLng(37.87466178565046, -122.25784756243227));
        chemEngTourPoints.add(new LatLng(37.8746850750325, -122.25788276642562));
        chemEngTourPoints.add(new LatLng(37.87472000909178, -122.25792601704596));
        chemEngTourPoints.add(new LatLng(37.874743563107224, -122.25794479250908));
        chemEngTourPoints.add(new LatLng(37.87480046322584, -122.25797295570374));
        chemEngTourPoints.add(new LatLng(37.87484095491138, -122.25799173116684));
        chemEngTourPoints.add(new LatLng(37.87535331936933, -122.2581871971488));
        // entered Hearst ^
        chemEngTourPoints.add(new LatLng(37.87531229766587, -122.25847687572241));
        chemEngTourPoints.add(new LatLng(37.87523052084047, -122.25845541805029));
        chemEngTourPoints.add(new LatLng(37.87521093673289, -122.2584530711174));
        chemEngTourPoints.add(new LatLng(37.8751995567761, -122.25845575332642));
        chemEngTourPoints.add(new LatLng(37.875175473605935, -122.25847452878953));
        chemEngTourPoints.add(new LatLng(37.875154830882366, -122.25850537419319));
        chemEngTourPoints.add(new LatLng(37.87514556811989, -122.25853521376848));
        chemEngTourPoints.add(new LatLng(37.87503838464108, -122.25851040333508));
        chemEngTourPoints.add(new LatLng(37.8749904828384, -122.2584979981184));
        chemEngTourPoints.add(new LatLng(37.87492881914596, -122.2584741935134));
        chemEngTourPoints.add(new LatLng(37.87492008565748, -122.2584741935134));
        chemEngTourPoints.add(new LatLng(37.87491399868002, -122.25848324596882));
        chemEngTourPoints.add(new LatLng(37.87491082286551, -122.2584879398346));
        chemEngTourPoints.add(new LatLng(37.87482295860946, -122.25883092731237));
        // passed CITRIS
        chemEngTourPoints.add(new LatLng(37.874817136275524, -122.25883964449166));
        chemEngTourPoints.add(new LatLng(37.87479120041881, -122.25885540246964));
        chemEngTourPoints.add(new LatLng(37.87474965009874, -122.25895430892707));
        chemEngTourPoints.add(new LatLng(37.874691691332885, -122.2589372098446));
        chemEngTourPoints.add(new LatLng(37.874626586911354, -122.2589123994112));
        chemEngTourPoints.add(new LatLng(37.87452734225565, -122.2588725015521));
        chemEngTourPoints.add(new LatLng(37.87439448653394, -122.25881919264793));
        chemEngTourPoints.add(new LatLng(37.87436563934357, -122.25880712270737));
        chemEngTourPoints.add(new LatLng(37.87430053463388, -122.2587601840496));
        chemEngTourPoints.add(new LatLng(37.87424469274359, -122.25873604416847));
        chemEngTourPoints.add(new LatLng(37.874174294849965, -122.25870352238415));
        chemEngTourPoints.add(new LatLng(37.87408616506126, -122.25866362452507));
        chemEngTourPoints.add(new LatLng(37.87401788425159, -122.25864350795747));
        chemEngTourPoints.add(new LatLng(37.87389931897435, -122.25860696285964));

        PolylineOptions chemEngOptions = new PolylineOptions().width(7).color(ContextCompat.getColor(context, R.color.ChemEngTourColor)).geodesic(true);
        for (int z = 0; z < chemEngTourPoints.size(); z++) {
            LatLng point = chemEngTourPoints.get(z);
            chemEngOptions.add(point);
        }
        chemEngOptions.visible(false);
        chemEngTourLine = mMap.addPolyline(chemEngOptions);

        ArrayList<LatLng> bearTourPoints = new ArrayList<LatLng>();
        // start: on oxford
        bearTourPoints.add(new LatLng(37.8720694726188, -122.26611848920584));
        bearTourPoints.add(new LatLng(37.872388918264086, -122.26616308093071));
        bearTourPoints.add(new LatLng(37.872408238461695, -122.26601589471102));
        bearTourPoints.add(new LatLng(37.872273526293526, -122.2659870609641));
        bearTourPoints.add(new LatLng(37.87147107071065, -122.26589217782019));
        bearTourPoints.add(new LatLng(37.870613555744185, -122.26580534130333));
        // intersection of crescent and center
        bearTourPoints.add(new LatLng(37.8706230837431, -122.26567827165127));
        bearTourPoints.add(new LatLng(37.87064478640264, -122.26556796580553));
        bearTourPoints.add(new LatLng(37.870650873732835, -122.26555187255143));
        bearTourPoints.add(new LatLng(37.87062070174348, -122.26551432162522));
        bearTourPoints.add(new LatLng(37.870610379744264, -122.26547710597515));
        bearTourPoints.add(new LatLng(37.87060561574415, -122.26541910320522));
        bearTourPoints.add(new LatLng(37.87061884907705, -122.2653547301883));
        // intersection of eucalyptus rd and grinnell pathway below
        bearTourPoints.add(new LatLng(37.87074641828435, -122.26468753069639));
        bearTourPoints.add(new LatLng(37.87083772805852, -122.26411353796719));
        // below: intersection of grinnell and hilgard way
        bearTourPoints.add(new LatLng(37.870859695320625, -122.26389527320862));
//        bearTourPoints.add(new LatLng(37.8708731932736, -122.26364348083735));
//        bearTourPoints.add(new LatLng(37.870872928607874, -122.26356871426104));
//        bearTourPoints.add(new LatLng(37.87086737062752, -122.26351272314788));
//        bearTourPoints.add(new LatLng(37.87085651933131, -122.26343292742969));
//        bearTourPoints.add(new LatLng(37.87083931605357, -122.26335112005471));
//        bearTourPoints.add(new LatLng(37.87082449476491, -122.26330116391182));
        bearTourPoints.add(new LatLng(37.870798557502596, -122.26387649774551));
        bearTourPoints.add(new LatLng(37.87078003088104, -122.26387079805136));
        bearTourPoints.add(new LatLng(37.87076653291098, -122.26386208087206));
        bearTourPoints.add(new LatLng(37.87075594626608, -122.26384699344635));
        bearTourPoints.add(new LatLng(37.87074297762398, -122.2638087719679));
        bearTourPoints.add(new LatLng(37.87072365698969, -122.26376451551914));
        bearTourPoints.add(new LatLng(37.870704071684024, -122.2637440636754));
        // below: intersection of hilgard and schlessinger
        bearTourPoints.add(new LatLng(37.870615937744034, -122.26369511336087));
        bearTourPoints.add(new LatLng(37.87069533769466, -122.26330384612083));
        bearTourPoints.add(new LatLng(37.870665430389955, -122.26325824856758));
        bearTourPoints.add(new LatLng(37.87064293373684, -122.2632297500968));
        bearTourPoints.add(new LatLng(37.87061620241066, -122.26320561021565));
        bearTourPoints.add(new LatLng(37.87046851827541, -122.26310737431051));
        bearTourPoints.add(new LatLng(37.870395470099076, -122.2630513831973));
        bearTourPoints.add(new LatLng(37.87034041925169, -122.26299807429312));
        bearTourPoints.add(new LatLng(37.87023693413392, -122.26283982396124));
        bearTourPoints.add(new LatLng(37.870195116577065, -122.26274996995927));
        bearTourPoints.add(new LatLng(37.87016970842967, -122.26266916841269));
        bearTourPoints.add(new LatLng(37.8701435062685, -122.26253908127546));
        bearTourPoints.add(new LatLng(37.87013265486567, -122.26240463554858));
        bearTourPoints.add(new LatLng(37.87013927157489, -122.26229365915061));
        bearTourPoints.add(new LatLng(37.87016415039626, -122.26217564195395));
        // below: intersection of schlessinger and path sticking out of spiker plaza
        bearTourPoints.add(new LatLng(37.87022714141705, -122.26206298917532));
        bearTourPoints.add(new LatLng(37.8700905725811, -122.26183902472258));
        bearTourPoints.add(new LatLng(37.87018161849986, -122.26170256733893));
        bearTourPoints.add(new LatLng(37.87002149406189, -122.26138204336168));
        bearTourPoints.add(new LatLng(37.869924889893795, -122.26092740893364));
        bearTourPoints.add(new LatLng(37.86991589114293, -122.26085029542445));
        // below: interesection with grade st
        bearTourPoints.add(new LatLng(37.8699272719159, -122.26074501872061));
        bearTourPoints.add(new LatLng(37.86968721663429, -122.26070243865252));
        bearTourPoints.add(new LatLng(37.86950935819247, -122.26067092269658));
        bearTourPoints.add(new LatLng(37.86945192464557, -122.26062566041946));
        bearTourPoints.add(new LatLng(37.869399519803366, -122.2605401650071));
        bearTourPoints.add(new LatLng(37.86938205151436, -122.26042415946722));
        bearTourPoints.add(new LatLng(37.86938019881678, -122.26033195853232));
        // intersect of cross sproul path and lower sproul plaza path
        bearTourPoints.add(new LatLng(37.869395549738044, -122.26021897047758));
        bearTourPoints.add(new LatLng(37.869116321273786, -122.26016700267793));
        bearTourPoints.add(new LatLng(37.8692240427119, -122.2592691332102));
        bearTourPoints.add(new LatLng(37.86952973782737, -122.2593318298459));
        // UC berkeley visitor services
        bearTourPoints.add(new LatLng(37.87011942144545, -122.25945688784122));
        bearTourPoints.add(new LatLng(37.870123391471765, -122.25945722311735));
        bearTourPoints.add(new LatLng(37.870174472457975, -122.25921750068665));
        bearTourPoints.add(new LatLng(37.87018082449523, -122.25917156785727));
        bearTourPoints.add(new LatLng(37.870189558545576, -122.25913669914007));
        bearTourPoints.add(new LatLng(37.870200409940026, -122.2591256350279));
        bearTourPoints.add(new LatLng(37.870290132383964, -122.25913368165493));
        bearTourPoints.add(new LatLng(37.87033406722818, -122.25862976163626));
        // intersection at eschleman rd
        bearTourPoints.add(new LatLng(37.8704190254972, -122.25832164287569));
        bearTourPoints.add(new LatLng(37.87079538151066, -122.25846379995345));
        // south drive intersect
        bearTourPoints.add(new LatLng(37.87100182070155, -122.2585643827915));
        bearTourPoints.add(new LatLng(37.87113627053022, -122.25826296955347));
        // intersect campanile way
        bearTourPoints.add(new LatLng(37.87171482545806, -122.25847318768501));
        bearTourPoints.add(new LatLng(37.871717472084974, -122.25839406251909));
        // intersect campanile and s hall rd
        bearTourPoints.add(new LatLng(37.87175452485195, -122.25825257599354));
        // intersect glade path
        bearTourPoints.add(new LatLng(37.872977255707745, -122.25870922207834));
        bearTourPoints.add(new LatLng(37.872998428357675, -122.25860696285964));
        bearTourPoints.add(new LatLng(37.87323317970602, -122.2586941346526));
        bearTourPoints.add(new LatLng(37.87333057350698, -122.25874811410903));
        bearTourPoints.add(new LatLng(37.873416322290375, -122.25881818681955));
        bearTourPoints.add(new LatLng(37.87347110618304, -122.25881718099119));
        bearTourPoints.add(new LatLng(37.87350895203682, -122.25882221013308));
        bearTourPoints.add(new LatLng(37.873538328874936, -122.25882790982723));
        bearTourPoints.add(new LatLng(37.873611373935155, -122.25876923650502));
        bearTourPoints.add(new LatLng(37.873657953356044, -122.25859455764292));
        // next to building; star; inst of transport studies
        bearTourPoints.add(new LatLng(37.87388423364637, -122.25868172943592));
        bearTourPoints.add(new LatLng(37.873899583629196, -122.25860562175512));
        bearTourPoints.add(new LatLng(37.873761962979316, -122.25853219628335));
        bearTourPoints.add(new LatLng(37.87375349400784, -122.25851811468601));
        bearTourPoints.add(new LatLng(37.873751112109446, -122.25850269198419));
        bearTourPoints.add(new LatLng(37.873797691441965, -122.25832767784594));
        bearTourPoints.add(new LatLng(37.87379875006282, -122.25827906280756));
        bearTourPoints.add(new LatLng(37.87370479740302, -122.25825559347868));
        bearTourPoints.add(new LatLng(37.87311328987749, -122.25805040448903));
        // interesect university dr and carillon rd
        bearTourPoints.add(new LatLng(37.87307279724234, -122.2579186409712));
        // intersect before across campanile
        bearTourPoints.add(new LatLng(37.87252415954084, -122.25769031792879));
        bearTourPoints.add(new LatLng(37.87246196446397, -122.25792568176986));
        bearTourPoints.add(new LatLng(37.871642043180366, -122.25759845227005));
        bearTourPoints.add(new LatLng(37.871834982224634, -122.25739661604165));
        bearTourPoints.add(new LatLng(37.8720906455296, -122.2568581625819));
        bearTourPoints.add(new LatLng(37.87222271142349, -122.25646924227478));
        // intersect osuth dr and minor ln
        bearTourPoints.add(new LatLng(37.87231216674418, -122.2558231651783));
        bearTourPoints.add(new LatLng(37.872040095194954, -122.25569408386944));
        bearTourPoints.add(new LatLng(37.87184689202562, -122.25557237863542));
        // below: diagonal across
        bearTourPoints.add(new LatLng(37.872500075496035, -122.25510466843843));
        bearTourPoints.add(new LatLng(37.87250166345526, -122.25481398403645));
        bearTourPoints.add(new LatLng(37.87235054251413, -122.25463662296535));
        bearTourPoints.add(new LatLng(37.872219270832076, -122.25455615669489));
        // beginning of haas path
        bearTourPoints.add(new LatLng(37.87200780648138, -122.25453168153763));
        bearTourPoints.add(new LatLng(37.87201680497675, -122.2544042766094));
        bearTourPoints.add(new LatLng(37.87202421550153, -122.25436940789223));
        bearTourPoints.add(new LatLng(37.87203612527192, -122.25434627383946));
        bearTourPoints.add(new LatLng(37.87212319886813, -122.25425139069556));
        bearTourPoints.add(new LatLng(37.872130609382204, -122.25420009344815));
        bearTourPoints.add(new LatLng(37.87211949361081, -122.25416120141745));
        bearTourPoints.add(new LatLng(37.87209540943369, -122.25411124527456));
        bearTourPoints.add(new LatLng(37.87196651925321, -122.25400529801846));
        bearTourPoints.add(new LatLng(37.87193740645022, -122.25399021059275));
        bearTourPoints.add(new LatLng(37.8718892379691, -122.25397679954767));
        bearTourPoints.add(new LatLng(37.87172011871183, -122.25394930690528));
        bearTourPoints.add(new LatLng(37.87171535478345, -122.25379642099142));
        bearTourPoints.add(new LatLng(37.87172699994112, -122.2537524998188));
        bearTourPoints.add(new LatLng(37.87176643466595, -122.25367605686188));
        bearTourPoints.add(new LatLng(37.87178046177774, -122.25361537188292));
        bearTourPoints.add(new LatLng(37.87177702116566, -122.25354563444853));
        bearTourPoints.add(new LatLng(37.87175664215237, -122.2534976899624));
        bearTourPoints.add(new LatLng(37.87172038337449, -122.25347153842449));
        bearTourPoints.add(new LatLng(37.87162828070524, -122.25344371050598));
        bearTourPoints.add(new LatLng(37.871615047553625, -122.25341957062483));
        bearTourPoints.add(new LatLng(37.87161028361847, -122.25338101387023));
        bearTourPoints.add(new LatLng(37.87160340237827, -122.2531419619918));
        bearTourPoints.add(new LatLng(37.87159625647427, -122.25307960063219));
        bearTourPoints.add(new LatLng(37.8715832879801, -122.25303165614604));
        bearTourPoints.add(new LatLng(37.87153988320748, -122.25290827453135));
        bearTourPoints.add(new LatLng(37.871525591386494, -122.25277181714773));
        bearTourPoints.add(new LatLng(37.87146286613945, -122.2527687996626));
        // end memorial stadium
        bearTourPoints.add(new LatLng(37.87148112793574, -122.25265044718981));
        bearTourPoints.add(new LatLng(37.87103490385143, -122.25263804197313));
        bearTourPoints.add(new LatLng(37.871058988375225, -122.25233025848864));


        PolylineOptions bearOptions = new PolylineOptions().width(7).color(ContextCompat.getColor(context, R.color.BearsTourColor)).geodesic(true);
        for (int z = 0; z < bearTourPoints.size(); z++) {
            LatLng point = bearTourPoints.get(z);
            bearOptions.add(point);
        }
        bearOptions.visible(false);
        bearTourLine = mMap.addPolyline(bearOptions);



        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {

                for (Place place: places)  {
                    if (place.polygon.equals(polygon)) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
                        builder1.setCancelable(true);
//                        builder1.setInverseBackgroundForced(true);
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

    public void toggleTour(View v) {

        switch (v.getId()) {
            case (R.id.main_tour_button):
                if (mainTourLine.isVisible()) {
                    mainTourLine.setVisible(false);
                } else {
                    mainTourLine.setVisible(true);
                }
                chemEngTourLine.setVisible(false);
                bearTourLine.setVisible(false);
                break;
            case (R.id.chem_eng_tour_button):
                if (chemEngTourLine.isVisible()) {
                    chemEngTourLine.setVisible(false);
                } else {
                    chemEngTourLine.setVisible(true);
                }
                mainTourLine.setVisible(false);
                bearTourLine.setVisible(false);
                break;
            case (R.id.bears_tour_button):
                if (bearTourLine.isVisible()) {
                    bearTourLine.setVisible(false);
                } else {
                    bearTourLine.setVisible(true);
                }
                mainTourLine.setVisible(false);
                chemEngTourLine.setVisible(false);
                break;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mGroundOverlayRotated != null) {
            mGroundOverlayRotated.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    public void switchImage(View view) {
        mCurrentEntry = (mCurrentEntry + 1) % mImages.size();
        mGroundOverlay.setImage(mImages.get(mCurrentEntry));
    }



    /**
     * Toggles the visibility between 100% and 50% when a {@link GroundOverlay} is clicked.
     */
    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        // Toggle transparency value between 0.0f and 0.5f. Initial default value is 0.0f.
        mGroundOverlayRotated.setTransparency(0.5f - mGroundOverlayRotated.getTransparency());
    }

    /**
     * Toggles the clickability of the smaller, rotated overlay based on the state of the View that
     * triggered this call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mGroundOverlayRotated != null) {
            mGroundOverlayRotated.setClickable(((CheckBox) view).isChecked());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

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
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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