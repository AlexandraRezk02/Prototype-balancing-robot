package ioio.examples.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;



/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity implements SensorEventListener{
	private PIDController aPIDcontroller;
	private SensorManager mSensorManager;
	private SensorFusion sensorFusion; 
	private ToggleButton button_;
	private EditText EditText;
	private Button btn;
	private int  PulseWidth;
	private int  alpha;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	final float[] deltaRotationVector = new float[4];
	
	PIDController controller=new PIDController(240,80,120);


	TextView ACC_X;
	TextView ACC_Y;
	TextView ACC_Z;
	
	
	TextView GYRO_X;
	TextView GYRO_Y;
	TextView GYRO_Z;
	
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
		
		sensorFusion = new SensorFusion();
		sensorFusion.setMode(SensorFusion.Mode.ACC_MAG);
		
		ACC_X=(TextView) findViewById(R.id.ACC_X);
		ACC_Y=(TextView) findViewById(R.id.ACC_Y);

		ACC_Z=(TextView) findViewById(R.id.ACC_Z);

		GYRO_X=(TextView) findViewById(R.id.GYRO_X);

		GYRO_Y=(TextView) findViewById(R.id.GYRO_Y);

		GYRO_Z=(TextView) findViewById(R.id.GYRO_Z);

		
		//PulseWidth=1500;
		EditText=(android.widget.EditText) findViewById(R.id.edittext);
		button_ = (ToggleButton) findViewById(R.id.button);
		btn=(Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);  
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		private PwmOutput pwmoutput_;
		private PwmOutput pwmoutput2_;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			led_ = ioio_.openDigitalOutput(0, true);
			pwmoutput_=ioio_.openPwmOutput(11, 100);
			pwmoutput2_=ioio_.openPwmOutput(13, 100);
			
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			led_.write(!button_.isChecked());

			pwmoutput_.setPulseWidth(1400-PulseWidth);
		    pwmoutput2_.setPulseWidth(1600-PulseWidth);
			
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
public void updateOrientationDisplay() {
	double asimuthValue = sensorFusion.getAzimuth();
	double rollValue = sensorFusion.getRoll ();
	double pitchValie = sensorFusion.getPitch ();
	
    }
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		switch (event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			sensorFusion.setAccel(event.values);
			sensorFusion.calculateAccMagOrientation();
			break;
			
		case Sensor.TYPE_GYROSCOPE:
			sensorFusion.gyroFunction(event);
			break;
			
		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorFusion.setMagnet(event.values);
			break;
		}
		updateOrientationDisplay();

		//alpha is a time constant (subject to change)
		//final double alpha= 0.8;
		//double[] gravity= null;
		//double[] linear_acceleration= null;
		//double accAngle;
		
		//Isolates the force of gravity with LPF
		//gravity[0] = alpha * gravity[0] + (1-alpha) * event.values[0];
		//gravity[1] = alpha * gravity[1] + (1-alpha) * event.values[1];
		//gravity[2] = alpha * gravity[2] + (1-alpha) * event.values[2];
		//removing the force of gravity with HPF
		//linear_acceleration[0] = event.values[0] - gravity[0];
		//linear_acceleration[1] = event.values[1] - gravity[1];
		//linear_acceleration[2] = event.values[2] - gravity[2];
		
		//accAngle= asin (linear_acceleration[0]/10);
				
			if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
			ACC_X.setText(String.valueOf(event.values[0]));
			ACC_Y.setText(String.valueOf(event.values[1]));
			ACC_Z.setText(String.valueOf(event.values[2]));
			}
			
			// This timestep's delta rotation to be multiplied by the current rotation
			  // after computing it from the gyro sample data.
			  //if (timestamp != 0) {
			    //final float dT = (event.timestamp - timestamp) * NS2S;
			    // Axis of the rotation sample, not normalized yet.
			    //float axisX = event.values[0];
			    //float axisY = event.values[1];
			    //float axisZ = event.values[2];

			    // Calculate the angular speed of the sample
			    //float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

			    // Normalize the rotation vector if it's big enough to get the axis
			    // (that is, EPSILON should represent your maximum allowable margin of error)
			    //double EPSILON= 0.2;
			    //if (omegaMagnitude > EPSILON) {
			      //axisX /= omegaMagnitude;
			      //axisY /= omegaMagnitude;
			      //axisZ /= omegaMagnitude;
			    //}

			    // Integrate around this axis with the angular speed by the timestep
			    // in order to get a delta rotation from this sample over the timestep
			    // We will convert this axis-angle representation of the delta rotation
			    // into a quaternion before turning it into the rotation matrix.
			    //float thetaOverTwo = omegaMagnitude * dT / 2.0f;
			    //float sinThetaOverTwo = sin(thetaOverTwo);
			    //float cosThetaOverTwo = cos(thetaOverTwo);
			    //deltaRotationVector[0] = sinThetaOverTwo * axisX;
			    //deltaRotationVector[1] = sinThetaOverTwo * axisY;
			    //deltaRotationVector[2] = sinThetaOverTwo * axisZ;
			    //deltaRotationVector[3] = cosThetaOverTwo;
			  //}
			  //timestamp = event.timestamp;
			  //float[] deltaRotationMatrix = new float[9];
			  //double rotationCurrent = 0;
			  //SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
			    // User code should concatenate the delta rotation we computed with the current rotation
			    // in order to get the updated rotation.
			  //float[] values = null;
			  //SensorManager.getOrientation (deltaRotationMatrix, values);
			  //float x=0;
			  //for(int i = 0; i<4;i++){
				// x = deltaRotationMatrix[i];
			  //}
			  //rotationCurrent =  rotationCurrent * x; //deltaRotationMatrix;
		if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
			GYRO_X.setText(String.valueOf(event.values[0]));
			GYRO_Y.setText(String.valueOf(event.values[1]));
			GYRO_Z.setText(String.valueOf(event.values[2]));
		controller.enable();
		controller.setSetpoint(0);
		controller.setInputRange(0, event.values[0]);
		controller.setTolerance(5);
		controller.calculate();
		controller.setContinuous();
		double PIDOutput = controller.performPID();
		PulseWidth = (int) (50*PIDOutput);
		}
		
	}

	

	
	
	//private double asin(double d) {
		//return d;
		// TODO Auto-generated method stub
		
	//}

	//private float cos(float thetaOverTwo) {
		// TODO Auto-generated method stub
		//return 0;
	//}

	//private float sin(float thetaOverTwo) {
		// TODO Auto-generated method stub
		//return 0;
	//}

	//private float sqrt(float f) {
		// TODO Auto-generated method stub
		//return 0;
	//}
	
}

 
 