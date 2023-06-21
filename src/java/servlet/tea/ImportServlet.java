package java.servlet.tea;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import java.entity.Student;
import java.listener.ExcelListener;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/import")
public class ImportServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject respJson = new JSONObject();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        try {
            List<FileItem> list = fileUpload.parseRequest(req);
            for (FileItem fileItem : list) {
                if (!fileItem.isFormField()) {
                    InputStream inputStream = fileItem.getInputStream();
                    EasyExcel.read(inputStream, Student.class, new ExcelListener())
                            .sheet().doRead();
                }
            }
            respJson.put("code", 200);
            respJson.put("msg", "import success");
        } catch (FileUploadException e) {
            respJson.put("code", 400);
            respJson.put("msg", "import failed!");
            e.printStackTrace();
        }finally {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json");
            resp.getWriter().write(String.valueOf(respJson));
        }
    }
}
