package controllers;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.servlet.http.HttpServlet;


@WebServlet("/Logout")
public class LogoutController extends HttpServlet {
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Invalidate the session (logout)
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirect to the login page after logout
        res.sendRedirect("Login");
    }
}
