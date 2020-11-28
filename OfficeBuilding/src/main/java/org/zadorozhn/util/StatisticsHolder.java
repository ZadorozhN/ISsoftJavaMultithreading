package org.zadorozhn.util;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsHolder {
    private static volatile StatisticsHolder instance;

    private final AtomicInteger numberOfDeliveredPeople;
    private final AtomicInteger numberOfGeneratedPeople;
    private final AtomicInteger numberOfPassedFloors;

    private StatisticsHolder() {
        numberOfDeliveredPeople = new AtomicInteger(0);
        numberOfGeneratedPeople = new AtomicInteger(0);
        numberOfPassedFloors = new AtomicInteger(0);
    }

    public static StatisticsHolder getInstance() {
        StatisticsHolder localInstance = instance;
        if (localInstance == null) {
            synchronized (StatisticsHolder.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new StatisticsHolder();
                    localInstance = instance;
                }
            }
        }

        return localInstance;
    }

    public void restart() {
        numberOfDeliveredPeople.set(0);
        numberOfGeneratedPeople.set(0);
        numberOfPassedFloors.set(0);
    }

    public void incrementNumberOfDeliveredPeople() {
        numberOfDeliveredPeople.incrementAndGet();
    }

    public void incrementNumberOfGeneratedPeople() {
        numberOfGeneratedPeople.incrementAndGet();
    }

    public void incrementNumberOfPassedFloors() {
        numberOfPassedFloors.incrementAndGet();
    }

    public int getNumberOfDeliveredPeople() {
        return numberOfDeliveredPeople.get();
    }

    public int getNumberOfGeneratedPeople() {
        return numberOfGeneratedPeople.get();
    }

    public int getNumberOfPassedFloors() {
        return numberOfPassedFloors.get();
    }

}
