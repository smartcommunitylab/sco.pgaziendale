/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package it.smartcommunitylab.pgazienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackLegDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackPointDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.TrackUtils;

/**
 * @author raman
 *
 */
@SpringBootTest(classes = PGAziendaApp.class)
public class TrackingDataServiceTest {

	@Autowired
	private DayStatRepository dayStatRepo;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private TrackingDataService tds;
	@Autowired
	private  UserRepository userRepo;
	@Autowired
	private  EmployeeRepository employeeRepo;
	
	private Campaign campaign;
	private Company company;
	private Employee employee;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Set<String> employees;
	private List<Constants.STAT_FIELD> fields;

	private static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
	private static DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("YYYY-ww", Constants.DEFAULT_LOCALE);

    /**
     * Init test data before each test.
     *
     * This method is annotated with @BeforeEach, so it will be called before each test.
     * It deletes all test data, creates a new campaign, a new company and a new employee.
     * It also creates a new user and a new subscription.
     *
     */
    @BeforeEach
    public void init() {
    	dayStatRepo.deleteAll();
    	campaignRepo.deleteAll();
    	companyRepo.deleteAll();
    	userRepo.deleteAll();
    	employeeRepo.deleteAll();

		String from = "2020-01-01";
		String to = "2020-12-31";

		fromDate = LocalDate.parse(from);
		toDate = LocalDate.parse(to);
    	
    	campaign = new Campaign();
    	campaign.setActive(true);
    	campaign.setMeans(Collections.singletonList(MEAN.bike.toString()));
    	campaign.setId("biketowork");
		campaign.setFrom(fromDate);
		campaign.setTo(toDate);
    	
		LinkedList<Limit> limits = new LinkedList<>();
		limits.add(new Limit(Constants.AGG_DAY, Constants.MEAN.bike.toString(), 20d)); 
		limits.add(new Limit(Constants.AGG_MONTH, Constants.MEAN.bike.toString(), 250d));
		campaign.setScoreLimits(limits);
		limits = new LinkedList<>();
		limits.add(new Limit(Constants.AGG_DAY, Constants.MEAN.bike.toString(), 4d)); 
		campaign.setTrackLimits(limits);
    	
		campaign.getVirtualScore().setBike(new VirtualScoreValue(Constants.METRIC_DISTANCE, 0.001));

    	campaign = campaignRepo.save(campaign);
    	
    	company = new Company();
    	company.setCode("code");
    	company.setName("test company");
    	CompanyLocation loc = new CompanyLocation();
    	loc.setId("testlocation");
    	loc.setAddress("someaddress");
    	loc.setStreetNumber("1");
    	loc.setCity("somecity");
    	loc.setProvince("someprovince");
		loc.setLatitude(10d);
		loc.setLongitude(10d);
		loc.setRadius(0.2);
    	loc.setZip("123456");
    	company.setLocations(Collections.singletonList(loc));
    	company = companyRepo.save(company);
    	
    	employee = new Employee();
    	employee.setCompanyId(company.getId());
    	employee.setCode("employeecode");
    	employee.setName("First");
    	employee.setSurname("Last");
    	employee.setLocation("testlocation");
		employee = employeeRepo.save(employee);
    	
		User user = new User();
		user.setPlayerId("test");
        user.setUsername("test@example.com");
		Subscription s = new Subscription();
		s.setCompanyCode(company.getCode());
		s.setCampaign(campaign.getId());
		s.setKey(employee.getCode());
		
		UserRole role = UserRole.createAppUserRole(s);
		user.setRoles(Collections.singletonList(role));
		userRepo.save(user);

		employees = new HashSet<>();
		employees.add(employee.getId());
		fields = new ArrayList<>();
		fields.add(Constants.STAT_FIELD.score);
		fields.add(Constants.STAT_FIELD.limitedScore);
		fields.add(Constants.STAT_FIELD.trackCount);
		fields.add(Constants.STAT_FIELD.limitedTrackCount);
		fields.add(Constants.STAT_FIELD.co2saved);

		prepareStatData();
    }


