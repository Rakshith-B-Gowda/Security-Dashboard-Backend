package com.jpmc.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class RouteValidator {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Returns false if the request matches an open (unsecured) endpoint.
     * In this case, any request whose path matches /auth/** is considered open.
     */
    public boolean isSecured(ServerHttpRequest request) {
        String path = request.getPath().value();
        // If the path matches /auth/**, then no JWT check is required.
        return !pathMatcher.match("/auth/**", path);
    }
}
