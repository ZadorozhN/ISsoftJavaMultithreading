package org.zadorozhn.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class StatisticsHolderTest {

    @BeforeEach
    void init() {
        StatisticsHolder.getInstance().restart();
    }

    @Test
    void restartTest() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        statisticsHolder.incrementNumberOfDeliveredPeople();
        statisticsHolder.incrementNumberOfGeneratedPeople();
        statisticsHolder.incrementNumberOfPassedFloors();

        statisticsHolder.restart();

        assertThat(statisticsHolder.getNumberOfDeliveredPeople(), equalTo(0));
        assertThat(statisticsHolder.getNumberOfGeneratedPeople(), equalTo(0));
        assertThat(statisticsHolder.getNumberOfPassedFloors(), equalTo(0));
    }

    @Test
    void incrementNumberOfDeliveredPeople() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        statisticsHolder.incrementNumberOfDeliveredPeople();
        statisticsHolder.incrementNumberOfDeliveredPeople();
        statisticsHolder.incrementNumberOfDeliveredPeople();

        assertThat(statisticsHolder.getNumberOfDeliveredPeople(), equalTo(3));
    }

    @Test
    void incrementNumberOfGeneratedPeople() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        statisticsHolder.incrementNumberOfGeneratedPeople();
        statisticsHolder.incrementNumberOfGeneratedPeople();
        statisticsHolder.incrementNumberOfGeneratedPeople();

        assertThat(statisticsHolder.getNumberOfGeneratedPeople(), equalTo(3));
    }

    @Test
    void incrementNumberOfPassedFloors() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        statisticsHolder.incrementNumberOfPassedFloors();
        statisticsHolder.incrementNumberOfPassedFloors();
        statisticsHolder.incrementNumberOfPassedFloors();

        assertThat(statisticsHolder.getNumberOfPassedFloors(), equalTo(3));
    }

    @Test
    void getNumberOfDeliveredPeople() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        assertThat(statisticsHolder.getNumberOfDeliveredPeople(), equalTo(0));
    }

    @Test
    void getNumberOfGeneratedPeople() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        assertThat(statisticsHolder.getNumberOfGeneratedPeople(), equalTo(0));
    }

    @Test
    void getNumberOfPassedFloors() {
        StatisticsHolder statisticsHolder = StatisticsHolder.getInstance();

        assertThat(statisticsHolder.getNumberOfPassedFloors(), equalTo(0));
    }
}