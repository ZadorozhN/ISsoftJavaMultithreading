package org.zadorozhn.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.zadorozhn.building.Building;
import org.zadorozhn.building.Controller;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.human.Human;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HumanGeneratorTest {
    public static final int NUMBER_OF_FLOORS = 10;
    public static final int VALID_SPEED = 500;
    public static final int INVALID_FAST_SPEED = 2000;
    public static final int INVALID_NEGATIVE_SPEED = -10;
    public static final int INVALID_SLOW_SPEED = 50;

    static Object[][] getInvalidSpeedsData(){
        return new Object[][]{
            {INVALID_FAST_SPEED},
            {INVALID_SLOW_SPEED},
            {INVALID_NEGATIVE_SPEED},
        };
    }

    @Test
    void createValidHumanGeneratorTest() {
        Building building = Building.of(NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        assertDoesNotThrow(() -> HumanGenerator.of(building, Human.MIN_WEIGHT, Human.MAX_WEIGHT, VALID_SPEED));
    }

    @Test
    void createValidHumanGeneratorWithoutSpeedTest() {
        Building building = Building.of(NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        assertDoesNotThrow(() -> HumanGenerator.of(building, Human.MIN_WEIGHT, Human.MAX_WEIGHT));
    }

    @Test
    void createValidHumanGeneratorOnlyWithBuildingTest() {
        Building building = Building.of(NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        assertDoesNotThrow(() -> HumanGenerator.of(building));
    }

    @Test
    void createInvalidWithNullBuildingHumanGeneratorTest() {
        Building building = null;

        assertThrows(NullPointerException.class,
                () -> HumanGenerator.of(building, Human.MIN_WEIGHT, Human.MAX_WEIGHT, VALID_SPEED));
    }

    @Test
    void createHumanGeneratorWithInvalidWeightLimitTest(){
        Building building = Building.of(NUMBER_OF_FLOORS);

        int invalidMaxWeightLimit = Human.MAX_WEIGHT + 1;
        int invalidMinWeightLimit = Human.MIN_WEIGHT - 1;

        assertThrows(IllegalArgumentException.class,
                () -> HumanGenerator.of(building, invalidMinWeightLimit, invalidMaxWeightLimit, VALID_SPEED));
    }

    @Test
    void createHumanGeneratorWithSwappedLimitsTest(){
        Building building = Building.of(NUMBER_OF_FLOORS);

        assertThrows(IllegalArgumentException.class,
                () -> HumanGenerator.of(building, Human.MAX_WEIGHT, Human.MIN_WEIGHT, VALID_SPEED));
    }

    @ParameterizedTest
    @MethodSource("getInvalidSpeedsData")
    void createHumanGeneratorWithInvalidSpeedTest(int invalidSpeed){
        Building building = Building.of(NUMBER_OF_FLOORS);

        assertThrows(IllegalArgumentException.class,
                () -> HumanGenerator.of(building, Human.MAX_WEIGHT, Human.MIN_WEIGHT, invalidSpeed));
    }
    @Test
    void generateTest() {
        int numberOfGeneratedHuman = 10;
        Building building = Building.of(NUMBER_OF_FLOORS)
                .setController(Controller.getEmpty());

        HumanGenerator humanGenerator = HumanGenerator.of(building,
                Human.MIN_WEIGHT, Human.MAX_WEIGHT, VALID_SPEED);

        IntStream.range(0, numberOfGeneratedHuman).forEach(i -> humanGenerator.generate());
        int actualNumberOfHuman = building.getFloors().stream()
                .mapToInt(i -> i.getNumberOfPeople(Direction.UP) + i.getNumberOfPeople(Direction.DOWN)).sum();

        assertThat(actualNumberOfHuman, equalTo(numberOfGeneratedHuman));
    }

    @Test
    void turnOnTest(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        HumanGenerator humanGenerator = HumanGenerator.of(building);

        humanGenerator.turnOn();

        assertThat(humanGenerator.isRunning(), equalTo(true));
    }

    @Test
    void turnOffTest(){
        Building building = Building.of(NUMBER_OF_FLOORS);
        HumanGenerator humanGenerator = HumanGenerator.of(building);

        humanGenerator.turnOn();
        humanGenerator.turnOff();

        assertThat(humanGenerator.isRunning(), equalTo(false));
    }
}