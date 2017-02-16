package com.tpl.secondtangramdemo;

import android.content.res.Configuration;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.MapController;
import com.mapzen.tangram.MapView;
import com.mapzen.tangram.Marker;
import com.mapzen.tangram.TouchInput;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MapView.OnMapReadyCallback, View.OnClickListener,
        Switch.OnCheckedChangeListener {

    MapController mapController;
    MapView mapView;

    //HttpHandler httpHandler;
    CustomHttpHandler httpHandler;


    Marker pointMarker;
    Marker lineMarker;
    Marker polygonMarker;


    boolean isChangedFile;
    File sceneFile;
    File[] applicationFiles;


    String pointStyle = "{ style: 'points', color: 'white', size: [40px, 40px], order: 2000, collide: false }";
    //    String lineStyle = "{ style: 'lines', color: '#06a6d4', width: 5px, order: 2000 }";
    //    String polygonStyle = "{ style: 'polygons', color: '#06a6d4', width: 5px, order: 2000 }";
    //  String pointStyle =  "draw: droppin: priority: 0.5 sprite: SPRITE size: [[3, 44px], [21, 44px]];

    Button clearButton;
    Button changeSceneFileButton;
    Switch changeSceneSwitch;
    Switch show3dBuildings;
    TextView zoomTextView;
    Marker current;

    ArrayList<LngLat> taps = new ArrayList<>();
    private String[] filesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isChangedFile = false;
        clearButton = (Button) findViewById(R.id.reset_marker);
        changeSceneFileButton = (Button) findViewById(R.id.change_scene_file);
        zoomTextView = (TextView) findViewById(R.id.zoom_text_view);

        changeSceneSwitch = (Switch) findViewById(R.id.change_scene);
        show3dBuildings = (Switch) findViewById(R.id.show_3d_buildings);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this, "bubble-wrap/bubble-wrap.yaml");

        clearButton.setOnClickListener(this);
        changeSceneFileButton.setOnClickListener(this);

        changeSceneSwitch.setOnCheckedChangeListener(this);
        show3dBuildings.setOnCheckedChangeListener(this);

        setSceneFile();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(final MapController mapCont) {

        mapController = mapCont;
        httpHandler = new CustomHttpHandler();

        mapController.setZoom(16);

        mapController.setPosition(new LngLat(-74.00976419448854, 40.70532700869127));

        File cacheDir = getExternalCacheDir();
        httpHandler.setCache(new File(cacheDir, "map_cached"), 1024 * 1024 * 40);
        mapController.setHttpHandler(httpHandler);


       /* if(mapController.getZoom()>12){
         }*/

        mapController.loadSceneFile(sceneFile.getPath());

        //this.mapController.setZoom(16);
           /* LngLat pos= new LngLat(33.689280, 73.031763);
            this.mapController.setPosition(pos);
            //this.mapController.
            this.mapController.setZoom(10);
            this.mapController.applySceneUpdates();*/


        resetMarkers();


        mapController.setTapResponder(new TouchInput.TapResponder() {
            @Override
            public boolean onSingleTapUp(float x, float y) {
                LngLat tap = mapController.screenPositionToLngLat(new PointF(x, y));
                taps.add(tap);
                if (current == pointMarker) {
                    pointMarker.setPoint(tap);
                    taps.clear();
                } /*else if (current == lineMarker && taps.size() >= 2) {
                    lineMarker.setPolyline(new Polyline(taps, null));
                    taps.remove(0);
                } else if (current == polygonMarker && taps.size() >= 3) {
                    ArrayList<List<LngLat>> polygon = new ArrayList<>();
                    polygon.add(taps);
                    polygonMarker.setPolygon(new Polygon(polygon, null));
                    taps.remove(0);
                }*/
                mapController.requestRender();
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(float x, float y) {
                return false;
            }
        });

        mapController.setFeaturePickListener(new MapController.FeaturePickListener() {
            @Override
            public void onFeaturePick(Map<String, String> properties, final float positionX, final float positionY) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Tap on : " + positionX + "  " + positionY, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mapController.addMarker();


    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        if (!checked) {
            return;
        }

        taps.clear();

        switch (view.getId()) {
            case R.id.radio_points:
                current = pointMarker;
                break;
            case R.id.radio_lines:
                current = lineMarker;
                break;
            case R.id.radio_polygons:
                current = polygonMarker;
                break;
        }
    }

    void resetMarkers() {
        mapController.removeAllMarkers();
        //pointMarker = null;
        lineMarker = null;
        polygonMarker = null;


        pointMarker = mapController.addMarker();
        pointMarker.setStyling(pointStyle);
        pointMarker.setDrawable(R.drawable.checkin);

//        lineMarker = mapController.addMarker();
//        lineMarker.setStyling(lineStyle);
//
//        polygonMarker = mapController.addMarker();
//        polygonMarker.setStyling(polygonStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_marker:
                resetMarkers();
                break;
            case R.id.change_scene_file:

                //method to set new scene file
                changeSceneFile();
                mapController.loadSceneFile(sceneFile.getPath());

                break;

        }
    }


    /**
     * change the scene file
     */
    private void changeSceneFile() {

        if (isChangedFile) {
            sceneFile = new File(filesList[2]);
            isChangedFile = false;
        } else {
            sceneFile = new File(filesList[3]);
            isChangedFile = true;
        }

    }


    /**
     * set scene file
     */
    private void setSceneFile() {
        try {
            applicationFiles = getApplicationContext().getFilesDir().listFiles();
            this.filesList = getApplicationContext().getAssets().list("");
            sceneFile = new File(filesList[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.change_scene:
                if (isChecked) {
                    mapController.queueSceneUpdate("layers.buildings.draw.polygons.color", "[.60, .63, .90]");
                  } else {

                    mapController.queueSceneUpdate("layers.buildings.draw.polygons.color",  getSelectedSceneFileColor());
                }
                mapController.applySceneUpdates();

                break;
            case R.id.show_3d_buildings:
                if (isChecked) {
                    mapController.queueSceneUpdate("layers.buildings.draw.polygons.visible", "true");
                } else {
                    mapController.queueSceneUpdate("layers.buildings.draw.polygons.visible", "false");
                }
                mapController.applySceneUpdates();
                break;
        }

    }

    private String getSelectedSceneFileColor() {

        if (isChangedFile){
            return "[.93, .93, .60]";
        }else {
            return "[.63, .83, .76]";
        }
    }



}