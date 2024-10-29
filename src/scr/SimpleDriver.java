package scr;

public class SimpleDriver extends Controller {

	final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
	final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };

	public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		System.out.println("Bye bye!");
	}

	private NearestNeighbor knn;
	private int k; 

	public SimpleDriver(int k) {

		if((k%2 != 0) && (k%2 > 0)){
			this.knn = new NearestNeighbor("dataset.csv");
			this.k = k;
		} else {
			System.out.println("Valore di k non valido");
			System.exit(1); 
		}	
	}


	private int getGear(SensorModel sensors) {
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		if (gear < 1)
			return 1;

		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		else

		if (gear > 1 && rpm <= gearDown[gear - 1])
			return gear - 1;
		else
			return gear;
	}

	private float currBrake = (float) 0.3;
	private float brake(boolean reset) {
		if(reset){
			currBrake = 0;
		} else {
			currBrake += 0.1;
		}
		return currBrake > 1 ? 1 : currBrake;
	}

	private float currSteer = (float) 0;
	private float steer(boolean reset, boolean right) {
		if(reset){
			currSteer = 0;
		} else if (right){
			if (currSteer>0) currSteer = (float) -0.1;
			else currSteer -= 0.01;
		} else {
			if (currSteer<0) currSteer = (float) 0.1;
			else currSteer += 0.01;
		}
		return currSteer > 0.2 ? (float) 0.2 : (currSteer < -0.2 ? (float) -0.2 : currSteer);
	}

	public Action control(SensorModel sensors) {

		Action action = new Action();
		action.gear = getGear(sensors); 

		double[] features = new double[]{
			sensors.getAngleToTrackAxis(), 

			sensors.getSpeed(), 
			sensors.getLateralSpeed(),
			
			sensors.getTrackEdgeSensors()[3], 
			sensors.getTrackEdgeSensors()[5],
			sensors.getTrackEdgeSensors()[9], 
			sensors.getTrackEdgeSensors()[13], 
			sensors.getTrackEdgeSensors()[15],
			
			sensors.getTrackPosition()
		};
		Sample corrente = new Sample(features);

		int cls = knn.classify(corrente, k);

		switch (cls) {
			case 0:
				brake(true);
				steer(true, false);
				break;

			case 1:
				action.accelerate = 1;
				brake(true);
				steer(true, false);
				break;
			
			case 2:
				action.accelerate = 0.8;
				brake(true);
				action.steering = steer(false, false);
				break;
			
			case 3:
				action.accelerate = 0.8;
				brake(true);
				action.steering = steer(false, true);
				break;
			
			case 4:
				brake(true);
				action.steering = steer(false, false);
				break;
			
			case 5:
				float tmp = brake(false);
				if (tmp > 0.5) {
					if (sensors.getSpeed() > 85) action.brake = 0.5;
					else if (sensors.getSpeed() > 65) action.brake = 0.75;
				} else {
					action.brake = brake(false);
				}
				steer(true, false);
				break;
				
			case 6:
				brake(true);
				action.steering = steer(false, true);
				break;
			
			case 7:
				action.gear = -1;
				action.accelerate = 1;
				action.steering = 0.80;
				brake(true);
				steer(true, false);
				break;
			
			case 8:
				action.gear = -1;
				action.accelerate = 1;
				brake(true);
				steer(true, false);
				break;
			
			case 9:
				action.gear = -1;
				action.accelerate = 1;
				action.steering = -0.80;
				brake(true);
				steer(true, false);
				break;
		}
		return action;
	}
	
	public float[] initAngles() {

		float[] angles = new float[19];

		for (int i = 0; i < 5; i++) {
			angles[i] = -90 + i * 15;
			angles[18 - i] = 90 - i * 15;
		}

		for (int i = 5; i < 9; i++) {
			angles[i] = -20 + (i - 5) * 5;
			angles[18 - i] = 20 - (i - 5) * 5;
		}
		angles[9] = 0;
		return angles;
	}
}
