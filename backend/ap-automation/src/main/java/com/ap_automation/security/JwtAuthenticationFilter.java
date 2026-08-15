package com.ap_automation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        // 1. Skip CORS preflight request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            filterChain.doFilter(request, response);

            return;
        }


        // 2. Get Authorization header
        String authHeader = request.getHeader("Authorization");


        // 3. No JWT token
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }


        // 4. Extract JWT token
        String token = authHeader.substring(7);


        try {

            // 5. Extract username from JWT
            String username =
                    jwtService.extractUsername(token);


            // 6. Check whether user is already authenticated
            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {


                // 7. Load user from database
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);


                // 8. Validate JWT
                if (jwtService.isTokenValid(
                        token,
                        userDetails
                )) {


                    // 9. Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    // 10. Add request details
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    // 11. Store authentication
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {

            // Invalid / expired JWT
            SecurityContextHolder
                    .clearContext();

            // Continue request.
            // Spring Security will decide whether
            // the endpoint requires authentication.

        }


        // 12. Continue filter chain
        filterChain.doFilter(request, response);
    }
}