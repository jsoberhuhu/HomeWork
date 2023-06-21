package java.entity;

import com.alibaba.excel.annotation.ExcelProperty;

/***
 * 学生实体类
 * @author cywscs
 */
public class Student {

    @ExcelProperty(value = "学号")
    private Integer id;

    @ExcelProperty(value = "用户名")
    private String username;

    @ExcelProperty(value = "密码")
    private String password;

    @ExcelProperty(value = "姓名")
    private String name;

    @ExcelProperty(value = "教学班")
    private String grade;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
