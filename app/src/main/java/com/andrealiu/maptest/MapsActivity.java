package com.andrealiu.maptest;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        // Add a marker in Sydney and move the camera
        LatLng statue = new LatLng(37.873046331455676 , -122.26484343409538);
        mMap.addMarker(new MarkerOptions().position(statue).title("Li Ka Shing Statue"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(statue));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.873046331455676, -122.26484343409538), 18));
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

        Polygon polygon = mMap.addPolygon(new PolygonOptions()
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

        polygon.setClickable(true);
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {
                LatLng iLatlng = new LatLng(37.872933057781424, -122.26527929306029);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(iLatlng)
                        .title("Li Ka Shing")
                        .snippet("Description Here")
                        );
                marker.setAlpha(0.0f);
                marker.showInfoWindow();

            }
        });


    }

}
