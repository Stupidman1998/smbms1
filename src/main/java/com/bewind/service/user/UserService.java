package com.bewind.service.user;

import com.bewind.pojo.User;

import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String password);

    //根据用户ID修改密码
    public boolean updatePwd(int id,String pwd);

    //查询记录数
    public int getUserCount(String username, int userRole);

    //根据条件查询用户表记录数
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    public boolean add(User user);

    public User selectUserCodeExist(String userCode);
}
