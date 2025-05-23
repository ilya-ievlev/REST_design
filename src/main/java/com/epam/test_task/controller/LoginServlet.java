package com.epam.test_task.controller;

import com.epam.test_task.util.JwtUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Simplified authentication check, password encryption must be provided, should connect to a database to check if a user exists etc.
        if ("admin".equals(username) && "password".equals(password)) {
            String token = JwtUtil.generateToken(username);

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print("{\"token\":\"" + token + "\"}");
            out.flush();
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
