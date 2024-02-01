package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbcontext.DatabaseUtility;
import dbcontext.GeneralUtility;
import model.Voter;


@WebServlet("/Register")
public class RegisterController extends HttpServlet {
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String targetJSP = "/JSP/register.jsp";
        RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
        dispatcher.forward(req, res);
    }

	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
        String email = req.getParameter("email");
        String name = req.getParameter("name");
        String dob = req.getParameter("dob");
        String password = req.getParameter("password");
        String uvc = req.getParameter("uvc");
        String constituency = req.getParameter("constituency");
                
        Voter voter = new Voter(email, name, dob, password, uvc, constituency);
             
        if(checkUVC(voter.getUvc())){
        	
            if(authenticateUVC(voter.getUvc())) {
            	
            	if (authenticateEmail(voter.getEmail())) {
            		
                	markUVC(voter.getUvc());
                	addNewVoter(voter);
                	res.sendRedirect("Login");
            	}

                else {
                	String message3 = "This email address is already registered.";
                	String script3 = "alert('" + message3 + "');";
                	res.getWriter().write("<script>" + script3 + "window.location.href='Register';</script>");  
                }	
            }
            
            else {
            	String message2 = "Your Unique Voter Code (UVC) is already used.";
            	String script2 = "alert('" + message2 + "');";
            	res.getWriter().write("<script>" + script2 + "window.location.href='Register';</script>");
            }
        }
        
        else {
        	String message1 = "Your Unique Voter Code (UVC) is not valid.";
        	String script1 = "alert('" + message1 + "');";
        	res.getWriter().write("<script>" + script1 + "window.location.href='Register';</script>");
        }

	}
	
	// checking if UVC taken
	private boolean authenticateUVC(String uvc) {
		
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT used FROM uvc_code WHERE UVC = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
    	
    	{	
	    	 	pstmt.setString(1,uvc);
	    	 	
	            
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    int usedValue = rs.getInt("used");
	                    
	                    System.out.println("UVC check performed");
	                    
	                    return usedValue == 0;
	                } else {
	                	
	                	System.out.println("UVC used");
	                    return false;
	                }
	            }
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    	
    	System.out.println("UVC check failed");
    	return false;
	}
	// registering new voter
	private void addNewVoter(Voter voter) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "INSERT INTO voter (voter_id, full_name, DOB, password, UVC, constituency_id, vote_status) VALUES (?, ?, ?, ?, ?, ?, 0)";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
        {	
	    	 	pstmt.setString(1,voter.getEmail());
	    	 	pstmt.setString(2,voter.getName());
	    	 	pstmt.setString(3,voter.getDob());
	    	 	String password = GeneralUtility.getSHA256(voter.getPassword());
	    	 	pstmt.setString(4,password);
	    	 	pstmt.setString(5,voter.getUvc());
	    	 	pstmt.setString(6,voter.getConstituency()); 		
	    	 	
	    	 	int rowsAffected = pstmt.executeUpdate();
	    	 	
	    	 	System.out.println("Rows affected: " + rowsAffected);	    	 	
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
        	
    }
	// marking UVC as taken
	private void markUVC(String uvc) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "UPDATE uvc_code SET used = 1 WHERE UVC = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
        {	
	    	 	pstmt.setString(1, uvc); 		
	    	 	
	    	 	int rowsAffected = pstmt.executeUpdate();
	    	 	
	    	 	System.out.println("Set UVC status: " + rowsAffected);
	    	 	
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }
	}
	// checking if email taken
	private boolean authenticateEmail(String email) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT * FROM voter WHERE voter_id = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
    	
    	{	
	    	 	pstmt.setString(1, email);
	               	 	
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	     	                    
	                    System.out.println("Email already used");
	                    
	                    return false;
	                } else {
	                	
	                	System.out.println("Email check good");
	                    return true;
	                }
	            }
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    	
    	System.out.println("Login auth failed");
    	return false;
	}
	// checking if UVC exists
	private boolean checkUVC(String uvc) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT UVC FROM uvc_code WHERE UVC = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
    	
    	{	
	    	 	pstmt.setString(1,uvc);
	    	 	
	            
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    
	                    System.out.println("UVC found");
	                    
	                    return true;
	                } else {
	                	
	                	System.out.println("UVC missing");
	                    return false;
	                }
	            }
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    	
    	System.out.println("UVC check failed");
    	return false;
	}

}
