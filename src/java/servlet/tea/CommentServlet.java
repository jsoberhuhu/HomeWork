package java.servlet.tea;

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


@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject data = JsonUtil.getJson(req);  // receive JSON-data
        String workId = data.getString("id");
        String score = data.getString("score");
        String comment = data.getString("comment");

        String sql1 = "select * from stu_work where id = ?";
        List<StuWork> stuWorkList = JdbcUtil.queryList(StuWork.class, sql1, workId);
        StuWork work = stuWorkList.get(0);
        work.setStatus("true");
        work.setScore(score);
        work.setComment(comment);
        String sql2 = "insert into stu_work value(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JdbcUtil.exeUpdate("delete from stu_work where id=?", work.getId());
        JdbcUtil.exeUpdate(sql2, work.getId(), work.getWork_id(), work.getStu_id(), work.getAttachment(), work.getAnswer(),
                work.getStatus(), work.getScore(), work.getComment(), work.getArgument());

        JSONObject respJson = new JSONObject();
        respJson.put("code", 200);
        respJson.put("msg", "comment success");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
