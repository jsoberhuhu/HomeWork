package java.util;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class JdbcUtil {
    private static String driverClass;//加载驱动时的路径
    private static String url;//数据库的路径
    private static String user;//数据库的登录名
    private static String password;//数据据库的登录密码
    /**
     * Druid连接池参数
     */
    private static int maxActive;//最大连接数量
    private static int minIdle;//最小闲置数量
    private static long maxWait;//最大等待时间
    private static DruidDataSource dataSource;//声明连接池对象

    public static void init() {

        Properties prop = new Properties();
        try {
            InputStream in = JdbcUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            prop.load(in);
            driverClass = prop.getProperty("driverClass");
            url = prop.getProperty("url");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            maxActive = Integer.parseInt(prop.getProperty("maxActive"));
            minIdle = Integer.parseInt(prop.getProperty("minIdle"));
            maxWait = Long.parseLong(prop.getProperty("maxWait"));

            dataSource = new DruidDataSource();
            dataSource.setDriverClassName(driverClass);
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            dataSource.setMaxActive(maxActive);
            dataSource.setMinIdle(minIdle);
            dataSource.setMaxWait(maxWait);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将数据初始化操作放在程序加载初处理
    static {
        init();
    }

    // 获取连接对象
    public static synchronized Connection getConnection() {
        if (dataSource == null || dataSource.isClosed()) {
            init();
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 封装增、删、改操作
    public static int exeUpdate(String sql, Object... params) {
        Connection coon = null;
        PreparedStatement ps = null;
        try {
            coon = getConnection();
            ps = coon.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(params)) {
            for (int i = 0; i < params.length; i++) {
                try {
                    ps.setObject(i + 1, params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        int i = 0;
        try {
            i = ps.executeUpdate();
//            close(coon, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    // 针对查询语句返回一个记录
    public static <T> T queryOne(Class<T> t, String sql, Object... params) {
        List<Map<String, Object>> list = getDataPair(sql,params);
        if(!list.isEmpty()){
            Map<String, Object> map = list.get(0);
            T obj = parseMapToBean(map,t);
            return obj;
        }
        return null;
    }

    // 针对查询语句返回一个集合
    public static <T> List<T> queryList(Class<T> t, String sql, Object... params) {
        //声明空集合
        List<T> data = new ArrayList<>();
        //获取查询结果
        List<Map<String, Object>> list = getDataPair(sql,params);
        if(list.isEmpty()){
            return null;
        }
        //遍历集合
        for (Map<String, Object> map : list) {
            T obj = parseMapToBean(map,t);
            data.add(obj);
        }
        return data;
    }

    /** 解析指定查询语句，并获取数据 */
    private static List<Map<String, Object>> getDataPair(String sql, Object... params) {
        Connection coon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // 声明集合存储获取的表数据
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            coon = getConnection();
            ps = coon.prepareStatement(sql);
            if (Objects.nonNull(params)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            // 获取结果集元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取列总数、列名称、获取标签名、获取列值、将相关数据存到map集合中
            // 列总数
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                // 对结果集每遍历一次。获取一条数据（作为一个map对象）
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    // 获取表中列名称
                    String columnName = rsmd.getColumnName(i);
                    // 获取标签名，即别名
                    // String columnLable = rsmd.getColumnLabel(i);
                    // 获取列值
                    Object value = rs.getObject(i);
                    // 将数据存入map
                    //进一步判断：如果value为空，则不添加
                    if (Objects.nonNull(value)) {
                        map.put(columnName, value);
                    }
                }
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                close(coon,ps,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //将一个map集合对象转为一个JavaBean并返回
    private static<T> T parseMapToBean(Map<String, Object> map, Class<T> t) {
        T obj = null;
        try {
            //获取一个空的实例
            obj = t.newInstance();
            //获取map集合中的所有键集，即列名
            Set<String> keys = map.keySet();
            for (String cname : keys) {
                try {
                    //获取属性对象
                    Field f = t.getDeclaredField(cname);
                    //获取目标实例的set方法
                    String setMethodName = "set"+cname.substring(0,1).toUpperCase()+cname.substring(1);
                    try {
                        //获取方法对象
                        Method setMethod = t.getMethod(setMethodName,f.getType());
                        setMethod.invoke(obj, map.get(cname));
                    } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**释放资源*/

    public static void close(Connection coon, Statement st, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (st != null) {
            st.close();
        }
        if (coon != null) {
            coon.close();
        }
    }
}
