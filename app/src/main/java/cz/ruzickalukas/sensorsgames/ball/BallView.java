package cz.ruzickalukas.sensorsgames.ball;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.hardware.SensorEventListener;

import cz.ruzickalukas.sensorsgames.R;
import cz.ruzickalukas.sensorsgames.main.GameStatus;

public class BallView extends View implements SensorEventListener {

    private Activity activity;
    private SensorManager mSensorManager;
    private Sensor accelerometer;

    private TrackView track;

    private Bitmap bmp;
    private float xPos, yPos;
    private float xSpeed, ySpeed;
    private static final float FRAME_TIME = 0.666f;

    private long startTime;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    void init(Activity activity) {
        this.activity = activity;

        Display display = activity.getWindowManager().getDefaultDisplay();

        float cellWidth = (float)display.getWidth() / 14;
        float cellHeight = (float)display.getHeight() / 25;

        track = activity.findViewById(R.id.track);
        track.init(activity, cellWidth, cellHeight, display.getWidth(), display.getHeight());

        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.ball), (int) cellWidth, (int) cellHeight, false);
        xPos = cellWidth / 2;
        yPos = cellHeight / 2;

        invalidate();

        startTime = System.currentTimeMillis();
    }

    void register() {
        mSensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    void unregister() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            updateBall(event.values[0], event.values[1]);
        }
    }

    private void updateBall(float x, float y) {
        xSpeed += x * FRAME_TIME;
        ySpeed += y * FRAME_TIME;

        float lastX = xPos;
        float lastY = yPos;

        xPos -= (xSpeed/2) * FRAME_TIME;
        yPos += (ySpeed/2) * FRAME_TIME;

        if (track.checkBarrier(xPos, yPos)) {
            switch (track.getBarrierDirection(xPos, yPos, lastX, lastY)) {
                case TrackView.X_DIRECTION:
                    xSpeed = 0;
                    xPos = lastX;
                    break;
                case TrackView.Y_DIRECTION:
                    ySpeed = 0;
                    yPos = lastY;
                    break;
                case TrackView.BOTH_DIRECTIONS:
                    xSpeed = 0;
                    ySpeed = 0;
                    xPos = lastX;
                    yPos = lastY;
                    break;
            }
        }

        if (track.checkHole(xPos, yPos)) {
            gameOver();
        }

        invalidate();
    }

    private void gameOver() {
        float time = (float)(System.currentTimeMillis() - startTime) / 1000.0f;
        GameStatus.updateScore(activity, R.string.ball, time, true);

        unregister();

        final BallView ballView = this;
        new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.game_over_title))
                .setMessage(String.format(activity.getResources()
                        .getString(R.string.game_over_ball), time))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.play_again_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ballView.init(activity);
                                register();
                            }
                        })
                .setNegativeButton(activity.getResources().getString(R.string.go_back_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                .show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, xPos, yPos, null);
    }
}
