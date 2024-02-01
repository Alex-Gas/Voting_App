package controllers;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.servlet.http.HttpServlet;

import dbcontext.DatabaseUtility;
import dbcontext.GeneralUtility;

@WebServlet("/Login")
public class LoginController extends HttpServlet{
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	
        String targetJSP = "/JSP/login.jsp";
        RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
        dispatcher.forward(req, res);
    }
    
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = GeneralUtility.getSHA256(req.getParameter("password"));
        System.out.println(password);
        
        // check if credential match the officer
        if (authenticateOfficer(email, password)) {
            // create session and store user information
            HttpSession session = req.getSession();
            session.setAttribute("email", email);

            // if valid, send to officer page
            System.out.println("redirecting to officer page");
            res.sendRedirect("Officer");
        }
        
        // check if credential match any user
        else if (authenticateUser(email, password)) {
            // create session and store user information
            HttpSession session = req.getSession();
            session.setAttribute("email", email);

            // if valid, send to voter page
            System.out.println("redirecting to voter page");
            res.sendRedirect("Vote");
        } 
        
        else {
        	// if invalid credentials, return to login page.
        	String message1 = "Invalid Email or Password.";
        	String script1 = "alert('" + message1 + "');";
        	res.getWriter().write("<script>" + script1 + "window.location.href='Login';</script>");
        }
    }
    
    // checking if voter credentials match
    private boolean authenticateUser(String email, String password) {
    	
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT * FROM voter WHERE (voter_id = ? AND password = ?)";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
    	
    	{	
	    	 	pstmt.setString(1, email);
	    	 	pstmt.setString(2, password);
	               	 	
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	     	                    
	                    System.out.println("Login auth succesful");
	                    
	                    return true;
	                } else {
	                	
	                	System.out.println("No user or wrong user or password");
	                    return false;
	                }
	            }
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    	
    	System.out.println("Login auth failed");
    	return false;
        
    }
    // checking if officer credentials match
    private boolean authenticateOfficer(String email, String password) {
    	
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT * FROM officer WHERE (officer_id = ? AND password = ?)";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
    	
    	{	
	    	 	pstmt.setString(1, email);
	    	 	pstmt.setString(2, password);
	               	 	
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	     	                    
	                    System.out.println("Officer login auth succesful");
	                    
	                    return true;
	                } else {
	                	
	                	System.out.println("No officer or wrong user or password");
	                    return false;
	                }
	            }
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    	
    	System.out.println("Officer login auth failed");
    	return false;
    }
    
}
