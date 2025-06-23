package com.jpmc.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class RouteValidator {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // List of endpoints that don't require authentication
    private static final String[] openApiEndpoints = {
            "/auth/**"
    };

    /**
     * Returns false if the request matches any of the open (unsecured) endpoints.
     * Otherwise, returns true, indicating JWT validation is required.
     */
    public boolean isSecured(ServerHttpRequest request) {
        String path = request.getPath().value();

        for (String openEndpoint : openApiEndpoints) {
            if (pathMatcher.match(openEndpoint, path)) {
                return false;
            }
        }

        return true;
    }
}
