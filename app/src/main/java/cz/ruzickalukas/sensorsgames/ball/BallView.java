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
import android.view.Display;
import android.view.View;
import android.hardware.SensorEventListener;

import cz.ruzickalukas.sensorsgames.R;

public class BallView extends View implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer;

    private Bitmap bmp;
    private float xPos, yPos;
    private float xSpeed, ySpeed;
    private float xMax, yMax;
    private static final float FRAME_TIME = 0.666f;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.compass), 50, 50, false);
        xPos = 5;
        yPos = 5;
    }

    void init(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        xMax = display.getWidth() - 50;
        yMax = display.getHeight() - 50;
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
        yPos -= (ySpeed/2) * FRAME_TIME;

        if (xPos < 0) {
            xPos = 0;
        } else if (xPos > xMax) {
            xPos = xMax;
        }

        if (yPos < 0) {
            yPos = 0;
        } else if (yPos > yMax) {
            yPos = yMax;
        }

        this.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, xPos, yPos, null);
    }
}
