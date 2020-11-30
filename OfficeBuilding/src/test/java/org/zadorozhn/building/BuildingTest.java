package org.zadorozhn.building;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.human.Human;
import org.zadorozhn.util.HumanGenerator;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class BuildingTest {
    public static final int VALID_NUMBER_OF_FLOORS = 5;
    public static final int VALID_ELEVATOR_CAPACITY = 500;
    public static final int INVALID_NEGATIVE_NUMBER_OF_FLOORS = -1;
    public static final int INVALID_SMALL_NUMBER_FLOORS = 1;
    public static final int INVALID_ZERO_FLOORS = 0;

    static Object[][] invalidNumberOfFloorsData() {
        return new Object[][]{
                {INVALID_NEGATIVE_NUMBER_OF_FLOORS},
                {INVALID_ZERO_FLOORS},
                {INVALID_SMALL_NUMBER_FLOORS}
        };
    }

    @ParameterizedTest
    @MethodSource("invalidNumberOfFloorsData")
    void createInvalidBuildingTest(int numberOfFloors) {
        assertThrows(IllegalArgumentException.class,
                () -> Building.of(numberOfFloors));
    }

    @Test
    void createValidBuildingTest() {
        assertDoesNotThrow(() -> Building.of(VALID_NUMBER_OF_FLOORS));
    }

    @Test
    void getValidFloorTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        int validFloorId = VALID_NUMBER_OF_FLOORS - 1;

        assertThat(building.getFloor(validFloorId).getFloorNumber(), equalTo(validFloorId));
    }

    @Test
    void getInvalidFloorTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        int invalidFloorId = VALID_NUMBER_OF_FLOORS + 1;

        assertThrows(IllegalArgumentException.class,
                () -> building.getFloor(invalidFloorId));
    }

    @Test
    void getNumberOfFloorsTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        assertThat(building.getNumberOfFloors(), equalTo(VALID_NUMBER_OF_FLOORS));
    }

    @Test
    void getFloorsTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        assertThat(building.getFloors().size(), equalTo(VALID_NUMBER_OF_FLOORS));
    }

    @Test
    void addElevatorTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);
        Elevator elevator = Elevator.of(VALID_ELEVATOR_CAPACITY);

        building.addElevator(elevator);

        assertThat(building.getElevators().size(), equalTo(1));
        assertThat(building.getElevators(), contains(elevator));
    }

    @Test
    void addNullElevatorTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        assertThrows(NullPointerException.class, () -> building.addElevator(null));
    }

    @Test
    void addControllerTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);
        Controller controller = Controller.getEmpty();

        building.setController(controller);

        assertThat(building.getController(), equalTo(controller));
    }

    @Test
    void addNullControllerTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        assertThrows(NullPointerException.class, () -> building.setController(null));
    }

    @Test
    void getElevatorsTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);
        Elevator firstElevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Elevator secondElevator = Elevator.of(VALID_ELEVATOR_CAPACITY);

        building.addElevator(firstElevator).addElevator(secondElevator);

        assertThat(building.getElevators().size(), equalTo(2));
        assertThat(building.getElevators(), contains(firstElevator, secondElevator));
    }

    @Test
    void startElevatorsAndControllerTest() {
        Elevator elevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Controller controller = Controller.getEmpty();

        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .addElevator(elevator)
                .setController(controller);

        assertDoesNotThrow(building::start);
        assertDoesNotThrow(building::stop);
    }

    @Test
    void startWithNothingTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);

        assertThrows(NullPointerException.class, building::start);
    }

    @Test
    void startControllerTest() {
        Elevator elevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Controller controller = Controller.getEmpty();

        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .addElevator(elevator)
                .setController(controller);

        assertDoesNotThrow(building::startController);
        assertDoesNotThrow(building::stopController);
    }

    @Test
    void startNullControllerTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY));

        assertThrows(NullPointerException.class, building::startController);
    }

    @Test
    void stopNullControllerTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY));

        assertThrows(NullPointerException.class, building::stopController);
    }

    @Test
    void startElevatorsTest() {
        Elevator elevator = Elevator.of(VALID_ELEVATOR_CAPACITY);
        Controller controller = Controller.getEmpty();

        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .addElevator(elevator)
                .setController(controller);

        assertDoesNotThrow(building::startElevators);
        assertDoesNotThrow(building::stopElevators);
    }

    @Test
    void startNullElevatorsTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        assertThrows(IllegalStateException.class, building::startElevators);
    }

    @Test
    void stopNullElevatorsTest() {
        Building building = Building.of(VALID_NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        assertThrows(IllegalStateException.class, building::stopElevators);
    }

    @SneakyThrows
    @Test
    void deliverPeopleTest() {
        int numberOfGeneratedHuman = 10;
        Building building = Building.of(VALID_NUMBER_OF_FLOORS);
        building.setController(Controller.getEmpty())
                .addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY, Floor.GROUND_FLOOR, Elevator.MAX_SPEED));
        HumanGenerator humanGenerator = HumanGenerator.of(building,
                Human.MIN_WEIGHT, Human.MAX_WEIGHT, HumanGenerator.MAX_SPEED);

        IntStream.range(0, numberOfGeneratedHuman).forEach(i -> humanGenerator.generate());

        building.start();

        TimeUnit.SECONDS.sleep(20);

        int numberOfPeople = building.getFloors().stream().mapToInt(i -> i.getNumberOfPeople(Direction.UP)
                + i.getNumberOfPeople(Direction.DOWN)).sum();

        assertThat(numberOfPeople, equalTo(0));
    }
}