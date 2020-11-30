package org.zadorozhn.building;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zadorozhn.building.state.Direction;
import org.zadorozhn.building.state.State;
import org.zadorozhn.human.Human;
import org.zadorozhn.util.StatisticsHolder;
import org.zadorozhn.util.interrupt.Interruptable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

@Slf4j
public class Elevator implements Runnable, Interruptable {
    public final static int MIN_CAPACITY = 0;

    @Getter
    private final UUID id;
    @Getter
    private final int capacity;
    @Getter
    private final int moveSpeed;
    @Getter
    private final int doorWorkSpeed;
    @Getter
    private int numberOfDeliveredPeople;
    private int currentFloorNumber;
    private boolean isRunning;
    private Building building;
    private final List<Human> passengers;
    private final List<Call> calls;

    private final Condition elevatorStopCondition;
    private final Lock currentFloorLock;
    private final Lock peopleLock;
    private final Lock stateLock;
    private final Lock callLock;

    private Direction direction;
    private State state;

    private Elevator(int capacity, int currentFloorNumber, int moveSpeed, int doorWorkSpeed) {
        checkArgument(capacity > MIN_CAPACITY);
        checkArgument(moveSpeed >= MIN_SPEED && moveSpeed <= MAX_SPEED);
        checkArgument(doorWorkSpeed >= MIN_SPEED && doorWorkSpeed <= MAX_SPEED);

        this.id = UUID.randomUUID();
        this.capacity = capacity;
        this.moveSpeed = moveSpeed;
        this.doorWorkSpeed = doorWorkSpeed;
        this.currentFloorNumber = currentFloorNumber;

        this.currentFloorLock = new ReentrantLock(true);
        this.peopleLock = new ReentrantLock(true);
        this.stateLock = new ReentrantLock(true);
        this.callLock = new ReentrantLock(true);
        this.elevatorStopCondition = callLock.newCondition();

        this.passengers = new ArrayList<>();
        this.calls = new ArrayList<>();

        this.direction = Direction.NONE;
        this.state = State.STOP;

        this.numberOfDeliveredPeople = 0;
    }

    public static Elevator of(int capacity) {
        return new Elevator(capacity, Floor.GROUND_FLOOR, MIN_SPEED, MIN_SPEED);
    }

    public static Elevator of(int capacity, int startFloorNumber) {
        return new Elevator(capacity, startFloorNumber, MIN_SPEED, MIN_SPEED);
    }

    public static Elevator of(int capacity, int startFloorNumber, int speed) {
        return new Elevator(capacity, startFloorNumber, speed, speed);
    }

    public static Elevator of(int capacity, int startFloorNumber, int moveSpeed, int doorWorkSpeed) {
        return new Elevator(capacity, startFloorNumber, moveSpeed, doorWorkSpeed);
    }

    public static Elevator of(int capacity, Floor startFloor) {
        checkNotNull(startFloor);

        return new Elevator(capacity, startFloor.getFloorNumber(), MIN_SPEED, MIN_SPEED);
    }

    public static Elevator of(int capacity, Floor startFloor, int speed) {
        checkNotNull(startFloor);

        return new Elevator(capacity, startFloor.getFloorNumber(), speed, speed);
    }

    public static Elevator of(int capacity, Floor startFloor, int moveSpeed, int doorWorkSpeed) {
        checkNotNull(startFloor);

        return new Elevator(capacity, startFloor.getFloorNumber(), moveSpeed, doorWorkSpeed);
    }

    public void addTo(Building building) {
        checkNotNull(building);

        this.building = building;
    }

    public int getCurrentFloorNumber() {
        currentFloorLock.lock();
        int floor = currentFloorNumber;
        currentFloorLock.unlock();

        return floor;
    }

    public Floor getCurrentFloor() {
        currentFloorLock.lock();
        Floor floor = building.getFloor(currentFloorNumber);
        currentFloorLock.unlock();

        return floor;
    }

    public Controller getController() {
        checkNotNull(building);

        return building.getController();
    }

    public int getNumberOfPeople() {
        peopleLock.lock();
        int size = passengers.size();
        peopleLock.unlock();

        return size;
    }

    public State getState() {
        stateLock.lock();
        State state = this.state;
        stateLock.unlock();

        return state;
    }

    public Direction getDirection() {
        stateLock.lock();
        Direction direction = this.direction;
        stateLock.unlock();

        return direction;
    }

