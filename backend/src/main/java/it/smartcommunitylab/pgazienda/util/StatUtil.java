package it.smartcommunitylab.pgazienda.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;

public class StatUtil {
	
	public static String getGroupKey(String campaignId, String timeGroup, String dataGroup) {
		String key = campaignId + "_" + timeGroup;
		if(StringUtils.isNotBlank(dataGroup)) key += "_" + dataGroup;
		return key;
	}

	public static List<String> getTimeGroupList(LocalDate start, LocalDate end, GROUP_BY_TIME timeGroupBy) {
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return DateUtils.getDateRangeStrings(start, end);
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return DateUtils.getDateRangeByWeek(start, end);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return DateUtils.getDateRangeByMonth(start, end);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return DateUtils.getDateRangeByYear(start, end);
		if (GROUP_BY_TIME.hour.equals(timeGroupBy)) return DateUtils.getDateRangeByHour(start, end);
		if (GROUP_BY_TIME.dayOfWeek.equals(timeGroupBy)) return DateUtils.getDateRangeByDayOfWeek(start, end); 
		if (GROUP_BY_TIME.total.equals(timeGroupBy)) return DateUtils.getDateRangeByTotal(start, end);
		return Collections.emptyList();		
	}

	public static List<String> getHeadersFromStats(List<Map<String, Object>> stats, GROUP_BY_TIME timeGroupBy) {
		Set<String> headers = new LinkedHashSet<>();
		for (Map<String, Object> r : stats) {
			for (String key : r.keySet()) {
				if (!key.equals("campaign") && !key.equals("id") 
					&& !key.equals("name") && !key.equals(timeGroupBy.toString())) {
					headers.add(key);
				}
			}
		}
		// sort headers to have a consistent order
		List<String> sortedHeaders = new ArrayList<>(headers);
		Collections.sort(sortedHeaders);
		return sortedHeaders;
	}

	public static List<String> buildPivotHeaders(List<String> metricHeaders, List<String> timeHeaders) {
		List<String> headers = new ArrayList<>();
		headers.add("id");
		headers.add("name");
		for (String timeHeader : timeHeaders) {
			for (String metricHeader : metricHeaders) {
				headers.add(timeHeader + "__" + metricHeader);
			}
		}
		return headers;
	}	

	public static List<String[]> buildPivotRows(List<Map<String, Object>> stats, GROUP_BY_TIME timeGroupBy, List<String> timeHeaders, List<String> metricHeaders) {
		Map<String, List<Map<String, Object>>> groupedById = stats.stream().collect(Collectors.groupingBy(r -> String.valueOf(r.getOrDefault("id", ""))));
		List<String[]> rows = new ArrayList<>();
		for (Map.Entry<String, List<Map<String, Object>>> entry : groupedById.entrySet()) {
			List<String> row = new ArrayList<>();
			List<Map<String, Object>> groupRows = entry.getValue();
			row.add(entry.getKey());
			row.add(String.valueOf(groupRows.get(0).getOrDefault("name", "")));
			for (String timeHeader : timeHeaders) {
				Map<String, Object> timeRow = groupRows.stream()
						.filter(r -> timeHeader.equals(String.valueOf(r.getOrDefault(timeGroupBy.toString(), ""))))
						.findFirst()
						.orElse(Collections.emptyMap());
				for (String metricHeader : metricHeaders) {
					Object value = timeRow.get(metricHeader);
					row.add(value == null ? "" : value.toString());
				}
			}
			rows.add(row.toArray(new String[0]));
		}
		rows.sort((a, b) -> a[1].compareToIgnoreCase(b[1]));
		return rows;
	}	
}
