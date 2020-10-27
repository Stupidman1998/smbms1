package com.bewind.filter;

import com.bewind.pojo.User;
import com.bewind.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);

        //如果user为空，即用户已经注销
        if (user == null) {
            resp.sendRedirect("/smbms/error.jsp");
        } else {
            chain.doFilter(req,resp);
        }

    }

    public void destroy() {

    }
}
