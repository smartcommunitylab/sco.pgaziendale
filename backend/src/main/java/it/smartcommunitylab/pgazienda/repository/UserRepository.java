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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import it.smartcommunitylab.pgazienda.domain.User;

/**
 * @author raman
 *
 */
public interface UserRepository  extends MongoRepository<User, String> {

	@Query("{'roles.companyId': ?0}")
	List<User> findByCompanyId(String id);
	
	@Query(value = "{'roles.subscriptions.companyCode': ?0}", count = true)
	long countSubscriptionByCompanyCode(String companyCode);

	Optional<User> findByUsername(String username);

	Optional<User> findByPlayerId(String playerId);

	Optional<User> findOneByActivationKey(String key);

	Optional<User> findOneByResetKey(String key);

	Optional<User> findOneByUsernameIgnoreCase(String mail);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

	@Query("{'roles.subscriptions.campaign': {$in: ?0}}")
	List<User> findByCampaignIn(List<String> campaigns);

		@Query("{'playerId': {$in: ?0}}")
	List<User> findByPlayerIdIn(Set<String> ids);

	@Query("{'roles.subscriptions': {$elemMatch: {companyCode:?0, key:?1}}}")
	Optional<User> findOneByEmployeeCode(String companyCode, String key);
	
	@Query("{'roles.subscriptions': {$elemMatch: {campaign: ?0, companyCode:?1, key:{$in:?2}}}}")
	List<User> findByCampaignAndCompanyAndEmployeeCode(String campaign, String companyCode, Set<String> keys);

	@Query("{'roles.subscriptions': {$elemMatch: {campaign: ?0, companyCode:?1, key:{$regex:?2, $options: 'i'}}}}")
	List<User> findByCampaignAndCompanyAndEmployeeCode(String campaign, String companyCode, String key);

	@Query("{'roles.subscriptions': {$elemMatch: {campaign: ?0, companyCode:?1, key:{$in:?2}, upgraded: {$ne: true}}}}")
	List<User> findByCampaignAndCompanyAndEmployeeCodeNotUpgraded(String campaign, String companyCode, Set<String> keys);

	@Query("{'roles.subscriptions': {$elemMatch: {campaign: ?0, companyCode:?1}}}")
	List<User> findByCampaignAndCompany(String campaign, String companyCode);
	
	@Query("{'roles.subscriptions': {$elemMatch: {campaign: ?0, companyCode:?1, key:?2}}}")
	Optional<User> findOneByCampaignAndCompanyAndKey(String campaign, String companyCode, String key);
	
}
