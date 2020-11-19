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

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;

/**
 * @author raman
 *
 */
@Service
public class PGAppService {

	@Autowired
	private PGAppRepository repo;
	

	public List<PGApp> getApps() {
		return repo.findAll();
	}

	public Optional<PGApp> getApp(String id) {
		return repo.findById(id);
	}

	public PGApp saveApp(PGApp app) {
		return repo.save(app);
	}

	public void deleteApp(String id) {
		repo.deleteById(id);
	}
}
