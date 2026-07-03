package com.biznopay.authservice.infra.handler;

import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.gateway.UserGatewayImpl;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtHelper helper;
   private final UserGatewayImpl userGateway;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username = this.helper.getUsername(token);

        if (StringUtils.isNotEmpty(username)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails =  this.userGateway.findByUsername(username);

            if (this.validateToken.isValid(token, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}