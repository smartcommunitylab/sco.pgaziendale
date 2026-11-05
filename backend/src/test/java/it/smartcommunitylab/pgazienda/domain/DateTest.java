package it.smartcommunitylab.pgazienda.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.smartcommunitylab.pgazienda.util.DateUtils;

public class DateTest {

	@Test
	public void testWeek() {
		LocalDate start = LocalDate.of(2025, 7, 1);
		LocalDate end = LocalDate.of(2025, 7, 31);
		String s1 = start.format(DateUtils.WEEK_PATTERN);
		String s2 = end.format(DateUtils.WEEK_PATTERN);
		List<String> dr = DateUtils.getDateRangeByWeek(start, end);
		assertTrue(s1.equalsIgnoreCase(dr.get(0)));
		assertTrue(s2.equalsIgnoreCase(dr.get(dr.size()-1)));
	}
}
