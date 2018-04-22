package cz.ruzickalukas.sensorsgames.treasure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.hardware.SensorEventListener;

import cz.ruzickalukas.sensorsgames.R;

class CompassView extends View implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationValues = new float[3];
    private boolean firstCall = false;

    private int compassSize;
    private Bitmap bmp;
    private int px, py;
    private float degreeRotation = 0.0f;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        compassSize = (int)getResources().getDimension(R.dimen.compass_size);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.compass), compassSize, compassSize, false);
    }



    void init(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    void register() {
        mSensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregister() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = event.values.clone();
                break;
            default:
                return;
        }
        if(!firstCall) {
            updateDirection();
            firstCall = true;
        } else {
            firstCall = false;
        }
    }

    private void updateDirection() {
        boolean successful = SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerData, magnetometerData);
        if (successful) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
            degreeRotation = (float) Math.toDegrees(orientationValues[0]);
            this.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        px = compassSize / 2;
        py = compassSize / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(-degreeRotation, px, py);
        canvas.drawBitmap(bmp, 0, 0, null);
        canvas.restore();
    }
}
