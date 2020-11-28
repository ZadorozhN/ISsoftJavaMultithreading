package org.zadorozhn.building;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.zadorozhn.building.state.Direction;

import static org.junit.jupiter.api.Assertions.*;

class CallTest {
    public static final int NUMBER_OF_FLOORS = 10;
    public static final int VALID_FIRST_TARGET_FLOOR_NUMBER = 5;
    public static final int VALID_SECOND_TARGET_FLOOR_NUMBER = 6;
    public static final int INVALID_TARGET_FLOOR_NUMBER = -1;

    static Object[][] directionData() {
        return new Object[][]{
                {Direction.DOWN},
                {Direction.UP},
                {Direction.NONE},
        };
    }

    @ParameterizedTest
    @MethodSource("directionData")
    void createValidCall(Direction direction) {
        assertDoesNotThrow(() -> Call.of(VALID_FIRST_TARGET_FLOOR_NUMBER, direction));
    }

    @ParameterizedTest
    @MethodSource("directionData")
    void createInvalidCall(Direction direction) {
        assertThrows(IllegalArgumentException.class,
                () -> Call.of(INVALID_TARGET_FLOOR_NUMBER, direction));
    }

    @ParameterizedTest
    @MethodSource("directionData")
    void createCallFromFloorAndDirection(Direction direction){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor firstFloor = building.getFloor(VALID_FIRST_TARGET_FLOOR_NUMBER);

        assertDoesNotThrow(() -> Call.of(firstFloor, direction));
    }

    @Test
    void createCallFromFloors(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor firstFloor = building.getFloor(VALID_FIRST_TARGET_FLOOR_NUMBER);
        Floor secondFloor = building.getFloor(VALID_SECOND_TARGET_FLOOR_NUMBER);

        assertDoesNotThrow(() -> Call.of(firstFloor, secondFloor));
    }

    @Test
    void createCallFromFloorsNumber(){
        assertDoesNotThrow(() -> Call.of(VALID_FIRST_TARGET_FLOOR_NUMBER, VALID_SECOND_TARGET_FLOOR_NUMBER));
    }

    @Test
    void createCallFromFloorAndFloorNumber(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor firstFloor = building.getFloor(VALID_FIRST_TARGET_FLOOR_NUMBER);

        assertDoesNotThrow(() -> Call.of(firstFloor, VALID_SECOND_TARGET_FLOOR_NUMBER));
    }

    @Test
    void createIllegalCallWithTheSameFloorAndFloorNumber(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor firstFloor = building.getFloor(VALID_FIRST_TARGET_FLOOR_NUMBER);

        assertThrows(IllegalArgumentException.class,
                () -> Call.of(firstFloor, VALID_FIRST_TARGET_FLOOR_NUMBER));
    }

    @Test
    void createCallFromFloorNumberAndFloor(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor secondFloor = building.getFloor(VALID_SECOND_TARGET_FLOOR_NUMBER);

        assertDoesNotThrow(() -> Call.of(VALID_FIRST_TARGET_FLOOR_NUMBER, secondFloor));
    }

    @Test
    void createIllegalCallWithTheSameFloorNumberAndFloor(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        Floor secondFloor = building.getFloor(VALID_SECOND_TARGET_FLOOR_NUMBER);

        assertThrows(IllegalArgumentException.class,
                () -> Call.of(VALID_SECOND_TARGET_FLOOR_NUMBER, secondFloor));
    }
}