    public Direction getDestinationDirection() {
        callLock.lock();
        stateLock.lock();
        Direction direction = calls.isEmpty() ? Direction.NONE : calls.get(0).getDirection();
        stateLock.unlock();
        callLock.unlock();

        return direction;
    }

    public int getFreeSpace() {
        peopleLock.lock();
        int engagedSpace = passengers.stream().mapToInt(Human::getWeight).sum();
        peopleLock.unlock();

        return capacity - engagedSpace;
    }

    public boolean isRunning() {
        stateLock.lock();
        boolean result = isRunning;
        stateLock.unlock();

        return result;
    }

    public List<Human> getPassengers() {
        peopleLock.lock();
        List<Human> list = ImmutableList.copyOf(passengers);
        peopleLock.unlock();

        return list;
    }

    public List<Call> getCalls() {
        callLock.lock();
        List<Call> list = ImmutableList.copyOf(calls);
        callLock.unlock();

        return list;
    }

    public void addCall(Call call) {
        checkNotNull(call);

        callLock.lock();
        calls.add(call);
        elevatorStopCondition.signal();
        callLock.unlock();

        stateLock.lock();
        currentFloorLock.lock();
        if (direction == Direction.NONE) {
            direction = call.getTargetFloorNumber() - currentFloorNumber > 0 ? Direction.UP : Direction.DOWN;
        }
        currentFloorLock.unlock();
        stateLock.unlock();

        log.info("elevator called to " + call);
    }

    @SneakyThrows
    public void goUp() {
        checkState(getCurrentFloorNumber() < building.getNumberOfFloors());

        stateLock.lock();
        direction = Direction.UP;
        state = State.MOVE;
        stateLock.unlock();

        currentFloorLock.lock();
        currentFloorNumber++;
        currentFloorLock.unlock();

        StatisticsHolder.getInstance().incrementNumberOfPassedFloors();

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - moveSpeed);

