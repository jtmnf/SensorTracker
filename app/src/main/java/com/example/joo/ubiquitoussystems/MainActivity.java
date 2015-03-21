package com.example.joo.ubiquitoussystems;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    private float max[] = {0, 0, 0};
    private float values[] = {0, 0};
    private Button buttonReset;

    private ScrollView parent;
    private ScrollView child;

    private int step = 0;

    private TextView X;
    private TextView Y;
    private TextView Z;

    private TextView Mx;
    private TextView My;
    private TextView Mz;

    private TextView Sensors;
    private TextView Step;

    private TextView SensorTypeOne;
    private TextView SensorTypeTwo;

    private TextView Latitude;
    private TextView Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setContentView(R.layout.activity_main);
        SensorTypeOne = (TextView)findViewById(R.id.sensorOne);
        SensorTypeTwo = (TextView)findViewById(R.id.sensorTwo);

        Latitude = (TextView)findViewById(R.id.latitudeValue);
        Longitude = (TextView)findViewById(R.id.longitudeValue);

        X = (TextView)findViewById(R.id.xId);
        Y = (TextView)findViewById(R.id.yId);
        Z = (TextView)findViewById(R.id.zId);
        Mx = (TextView)findViewById(R.id.mxId);
        My = (TextView)findViewById(R.id.myId);
        Mz = (TextView)findViewById(R.id.mzId);

        getListOfSensors();
        gpsConnection();

        buttonReset = (Button)findViewById(R.id.reset);
        parent = (ScrollView)findViewById(R.id.parent);
        child = (ScrollView)findViewById(R.id.child);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < max.length; ++i){
                    max[i] = 0;
                }
                step = 0;
            }
        });

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float average = 0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        if(event.sensor.getName().equals(mAccelerometer.getName())){
            SensorTypeOne.setText(""+event.sensor.getName());
            X.setText("X: "+getStringFormated(df.format(event.values[0]))+"\t Max: "+df.format(getHigher(0, event.values[0])));
            Y.setText("Y: "+getStringFormated(df.format(event.values[1]))+"\t Max: "+df.format(getHigher(1, event.values[1])));
            Z.setText("Z: "+getStringFormated(df.format(event.values[2]-9.7))+"\t Max: "+df.format(getHigher(2, event.values[2])));
        }
        if (event.sensor.getName().equals(mMagnetic.getName())) {
            SensorTypeTwo.setText(""+event.sensor.getName());
            float[] x = event.values;

            switch (x.length){
                case 1:
                    Mx.setText("Mx: " + event.values[0]);
                    My.setText("My: -");
                    Mz.setText("Mz: -");
                    break;
                case 2:
                    Mx.setText("Mx: " + event.values[0]);
                    My.setText("My: " + event.values[1]);
                    Mz.setText("Mz: -");
                    break;
                case 3:
                    Mx.setText("Mx: " + event.values[0]);
                    My.setText("My: " + event.values[1]);
                    Mz.setText("Mz: " + event.values[2]);
                    break;
            }
        }
    }

    private float getHigher(int value, float newMax){
        if(max[value] > newMax){
            return max[value];
        }
        else{
            max[value] = newMax;
            return max[value];
        }
    }

    private String getStringFormated(String string){
        if(string.length() == 1){
            return string+",00";
        }
        return string;
    }

    private void getListOfSensors(){
        final List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        int i;

        for(i = 0; i < deviceSensors.size(); ++i){
            Button b = new Button(this);

            b.setText(deviceSensors.get(i).getName());
            LinearLayout LL = (LinearLayout)findViewById(R.id.layoutButton);
            LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final int aux = i;

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {;
                    mMagnetic = deviceSensors.get(aux);
                    onResume();
                }
            });

            LL.addView(b, LP);
        }
    }

    private void gpsConnection(){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Latitude.setText(""+location.getLatitude());
                Longitude.setText(""+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}
 