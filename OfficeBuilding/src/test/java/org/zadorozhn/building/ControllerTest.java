package org.zadorozhn.building;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.human.Human;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ControllerTest {
    public static final int VALID_FIRST_TARGET_FLOOR_NUMBER = 5;
    public static final int VALID_SECOND_TARGET_FLOOR_NUMBER = 8;
    public static final int VALID_START_FLOOR_NUMBER = 3;
    public static final int VALID_ELEVATOR_CAPACITY = 200;
    public static final int VALID_WEIGHT = 100;
    public static final int NUMBER_OF_FLOORS = 10;
    public static Building building;

    @BeforeEach
    void init() {
        building = Building.of(NUMBER_OF_FLOORS);
    }

    @Test
    void getAllCallsTest() {
        Controller controller = Controller.getEmpty();
        building.addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY)).setController(controller);

        Human firstHuman = Human.of(VALID_WEIGHT,
                VALID_FIRST_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));
        Human secondHuman = Human.of(VALID_WEIGHT,
                VALID_SECOND_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        building.addHuman(firstHuman);
        building.addHuman(secondHuman);

        controller.addCall(firstHuman.getCall());
        controller.addCall(secondHuman.getCall());

        assertThat(controller.getAllCalls(),
                containsInAnyOrder(Call.of(firstHuman.getStartFloor(), firstHuman.getCall().getDirection()),
                        firstHuman.getCall(), secondHuman.getCall()));
    }

    @Test
    void addCallTest() {
        Controller controller = Controller.getEmpty();
        building.addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY)).setController(controller);
        Human firstHuman = Human.of(VALID_WEIGHT,
                VALID_FIRST_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        controller.addCall(firstHuman.getCall());

        assertThat(controller.getAllCalls(), hasItem(firstHuman.getCall()));
    }

    @Test
    void removeCallTest() {
        Controller controller = Controller.getEmpty();
        building.addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY)).setController(controller);
        Human firstHuman = Human.of(VALID_WEIGHT,
                VALID_FIRST_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        controller.addCall(firstHuman.getCall());
        controller.removeCall(firstHuman.getCall());

        assertThat(controller.getAllCalls(), not(hasItem(firstHuman.getCall())));
    }

    @Test
    void dispatchCallTest() {
        Controller controller = Controller.getEmpty();
        Elevator elevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Human firstHuman = Human.of(VALID_WEIGHT,
                VALID_FIRST_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        building.addElevator(elevator).setController(controller);
        building.addHuman(firstHuman);

        controller.dispatchCall();

        assertThat(controller.getAllCalls(), not(hasItem(firstHuman.getCall())));
        assertThat(elevator.getCalls(), hasItem(Call.of(firstHuman.getStartFloor(),
                firstHuman.getCall().getDirection())));
    }

    @Test
    void dispatchCallToIdleElevatorTest() {
        Controller controller = Controller.getEmpty();
        Elevator stoppedElevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Elevator movingElevator = Elevator.of(VALID_ELEVATOR_CAPACITY);

        Building.of(NUMBER_OF_FLOORS).setController(controller)
                .addElevator(stoppedElevator)
                .addElevator(movingElevator);

        Call call = Call.of(VALID_SECOND_TARGET_FLOOR_NUMBER, VALID_START_FLOOR_NUMBER);

        movingElevator.goUp();

        controller.addCall(call);
        controller.dispatchCall();

        assertThat(controller.getAllCalls(), not(hasItem(call)));
        assertThat(stoppedElevator.getCalls(), hasItem(call));
        assertThat(movingElevator.getCalls(), not(hasItem(call)));
    }

    @Test
    void dispatchCallToTheNearestElevatorTest() {
        int firstFloor = 0;
        int secondFloor = 1;

        Controller controller = Controller.getEmpty();
        Elevator farthestElevator = Elevator.of(VALID_ELEVATOR_CAPACITY, firstFloor);
        Elevator nearestElevator = Elevator.of(VALID_ELEVATOR_CAPACITY, secondFloor);

        Building.of(NUMBER_OF_FLOORS).setController(controller)
                .addElevator(nearestElevator)
                .addElevator(farthestElevator);

        Call call = Call.of(VALID_SECOND_TARGET_FLOOR_NUMBER, VALID_START_FLOOR_NUMBER);

        controller.addCall(call);
        controller.dispatchCall();

        assertThat(controller.getAllCalls(), not(hasItem(call)));
        assertThat(nearestElevator.getCalls(), hasItem(call));
        assertThat(farthestElevator.getCalls(), not(hasItem(call)));
    }

    @Test
    void doNotDispatchCallToNotSuitableElevatorTest() {
        int firstFloor = 5;
        int secondFloor = 1;

        Controller controller = Controller.getEmpty();
        Elevator firstElevator = Elevator.of(VALID_ELEVATOR_CAPACITY, firstFloor);
        Elevator secondElevator = Elevator.of(VALID_ELEVATOR_CAPACITY, secondFloor);

        Building.of(NUMBER_OF_FLOORS).setController(controller)
                .addElevator(firstElevator)
                .addElevator(secondElevator);

        firstElevator.goUp();
        secondElevator.goUp();

        Call call = Call.of(VALID_SECOND_TARGET_FLOOR_NUMBER, Direction.DOWN);

        controller.addCall(call);
        controller.dispatchCall();

        assertThat(controller.getAllCalls(), hasItem(call));
        assertThat(firstElevator.getCalls(), not(hasItem(call)));
        assertThat(secondElevator.getCalls(), not(hasItem(call)));
    }

    @Test
    void turnOnTest() {
        Controller controller = Controller.getEmpty();

        controller.turnOn();

        assertThat(controller.isRunning(), equalTo(true));
    }

    @Test
    void turnOffTest() {
        Controller controller = Controller.getEmpty();

        controller.turnOn();
        controller.turnOff();

        assertThat(controller.isRunning(), equalTo(false));
    }
}
