package org.zadorozhn.util.interrupt;

public interface Interruptable {
    int DEFAULT_OPERATION_TIME = 1100;
    int MAX_SPEED = 1000;
    int MIN_SPEED = 100;

    void turnOff();

    void turnOn();
}
