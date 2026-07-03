package it.smartcommunitylab.pgazienda.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.util.StatUtil;

public class StatTrackServiceTest {

    @Test
    public void buildPivotRowsShouldCreateColumnsPerTimeValueAndMetric() {
        List<Map<String, Object>> stats = new ArrayList<>();

        Map<String, Object> first = new HashMap<>();
        first.put("id", "abc");
        first.put("name", "Comune A");
        first.put("year", "2024");
        first.put("train_mean_tripCount", 3);
        first.put("bus_mean_score", 10.5);
        stats.add(first);

        Map<String, Object> second = new HashMap<>();
        second.put("id", "abc");
        second.put("name", "Comune A");
        second.put("year", "2025");
        second.put("train_mean_tripCount", 4);
        second.put("bus_mean_score", 11.5);
        stats.add(second);

        Map<String, Object> third = new HashMap<>();
        third.put("id", "def");
        third.put("name", "Comune B");
        third.put("year", "2024");
        third.put("train_mean_tripCount", 6);
        third.put("bus_mean_score", 12.5);
        stats.add(third);

        List<String[]> rows = StatUtil.buildPivotRows(stats, GROUP_BY_TIME.year, List.of("2024", "2025"), List.of("train_mean_tripCount", "bus_mean_score"));

        assertEquals(2, rows.size());
        assertArrayEquals(new String[] {"abc", "Comune A", "3", "10.5", "4", "11.5"}, rows.get(0));
        assertArrayEquals(new String[] {"def", "Comune B", "6", "12.5", "", ""}, rows.get(1));
    }

    @Test
    public void dateTest() {
        LocalDate date = LocalDate.of(2026, 7, 2);
        LocalDate nextMonth = date.withDayOfMonth(1).plusMonths(1);
        System.out.println("Next month: " + nextMonth);
        LocalDate nextYear = date.withDayOfMonth(1).plusYears(1);
        System.out.println("Next year: " + nextYear);
        LocalDate nextWeek = date.with(DayOfWeek.MONDAY).plusWeeks(1);
        System.out.println("Next week: " + nextWeek);
    }
}
