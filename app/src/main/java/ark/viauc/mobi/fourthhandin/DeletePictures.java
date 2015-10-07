package ark.viauc.mobi.fourthhandin;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class DeletePictures extends Service {
    private long deletePictureTime;

    public DeletePictures() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deletePictureTime = intent.getIntExtra("TIME", 10000);
        new GetCurrentTimeAndDeleteOldPictures().execute();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class GetCurrentTimeAndDeleteOldPictures extends AsyncTask<Integer, Void, Long> {

        long currentTime;

        @Override
        protected void onPreExecute() {
            currentTime = parseLong(new SimpleDateFormat("yyyMMddHHmmss").format(new Date()));
        }

        @Override
        protected Long doInBackground(Integer... params) {
            long newTime = parseLong(new SimpleDateFormat("yyyMMddHHmmss").format(new Date()));
            while (newTime - currentTime < 100) {
                newTime = parseLong(new SimpleDateFormat("yyyMMddHHmmss").format(new Date()));
            }
            return currentTime;
        }

        @Override
        protected void onPostExecute(Long time) {
            deleteImages(time);
        }
    }

    private void deleteImages(Long time) {

        Intent intent = new Intent(ServiceActivity.BROADCAST_CURRENTTIME);
        File dirPictures = getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + Intro.PICTURES_DIR);
        // find pictures
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            // Go trough all images and look for ones with geotag
            if (dirPictures.exists()) {
                File[] files = dirPictures.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".jpg")) {

                        String imgName = file.getName().replace(".jpg", "");
                        imgName = imgName.replace("_", "");
                        imgName = imgName.replace("IMG", "");

                        Long imgTime = parseLong(imgName);

                        if (time - imgTime > deletePictureTime)
                            file.delete();

                    }
                }
            } else {
                Log.d("ark", "pictures dir doesnt exists!");
            }
        }


        sendBroadcast(intent);
    }
}
