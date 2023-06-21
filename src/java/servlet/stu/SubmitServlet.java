package java.servlet.stu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.entity.Homework;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.util.JdbcUtil;

import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;


@WebServlet("/submit")
//@MultipartConfig
public class SubmitServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        //  multipart/form-data
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) {
            System.out.println("ok");
        }else{
            try {
                HttpSession session = req.getSession();
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(session.getAttribute("user")));
                String stuId = object.getString("id");
                Integer workId = null;
                String answer = null;
                String filePath = null;
                List<FileItem> fileItems = upload.parseRequest(req);
                for (FileItem fileItem : fileItems) {
                    if (fileItem.isFormField()) {
                        System.out.println(fileItem.getFieldName()+"\t"+fileItem.getString("UTF-8"));
                        if(fileItem.getFieldName().equals("id")){
                            workId = Integer.parseInt(fileItem.getString("UTF-8"));
                        }
                        if(fileItem.getFieldName().equals("answer")){
                            answer = fileItem.getString("UTF-8");
                        }
                    } else {
                        String fileName = fileItem.getName();
                        System.out.println(fileName);
                        String ext = fileName.substring(fileName.lastIndexOf("."));
                        String name = java.util.UUID.randomUUID()+ext;
                        filePath = File.separator + name;
                        File file = new File("/opt/apache-tomcat-9.0.75/webapps/file" + filePath);
                        fileItem.write(file);
                    }
                }
                String sql = "insert into stu_work value(null, ?, ?, ?, ?, 'false', null, null, null)";
                JdbcUtil.exeUpdate(sql, workId, stuId, "http://sdauqihang.club:8080/file"+filePath, answer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject respJson = new JSONObject();
        respJson.put("code", 200);
        respJson.put("msg", "success");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(String.valueOf(respJson));
    }
}
