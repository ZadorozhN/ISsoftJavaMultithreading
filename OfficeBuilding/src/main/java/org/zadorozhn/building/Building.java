package org.zadorozhn.building;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.zadorozhn.human.Human;

import static com.google.common.base.Preconditions.*;

@Slf4j
public class Building {
    public static final int MIN_NUMBER_OF_FLOORS = 2;
    public static final int MIN_NUMBER_OF_ELEVATORS = 1;

    @Getter
    private final int numberOfFloors;
    @Getter
    private Controller controller;
    private final List<Floor> floors;
    private final List<Elevator> elevators;
    private final Lock buildingLock;

    private Building(int numberOfFloors) {
        checkArgument(numberOfFloors >= MIN_NUMBER_OF_FLOORS);

        this.buildingLock = new ReentrantLock();
        this.numberOfFloors = numberOfFloors;
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();

        IntStream.range(Floor.GROUND_FLOOR, numberOfFloors).forEachOrdered(i -> floors.add(Floor.of(i, this)));
    }

    public static Building of(int numberOfFloors) {
        return new Building(numberOfFloors);
    }

    public Building addElevator(Elevator elevator) {
        checkNotNull(elevator);

        elevator.addTo(this);
        elevators.add(elevator);

        return this;
    }

    public Building setController(Controller controller) {
        checkNotNull(controller);

        this.controller = controller;
        controller.setElevators(elevators);

        return this;
    }

    public Building start() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        startElevators();
        startController();

        return this;
    }

    public Building startElevators() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        String threadName = "elevator ";
        IntStream.range(0, elevators.size())
                .forEachOrdered(i -> new Thread(elevators.get(i), threadName + i).start());

        return this;
    }

    public Building startController() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        String threadName = "controller";
        new Thread(controller, threadName).start();

        return this;
    }

    public Building stop() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        stopElevators();
        stopController();

        return this;
    }

    public Building stopController() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        controller.turnOff();

        return this;
    }

    public Building stopElevators() {
        checkNotNull(controller);
        checkState(elevators.size() >= MIN_NUMBER_OF_ELEVATORS);

        elevators.forEach(Elevator::turnOff);

        return this;
    }

    public Floor getFloor(int number) {
        checkArgument(number < numberOfFloors);

        return floors.get(number);
    }

    public Building addHuman(Human human) {
        checkNotNull(human);
        checkArgument(floors.contains(human.getStartFloor()));

        this.getFloor(human.getStartFloor().getFloorNumber()).addHuman(human);

        return this;
    }

    public List<Elevator> getElevators() {
        buildingLock.lock();
        List<Elevator> list = ImmutableList.copyOf(elevators);
        buildingLock.unlock();

        return list;
    }

    public List<Floor> getFloors() {
        return ImmutableList.copyOf(floors);
    }
}
