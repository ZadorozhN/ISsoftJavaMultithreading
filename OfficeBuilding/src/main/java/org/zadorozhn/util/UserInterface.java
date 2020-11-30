package org.zadorozhn.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zadorozhn.building.Building;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.util.interrupt.Interruptable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class UserInterface extends Thread implements Interruptable {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[1;41m\u001B[97m";
    public static final String ANSI_GREEN = "\u001B[1;42m\u001B[97m";
    public static final String ANSI_YELLOW = "\u001B[1;43m\u001B[97m";
    public static final String ANSI_BLUE = "\u001B[1;44m\u001B[97m";
    public static final String ANSI_PURPLE = "\u001B[1;45m\u001B[97m";
    public static final String ANSI_CYAN = "\u001B[1;46m\u001B[97m";
    public static final String ANSI_WHITE = "\u001B[1;47m\u001B[97m";

    @Getter
    private boolean isRunning;
    private final Building building;
    private final int renderingSpeed;

    private UserInterface(Building building, int renderingSpeed){
        checkNotNull(building);
        checkArgument(renderingSpeed <= MAX_SPEED && renderingSpeed >= MIN_SPEED);

        this.building = building;
        this.renderingSpeed = renderingSpeed;

        String threadName = "userInterface";
        this.setName(threadName);
    }

    public static UserInterface of(Building building, int renderingSpeed){
        return new UserInterface(building, renderingSpeed);
    }

    public void printBuilding() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String color = ANSI_WHITE;
        System.out.printf("Delivered: %s\n", StatisticsHolder.getInstance().getNumberOfDeliveredPeople());
        System.out.printf("Generated: %s\n", StatisticsHolder.getInstance().getNumberOfGeneratedPeople());
        System.out.printf("Floors passed: %s\n", StatisticsHolder.getInstance().getNumberOfPassedFloors());

        for (int i = building.getNumberOfFloors() - 1; i >= 0; i--) {
            for (int j = 0; j < building.getElevators().size(); j++) {
                if (building.getElevators().get(j).getCurrentFloorNumber() == i) {
                    switch (building.getElevators().get(j).getState()) {
                        case OPEN_DOOR:
                            color = ANSI_GREEN;
                            break;
                        case CLOSE_DOOR:
                            color = ANSI_RED;
                            break;
                        case LOAD:
                            color = ANSI_BLUE;
                            break;
                        case STOP:
                            color = ANSI_CYAN;
                            break;
                        case MOVE:
                            color = ANSI_PURPLE;
                            break;
                        case END:
                            color = ANSI_YELLOW;
                            break;
                    }
                    System.out.printf(color + "|%d%4s|" + ANSI_RESET,
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

    @SneakyThrows
    @Override
    public void run(){
        turnOn();
        while (isRunning){
            TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - renderingSpeed);
            printBuilding();
        }
    }

    @Override
    public void turnOff() {
        isRunning = false;
    }

    @Override
    public void turnOn() {
        isRunning = true;
    }
}
