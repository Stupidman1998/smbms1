package com.bewind.service.user;

import com.bewind.dao.BaseDao;
import com.bewind.dao.user.UserDao;
import com.bewind.dao.user.UserDaoImpl;
import com.bewind.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {

    //业务层都会调用Dao层，所以要引入Dao层

    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        if (null != user) {
            if (!user.getUserPassword().equals(password)) {
                user = null;
            }
        }

        return user;
    }

    public int getUserCount(String username, int userRole) {
        int count = 0;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection,user);
            connection.commit();
            if(updateRows > 0){
                flag = true;
                System.out.println("add success!");
            }else{
                System.out.println("add failed!");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                System.out.println("rollback==================");
                connection.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }finally{
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    public User selectUserCodeExist(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName,queryUserRole,currentPageNo,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;
        //修改密码
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, id, pwd) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }


}
