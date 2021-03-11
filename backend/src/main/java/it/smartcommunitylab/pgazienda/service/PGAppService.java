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
import java.util.stream.Collectors;

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
		return repo.findAll().stream().map(app -> app.cleanPassword()).collect(Collectors.toList());
	}

	public Optional<PGApp> getApp(String id) {
		return repo.findById(id).map(app -> app.cleanPassword());
	}

	public PGApp createApp(PGApp app) {
		return repo.save(app).cleanPassword();
	}

	public PGApp updateApp(PGApp app) {
		PGApp old = repo.findById(app.getId()).orElse(null);
		if (old != null) {
			old.setName(app.getName());
			old.setEndpoint(app.getEndpoint());
			if (app.getPassword() != null) old.setPassword(app.getPassword());
			return repo.save(old).cleanPassword();
		} else {
			return createApp(app);
		}
	}

	public void deleteApp(String id) {
		repo.deleteById(id);
	}
}
