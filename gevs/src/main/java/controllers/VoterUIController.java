package controllers;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dbcontext.DatabaseUtility;
import model.Candidate;
import model.Constituency;


@WebServlet("/Vote")
public class VoterUIController extends HttpServlet{
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	
    	HttpSession session = req.getSession(false);
    	String email = (String) session.getAttribute("email");
    	
    	if(verifyEmail(email) && email != null) {
        	
        	// getting an constituency object which contains all information about constituency of voter
        	Constituency constituency = getConstituency(email);
        	boolean isElection = checkElection();
        	boolean hasVoted = checkIfVoted(email);
        	
        	req.setAttribute("constituency", constituency);
        	req.setAttribute("isElection",  isElection);
        	req.setAttribute("hasVoted", hasVoted);
        	
            String targetJSP = "/JSP/voterUI.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
            dispatcher.forward(req, res);
    	}
    
    	else {
    		System.out.println("officer email verification failed or no session");
    		res.sendRedirect("Login");
    	}
    }    
       
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        	
    	HttpSession session = req.getSession(false);
    	String email = (String) session.getAttribute("email");
    	
    	System.out.println("Form submitted!");
    	
    	String candidateID = req.getParameter("candidate");
    	
    	markVoted(email);
    	addVote(candidateID);
    	
    	
    	res.sendRedirect("Vote");
    	
    }
    
    
    // verifying that session email checks out
    private boolean verifyEmail(String email) {
    	DatabaseUtility db = new DatabaseUtility();
    	// getting constituency name
    	String sql = "SELECT voter_id FROM voter WHERE voter_id = ?";
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	    	 	pstmt.setString(1,email);
    	    	 
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                if (rs.next()) {
    	                    return true;
    	                    
    	                } else {    	                	
    	                	return false;
    	                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	return false;
    }
    //
    private Constituency getConstituency(String email) {
    	
		int constituencyID = getVoterConstID(email);
		String constituencyName = getConstName(constituencyID);
		List<Candidate> candidates = getCandidatesOfConst(constituencyID);
			
    	Constituency constituency = new Constituency(constituencyID, constituencyName, candidates);
    	
    	return constituency;
    }
     
    private int getVoterConstID(String email) {
    	
    	DatabaseUtility db = new DatabaseUtility();
    	// getting constituency if from voter
    	String sql = "SELECT constituency_id FROM voter WHERE voter_id = ?";
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	    	 	pstmt.setString(1,email);
    	    	 
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                if (rs.next()) {
    	                    int constituencyID = rs.getInt("constituency_id");
    	          
    	                    System.out.println("Voter constituency obtained");
    	                    
    	                    return constituencyID;
    	                } else {
    	                	
    	                	System.out.println("Voter constituency missing or voter doesn't exist");
    	                	return 0;
    	                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	System.out.println("Voter constituency retrieval failed");
    	return 0;
    }
     
    private String getConstName(int constID) {
    	
    	DatabaseUtility db = new DatabaseUtility();
    	// getting constituency name
    	String sql = "SELECT constituency_name FROM constituency WHERE constituency_id = ?";
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	    	 	pstmt.setInt(1,constID);
    	    	 
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                if (rs.next()) {
    	                    String constituencyID = rs.getString("consituency_name");
    	          
    	                    System.out.println("Constituency name obtained");
    	                    
    	                    return constituencyID;
    	                } else {
    	                	
    	                	System.out.println("Constituency name missing or constituency doesn't exist");
    	                	return "constituency_name_missing";
    	                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	System.out.println("Constituency name retrieval failed");
    	return "constituency_name_missing";
    }
    
    private List<Candidate> getCandidatesOfConst(int constID){
    	
    	DatabaseUtility db = new DatabaseUtility();
    	
    	String sql = "SELECT * FROM candidate WHERE constituency_id = ?";
    	List<Candidate> candidateList = new ArrayList<>();
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	    	 	pstmt.setInt(1,constID);
    	    	 
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	            	
    	                while (rs.next()) {
    	                	
    	                	Candidate candidate = new Candidate();
    	                	
    	                	candidate.setID(rs.getInt("canid"));
    	                	candidate.setName(rs.getString("candidate"));   	               
    	                	candidate.setPartyID(rs.getInt("party_id"));
    	                	candidate.setConstID(rs.getInt("constituency_id"));
    	                		
    	                	candidateList.add(candidate);
    	                	
    	                    System.out.println("Added candidate "+ rs.getString("candidate") +" to constituency list");
    	                    
    	                }
    	                
    	                return candidateList;

    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	System.out.println("Candidate list creation failed");
    	return candidateList;
    	
    }
    
    private void addVote(String candidateID) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "UPDATE candidate SET vote_count = vote_count + 1 WHERE canid = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
        {	
	    	 	pstmt.setString(1, candidateID); 		
	    	 	
	    	 	int rowsAffected = pstmt.executeUpdate();
	    	 	
	    	 	System.out.println("Add vote rows affected: " + rowsAffected);
	    	 	
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    }
    
    private boolean checkElection() {
    	
    	DatabaseUtility db = new DatabaseUtility();

    	String sql = "SELECT * FROM election";
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{		 
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                if (rs.next()) {
    	                	int status = rs.getInt("election_status");
    	                	
    	                	if (status != 0) {
    	                		return true;
    	                	}
    	                    
    	                } else {    	                	
    	                	return false;
    	                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
    	
    	return false;
    }

    private boolean checkIfVoted(String email) {
    	DatabaseUtility db = new DatabaseUtility();

    	String sql = "SELECT vote_status FROM voter WHERE voter_id = ?";
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{		 
    				pstmt.setString(1, email); 
    				
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                if (rs.next()) {
    	                	int status = rs.getInt("vote_status");
    	                	
    	                	if (status != 0) {
    	                		return true;
    	                	}
    	                    
    	                } else {    	                	
    	                	return false;
    	                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
    	
    	return false;
    }

    private void markVoted(String email) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "UPDATE voter SET vote_status = 1 WHERE voter_id = ?";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
        {	
	    	 	pstmt.setString(1, email); 		
	    	 	
	    	 	int rowsAffected = pstmt.executeUpdate();
	    	 	
	    	 	System.out.println("Voter marked rows affected: " + rowsAffected);
	    	 	
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }	
    
    }
}
