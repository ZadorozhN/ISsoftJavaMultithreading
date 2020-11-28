package org.zadorozhn.util;

import org.zadorozhn.building.Building;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.building.state.State;

public class UserInterface {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[1;41m\u001B[97m";
    public static final String ANSI_GREEN = "\u001B[1;42m\u001B[97m";
    public static final String ANSI_YELLOW = "\u001B[1;43m\u001B[97m";
    public static final String ANSI_BLUE = "\u001B[1;44m\u001B[97m";
    public static final String ANSI_PURPLE = "\u001B[1;45m\u001B[97m";
    public static final String ANSI_CYAN = "\u001B[1;46m\u001B[97m";
    public static final String ANSI_WHITE = "\u001B[1;47m\u001B[97m";

    public static void printBuilding(Building building) {
        String COLOR = ANSI_WHITE;
        System.out.printf("Delivered: %s\n", StatisticsHolder.getInstance().getNumberOfDeliveredPeople());
        System.out.printf("Generated: %s\n", StatisticsHolder.getInstance().getNumberOfGeneratedPeople());
        System.out.printf("Floors passed: %s\n", StatisticsHolder.getInstance().getNumberOfPassedFloors());
        for (int i = building.getNumberOfFloors() - 1; i >= 0; i--) {
            for (int j = 0; j < building.getElevators().size(); j++) {
                if (building.getElevators().get(j).getCurrentFloorNumber() == i) {
                    if (building.getElevators().get(j).getState() == State.LOAD) {
                        COLOR = ANSI_BLUE;
                    } else if (building.getElevators().get(j).getState() == State.CLOSE_DOOR) {
                        COLOR = ANSI_RED;
                    } else if (building.getElevators().get(j).getState() == State.OPEN_DOOR) {
                        COLOR = ANSI_GREEN;
                    } else if (building.getElevators().get(j).getState() == State.STOP) {
                        COLOR = ANSI_CYAN;
                    } else if (building.getElevators().get(j).getState() == State.MOVE) {
                        COLOR = ANSI_PURPLE;
                    }
                    System.out.printf(COLOR + "|%d%4s|" + ANSI_RESET,
                            building.getElevators().get(j).getNumberOfPeople(),
                            building.getElevators().get(j).getDirection());
                } else {
                    System.out.printf("|%5s|", " ");
                }
            }
            System.out.printf("%s%d UP) %s%s\t", ANSI_PURPLE, (building.getFloor(i).getFloorNumber()),
                    building.getFloor(i).getNumberOfPeople(Direction.UP), ANSI_RESET);
            System.out.printf("%s%d DOWN) %s%s\n", ANSI_CYAN, (building.getFloor(i).getFloorNumber()),
                    building.getFloor(i).getNumberOfPeople(Direction.DOWN), ANSI_RESET);
        }

        System.out.println(building.getController().getAllCalls());
        for (int i = 0; i < building.getElevators().size(); i++) {
            System.out.println(i + " " + building.getElevators().get(i).toString());
        }
    }
}
