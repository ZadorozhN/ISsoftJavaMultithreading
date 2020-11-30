package org.zadorozhn.util;

import org.junit.jupiter.api.Test;
import org.zadorozhn.building.Building;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class UserInterfaceTest {
    public static final int VALID_SPEED_OF_RENDERING = 500;
    public static final int INVALID_FAST_SPEED_OF_RENDERING = 1001;
    public static final int INVALID_SLOW_SPEED_OF_RENDERING = 99;
    public static final int NUMBER_OF_FLOORS = 10;

    @Test
    void createValidUserInterfaceTest() {
        Building building = Building.of(NUMBER_OF_FLOORS);
        assertDoesNotThrow(() -> UserInterface.of(building, VALID_SPEED_OF_RENDERING));
    }

    @Test
    void createInvalidFastUserInterfaceTest() {
        Building building = Building.of(NUMBER_OF_FLOORS);
        assertThrows(IllegalArgumentException.class,
                () -> UserInterface.of(building, INVALID_FAST_SPEED_OF_RENDERING));
    }

    @Test
    void createInvalidSlowUserInterfaceTest() {
        Building building = Building.of(NUMBER_OF_FLOORS);
        assertThrows(IllegalArgumentException.class,
                () -> UserInterface.of(building, INVALID_SLOW_SPEED_OF_RENDERING));
    }

    @Test
    void createInvalidUserInterfaceWithNullBuildingTest() {
        Building building = null;
        assertThrows(NullPointerException.class,
                () -> UserInterface.of(building, VALID_SPEED_OF_RENDERING));
    }

    @Test
    void turnOffTest() {
        Building building = Building.of(NUMBER_OF_FLOORS);
        UserInterface userInterface = UserInterface.of(building, VALID_SPEED_OF_RENDERING);

        userInterface.turnOn();
        userInterface.turnOff();

        assertThat(userInterface.isRunning(), equalTo(false));
    }

    @Test
    void turnOnTest() {
        Building building = Building.of(NUMBER_OF_FLOORS);
        UserInterface userInterface = UserInterface.of(building, VALID_SPEED_OF_RENDERING);

        userInterface.turnOn();

        assertThat(userInterface.isRunning(), equalTo(true));
    }
}