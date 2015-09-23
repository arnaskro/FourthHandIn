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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity {

    final static String CAMERA_PREFS = "CAMERA_PREFS";
    final static String FILENAME = "FILENAME";
    final static int REQUEST_FILE = 100;
    final static int RESULT_TRUE = -1;
    final static int RESULT_FALSE = 0;
    Button btnTakePicture;
    File dirPictures;
    ImageView ivLastImage;
    TextView txtOrientation, txtLatitude, txtLongtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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
    }

    private void setupTextViews() {
        txtOrientation = (TextView) findViewById(R.id.txtOr);
        txtLatitude = (TextView) findViewById(R.id.txtLat);
        txtLongtitude = (TextView) findViewById(R.id.txtLong);
    }

    private void setDirectory() {
        dirPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + "4HandIn");
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
        edit.apply();
    }

    private String loadAttemptFilename() {
        String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String otherFilename = dirPictures.getPath() + File.separator + "IMG_" + timestamp + ".jpg";

        SharedPreferences prefs = getSharedPreferences(CAMERA_PREFS, MODE_PRIVATE);
        return prefs.getString(FILENAME, otherFilename);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            txtOrientation.setText("Orientation: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
            txtLatitude.setText("Latitude: " + exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, -1));
            txtLongtitude.setText("Longtitude: " + exif.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, -1));

        } catch (IOException e) {
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
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExtrenalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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
}
