package eu.opends.input.action.simulator;

import eu.opends.car.SteeringCar;
import eu.opends.input.SimulatorActionListener;

public class Reset_car_pos9 {
  
  public static void action(boolean value) {
    SteeringCar car = SimulatorActionListener.getCar();
    if (value)
    {
        //sim.getObjectManipulationCenter().setRotation("RoadworksSign1", new float[]{0,0,0});
        car.setToResetPosition(8);
    }
  }
}
