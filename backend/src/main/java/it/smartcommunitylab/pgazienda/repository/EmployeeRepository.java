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

import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.pgazienda.domain.Employee;

/**
 * @author raman
 *
 */
public interface EmployeeRepository  extends MongoRepository<Employee, String> {

	public List<Employee> findByCompanyId(String companyId);
	public List<Employee> findByCompanyIdAndLocation(String id, String location);
	
}
