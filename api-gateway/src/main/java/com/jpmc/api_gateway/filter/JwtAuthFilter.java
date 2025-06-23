package com.jpmc.api_gateway.filter;

import com.jpmc.api_gateway.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtTokenService jwtTokenService;
    private final RouteValidator routeValidator;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtTokenService jwtTokenService, RouteValidator routeValidator) {
        super(Config.class);
        this.jwtTokenService = jwtTokenService;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            logger.info("Incoming request to path: {}", path);

            // Check if the route should be secured
            if (!routeValidator.isSecured(request)) {
                logger.info("Public endpoint detected. Skipping JWT validation for: {}", path);
                return chain.filter(exchange);
            }

            logger.debug("Secured endpoint. Verifying Authorization header for: {}", path);

            if (!request.getHeaders().containsKey("Authorization")) {
                logger.warn("Authorization header missing for request to: {}", path);
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format received: {}", authHeader);
                return onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.replace("Bearer ", "").trim();
            logger.debug("Extracted token: {}", token);

            try {
                Claims claims = jwtTokenService.validateToken(token);
                logger.info("JWT validated successfully. Claims: {}", claims);

                String role = claims.get("role", String.class);
                logger.info("Extracted role from claims: {}", role);

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Role", role)
                        .build();

                logger.info("Forwarding request with added X-Role header: {}", role);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (JwtException e) {
                logger.error("JWT validation failed: {}", e.getMessage());
                return onError(exchange, "Invalid JWT Token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        logger.warn("Authentication error: {}, returning {}", err, status);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String errorJson = "{\"error\": \"" + err + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {
        // You can add additional properties for customization here in the future.
    }
}
