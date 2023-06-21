package java.servlet.tea;

import com.alibaba.fastjson.JSONObject;
import java.entity.Student;
import java.util.JdbcUtil;
import java.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/addstu")
public class AddStuServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject data = JsonUtil.getJson(req);  // receive JSON-data
        String id = data.getString("id");
        String name = data.getString("name");
        String grade = data.getString("grade");

        Integer stuid = Integer.parseInt(id);
        String sql = "insert into student value(?, ?, ?, ?, ?)";
        JdbcUtil.exeUpdate(sql, stuid, id, id, name, grade);

        JSONObject respJson = new JSONObject();
        respJson.put("code", 200);
        respJson.put("msg", "success");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
