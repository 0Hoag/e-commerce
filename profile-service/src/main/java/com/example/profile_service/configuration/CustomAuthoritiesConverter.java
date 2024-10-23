package com.example.profile_service.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public class CustomAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
    private final String REALM_ACCESS = "realm_access";
    private final String ROLE_PREFIX = "ROLE_";
    private final String ROLES = "roles";

    @Override
    public Flux<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccessMap = source.getClaimAsMap(REALM_ACCESS);

        if (realmAccessMap == null) {
            return Flux.empty();
        }

        Object roles = realmAccessMap.get(ROLES);

        if (roles instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> stringRoles = (List<String>) roles;
            return Flux.fromIterable(stringRoles)
                    .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role));
        }

        return Flux.empty();
    }
}
