package org.zadorozhn.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zadorozhn.building.Building;
import org.zadorozhn.building.Floor;
import org.zadorozhn.human.Human;
import org.zadorozhn.util.interrupt.Interruptible;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class HumanGenerator extends Thread implements Interruptible {
    private final Building building;
    private final int generateSpeed;
    private final int weightFrom;
    private final int weightTo;

    @Getter
    public boolean isRunning;

    private HumanGenerator(Building building, int weightFrom, int weightTo, int generateSpeed) {
        checkArgument(generateSpeed >= MIN_SPEED && generateSpeed <= MAX_SPEED);
        checkArgument(weightFrom >= Human.MIN_WEIGHT);
        checkArgument(weightTo <= Human.MAX_WEIGHT);
        checkArgument(weightTo >= weightFrom);
        checkNotNull(building);

        this.generateSpeed = generateSpeed;
        this.weightFrom = weightFrom;
        this.building = building;
        this.weightTo = weightTo;

        String threadName = "humanGenerator";
        this.setName(threadName);
    }

    public static HumanGenerator of(Building building, int weightFrom, int weightTo, int generateSpeed) {
        return new HumanGenerator(building, weightFrom, weightTo, generateSpeed);
    }

    public static HumanGenerator of(Building building, int weightFrom, int weightTo) {
        return new HumanGenerator(building, weightFrom, weightTo, MIN_SPEED);
    }

    public static HumanGenerator of(Building building) {
        return new HumanGenerator(building, Human.MIN_WEIGHT, Human.MAX_WEIGHT, MIN_SPEED);
    }

    @SneakyThrows
    public void generate() {
        Random random = new Random();
        Floor floor = building.getFloor(Math.abs(random.nextInt()) % building.getNumberOfFloors());
        int weight = Math.abs(random.nextInt()) % (weightTo - weightFrom) + weightFrom;
        int targetFloor;

        do {
            targetFloor = Math.abs(random.nextInt()) % building.getNumberOfFloors();
        } while (targetFloor == floor.getFloorNumber());

        floor.addHuman(Human.of(weight, targetFloor, floor));

        StatisticsHolder.getInstance().incrementNumberOfGeneratedPeople();

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - generateSpeed);

        log.info("human has been generated at {}", targetFloor);
    }

    public void turnOff() {
        isRunning = false;
    }

    public void turnOn() {
        isRunning = true;
    }

    @Override
    public void run() {
        turnOn();

        try {
            while (isRunning) {
                generate();
            }
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            log.error(exception.getCause().getMessage());
        }
    }
}