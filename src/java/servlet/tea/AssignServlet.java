package java.servlet.tea;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.entity.Homework;
import java.util.JdbcUtil;
import java.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/assign")
public class AssignServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject data = JsonUtil.getJson(req);  // receive JSON-data
        JSONObject respJson = new JSONObject();  // return JSON-data
        if (data != null){
            JSONArray works = data.getJSONArray("works");
            for(int i = 0; i < works.size();i++){
                Homework homework = works.getObject(i, Homework.class);
                String sql = "insert into homework value(null, ?, ?, ?)";
                JdbcUtil.exeUpdate(sql, homework.getContent(), homework.getRequirement(), homework.getDeadline());
            }
            respJson.put("msg", "assign homework success");
        }else {
            respJson.put("msg", "data is null");
        }
        respJson.put("code", 200);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
