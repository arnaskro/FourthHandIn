package ark.viauc.mobi.fourthhandin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Intro extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        View.OnClickListener {

    final static String PICTURES_DIR = "Pictures4handIn";
    final static String IMAGE_NAME = "IMAGE_NAME";
    final static String CAMERA_PREFS = "CAMERA_PREFS";
    final static String FILENAME = "FILENAME";
    public static final String LAST_PICTURE_TIME = "LAST PICTURE TIME";
    final static int REQUEST_FILE = 100;
    final static int RESULT_TRUE = -1;
    final static int RESULT_FALSE = 0;

    private GoogleMap gmap;

    Button btnTakePicture, btnService;
    File dirPictures;
    ImageView ivLastImage;
    TextView txtOrientation, txtLatitude, txtLongtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Check acces to external storage
        checkAccess();

        // Setup directory
        setDirectory();

        // Setup buttons
        setupButtons();

        // Setup text views
        setupTextViews();

        // Setup image view for last image
        ivLastImage = (ImageView) findViewById(R.id.ivLastImage);

        // Map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void setupTextViews() {
        txtOrientation = (TextView) findViewById(R.id.txtOr);
        txtLatitude = (TextView) findViewById(R.id.txtLat);
        txtLongtitude = (TextView) findViewById(R.id.txtLong);
    }

    private void setDirectory() {
        dirPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + PICTURES_DIR);
        dirPictures.mkdirs();
    }


    private void checkAccess() {
        if (isExternalStorageWritable() && isExtrenalStorageReadable()) {
            Toast.makeText(this, "External storage is accessible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "External storage is not accessible!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        btnService = (Button) findViewById(R.id.btnSettings);
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentService = new Intent(Intro.this, ServiceActivity.class);
                startActivity(intentService);

            }
        });

        btnTakePicture = (Button) findViewById(R.id.btnTakePicutre);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create filename
                String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
                String filename = dirPictures.getPath() + File.separator + "IMG_" + timestamp + ".jpg";
                File imagefile = new File(filename);
                imagefile.delete();
                try {
                    imagefile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri imageuri = Uri.fromFile(imagefile);

                // Save last attempted image capture filename
                saveAttemptFilename(filename);

                // Take picture
                takePicture(imageuri);
            }
        });
    }

    private void saveAttemptFilename(String filename) {
        SharedPreferences prefs = getSharedPreferences(CAMERA_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(FILENAME, filename);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String timeText = df.format(new Date());
        edit.putString(LAST_PICTURE_TIME, timeText);
        edit.apply();
    }

    private String loadAttemptFilename() {
        String timestamp = new SimpleDateFormat("yyyMMddHHmmss").format(new Date());
        String otherFilename = dirPictures.getPath() + File.separator + "IMG_" + timestamp + ".jpg";

        SharedPreferences prefs = getSharedPreferences(CAMERA_PREFS, MODE_PRIVATE);
        return prefs.getString(FILENAME, otherFilename);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (status== ConnectionResult.SUCCESS)
            Toast.makeText(this, "Google Play Services Are Available", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Google Play Services Are NOT Available!", Toast.LENGTH_LONG).show();

        String filename = loadAttemptFilename();
        setPicture(filename, ivLastImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE && resultCode == RESULT_TRUE) {
            String filename = loadAttemptFilename();

            setPicture(filename, ivLastImage);
        }

    }

    private void setPicture(String filename, ImageView ivLastImage) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, bmOptions);

        int scaleFactor = Math.min(bmOptions.outWidth / 200, bmOptions.outHeight / 200);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(filename, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(filename);

            // Set texts
            txtOrientation.setText("Orientation: \n" + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));

            float[] latlong = new float[2];
            if(exif.getLatLong(latlong)){
                txtLatitude.setText("Latitude:\n"+latlong[0]);
                txtLongtitude.setText("Longtitude: \n"+latlong[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ivLastImage.setRotation(90);
        ivLastImage.setImageBitmap(bitmap);
    }

    private void takePicture(Uri imageuri) {
        Intent intentGoToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentGoToCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);

        if (intentGoToCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentGoToCamera, REQUEST_FILE);
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExtrenalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);

        myFindImagesWithGeoTagAndAddToGmap(googleMap);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // filename
        String imgName = marker.getTitle();
        Intent inAbout = new Intent(Intro.this, ImageDetails.class);
        inAbout.putExtra(IMAGE_NAME, imgName);

        // Launch intent
        startActivity(inAbout);

        return true;
    }

    private void myFindImagesWithGeoTagAndAddToGmap(GoogleMap gmap) {

        // Check storage is mounted
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            // Go trough all images and look for ones with geotag
            if (dirPictures.exists()) {
                Log.d("ark", "pictures dir exists!");
                File[] files = dirPictures.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".jpg")) {

                        // Get position
                        LatLng pos = getLatLongFromExif(file.getAbsolutePath());

                        // Add geotag to google map if position is not null
                        if (pos != null) {
                            addGeoTag(pos, file.getName(), gmap);
                        }
                    }
                }
            } else {
                Log.d("ark", "pictures dir doesnt exists!");
            }
        }
    }

    private void addGeoTag(LatLng pos, String filename, GoogleMap gmap) {
        gmap.setMyLocationEnabled(true);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13));
        gmap.addMarker(new MarkerOptions().position(pos)).setTitle(filename);
    }

    private LatLng getLatLongFromExif(String filename) {
        float[] latlong = new float[2];
        LatLng pos      = null;

        try {

            ExifInterface exif = new ExifInterface(filename);
            if (exif.getLatLong(latlong)) {
                pos = new LatLng(latlong[0], latlong[1]);
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return pos;
    }

}





















