package org.zadorozhn.building;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.human.Human;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

class FloorTest {
    public static final int VALID_FLOOR_NUMBER = 1;
    public static final int VALID_FIRST_TARGET_FLOOR_NUMBER = 2;
    public static final int VALID_SECOND_TARGET_FLOOR_NUMBER = 3;
    public static final int VALID_WEIGHT = 60;
    public static final int VALID_ELEVATOR_CAPACITY = 500;
    public static final int INVALID_FLOOR_NUMBER = -1;
    public static final int NUMBER_OF_FLOORS = 10;
    public static Building building;

    @BeforeEach
    void init(){
        building = Building.of(NUMBER_OF_FLOORS)
                .addElevator(Elevator.of(VALID_ELEVATOR_CAPACITY))
                .setController(Controller.getEmpty());
    }

    @Test
    void createValidFloorTest(){
        assertDoesNotThrow(() -> Floor.of(VALID_FLOOR_NUMBER, building));
    }

    @Test
    void createInvalidFloorTest(){
        assertThrows(IllegalArgumentException.class,
                () -> Floor.of(INVALID_FLOOR_NUMBER, building));
    }

    @Test
    void pollFirstHumanTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Human firstHuman = Human.of(VALID_WEIGHT, VALID_FIRST_TARGET_FLOOR_NUMBER, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, VALID_SECOND_TARGET_FLOOR_NUMBER, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
        assertThat(floor.pollFirstHuman(direction), equalTo(firstHuman));
        assertThat(floor.getHumanQueue(direction), contains(secondHuman));
    }

    @Test
    void pollFirstHumanFromNullQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);

        assertDoesNotThrow(() -> floor.pollFirstHuman(Direction.UP));
        assertDoesNotThrow(() -> floor.pollFirstHuman(Direction.DOWN));
        assertDoesNotThrow(() -> floor.pollFirstHuman(Direction.NONE));
    }

    @Test
    void getFirstHumanFromNullQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);

        assertDoesNotThrow(() -> floor.getFirstHuman(Direction.UP));
        assertDoesNotThrow(() -> floor.getFirstHuman(Direction.DOWN));
        assertDoesNotThrow(() -> floor.getFirstHuman(Direction.NONE));
    }

    @Test
    void addHumanTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Human firstHuman = Human.of(VALID_WEIGHT, VALID_FIRST_TARGET_FLOOR_NUMBER, floor);

        floor.addHuman(firstHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getHumanQueue(direction), contains(firstHuman));
        assertThat(building.getController().getAllCalls(),
                hasItem(Call.of(floor, firstHuman.getCall().getDirection())));
    }

    @Test
    void addNullHumanTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);

        assertThrows(NullPointerException.class, ()-> floor.addHuman(null));
    }

    @Test
    void getFirstHumanTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Human firstHuman = Human.of(VALID_WEIGHT, VALID_FIRST_TARGET_FLOOR_NUMBER, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, VALID_SECOND_TARGET_FLOOR_NUMBER, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
        assertThat(floor.getFirstHuman(direction), equalTo(firstHuman));
        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
    }

    @Test
    void getNumberOfPeopleTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Human firstHuman = Human.of(VALID_WEIGHT, VALID_FIRST_TARGET_FLOOR_NUMBER, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, VALID_SECOND_TARGET_FLOOR_NUMBER, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getNumberOfPeople(direction), equalTo(2));
    }

    @Test
    void getHumanQueueTest() {
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Human firstHuman = Human.of(VALID_WEIGHT, VALID_FIRST_TARGET_FLOOR_NUMBER, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, VALID_SECOND_TARGET_FLOOR_NUMBER, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
    }

    @Test
    void callElevatorTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Direction direction = Direction.DOWN;
        floor.callElevator(direction);

        assertThat(building.getController().getAllCalls(),
                hasItem(Call.of(floor.getFloorNumber(), direction)));
    }
}