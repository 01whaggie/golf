package nl.maastrichtuniversity.golfbat;


import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.util.List;

import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView tv;
    private double[] previousCoordValues = new double[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        tv = (TextView)findViewById(R.id.TextId);
        tv.setVisibility(View.VISIBLE);

        previousCoordValues[0] = 0;
        previousCoordValues[1] = 0;
        previousCoordValues[2] = 0;

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tv.append("\n" + sensor.getName() + "\n" + sensor.getVendor() + "\n" + sensor.getVersion());

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        startServerThread();
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

    @Override
    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        tv = (TextView)findViewById(R.id.TextId);

        tv.setVisibility(View.VISIBLE);

        final double alpha = 0.8;

        double[] gravity = new double[3];
        double[] linear_acceleration = new double[3];

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];


        double deltaX = previousCoordValues[0] - linear_acceleration[0];
        double deltaY = previousCoordValues[1] - linear_acceleration[1];
        double deltaZ = previousCoordValues[2] - linear_acceleration[2];

        /*if (deltaX > 0.1)
            tv.append("\n deltaX = " + deltaX + "\n");
        if (deltaY > 0.1)
            tv.append("\n deltaY = " + deltaY  + "\n");
        if (deltaZ > 0.1)
            tv.append("\n deltaZ = " + deltaZ  + "\n");
        */

        synchronized (this) {
            this.x = deltaX;
            this.y = deltaY;
            this.z = deltaZ;
        }


        previousCoordValues[0] = linear_acceleration[0];
        previousCoordValues[1] = linear_acceleration[1];
        previousCoordValues[2] = linear_acceleration[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
    @Override
    protected void onResume() {
        super.onResume();

         }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void startServerThread() {
        tv.append(Utils.getIPAddress(true)); // IPv4

        Thread thread = new Thread(){
            public void run(){
                try {
                    String requestFromClient;
                    String coordsToClient;
                    ServerSocket welcomeSocket = new ServerSocket(7544);

                    while (true) {
                        Socket connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        requestFromClient = inFromClient.readLine();

                        synchronized (this) {
                            coordsToClient = x.toString() + ';' + y.toString() + ';' + z.toString();
                        }

                        outToClient.writeBytes(coordsToClient + '\n');
                    }
                }
                catch (Exception ex)
                {
                    tv.append(ex.getMessage());
                }
            }
        };

        thread.start();
    }

    private Double x;
    private Double y;
    private Double z;
}
