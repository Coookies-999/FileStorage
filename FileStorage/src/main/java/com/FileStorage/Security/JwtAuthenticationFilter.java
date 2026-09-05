package com.FileStorage.Security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;


    //This method will run once per every request made may it be login or signup or logout
    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        System.out.println(request.getMethod()+" Header "+ request.getRequestURI());


        String authHeader=request.getHeader("Authorization");

        // Allow CORS preflight request to pass through
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }


        //But when we login or signup we will not be having jwt with us after login we will bur before how will we so we just forward to spring security just to allow login and signup without jwt
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
//            System.out.println("Unauthorized bro...");
            filterChain.doFilter(request,response);
            return;
        }


        //Checking happens here , are you authenticated user or not!
        String token=authHeader.substring(7);
        try {
        String userId=jwtService.extractUserId(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId,null, Collections.emptyList());


        //added info about user to spring security context
        //And we can extract the userInfo such as userId from this security context while processing any user request
        SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Authorized");
        }catch (JwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //this line means I have completed authentication from my side now you can go on with your request
        filterChain.doFilter(request,response);
    }
}
