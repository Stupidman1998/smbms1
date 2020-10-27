package com.bewind.servlet.user;

import com.bewind.pojo.User;
import com.bewind.service.user.UserService;
import com.bewind.service.user.UserServiceImpl;
import com.bewind.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    //Servlet:控制层，调用业务层代码

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //获取前端传递的用户名和密码
        System.out.println("---LoginServlet:start LoginServlet---");
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中的密码进行对比，调用业务层
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        //查有此人，可以登录
        if (user != null) {
            //将用户的信息放到Session中
            req.getSession().setAttribute(Constants.USER_SESSION, user);
            //跳转到用户主页
            resp.sendRedirect("jsp/frame.jsp");
        } else {
            //查无此人，登陆失败
            req.setAttribute("error","用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
