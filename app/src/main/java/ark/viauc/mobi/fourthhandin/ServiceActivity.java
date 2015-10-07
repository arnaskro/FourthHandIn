package ark.viauc.mobi.fourthhandin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ServiceActivity extends AppCompatActivity {

    Button btnBack, btnUpdate;

    public final static String BROADCAST_CURRENTTIME = "BROADCAST_CURRENTTIME";
    public final static String UPDATED_TIME = "UPDATED_TIME";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle!=null) {
                String currentTime = bundle.getString(UPDATED_TIME);

                    Log.d("ark", "Updated time: " + currentTime);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        // Setup buttons
        setupButtons();

    }

    public void setupButtons() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(ServiceActivity.this, DeletePictures.class);
                ServiceActivity.this.startService(updateIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(BROADCAST_CURRENTTIME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
