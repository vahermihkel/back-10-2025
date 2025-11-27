package ee.mihkel.veebipood.service;

import ee.mihkel.veebipood.entity.PersonRole;
import ee.mihkel.veebipood.model.TokenData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // if - pannakse authentication või mitte

        // paneb authenticationi
        if (request.getHeader(HttpHeaders.AUTHORIZATION) != null && request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")) {
            String token =  request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
            TokenData tokenData = jwtService.parseToken(token);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            if (PersonRole.valueOf(tokenData.getRole()) == PersonRole.ADMIN) {
                grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
            }
            if (PersonRole.valueOf(tokenData.getRole()) == PersonRole.SUPERADMIN) {
                grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
                grantedAuthorities.add(new SimpleGrantedAuthority("superadmin"));
            }
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    tokenData.getId(),tokenData.getEmail(), grantedAuthorities
            ));
        }

        filterChain.doFilter(request,response); // teeb sama mis originaalis
    }
}
