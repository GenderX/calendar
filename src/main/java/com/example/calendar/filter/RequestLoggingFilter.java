package com.example.calendar.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println(
                "Incoming request: " + req.getMethod() + " " + req.getRequestURI() + " from " + req.getRemoteAddr());
        chain.doFilter(request, response);
    }
}
