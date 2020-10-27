package com.bewind.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.bewind.pojo.Role;
import com.bewind.pojo.User;
import com.bewind.service.role.RoleServiceImpl;
import com.bewind.service.user.UserService;
import com.bewind.service.user.UserServiceImpl;
import com.bewind.util.Constants;
import com.bewind.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

//实现Servlet复用
//将方法提取出来，在doGet/doPost中判断前端需要调用的方法，再调用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("UserServlet---start....");
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.pwdModify(req, resp);
        } else if (method.equals("query") && method != null) {
            this.query(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //查询方法
    public void query(HttpServletRequest req, HttpServletResponse resp){

        //查询用户列表
        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserServiceImpl userService = new  UserServiceImpl();
        List<User> userList = null;

        //第一次请求，一定是第一页
        int pageSize = 5;
        int currentPageNo = 1;

        if(queryUserName == null){
            queryUserName = "";
        }
        if (temp!=null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex!=null){
            currentPageNo = Integer.parseInt(pageIndex);
        }
        
        //获取用户的总数
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        //控制首页和尾页
        if (currentPageNo<1){
            currentPageNo = 1;
        } else if(currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
        req.setAttribute("userList",userList);

        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从Session中拿ID
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if (attribute != null && newpassword != null) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) attribute).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功，请重新登录");
                //修改密码成功，移除当前Session
                req.getSession().removeAttribute(Constants.USER_SESSION);
                System.out.println("----Remove Success----");
            } else {
                req.setAttribute("message", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码,Session中有用户的旧密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的Map : 结果集
        HashMap<String, String> resultMap = new HashMap<String, String>();

        //若Session失效了
        if (attribute == null) {
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) attribute).getUserPassword();
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //往前端写JSON
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
