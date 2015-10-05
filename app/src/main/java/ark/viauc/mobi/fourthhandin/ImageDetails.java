package ark.viauc.mobi.fourthhandin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ImageDetails extends AppCompatActivity {

    private String imgName;
    private Button btnBack, btnSave;
    private File dirPictures;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        // Button setup
        setupButtons();

        // Intent setup
        setupIntent();

        // Image setup
        setupImage();
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

                // TODO: save notes

            }
        });


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
}
