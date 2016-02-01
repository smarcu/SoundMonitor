package teakdata.com.soundmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SoundMonitor soundMonitor;
    private TextView statusTextView;
    private TextView soundLevelTextView;
    private TextView soundMaxLevelTextView;
    private TextView soundAvgLevelTextView;
    private ScheduledThreadPoolExecutor executor;
    private SoundVolumeMonitor soundVolumeMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusTextView = (TextView) findViewById(R.id.textViewStatus);
        soundLevelTextView = (TextView) findViewById(R.id.textViewRealtimeValue);
        soundMaxLevelTextView = (TextView) findViewById(R.id.textViewMaxValue);
        soundAvgLevelTextView = (TextView) findViewById(R.id.textViewAvgValue);

        Button resetButton = (Button) findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundVolumeMonitor.reset();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        setStatus("initializing ...");
        if (soundMonitor == null) {
            soundMonitor = new SoundMonitor();
            soundMonitor.start();
        }

        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(1);
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    updateLevel();
                }
            }, 0, 200, TimeUnit.MILLISECONDS);
        }

        if (soundVolumeMonitor == null) {
            soundVolumeMonitor = new SoundVolumeMonitor(300);
        }

        setStatus("monitoring");
    }

    @Override
    protected void onPause() {
        super.onPause();

        soundMonitor.stop();
        soundMonitor = null;

        executor.shutdownNow();
        executor = null;

        setStatus("stopped");
    }

    private void updateLevel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double value = soundMonitor.getDbLevel();
                soundLevelTextView.setText(String.format( "%.0f", value ));
                soundVolumeMonitor.addVolume(value);
                soundMaxLevelTextView.setText(String.format( "%.0f", soundVolumeMonitor.getMax()));
                soundAvgLevelTextView.setText(String.format( "%.0f", soundVolumeMonitor.getAvg()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void setStatus(String text) {
        statusTextView.setText(text);
    }
}