    /**
     * Tests the validation of tracks for the "biketowork" campaign.
     *
     * This test covers various scenarios:
     * 1. Nominal case: The track starts at a valid location and should be valid.
     * 2. Multi-location enabled: The track does not finish at a valid location and should be invalid.
     * 3. Multi-location with added end location: The track finishes at a new valid location and should be valid.
     * 4. Multi-location with user locations: Employee-specific locations are considered, and the track should be valid.
     *
     * @throws InconsistentDataException if there is an issue with the track data during validation
     */
	@Test
	public void testValidation() throws InconsistentDataException {
		// nominal case: valid as starts at location
		TrackDTO track = prepareTrack(MEAN.bike);
		TrackValidityDTO validity = tds.validate("biketowork", "test", track);
		assertEquals(true, validity.isValid());
		
		// multi-location - should fail as not finishes at location
		campaign.setUseMultiLocation(true);
		campaign = campaignRepo.save(campaign);
		validity = tds.validate("biketowork", "test", track);
		assertEquals(false, validity.isValid());
		
		// multi-location - should succeed as the new location added at the end of the trip
		List<CompanyLocation> locations = new LinkedList<>(company.getLocations());
		CompanyLocation loc = new CompanyLocation();
    	loc.setId("testlocation2");
    	loc.setAddress("someaddress");
    	loc.setStreetNumber("1");
    	loc.setCity("somecity");
    	loc.setProvince("someprovince");
		loc.setLatitude(19d);
		loc.setLongitude(19d);
		loc.setRadius(0.2);
    	loc.setZip("123456");
		locations.add(loc);
    	company.setLocations(locations);
    	company = companyRepo.save(company);
		validity = tds.validate("biketowork", "test", track);
		assertEquals(true, validity.isValid());


		// multi-location with user locations - should succeed
		campaign.setUseEmployeeLocation(true);
		campaign = campaignRepo.save(campaign);
		validity = tds.validate("biketowork", "test", track);
		assertEquals(true, validity.isValid());

	}
    
