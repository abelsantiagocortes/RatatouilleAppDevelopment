package com.example.ratatouille.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatouille.Class.Agree;
import com.example.ratatouille.Class.Solicitud;
import com.example.ratatouille.Class.UserChef;
import com.example.ratatouille.Class.UserClient;
import com.example.ratatouille.R;
import com.example.ratatouille.permissions.PermissionIds;
import com.example.ratatouille.permissions.PermissionsActions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.localization.MapLocale;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;

import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;


public class MapsActivityOSM extends AppCompatActivity {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private static final LatLngBounds BOGOTA_BBOX = new LatLngBounds.Builder()
            .include(new LatLng(4.550024,  -74.127039))
            .include(new LatLng(4.766830,  -74.044250)).build();
    public static final MapLocale BOGOTAMAP = new MapLocale(MapLocale.SPANISH, BOGOTA_BBOX);

    private MapView mapView;
    private MarkerViewManager markerViewManager;
    private MarkerView markerViewChef;

    private SymbolManager symbolManager;
    private List<Symbol> symbols = new ArrayList<>();
    SymbolOptions markerClient, markerChef;

    private static final double RADIUS_OF_EARTH_KM = 6372.795;
    Agree acu;
    String idChef, idCliente;
    LatLng Chef = null, Cliente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token_OSM));
        setContentView(R.layout.activity_maps_osm);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);  //your choice t/f
                        symbolManager.setTextAllowOverlap(true);  //your choice t/f
                        Bitmap bmChef = BitmapFactory.decodeResource(getResources(), R.drawable.chef);
                        mapboxMap.getStyle().addImage("chefMarker",bmChef);
                        Bitmap bmCliente = BitmapFactory.decodeResource(getResources(), R.drawable.client);
                        mapboxMap.getStyle().addImage("clienteMarker",bmCliente);
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                        markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                        try {
                            localizationPlugin.matchMapLanguageWithDeviceDefault();
                            localizationPlugin.setCameraToLocaleCountry(BOGOTAMAP,0);
                            MarkerViewManager markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                        } catch (RuntimeException exception) {
                            Log.i("MAP Lang", exception.toString());
                        }
                    }
                });

            }
        });
        acu = (Agree) getIntent().getSerializableExtra("Agreement");
        String solicitud = acu.getSolicitudId();
        Query querySolicitud = FirebaseDatabase.getInstance().getReference("solicitud").orderByChild("idSolicitud").equalTo(solicitud);
        querySolicitud.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(PermissionsActions.checkPermission(this,PermissionIds.REQUEST_WRITE_EXTERNAL_STORAGE)){

        }
        if(PermissionsActions.checkPermission(this,PermissionIds.REQUEST_LOCATION)){

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists())
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Solicitud s = snapshot.getValue(Solicitud.class);
                    idChef = s.getIdChef();
                    idCliente = s.getIdCliente();
                    Query queryCliente = FirebaseDatabase.getInstance().getReference("userClient").orderByChild("userId").equalTo(idCliente);
                    queryCliente.addListenerForSingleValueEvent(valueEventListenerCliente);
                    Query queryChef = FirebaseDatabase.getInstance().getReference("userChef").orderByChild("userId").equalTo(idChef);
                    queryChef.addListenerForSingleValueEvent(valueEventListenerChef);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListenerCliente = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists())
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserClient s = snapshot.getValue(UserClient.class);
                    Cliente = new LatLng(s.getLat(), s.getLongi());
                    if(mapView != null){
                        markerClient = new SymbolOptions()
                                .withLatLng(Cliente)
                                .withIconImage("clienteMarker")
                                //set the below attributes according to your requirements
                                .withIconSize(0.4f)
                                .withIconOffset(new Float[] {0f,-1.5f})
                                .withTextField(s.getName())
                                .withTextHaloColor("rgba(255, 255, 255, 255)")
                                .withTextHaloWidth(5.0f)
                                .withTextAnchor("bottom")
                                .withTextOffset(new Float[] {0f, 1.5f})
                                .withDraggable(false)
                                .withTextSize(12f);
                        symbolManager.create(markerClient);
                    }
                    if(Chef != null)
                        generarRuta();
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListenerChef = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists())
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserChef s = snapshot.getValue(UserChef.class);
                    Chef = new LatLng(s.getLat(), s.getLongi());
                    if(mapView != null) {
                        markerChef = new SymbolOptions()
                                .withLatLng(Chef)
                                .withIconImage("chefMarker")
                                //set the below attributes according to your requirements
                                .withIconSize(0.4f)
                                .withIconOffset(new Float[] {0f,-1.5f})
                                .withTextField(s.getName())
                                .withTextHaloColor("rgba(255, 255, 255, 255)")
                                .withTextHaloWidth(5.0f)
                                .withTextAnchor("bottom")
                                .withTextOffset(new Float[] {0f, 1.5f})
                                .withDraggable(false)
                                .withTextSize(12f);
                        symbolManager.create(markerChef);
                    }
                    if(Cliente != null)
                        generarRuta();
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void generarRuta() {

    }

}