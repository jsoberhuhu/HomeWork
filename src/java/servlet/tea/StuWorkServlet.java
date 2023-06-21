package java.servlet.tea;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.entity.StuWork;
import java.util.JdbcUtil;
import java.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/stuwork")
public class StuWorkServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject data = JsonUtil.getJson(req);  // receive JSON-data
        JSONObject respJson = new JSONObject();
        String stuid = data.getString("stuid");
        String grade = data.getString("grade");
        String status = data.getString("status");

        String sql = "select * from stu_work";
        List<StuWork> list = JdbcUtil.queryList(StuWork.class, sql);
        JSONArray return_data = JSONArray.parseArray(JSON.toJSONString(list));

        respJson.put("code", 200);
        respJson.put("msg", "success");
        respJson.put("data", return_data);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
