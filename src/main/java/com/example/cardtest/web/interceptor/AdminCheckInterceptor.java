package com.example.cardtest.web.interceptor;

import com.example.cardtest.domain.Role;
import com.example.cardtest.web.SessionMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/member/login");
            return false;
        }

        SessionMember member = (SessionMember) session.getAttribute("loggedMember");

        if (member == null || member.getRole() != Role.ADMIN) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