	/**
	 * Utility method to create a TrackDTO given a set of means.
	 * The generated track has a single leg with 10 points, each one at a different location
	 * and with a distance of 10 meters from the previous one.
	 * The leg has a mean which is the first of the given means.
	 * The track has a duration of 10 seconds.
	 *
	 * @param means the means of the legs
	 * @return the generated track
	 */
	private TrackDTO prepareTrack(MEAN ... means) {
		TrackDTO track = new TrackDTO();
		List<TrackLegDTO> legs = new ArrayList<>();
		double lat = 10d, lon = 10d;
		for (MEAN m : means) {
			TrackLegDTO leg = new TrackLegDTO();
			double dist = 0d;
			leg.setId("1");
			leg.setMean(m.toString());
			leg.setValid(true);
			List<TrackPointDTO> points = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				TrackPointDTO point = new TrackPointDTO();
				point.setRecorded_at(System.currentTimeMillis() + i);
				point.setLatitude(lat);
				point.setLongitude(lon);
				lat++;
				lon++;
				dist += TrackUtils.harvesineDistance(point.getLatitude(), point.getLongitude(), lat, lon);
				points.add(point);
			}

			leg.setPoints(points);
			leg.setDistance(dist);
			leg.setDuration(10);
			legs.add(leg);
		}
		track.setStartTime(legs.get(0).getPoints().get(0).getRecorded_at());		
		track.setMultimodalId("123");
		track.setLegs(legs);
		return track;

	}


	/**
	 * Prepares 45 days of statistics, each with a single track with distance of 1000
	 * more than the previous day. The tracks are multimodal and have a mode of "bike".
	 * The campaign and company are set to the ones created in the setup method.
	 */
	private void prepareStatData() {
		LocalDate date = LocalDate.parse("2020-01-01");
		for (int i = 0; i < 45; i++) {
			DayStat ds = new DayStat();
			ds.setPlayerId("test");
			ds.setCampaign(campaign.getId());
			ds.setCompany(company.getId());
			ds.setMonth(date.format(monthFormatter));
			ds.setWeek(date.format(weekFormatter));
			ds.setDate(date.toString());

			TrackingData td = new TrackingData();
			td.setMode("bike");
			td.setDistance(1000 * i + 100);
			td.setDuration(1000);
			td.setCo2(10);
			td.setMultimodalId("mm" + i);
			ds.getTracks().add(td);

			
			tds.limitScore(campaign, "test", ds);
			dayStatRepo.save(ds);
			
			date = date.plusDays(1);
		}
	}


	/**
	 * Test the statistics method.
	 *
	 * This test verifies that the TrackingDataService#statistics method properly
	 * calls the DayStatRepository and returns the expected statistics.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the statistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatistics() throws Exception{
		//Basic test
		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				employees,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.company,
				fields,
				fromDate,
				toDate,
				true);
	}

	/**
	 * Test the statistics method with null employeeId.
	 *
	 * This test verifies that the TrackingDataService#statistics method properly
	 * calls the DayStatRepository and returns the expected statistics when the
	 * employeeId is null.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the statistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatisticsEmployeesNull() throws Exception{
		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				null,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.company,
				fields,
				fromDate,
				toDate,
				true);

	}

	/**
	 * Test the statistics method with GROUP_BY_DATA.campaign.
	 *
	 * This test verifies that the TrackingDataService#statistics method properly
	 * calls the DayStatRepository and returns the expected statistics when the
	 * dataGroupBy is GROUP_BY_DATA.campaign.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the statistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatisticsGroupByCampaign() throws Exception{
		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				employees,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.campaign,
				fields,
				fromDate,
				toDate,
				true);

	}

	/**
	 * Test the statistics method with GROUP_BY_DATA.employee.
	 *
	 * This test verifies that the TrackingDataService#statistics method properly
	 * calls the DayStatRepository and returns the expected statistics when the
	 * dataGroupBy is GROUP_BY_DATA.employee.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the statistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatisticsGroupByEmployees() throws Exception{
		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				null,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.employee,
				fields,
				fromDate,
				toDate,
				true);

		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				employees,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.employee,
				fields,
				fromDate,
				toDate,
				false);
	}

	/**
	 * Test the statistics method with GROUP_BY_DATA.location.
	 *
	 * This test verifies that the TrackingDataService#statistics method properly
	 * calls the DayStatRepository and returns the expected statistics when the
	 * dataGroupBy is GROUP_BY_DATA.location.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the statistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatisticsGroupByLocation() throws Exception{
		tds.statistics(
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				employees,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.location,
				fields,
				fromDate,
				toDate,
				true);
	}


	/**
	 * Test the csvStatistics method with GROUP_BY_DATA.company.
	 *
	 * This test verifies that the TrackingDataService#csvStatistics method properly
	 * calls the DayStatRepository and returns the expected statistics when the
	 * dataGroupBy is GROUP_BY_DATA.company.
	 *
	 * The test is parameterized with the following parameters:
	 * - campaignId: the ID of the campaign
	 * - companyId: the ID of the company
	 * - location: the location of the campaign
	 * - employeeId: a set of employee IDs
	 * - timeGroupBy: the time group by parameter
	 * - dataGroupBy: the data group by parameter
	 * - fields: a list of fields to include in the statistics
	 * - from: the start date of the statistics
	 * - to: the end date of the statistics
	 * - all: whether to include all employees or not
	 *
	 * The test performs a request to the csvStatistics endpoint and verifies that
	 * the response is a 200 OK with the expected statistics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCsvStatistics() throws Exception{

		fields.add(Constants.STAT_FIELD.meanScore);
		fields.add(Constants.STAT_FIELD.limitedMeanScore);
		fields.add(Constants.STAT_FIELD.meanDistance);
		fields.add(Constants.STAT_FIELD.meanDuration);
		fields.add(Constants.STAT_FIELD.meanCo2);
		fields.add(Constants.STAT_FIELD.meanTracks);


		PrintWriter writer = new PrintWriter(new FileWriter("src/test/resources/createdTestTrackStats.csv"));

		tds.csvStatistics(writer,
				campaign.getId(),
				company.getId(),
				company.getLocations().get(0).getId(),
				employees,
				Constants.GROUP_BY_TIME.month,
				Constants.GROUP_BY_DATA.company,
				fields,
				fromDate,
				toDate,
				true);
	}

}
