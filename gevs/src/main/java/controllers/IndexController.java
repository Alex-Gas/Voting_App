package controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class IndexController extends HttpServlet {
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String targetJSP = "/JSP/index.jsp";
        RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
        dispatcher.forward(req, res);
    }

}
