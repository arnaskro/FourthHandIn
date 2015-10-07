package ark.viauc.mobi.fourthhandin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class ServiceActivity extends AppCompatActivity {

    Button btnBack, btnStartService;
    public final static String BROADCAST_CURRENTTIME = "BROADCAST_CURRENTTIME";
    public final static String UPDATED_TIME = "UPDATED_TIME";
    private Spinner spinner;
    private String time;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                myStartService(time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        // Setup buttons
        setupButtons();

    }

    private void myStartService(String time) {
        Intent intent = new Intent(ServiceActivity.this, DeletePictures.class);
        switch(time) {
            case "5 minutes":
                intent.putExtra("TIME", 500);
                break;
            case "30 minutes":
                intent.putExtra("TIME", 3000);
                break;
            case "1 hour":
                intent.putExtra("TIME", 10000);
                break;
            case "24 hours":
                intent.putExtra("TIME", 240000);
                break;
            default:
                intent.putExtra("TIME", 10000);
                break;
        }
        startService(intent);
    }

    public void setupButtons() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStartService = (Button) findViewById(R.id.btnStartService);

        spinner = (Spinner) findViewById(R.id.time_picker);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = (String) spinner.getSelectedItem();
                myStartService(time);

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
