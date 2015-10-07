package ark.viauc.mobi.fourthhandin;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class DeletePictures extends Service {
    public DeletePictures() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new GetCurrentTimeAndDeleteOldPictures().execute();
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class GetCurrentTimeAndDeleteOldPictures extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Integer... params) {
            return new SimpleDateFormat("yyyMMddHHmmss").format(new Date());
        }

        @Override
        protected void onPostExecute(String time) {
            Log.d("ark", "time: " + time);
            publishTime(time);
        }
    }

    private void publishTime (String time) {
        Intent intent = new Intent(ServiceActivity.BROADCAST_CURRENTTIME);
        intent.putExtra(ServiceActivity.UPDATED_TIME, time);
        sendBroadcast(intent);
    }
}
