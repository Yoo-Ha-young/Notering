package com.project.Notering.configuration.filter;

import com.project.Notering.model.User;
import com.project.Notering.service.UserService;
import com.project.Notering.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String key;
    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/users/join") ||
                path.equals("/api/v1/users/login")  ||
                path.startsWith("/api/v1/posts/feed");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // get header
        final String token;


        try {
            if(TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else {
                final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                if(header == null || !header.startsWith("Bearer ")) {
                    log.error("Error occurs while getting header. header is null or invalid");
                    filterChain.doFilter(request, response);
                    return;
            }
                token = header.split(" ")[1].trim();

            }

            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            String userName = JwtTokenUtils.getUserName(token, key);
            log.debug("Extracted userName from token: {}", userName);
            log.info("Extracted userName from token: {}", userName);

            User user = userService.loadUserByUsername(userName);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            filterChain.doFilter(request, response);
            log.error("Error occurs while validating. {}", e.toString());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
