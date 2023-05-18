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
package it.smartcommunitylab.pgazienda.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiParam;
import it.smartcommunitylab.pgazienda.config.AppConfig.SwaggerConf;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author raman
 *
 */
@Configuration
public class SwaggerConfig {

	@Autowired
	private SwaggerConf conf;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30).select()
				.apis(RequestHandlerSelectors.basePackage("it.smartcommunitylab.pgazienda"))
				.paths(PathSelectors.regex("/.*")).build()
				.directModelSubstitute(Pageable.class, SwaggerPageable.class).apiInfo(apiInfo())
				.securitySchemes(Arrays.asList(securitySchema())).securityContexts(Arrays.asList(securityContext()));
	}

	private SecurityScheme securitySchema() {
		return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build();
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(conf.getTitle(), conf.getDescription(), conf.getVersion(), null,
				new Contact(conf.getContact().get("name"), conf.getContact().get("url"), conf.getContact().get("email")), 
				conf.getLicense(), conf.getLicenseUrl(), Collections.emptyList());
	}
	
	private static class SwaggerPageable {

		@ApiParam(required = false, value = "Number of records per page", example = "0")
		public int size;

		@ApiParam(required = false, value = "Results page you want to retrieve (0..N)", example = "0")
		public int page;

		@ApiParam(required = false, value = "Sorting option: field,[asc,desc]", example = "nickname,desc")
		public String sort;

	}
	
}
