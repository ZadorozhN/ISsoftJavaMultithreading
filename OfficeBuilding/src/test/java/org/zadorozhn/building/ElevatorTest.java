package org.zadorozhn.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.building.state.State;
import org.zadorozhn.human.Human;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ElevatorTest {
    public static final int VALID_CAPACITY = 100;
    public static final int VALID_LARGE_CAPACITY = 300;
    public static final int INVALID_ZERO_CAPACITY = 0;
    public static final int INVALID_NEGATIVE_CAPACITY = 0;
    public static final int NUMBER_OF_FLOORS = 10;
    public static Building building;

    @BeforeEach
    void initBuilding() {
        building = Building.of(NUMBER_OF_FLOORS).setController(Controller.getEmpty());
    }

    static Object[] getInvalidElevatorCharacteristics() {
        return new Object[][]{
                {INVALID_NEGATIVE_CAPACITY},
                {INVALID_ZERO_CAPACITY}
        };
    }

    static Object[][] getPickUpHumanTestData() {
        return new Object[][]{
                {55, 9, 8},
                {60, 7, 5},
                {70, 1, 3},
                {80, 3, 0}
        };
    }

    static Object[][] getDisembarkHumanTestData() {
        return new Object[][]{
                {50, 9, 8},
                {60, 7, 5},
                {70, 1, 3},
                {80, 3, 0}
        };
    }

    static Object[][] getCheckFloorWithPickingUpTestData() {
        return new Object[][]{
                {60, 4, 2, 7, 1},
                {70, 8, 6, 9, 5},
                {80, 9, 8, 9, 7},
                {60, 8, 7, 8, 6},
                {70, 9, 7, 9, 6},
                {80, 6, 5, 8, 4}
        };
    }

    static Object[][] getCheckFloorWithoutPickingUpTestData() {
        return new Object[][]{
                {60, 1, 2, 7, 1},
                {70, 3, 5, 9, 4},
                {80, 4, 7, 9, 6},
                {60, 3, 7, 9, 6},
                {70, 2, 7, 9, 6},
                {80, 1, 6, 8, 5}
        };
    }

    static Object[][] getPassengersTestData(){
        return new Object[][]{
                {50, 1, 2, 2},
                {50, 2, 3, 3},
                {50, 4, 5, 5},
                {50, 6, 3, 3}
        };
    }

    static Object[][] getLoadTestData(){
        return new Object[][]{
                {60, 7, 5, 8, 5},
                {60, 8, 3, 4, 3},
                {60, 2, 4, 1, 4},
                {60, 8, 2, 7, 2},
                {60, 2, 9, 6, 9}
        };
    }

    static Object[][] getLoadWithoutPickingUpTestData(){
        return new Object[][]{
                {60, 4, 5, 8, 4},
                {60, 2, 3, 8, 2},
                {60, 2, 4, 8, 3},
                {60, 1, 2, 8, 1},
                {60, 3, 8, 9, 7}
        };
    }

    static Object[][] getLoadWithPickingUpTestData(){
        return new Object[][]{
                {60, 7, 5, 8, 4},
                {60, 8, 3, 4, 2},
                {60, 7, 4, 6, 3},
                {60, 8, 2, 7, 1},
                {60, 7, 6, 9, 5}
        };
    }

    @Test
    void createValidElevatorTest() {
        assertDoesNotThrow(() -> Elevator.of(VALID_CAPACITY));
    }

    @ParameterizedTest
    @MethodSource("getInvalidElevatorCharacteristics")
    void createInvalidElevatorTest(int capacity) {
        assertThrows(IllegalArgumentException.class,
                () -> Elevator.of(capacity));
    }

    @Test
    void goUpTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        elevator.goUp();

        assertThat(elevator.getDirection(), equalTo(Direction.UP));
        assertThat(elevator.getState(), equalTo(State.MOVE));
    }

    @Test
    void goDownTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        elevator.goUp();
        elevator.goDown();

        assertThat(elevator.getDirection(), equalTo(Direction.DOWN));
        assertThat(elevator.getState(), equalTo(State.MOVE));
    }

    @Test
    void openDoorTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        elevator.openDoor();

        assertThat(elevator.getState(), equalTo(State.OPEN_DOOR));
    }

    @Test
    void closeDoorTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        elevator.closeDoor();

        assertThat(elevator.getState(), equalTo(State.CLOSE_DOOR));
    }

    @Test
    void endTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        elevator.end();

        assertThat(elevator.getDirection(), equalTo(Direction.NONE));
        assertThat(elevator.getState(), equalTo(State.END));
    }

    @Test
    void getControllerWithoutBuildingTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);

        assertThrows(NullPointerException.class, elevator::getController);
    }

    @Test
    void getControllerWhenBuildingHasControllerTest() {
        Elevator elevator = Elevator.of(VALID_CAPACITY);
        building.addElevator(elevator);

        assertDoesNotThrow(elevator::getController);
    }

    @ParameterizedTest
    @MethodSource("getPickUpHumanTestData")
    void pickUpHumanTest(int weight, int targetFloor, int startFloor) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, startFloor);
        building.addElevator(elevator);
        Human human = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(human);

        elevator.pickUpHuman(human);

        assertThat(elevator.getPassengers(), contains(human));
    }

    @ParameterizedTest
    @MethodSource("getDisembarkHumanTestData")
    void disembarkHumanTest(int weight, int targetFloor, int startFloor) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, startFloor);
        building.addElevator(elevator);
        Human human = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(human);

        elevator.pickUpHuman(human);
        elevator.disembark(human);

        assertThat(elevator.getPassengers(), not(contains(human)));
    }

    @ParameterizedTest
    @MethodSource("getPickUpHumanTestData")
    void loadWithNoSpaceTest(int weight, int targetFloor, int startFloor) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, startFloor);
        building.addElevator(elevator);
        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));
        Human secondHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(firstHuman);
        building.addHuman(secondHuman);

        elevator.load();

        assertThat(elevator.getPassengers(), hasItem(firstHuman));
        assertThat(elevator.getPassengers(), not(hasItem(secondHuman)));
        assertThat(building.getController().getAllCalls(),
                hasItems(Call.of(startFloor, firstHuman.getCall().getDirection())));
    }

    @ParameterizedTest
    @MethodSource("getCheckFloorWithPickingUpTestData")
    void checkFloorWithPickingUpTest(int weight, int targetFloor, int startFloor,
                                            int elevatorCallTargetFloorNumber, int elevatorStartFloorNumber) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, elevatorStartFloorNumber);
        building.addElevator(elevator);

        elevator.addCall(Call.of(elevatorCallTargetFloorNumber, Direction.UP));
        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(firstHuman);

        elevator.goUp();

        assertThat(elevator.checkFloor(), equalTo(true));
    }


    @ParameterizedTest
    @MethodSource("getCheckFloorWithoutPickingUpTestData")
    void checkFloorWithoutPickingUpTest(int weight, int targetFloor, int startFloor,
                                               int elevatorCallTargetFloorNumber, int elevatorStartFloorNumber) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, elevatorStartFloorNumber);
        building.addElevator(elevator);

        elevator.addCall(Call.of(elevatorCallTargetFloorNumber, Direction.UP));
        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(firstHuman);

        elevator.goUp();

        assertThat(elevator.checkFloor(), equalTo(false));
    }

    @ParameterizedTest
    @MethodSource("getPassengersTestData")
    void getPassengersTest(int weight, int targetFloor, int startFloor, int elevatorStartFloorNumber) {
        Elevator elevator = Elevator.of(VALID_LARGE_CAPACITY, elevatorStartFloorNumber);
        building.addElevator(elevator);

        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));
        Human secondHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));
        Human thirdHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(firstHuman);
        building.addHuman(secondHuman);
        building.addHuman(thirdHuman);

        elevator.load();

        assertThat(elevator.getPassengers(), contains(firstHuman, secondHuman, thirdHuman));
    }

    @ParameterizedTest
    @MethodSource("getLoadTestData")
    void loadTest(int weight, int targetFloor, int startFloor) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, startFloor);
        building.addElevator(elevator);
        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));
        building.addHuman(firstHuman);

        elevator.load();

        assertThat(elevator.getPassengers(), contains(firstHuman));
    }


    @ParameterizedTest
    @MethodSource("getLoadWithoutPickingUpTestData")
    void loadWithoutPickingUpTest(int weight, int targetFloor, int startFloor,
                                  int elevatorTargetFloorNumber, int elevatorStartFloorNumber) {
        Elevator elevator = Elevator.of(VALID_LARGE_CAPACITY, elevatorStartFloorNumber);
        building.addElevator(elevator);

        elevator.addCall(Call.of(elevatorTargetFloorNumber, elevatorStartFloorNumber));

        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));

        building.addHuman(firstHuman);

        elevator.goUp();
        elevator.load();

        assertThat(elevator.getPassengers(), not(hasItem(firstHuman)));
    }

    @ParameterizedTest
    @MethodSource("getLoadWithPickingUpTestData")
    void loadWithPickingUpTest(int weight, int targetFloor, int startFloor,
                               int elevatorTargetFloorNumber, int elevatorStartFloorNumber) {
        Elevator elevator = Elevator.of(VALID_CAPACITY, elevatorStartFloorNumber);
        Human firstHuman = Human.of(weight, targetFloor, building.getFloor(startFloor));
        building.addElevator(elevator);
        elevator.addCall(Call.of(elevatorTargetFloorNumber, elevatorStartFloorNumber));

        building.addHuman(firstHuman);

        elevator.goUp();
        elevator.load();

        assertThat(elevator.getPassengers(), hasItem(firstHuman));
    }

    @Test
    void turnOnTest(){
        Elevator elevator = Elevator.of(VALID_CAPACITY);

        elevator.turnOn();

        assertThat(elevator.isRunning(), equalTo(true));
    }

    @Test
    void turnOffTest(){
        Elevator elevator = Elevator.of(VALID_CAPACITY);

        elevator.turnOn();
        elevator.turnOff();

        assertThat(elevator.isRunning(), equalTo(false));
    }
}
