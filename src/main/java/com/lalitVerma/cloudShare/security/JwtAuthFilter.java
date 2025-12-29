package com.lalitVerma.cloudShare.security;

import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            log.info("incoming request: {}", request.getRequestURI());

            final String requestTokenHeader =  request.getHeader("Authorization");

            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestTokenHeader.substring(7);
            String email = this.authUtils.getUsernameFromToken(token);

            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = this.userRepository.findByEmail(email).orElseThrow();
                UsernamePasswordAuthenticationToken  authentication
                        = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // Adding in SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Moving forward in FilterChain
            filterChain.doFilter(request, response);
        }
        catch(Exception ex){
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
