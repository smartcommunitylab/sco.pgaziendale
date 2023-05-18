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

package it.smartcommunitylab.pgazienda.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.pgazienda.domain.Employee;

/**
 * @author raman
 *
 */
public interface EmployeeRepository  extends MongoRepository<Employee, String> {

	public Page<Employee> findByCompanyId(String companyId, Pageable pageable);
	public Page<Employee> findByCompanyIdAndLocation(String id, String location, Pageable pageable);
	public List<Employee> findByCompanyIdAndCodeIgnoreCase(String id, String key);
	public List<Employee> findByCompanyIdAndCampaigns(String companyId, String campaignId);

	public List<Employee> findByCampaigns(String campaignId);

	public List<Employee> findByCompanyIdAndLocation(String id, String location);

	public List<Employee> findByCompanyId(String companyId);

}
