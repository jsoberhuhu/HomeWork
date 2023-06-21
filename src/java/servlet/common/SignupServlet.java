package java.servlet.common;

import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.utils.CaptchaUtil;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.JdbcUtil;
import java.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        JSONObject data = JsonUtil.getJson(req);  // receive JSON-data
        JSONObject respJson = new JSONObject();
        String role = req.getParameter("role");
        String name = req.getParameter("name");
        String id = req.getParameter("id");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String grade = req.getParameter("grade");
        String verCode = req.getParameter("verCode");
        Integer stuid = Integer.parseInt(id);
        String pwd = DigestUtils.md5Hex(password);  //md5


        if (!CaptchaUtil.ver(verCode, req)) {
            CaptchaUtil.clear(req);  // 清除session中的验证码
            respJson.put("code", 400);
            respJson.put("msg", "check code not matched");
        }else {
            String sql;
            if(role.equals("teacher")){
                sql = "insert into teacher value(null, ?, ?, ?, ?)";
                JdbcUtil.exeUpdate(sql, username, pwd, name, grade);
            }else{
                sql = "insert into student value(?, ?, ?, ?, ?)";
                JdbcUtil.exeUpdate(sql, stuid, username, pwd, name, grade);
            }
            respJson.put("code", 200);
            respJson.put("msg", "sign up success");
        }
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
