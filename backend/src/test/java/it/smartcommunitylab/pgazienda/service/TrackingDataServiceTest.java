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
import java.util.Collections;
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
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.util.LimitsUtils;

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
    public void testStats() {
    	
		List<DayStat> aggregation = tds.doPlayerAggregation(campaign.getId(), LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-28"));
		assertEquals(500d, aggregation.get(0).getDistances().getBike());
    	
    }

    @Test
    public void testCompanyCSV() {
    	StringWriter writer = new StringWriter();
    	tds.createEmployeeStats(writer, campaign.getId(), company.getId(), LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-28"));
    	assertEquals("\"Nome\";\"Cognome\";\"CodiceSede\";\"ViaggiValidi\";\"KmTotValidi_bike\"\n"
    			+ "\"First\";\"Last\";\"testlocation\";\"45\";\"500.0\"", writer.toString().trim());
    }

    @Test
    public void testLocationCSV() {
    	StringWriter writer = new StringWriter();
    	tds.createLocationStats(writer, campaign.getId(), company.getId(), LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-28"));
    	assertEquals("\"Indentificativo\";\"Indirizzo\";\"Numero\";\"CAP\";\"Comune\";\"Provincia\";\"ViaggiValidi\";\"KmTotValidi_bike\"\n"
    			+ "\"testlocation\";\"someaddress\";\"1\";\"123456\";\"somecity\";\"someprovince\";\"45\";\"500.0\"", writer.toString().trim());
    }

    @Test
    public void testGlobalCSV() {
    	StringWriter writer = new StringWriter();
    	tds.createCampaignStats(writer, campaign.getId(), LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-28"));
    	System.err.println(writer.toString().trim());
//    	assertEquals("\"Indentificativo\";\"Indirizzo\";\"Numero\";\"CAP\";\"Comune\";\"Provincia\";\"ViaggiValidi\";\"KmTotValidi_bike\"\n"
//    			+ "\"testlocation\";\"someaddress\";\"1\";\"123456\";\"somecity\";\"someprovince\";\"45\";\"500.0\"", writer.toString().trim());
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
			ds.getDistances().setBike((double)i);
			ds.setTrackCount(1);
			ds.setMonth(date.format(monthFormatter));
			ds.setDate(date.toString());
			ds.setLimitedDistances(LimitsUtils.applyLimits(ds.getDistances(), Constants.AGG_DAY, campaign));
			dayStatRepo.save(ds);
			
			date = date.plusDays(1);
		}
	}
}
