package teakdata.com.soundmonitor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SoundMonitor soundMonitor;
    private TextView statusTextView;
    private TextView soundLevelTextView;
    private ScheduledThreadPoolExecutor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusTextView = (TextView) findViewById(R.id.textView);
        soundLevelTextView = (TextView) findViewById(R.id.textView2);

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
            }, 0, 1, TimeUnit.SECONDS);
        }

        setStatus("monitoring started");
    }

    @Override
    protected void onPause() {
        super.onPause();

        soundMonitor.stop();
        soundMonitor = null;

        executor.shutdownNow();
        executor = null;

        setStatus("monitoring stopped");
    }

    private void updateLevel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                soundLevelTextView.setText("" + soundMonitor.getAmplitude() + "dB");
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
