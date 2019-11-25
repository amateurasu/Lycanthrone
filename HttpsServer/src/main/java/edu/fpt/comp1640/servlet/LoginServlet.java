package edu.fpt.comp1640.servlet;

import edu.fpt.comp1640.database.Database;
import edu.fpt.comp1640.model.user.User;
import lombok.val;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        val username = request.getParameter("username");
        val password = request.getParameter("password");

        try (val db = new Database()) {
            val user = db.authenticate(username, password);
            System.out.println(user);
            if (user != null) {
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            } else {
                response.setContentType("application/json");
                response.getWriter().print("{\"error\": \"Please check username or password again!\"}");
            }
        } catch (Exception ignored) { }
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
