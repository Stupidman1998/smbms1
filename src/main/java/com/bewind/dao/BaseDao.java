package com.bewind.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        Properties properties = new Properties();
        //通过类加载器读取相应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");

    }

    //获取数据库的连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
    public static ResultSet execute(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet, String sql,
                                    Object[] params ) throws SQLException {
        //由于 PreparedStatement 对象已预编译过，所以其执行速度要快于 Statement 对象。
        //因此，多次执行的 SQL 语句经常创建为 PreparedStatement 对象，以提高效率。
        //但是单次运行效率statement更高
        //预编译的sql，在后面直接执行就可以，无需再传参

        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //setObject,占位符从1开始，但是我们的数组是从0开始！
            preparedStatement.setObject(i + 1, params[i]);
        }

        //resultSet是查询返回的结果集，并且只能读取一次
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共类
    public static int execute(Connection connection, PreparedStatement preparedStatement, String sql, Object[] params)
            throws SQLException {
        //由于 PreparedStatement 对象已预编译过，所以其执行速度要快于 Statement 对象。
        //因此，多次执行的 SQL 语句经常创建为 PreparedStatement 对象，以提高效率。

        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //setObject,占位符从1开始，但是我们的数组是从0开始！
            preparedStatement.setObject(i + 1, params[i]);
        }

        //返回受影响的行数
        return preparedStatement.executeUpdate();
    }

    /**
     * 释放资源
     */
    public static boolean closeResource(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        boolean flag = true;

        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception e) {
                e.printStackTrace();
                //如果出现错误，flag置false，表明关闭失败
                flag = false;
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (Exception e) {
                e.printStackTrace();
                //如果出现错误，flag置false，表明关闭失败
                flag = false;
            }
        }

        if (resultSet != null) {
            try {
                resultSet.close();
                resultSet = null;
            } catch (Exception e) {
                e.printStackTrace();
                //如果出现错误，flag置false，表明关闭失败
                flag = false;
            }
        }

        return flag;
    }
}
