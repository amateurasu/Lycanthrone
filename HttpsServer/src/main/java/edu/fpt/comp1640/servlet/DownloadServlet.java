package edu.fpt.comp1640.servlet;

import edu.fpt.comp1640.database.ResultSetHandler;
import edu.fpt.comp1640.model.user.User;
import edu.fpt.comp1640.utils.CompressUtils;
import edu.fpt.comp1640.utils.DatabaseUtils;
import lombok.val;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        val map = request.getParameterMap();

        if (map.containsKey("id")) {
            downloadSingleFile(map, response);
        } else if (map.containsKey("submission")) {
            downloadSubmissions(request, response);
        }
    }

    private void downloadSubmissions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            //language=SQL
            val sql = "SELECT disk_location FROM Submissions S JOIN Files F ON S.id = F.submission_id WHERE S.id = ?";
            val user = (User) request.getSession().getAttribute("user");
            val username = user.getUsername();

            val submissions = request.getParameter("submission");
            val sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            val now = sdf.format(new Date());
            val fileOut = new File(String.format("%s-%s.zip", username, now));

            ResultSetHandler rh = rs -> {
                while (rs.next()) {
                    CompressUtils.zipFile(rs.getString(1), fileOut);
                }
                sendFile(response, fileOut);
            };
            DatabaseUtils.getResult(sql, new Object[] {submissions}, rh);
        } catch (Exception e) {
            response.setContentType("application/json");
            response.getWriter().print("{\"error\": \"You don't have privilege to download this resource!\"}");
        }
    }

    private void downloadSingleFile(Map<String, String[]> map, HttpServletResponse response) {
        String fileSql = "SELECT disk_location FROM Files WHERE id = ?";
        try {
            String id = map.get("id")[0];
            DatabaseUtils.each(fileSql, new Object[] {id}, rs -> {
                String address = rs.getString("disk_location");
                sendFile(response, new File(address));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFile(HttpServletResponse response, File file) throws IOException {
        response.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getName()));
        try (val out = response.getOutputStream(); val in = new FileInputStream(file)) {
            val buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
        }
    }
}
