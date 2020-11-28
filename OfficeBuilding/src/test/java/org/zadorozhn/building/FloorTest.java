package org.zadorozhn.building;

import org.checkerframework.common.returnsreceiver.qual.This;
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
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor secondUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 2);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, secondUpperFloor, floor);

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
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);

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
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor secondUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 2);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, secondUpperFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
        assertThat(floor.getFirstHuman(direction), equalTo(firstHuman));
        assertThat(floor.getHumanQueue(direction), contains(firstHuman, secondHuman));
    }

    @Test
    void getHumanQueueWithDifferentDirectionTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Direction anotherDirection = Call.of(floor, firstUpperFloor).getDirection();

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        assertThat(floor.getHumanQueue(anotherDirection), not(hasItems(firstHuman, secondHuman)));
    }

    @Test
    void getHumanQueueFromTheLongestQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getHumanQueue(Direction.NONE), hasItems(firstHuman, secondHuman));
        assertThat(floor.getHumanQueue(Direction.NONE), not(hasItems(thirdHuman)));
    }

    @Test
    void getFirstHumanFromTheLongestQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getFirstHuman(Direction.NONE), equalTo(firstHuman));
    }

    @Test
    void getFirstHumanFromTheUpQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getFirstHuman(Direction.UP), equalTo(firstHuman));
    }

    @Test
    void getFirstHumanFromTheDownQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getFirstHuman(Direction.DOWN), equalTo(thirdHuman));
    }

    @Test
    void pollFirstHumanFromTheLongestQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.pollFirstHuman(Direction.NONE), equalTo(firstHuman));
    }

    @Test
    void pollFirstHumanFromTheUpQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.pollFirstHuman(Direction.UP), equalTo(firstHuman));
    }

    @Test
    void poolFirstHumanFromTheDownQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.pollFirstHuman(Direction.DOWN), equalTo(thirdHuman));
    }

    @Test
    void getNumberOfPeopleTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor secondUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 2);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, secondUpperFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);

        Direction direction = firstHuman.getCall().getDirection();

        assertThat(floor.getNumberOfPeople(direction), equalTo(2));
    }

    @Test
    void getNumberOfPeopleFromTheLongestQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getNumberOfPeople(Direction.NONE), equalTo(2));
    }

    @Test
    void getNumberOfPeopleFromUpQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getNumberOfPeople(Direction.UP), equalTo(2));
    }

    @Test
    void getNumberOfPeopleFromDownQueueTest(){
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor upperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor lowerFloor = building.getFloor(VALID_FLOOR_NUMBER - 1);
        Human firstHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, upperFloor, floor);
        Human thirdHuman = Human.of(VALID_WEIGHT, lowerFloor, floor);

        floor.addHuman(firstHuman);
        floor.addHuman(secondHuman);
        floor.addHuman(thirdHuman);

        assertThat(floor.getNumberOfPeople(Direction.DOWN), equalTo(1));
    }

    @Test
    void getHumanQueueTest() {
        Floor floor = building.getFloor(VALID_FLOOR_NUMBER);
        Floor firstUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 1);
        Floor secondUpperFloor = building.getFloor(VALID_FLOOR_NUMBER + 2);
        Human firstHuman = Human.of(VALID_WEIGHT, firstUpperFloor, floor);
        Human secondHuman = Human.of(VALID_WEIGHT, secondUpperFloor, floor);

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