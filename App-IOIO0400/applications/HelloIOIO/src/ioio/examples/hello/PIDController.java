package ioio.examples.hello;

public class PIDController {

	private double P; //factor for proportional control
	private double I; //factor for integral control
	private double D; //factor for derivative control
	private double input; //sensor input
	private double maximumOutput = 1;
	private double minimumOutput= 1;
	private double maximumInput =0;
	private double minimumInput=0;
	private boolean continuous =false;
	private boolean enabled = false;
	private double prevErr = 0;
	private double totalErr = 0;
	private double tolerance = 0.05;
	private double setpoint = 0;
	private double err = 0;
	private double result = 0;
	
	public PIDController(double Kp, double Ki, double kd){
		P = Kp;
		I = Ki;
		D = kd;
	}
public void calculate() {
	if (enabled) {
		err = setpoint -input;
				System.out.println(setpoint);
        if (continuous) {
            if (Math.abs(err) > (maximumInput - minimumInput) / 2) {
                if (err > 0) {
                    err = err - maximumInput + minimumInput;
                }
                else {
                    err = err + maximumInput - minimumInput;

	}
      }
        }
        if (((totalErr + err) * I<maximumOutput) && ((totalErr + err) * I>minimumOutput)) {
        	totalErr += err;
        }
        result = (P*err + I*totalErr + D*(err-prevErr));
        prevErr=err;
        if(result>maximumOutput){
        	result=maximumOutput;
        }
        else if (result<minimumOutput){
        	result=minimumOutput;
        }
	}
   }    
	 public void setPID(double p, double i, double d){
		 P=p;
		 I=i;
		 D=d;
	 }
	 public double getP(){
		 return P;
	 }
	 public double getI(){
		 return I;
	 }
		 public double getD(){
	 return D;
		 }
		public double performPID(){  
			calculate();
			return result;
		}
		public void setContinuous(boolean new_continuous){
		continuous = new_continuous;	
		}
		public void setContinuous(){
			this.setContinuous(true);
		}
		public void setInputRange(double new_minimumInput, double new_maximumInput) {
			minimumInput= new_minimumInput;
			maximumInput= new_maximumInput;
			setSetpoint(setpoint);
		}
		public void setOutputRange(double new_minimumOutput, double new_maximumOutput) {
			minimumOutput= new_minimumOutput;
			maximumOutput= new_maximumOutput;
		}
			public void setSetpoint(double new_setpoint){
				if(maximumInput>minimumInput){
					if(new_setpoint>maximumInput){
						setpoint=maximumInput;
					}
					else if (new_setpoint<minimumInput){
				setpoint=minimumInput;
					}
				}
					else{
						setpoint= new_setpoint;
					}
			}
				public double getSetpoint(){
					return setpoint;
				}
				public synchronized double getErr(){
					return err;
				}
				public void setTolerance(double percent){
					tolerance=percent;
				}
				public boolean onTarget(){
					return (Math.abs(err)<tolerance/100 * (maximumInput - minimumInput));
				}
				public void enable() {
					enabled = true;
				}
				public void disable (){
					enabled = false;
				}
				public void resest(){
					disable();
					prevErr = 0;
					totalErr = 0;
					result = 0;
				}
				public void getInput (double new_input){
					input = new_input;
				}
				
				}
	 
