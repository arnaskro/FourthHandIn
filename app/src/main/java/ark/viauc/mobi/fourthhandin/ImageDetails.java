package ark.viauc.mobi.fourthhandin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ImageDetails extends AppCompatActivity {

    public final static String NOTES_DIR = "NOTES_DIR";
    private String imgName, txtName;
    private Button btnBack, btnSave;
    private File dirPictures;
    private ImageView imgView;
    private EditText txtField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        // Intent setup
        setupIntent();

        // Image setup
        setupImage();

        // Notes setup
        setupNotes();

        // Button setup
        setupButtons();
    }

    private void setupNotes() {
        txtField = (EditText) findViewById(R.id.txtNotes);

        if (imgName.endsWith(".jpg")) txtName = imgName.replace(".jpg", ".txt");

        //Set the text
        txtField.setText(readFromFile(txtName));
    }

    private void setupImage() {

        imgView = (ImageView) findViewById(R.id.imgPic);
        dirPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + Intro.PICTURES_DIR);

        String filename = dirPictures + File.separator + imgName;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, bmOptions);

        int scaleFactor = Math.min(bmOptions.outWidth / 200, bmOptions.outHeight / 200);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(filename, bmOptions);


        imgView.setRotation(90);
        imgView.setImageBitmap(bitmap);

    }

    private void setupButtons() {

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotes();

                // TODO do something after a button was clicked?
            }
        });


    }

    private void saveNotes() {
        Log.d("ark", txtField.getText().toString());
        writeToFile(txtName, txtField.getText().toString());
    }

    private void setupIntent() {

        Intent anIntent = getIntent();

        if (anIntent.hasExtra(Intro.IMAGE_NAME)) {
            imgName = anIntent.getStringExtra(Intro.IMAGE_NAME);
            Toast.makeText(this, imgName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No image. Probably an error!", Toast.LENGTH_LONG).show();
        }
    }

    private void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
