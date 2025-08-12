package it.smartcommunitylab.pgazienda.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.smartcommunitylab.pgazienda.domain.Constants;

public class DateUtils {
	public static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	public static final DateTimeFormatter YEAR_PATTERN = DateTimeFormatter.ofPattern("yyyy");
	public static final DateTimeFormatter WEEK_PATTERN = DateTimeFormatter.ofPattern("YYYY-ww", Constants.DEFAULT_LOCALE);
	public static final DateTimeFormatter HOUR_PATTERN = DateTimeFormatter.ofPattern("HH", Constants.DEFAULT_LOCALE);
	
    public static List<String> getDateRangeStrings(LocalDate start, LocalDate end) {
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.toString()); // formato "YYYY-MM-DD"
            current = current.plusDays(1);
        }
        return dates;
    }
    
    public static List<String> getDateRangeByMonth(LocalDate start, LocalDate end) {
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(DateUtils.MONTH_PATTERN));
            current = current.plusMonths(1);
        }
        return dates;
    }
 
    public static List<String> getDateRangeByYear(LocalDate start, LocalDate end) {
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(DateUtils.YEAR_PATTERN));
            current = current.plusYears(1);
        }
        return dates;
    }
    
    public static List<String> getDateRangeByWeek(LocalDate start, LocalDate end) {
        List<String> dates = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(DateUtils.WEEK_PATTERN));
            current = current.plusWeeks(1);
        }
        return dates;
    }
    
    public static List<String> getDateRangeByHour(LocalDate start, LocalDate end) {
    	return Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "15", "17", "18", "19", "20", "21", "22", "23");
    }

    public static List<String> getDateRangeByDayOfWeek(LocalDate start, LocalDate end) {
    	return Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    }

}