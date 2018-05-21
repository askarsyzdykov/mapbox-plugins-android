package com.mapbox.mapboxsdk.plugins.testapp.activity.clustering;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin;
import com.mapbox.mapboxsdk.plugins.testapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity showcasing clustering Markers
 * <p>
 * Inspired by https://github.com/googlemaps/android-maps-utils.
 * </p>
 */
public class MarkerClusterActivity extends AppCompatActivity implements OnMapReadyCallback {

  @BindView(R.id.map_view)
  MapView mapView;

  private MapboxMap mapboxMap;
  private ClusterManagerPlugin<MyItem> mClusterManagerPlugin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cluster);
    ButterKnife.bind(this);

    mapView.setStyleUrl(Style.DARK);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @Override
  public void onMapReady(MapboxMap mapboxMap) {
    this.mapboxMap = mapboxMap;
    startDemo();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
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

  protected void startDemo() {
    mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

    mClusterManagerPlugin = new ClusterManagerPlugin<>(this, mapboxMap);
    mClusterManagerPlugin.setRenderer(new IconClusterRenderer<>(this, mapboxMap, mClusterManagerPlugin));
    mapboxMap.setOnCameraIdleListener(mClusterManagerPlugin);

    try {
      readItems();
    } catch (JSONException exception) {
      Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
    }
  }

  private void readItems() throws JSONException {
    InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
    List<MyItem> items = new MyItemReader().read(this, inputStream);
    mClusterManagerPlugin.addItems(items);
  }

  public class MyItemReader {

    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

    private Bitmap getMarkerIcon(Context context, String iconName) {
      Resources resources = context.getResources();
      final int resourceId = resources.getIdentifier(iconName, "drawable", context.getPackageName());

      Drawable drawable = resources.getDrawable(resourceId);

      int width = drawable.getIntrinsicWidth();
      int height = drawable.getIntrinsicHeight();
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);

      return bitmap;
    }

    public List<MyItem> read(Context context, InputStream inputStream) throws JSONException {
      List<MyItem> items = new ArrayList<MyItem>();
      String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
      JSONArray array = new JSONArray(json);
      for (int i = 0; i < array.length(); i++) {
        String title = null;
        String snippet = null;
        String iconName = null;
        JSONObject object = array.getJSONObject(i);
        double lat = object.getDouble("lat");
        double lng = object.getDouble("lng");
        if (!object.isNull("title")) {
          title = object.getString("title");
        }
        if (!object.isNull("snippet")) {
          snippet = object.getString("snippet");
        }
        if (!object.isNull("icon")) {
          iconName = object.getString("icon");
        }

        IconFactory iconFactory = IconFactory.getInstance(context);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .snippet(snippet);
        if (iconName != null) {
            markerOptions.icon(iconFactory.fromBitmap(getMarkerIcon(context, iconName)));
        }
        Marker marker = new Marker(markerOptions);
        items.add(new MyItem(marker));
      }
      return items;
    }
  }
}