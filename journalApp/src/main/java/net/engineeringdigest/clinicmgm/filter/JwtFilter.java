package net.engineeringdigest.clinicmgm.filter;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.utilis.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/*@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader=request.getHeader("Authorization");
        String email=null;
        String jwt=null;
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            jwt=authorizationHeader.substring(7);
            email=jwtUtil.extractEmail(jwt);

        }
        if(email!=null){
            UserDetails userDetails=userDetailsService.loadUserByUsername(email);
            if(jwtUtil.validateToken(jwt)){
                UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request,response);
    }
}*/

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*System.out.println("===== JWT FILTER DEBUG =====");
        System.out.println("1. Auth Header: " + authHeader);
        System.out.println("2. Request URI: " + request.getRequestURI());*/

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
//            System.out.println("3. JWT Token: " + jwt.substring(0, Math.min(50, jwt.length())) + "...");

            try {
                boolean isValid = jwtUtil.validateToken(jwt);
//                System.out.println("4. Token Valid: " + isValid);

                if (isValid) {
                    Long doctorId = jwtUtil.extractDoctorId(jwt);
                    String email=jwtUtil.extractEmail(jwt);
//                    System.out.println("5. Doctor ID: " + doctorId);

                    if (doctorId != null &&
                            SecurityContextHolder.getContext().getAuthentication() == null) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority("ROLE_DOCTOR")
                                        )
                                );

                        /*authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );*/
                        authentication.setDetails(doctorId);

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

//                        System.out.println("6. Authentication SET for doctorId: " + doctorId);
                    }
                } else {
//                    System.out.println("4. Token INVALID");
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                SecurityContextHolder.clearContext();
            }
        }

//        System.out.println("7. Final Auth in Context: " + SecurityContextHolder.getContext().getAuthentication());
//        System.out.println("=============================\n");

        filterChain.doFilter(request, response);
    }
}