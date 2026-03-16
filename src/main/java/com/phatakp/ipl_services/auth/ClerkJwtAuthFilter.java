package com.phatakp.ipl_services.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phatakp.ipl_services.config.AppProperties;
import com.phatakp.ipl_services.config.exceptions.APIException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    private final ClerkJwksProvider clerkJwksProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String token = authHeader.substring(7);
            String[] chunks = token.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);
            String kid = headerNode.get("kid").asText();

            // get correct public key
            PublicKey publicKey = clerkJwksProvider.getPublicKey(kid);

            //Verify token
            Claims claims = Jwts
                    .parser()
                    .verifyWith(publicKey)
                    .clockSkewSeconds(60)
                    .requireIssuer(appProperties.getClerkIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String clerkUserId = claims.getSubject();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    clerkUserId,null, List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            )
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("ClerkJwtAuthFilter doFilterInternal()", e);
            throw APIException.builder().message("Invalid JWT Token.").status(401).build();
        }

    }
}


