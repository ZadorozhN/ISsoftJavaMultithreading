package org.zadorozhn;

import org.zadorozhn.building.Building;
import org.zadorozhn.building.Controller;
import org.zadorozhn.building.Elevator;
import org.zadorozhn.human.Human;
import org.zadorozhn.util.HumanGenerator;
import org.zadorozhn.util.UserInterface;

public class Runner {
    public static void main(String[] args){
        Building building = Building.of(10)
                .setController(Controller.getEmpty())
                .addElevator(Elevator.of(5000,0,100))
                .addElevator(Elevator.of(5000,0,100));
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100))
//                .addElevator(Elevator.of(5000,0,100));

        //HumanGenerator humanGenerator = HumanGenerator.of(building, 50, 200, 100);
        UserInterface userInterface = UserInterface.of(building, 500);

        building.addHuman(Human.of(100, 2, building.getFloor(5)));
        building.addHuman(Human.of(100, 1, building.getFloor(4)));

        //humanGenerator.start();
        userInterface.start();
        building.start();
    }
}
