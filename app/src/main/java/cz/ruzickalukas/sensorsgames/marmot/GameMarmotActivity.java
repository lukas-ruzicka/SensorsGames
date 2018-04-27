package cz.ruzickalukas.sensorsgames.marmot;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.ruzickalukas.sensorsgames.R;

public class GameMarmotActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor stepDetector;
    private Sensor stepCounter;

    private MarmotManager marmotManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_marmot);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        FrameLayout gameLayout = findViewById(R.id.gameMarmotLayout);
        TextView score = findViewById(R.id.score);
        TextView time = findViewById(R.id.time);
        marmotManager = new MarmotManager(this, gameLayout, score, time);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.game_instructions_title))
                .setMessage(getResources().getString(R.string.marmot_instructions))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marmotManager.notWaitingAnymore();
                        Toast.makeText(GameMarmotActivity.this,
                                "You can start moving now", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, stepDetector,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                if (!marmotManager.isWaiting()) {
                    if (!marmotManager.isRunning()) {
                        marmotManager.startGame();
                    } else {
                        marmotManager.addStep(System.currentTimeMillis());
                    }
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onBackPressed() {
        marmotManager.pauseGame();
        onPause();
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.game_exit_title))
                .setMessage(getResources().getString(R.string.game_exit_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marmotManager.removeMessages(MarmotManager.UPDATE_TIME);
                        marmotManager.removeMessages(MarmotManager.ADD_NEW_MARMOT);
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                        marmotManager.resumeGame();
                    }
                })
                .show();
    }
}
