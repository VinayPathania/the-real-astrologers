package com.astro.theRealAstrologers.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. Look at the HTTP Headers for the VIP Pass
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // JWTs are traditionally passed in a header like this: "Bearer eyJhbGci..."
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Cut off the word "Bearer "
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                // Token is broken
            }
        }

        // 2. If we found an email, and Spring Security doesn't already know who they are...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 3. Scan the token!
            if (jwtUtil.validateToken(jwt)) {

                // 4. Success! Tell Spring Security: "This user is legit, let them in!"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Unlock the door
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Move to the next step in the server (either reject them or let them hit the Controller)
        chain.doFilter(request, response);
    }
}
