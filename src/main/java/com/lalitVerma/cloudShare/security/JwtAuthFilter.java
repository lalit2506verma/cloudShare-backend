package com.lalitVerma.cloudShare.security;

import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private static final PathPatternParser PARSER = new PathPatternParser();
    private static final List<PathPattern> PUBLIC_PATHS = List.of(
            PARSER.parse("/auth/login"),
            PARSER.parse("/api/user/register"),
            PARSER.parse("/files/public/**"),
            PARSER.parse("/files/download/**")
    );

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
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
            this.handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(pattern -> pattern.matches(PathContainer.parsePath(path)));
    }
}