        log.info("elevator moved to floor number " + currentFloorNumber);
    }

    @SneakyThrows
    public void goDown() {
        checkState(currentFloorNumber > Floor.GROUND_FLOOR);

        stateLock.lock();
        direction = Direction.DOWN;
        state = State.MOVE;
        stateLock.unlock();

        currentFloorLock.lock();
        currentFloorNumber--;
        currentFloorLock.unlock();

        StatisticsHolder.getInstance().incrementNumberOfPassedFloors();

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - moveSpeed);

        log.info("elevator moved to floor number " + currentFloorNumber);
    }

    @SneakyThrows
    public void openDoor() {
        stateLock.lock();
        state = State.OPEN_DOOR;
        stateLock.unlock();

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - doorWorkSpeed);

        log.info("elevator has opened his door");
    }

    @SneakyThrows
    public void pickUpHuman(Human human) {
        checkNotNull(human);

        stateLock.lock();
        if (direction == Direction.NONE) {
            direction = human.getCall().getDirection();
        }
        stateLock.unlock();

        peopleLock.lock();
        passengers.add(human);
        peopleLock.unlock();

        getController().removeCall(Call.of(getCurrentFloorNumber(), human.getCall().getDirection()));

        addCall(human.getCall());

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - doorWorkSpeed);

        log.info("elevator pick up the next human: " + human);
    }

    @SneakyThrows
    public void disembark(Human human) {
        checkNotNull(human);
        checkArgument(passengers.contains(human));

        peopleLock.lock();
        passengers.remove(human);
        peopleLock.unlock();

        StatisticsHolder.getInstance().incrementNumberOfDeliveredPeople();
        numberOfDeliveredPeople++;

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - doorWorkSpeed);

        log.info("elevator disembark the next human: " + human);
    }

    public boolean checkFloor() {
        Human human = null;
        boolean result = false;

        getCurrentFloor().getFloorLock().lock();
        stateLock.lock();
        Direction destinationDirection = getDestinationDirection();
        if (!destinationDirection.equals(Direction.NONE) && destinationDirection.equals(direction)) {
            human = getCurrentFloor().getFirstHuman(direction);
        }
        stateLock.unlock();
        getCurrentFloor().getFloorLock().unlock();

        peopleLock.lock();
        if (human != null && human.getWeight() <= getFreeSpace()) {
            stateLock.lock();
            result = human.getCall().getDirection() == direction;
            stateLock.unlock();
        }
        peopleLock.unlock();

        return result;
    }

    public void load() {
        stateLock.lock();
        state = State.LOAD;
        stateLock.unlock();

        peopleLock.lock();
        List<Human> peopleForDisembark = passengers.stream()
                .filter(i -> i.getCall().getTargetFloorNumber() == currentFloorNumber)
                .collect(Collectors.toList());
        peopleLock.unlock();

        peopleForDisembark.forEach(this::disembark);

        peopleLock.lock();
        stateLock.lock();
        if (passengers.isEmpty() && calls.isEmpty()) {
            log.info("elevator is empty");
            direction = Direction.NONE;
        } else if (passengers.isEmpty()) {
            direction = getDestinationDirection();
        }
        stateLock.unlock();
        peopleLock.unlock();

        log.info("elevator has finished disembarking");

        while (state == State.LOAD) {
            getCurrentFloor().getFloorLock().lock();
            stateLock.lock();
            Human human = getCurrentFloor().getFirstHuman(direction);
            Direction destinationDirection = getDestinationDirection();

            if (human != null && ((!destinationDirection.equals(Direction.NONE)
                    && destinationDirection.equals(human.getCall().getDirection()))
                    || (destinationDirection.equals(Direction.NONE)
                    && human.getCall().getDirection().equals(direction))
                    || direction.equals(Direction.NONE))) {

                if (human.getWeight() <= getFreeSpace()) {
                    if (direction.equals(Direction.NONE)) {
                        direction = human.getCall().getDirection();
                    }
                    stateLock.unlock();
                    human = getCurrentFloor().pollFirstHuman(direction);
                    getCurrentFloor().getFloorLock().unlock();
                    pickUpHuman(human);

                    log.info("human has been picked up " + human);
                } else {
                    stateLock.unlock();
                    getCurrentFloor().getFloorLock().unlock();
                    getController().addCall(Call.of(currentFloorNumber, human.getCall().getDirection()));

                    log.info("elevator cannot pick up human, 'cause there is not enough space " + human);
                    log.info("elevator recall" + human.getCall());

                    break;
                }
            } else {
                stateLock.unlock();
                getCurrentFloor().getFloorLock().unlock();

                break;
            }
        }

        log.info("elevator finishes load");
    }

    @SneakyThrows
    public void closeDoor() {
        stateLock.lock();
        state = State.CLOSE_DOOR;
        stateLock.unlock();

        TimeUnit.MILLISECONDS.sleep(DEFAULT_OPERATION_TIME - doorWorkSpeed);

        log.info("elevator has closed his door");
    }

    @SneakyThrows
    public void stop() {
        callLock.lock();

        stateLock.lock();
        direction = Direction.NONE;
        state = State.STOP;
        stateLock.unlock();

        while (calls.isEmpty()) {
            log.info("elevator stopped");
            elevatorStopCondition.await();
        }

        callLock.unlock();
    }

    public void end() {
        stateLock.lock();
        direction = Direction.NONE;
        state = State.END;
        stateLock.unlock();

        log.info("elevator has finished his way");
    }

    public boolean removeExecutedCalls(){
        boolean hasExecutedCalls = false;

        callLock.lock();
        List<Call> currentFloorCalls = calls.stream()
                .filter(i -> i.getTargetFloorNumber() == currentFloorNumber)
                .collect(Collectors.toList());
        hasExecutedCalls = calls.removeAll(currentFloorCalls);
        callLock.unlock();

        return hasExecutedCalls;
    }

    @Override
    public void turnOff() {
        isRunning = false;

        log.info("elevator has been stopped");
    }

    @Override
    public void turnOn() {
        isRunning = true;

        log.info("elevator has been started");
    }

    @Override
    public void run() {
        turnOn();
        while (isRunning) {
            callLock.lock();
            if (calls.isEmpty()) {
                callLock.unlock();
                stop();
            } else {
                boolean hasExecutedCalls = removeExecutedCalls();
                int currentCallFloorNumber = calls.isEmpty()
                        ? currentFloorNumber
                        : calls.get(0).getTargetFloorNumber();

                callLock.unlock();

                boolean areWaitingPeopleOnThisFloor = checkFloor();

                if (hasExecutedCalls || areWaitingPeopleOnThisFloor) {
                    openDoor();
                    load();
                    closeDoor();
                } else if (currentCallFloorNumber > currentFloorNumber) {
                    goUp();
                } else if (currentCallFloorNumber < currentFloorNumber) {
                    goDown();
                }
            }
        }
        end();
    }

    @Override
    public String toString() {
        return String.format("State: %s; Direction: %s; Free space: %s; PeopleDelivered: %d; Calls: %s; Passengers: %s; "
                , getState(), getDirection(),  getFreeSpace(), numberOfDeliveredPeople, getCalls(), getPassengers());
    }
}