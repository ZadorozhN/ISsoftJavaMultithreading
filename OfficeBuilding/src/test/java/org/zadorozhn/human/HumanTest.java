package org.zadorozhn.human;

import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zadorozhn.building.Building;
import org.zadorozhn.building.Call;
import org.zadorozhn.building.Controller;
import org.zadorozhn.building.Floor;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class HumanTest {
    public static final int VALID_TARGET_FLOOR_NUMBER = 5;
    public static final int VALID_START_FLOOR_NUMBER = 3;
    public static final int VALID_WEIGHT = 60;
    public static final int INVALID_TARGET_FLOOR_NUMBER = -1;
    public static final int INVALID_NEGATIVE_WEIGHT = -1;
    public static final int INVALID_WEIGHT = 300;
    public static final int NUMBER_OF_FLOORS = 10;
    public static Building building;

    @BeforeEach
    void init() {
        building = Building.of(NUMBER_OF_FLOORS).setController(Controller.getEmpty());
    }

    static Object[][] getInvalidHumanCharacteristicsData() {
        return new Object[][]{
                {VALID_WEIGHT, INVALID_TARGET_FLOOR_NUMBER},
                {INVALID_WEIGHT, VALID_TARGET_FLOOR_NUMBER},
                {INVALID_WEIGHT, INVALID_TARGET_FLOOR_NUMBER},
                {INVALID_NEGATIVE_WEIGHT, INVALID_TARGET_FLOOR_NUMBER},
                {INVALID_NEGATIVE_WEIGHT, VALID_TARGET_FLOOR_NUMBER}
        };
    }

    static Object[][] getInvalidNullFloorsCombinationData() {
        return new Object[][]{
                {Floor.of(VALID_START_FLOOR_NUMBER, building), null},
                {null, Floor.of(VALID_TARGET_FLOOR_NUMBER, building)},
                {null, null}
        };
    }

    @Test
    void createValidHumanTest() {
        assertDoesNotThrow(() -> Human.of(VALID_WEIGHT,
                VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER)));
    }

    @Test
    void createInvalidHumanWithTheSameFloor() {
        Floor floor = building.getFloor(VALID_START_FLOOR_NUMBER);
        assertThrows(IllegalArgumentException.class,
                () -> Human.of(VALID_WEIGHT, floor, floor));
    }

    @ParameterizedTest
    @MethodSource("getInvalidHumanCharacteristicsData")
    void createInvalidHumanByFloorNumberTest(int weight, int targetFloor) {
        assertThrows(IllegalArgumentException.class,
                () -> Human.of(weight, targetFloor, building.getFloor(VALID_START_FLOOR_NUMBER)));
    }

    @ParameterizedTest
    @MethodSource("getInvalidNullFloorsCombinationData")
    void createInvalidHumanWithNullFloorTest(Floor targetFloor, Floor startFloor) {
        assertThrows(NullPointerException.class,
                () -> Human.of(VALID_WEIGHT, targetFloor, startFloor));
    }

    @Test
    void getWeightTest() {
        Human human = Human.of(VALID_WEIGHT, VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        assertThat(human.getWeight(), equalTo(VALID_WEIGHT));
    }

    @Test
    void getTargetFloorNumberTest() {
        Human human = Human.of(VALID_WEIGHT, VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        assertThat(human.getCall().getTargetFloorNumber(), equalTo(VALID_TARGET_FLOOR_NUMBER));
    }

    @Test
    void getCallTest() {
        Human human = Human.of(VALID_WEIGHT, VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        assertThat(human.getCall(),
                equalTo(Call.of(VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER))));
    }

    @Test
    void getSsnTest() {
        Human human = Human.of(VALID_WEIGHT, VALID_TARGET_FLOOR_NUMBER, building.getFloor(VALID_START_FLOOR_NUMBER));

        assertThat(human.getSsn(), notNullValue());
    }

    @Test
    void pushUpButtonTest() {
        Floor lowerFloor = building.getFloor(0);
        Floor upperFloor = building.getFloor(1);
        Human human = Human.of(VALID_WEIGHT, upperFloor, lowerFloor);

        human.pushButton();

        assertThat(building.getController().getAllCalls(),
                hasItem(Call.of(human.getStartFloor(), human.getCall().getDirection())));
    }

    @Test
    void pushDownButtonTest() {
        Floor lowerFloor = building.getFloor(0);
        Floor upperFloor = building.getFloor(1);
        Human human = Human.of(VALID_WEIGHT, lowerFloor, upperFloor);

        human.pushButton();

        assertThat(building.getController().getAllCalls(),
                hasItem(Call.of(human.getStartFloor(), human.getCall().getDirection())));
    }
}