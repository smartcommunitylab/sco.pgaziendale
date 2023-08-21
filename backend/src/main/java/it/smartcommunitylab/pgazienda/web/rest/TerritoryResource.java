package it.smartcommunitylab.pgazienda.web.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Territory;
import it.smartcommunitylab.pgazienda.service.PGAppService;

@RestController
@RequestMapping("/api")
public class TerritoryResource {
    
    @Autowired
    private PGAppService appService;

    @GetMapping("/territories")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
    public ResponseEntity<Collection<Territory>> getTerritories() {
        return ResponseEntity.ok(appService.getTerritories());
    }
}
