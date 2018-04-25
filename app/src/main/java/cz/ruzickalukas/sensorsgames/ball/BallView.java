package cz.ruzickalukas.sensorsgames.ball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.hardware.SensorEventListener;

import cz.ruzickalukas.sensorsgames.R;

public class BallView extends View implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer;

    private Bitmap bmp;
    private int ballSize;
    private float xPos, yPos;
    private float xSpeed, ySpeed;
    private float xMax, yMax;
    private static final float FRAME_TIME = 0.666f;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ballSize = (int)context.getResources().getDimension(R.dimen.ball_size);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ball), ballSize, ballSize, false);
        xPos = 10;
        yPos = 10;
    }

    void init(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        xMax = display.getWidth() - ballSize;
        yMax = display.getHeight() - ballSize;
        invalidate();
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

        xPos -= (xSpeed/2) * FRAME_TIME;
        yPos += (ySpeed/2) * FRAME_TIME;

        // Barrier left
        if (xPos < 0) {
            xSpeed = 0;
            xPos = 0;
        }
        // Barrier right
        else if (xPos > xMax) {
            xSpeed = 0;
            xPos = xMax;
        }
        // Barrier up
        if (yPos < 0) {
            ySpeed = 0;
            yPos = 0;
        }
        // Barrier down
        else if (yPos > yMax) {
            ySpeed = 0;
            yPos = yMax;
        }

        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, xPos, yPos, null);
    }
}
