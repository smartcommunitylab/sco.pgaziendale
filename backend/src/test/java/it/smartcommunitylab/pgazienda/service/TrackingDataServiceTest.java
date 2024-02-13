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

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
	
	private static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
	private static DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("YYYY-ww", Constants.DEFAULT_LOCALE);

    @BeforeEach
    public void init() {
    	dayStatRepo.deleteAll();
    	campaignRepo.deleteAll();
    	companyRepo.deleteAll();
    	userRepo.deleteAll();
    	employeeRepo.deleteAll();
    	
    	campaign = new Campaign();
    	campaign.setActive(true);
    	campaign.setMeans(Collections.singletonList(MEAN.bike.toString()));
    	campaign.setId("biketowork");
    	
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
    	
    	Employee employee = new Employee();
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

		prepareStatData();
    }


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
	 * 
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
}
