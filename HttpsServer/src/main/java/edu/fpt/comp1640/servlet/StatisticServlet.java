package edu.fpt.comp1640.servlet;

import edu.fpt.comp1640.utils.DatabaseUtils;
import lombok.val;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "StatisticServlet")
public class StatisticServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        val map = request.getParameterMap();
        response.setContentType("application/json");

        try {
            if (map.containsKey("year")) {
                response.getWriter().write(getSubmissionStatistic(request, response));
            } else {
                response.getWriter().write("");
            }
        } catch (Exception e) {
            response.getWriter().print("{\"error\": \"Cannot fetch data!\"}");
        }
    }

    private String getSubmissionStatistic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //language=SQL
        val sql = "SELECT count(SM.id) AS count, publish_year, faculty_id, strftime('%Y-%m-%d', submit_time) AS submit_date, count(chosen) AS chosen FROM Submissions SM JOIN Students S ON SM.student_id = S.id WHERE SM.publish_year IN (?) GROUP BY submit_date, faculty_id ORDER BY submit_date";
        // AND submit_time >= (SELECT DATETIME(max(submit_time), '-7 day') FROM Submissions)
        val year = request.getParameter("year");
        return DatabaseUtils.getJSON(
                sql,
                new String[]{"count", "publish_year", "faculty_id", "submit_date", "chosen"},
                new Object[]{year});
    }
}
