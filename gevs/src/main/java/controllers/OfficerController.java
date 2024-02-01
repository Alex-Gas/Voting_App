package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbcontext.DatabaseUtility;
import model.Candidate;
import model.Result;

@WebServlet("/Officer")
public class OfficerController extends HttpServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
    	HttpSession session = req.getSession(false);
    	String email = (String) session.getAttribute("email");
    	
    	// checking if officer session checks out
    	if(verifyEmail(email) && email != null) {
        	       	
            String targetJSP = "/JSP/officer.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
            dispatcher.forward(req, res);
    	}
    
    	else {
    		System.out.println("officer email verification failed or no session");
    		res.sendRedirect("Login");
    	}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// button functionalities
    	if("startEle".equals(req.getParameter("action"))) {
    		setElectionStatus(1);
    		
            String targetJSP = "/JSP/officer.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
            dispatcher.forward(req, res);
    	}
    	
    	else if("endEle".equals(req.getParameter("action"))) {
    		setElectionStatus(0);
    		
            String targetJSP = "/JSP/officer.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
            dispatcher.forward(req, res);
    	}
    	
    	else if("updateRes".equals(req.getParameter("action"))) {
    		System.out.println(req.getParameter("action"));
    		List<Candidate> candidateVotes = getCandidateVotes();
    		req.setAttribute("candidates", candidateVotes);
    		
            String targetJSP = "/JSP/officer.jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(targetJSP);
            dispatcher.forward(req, res);
    	}    	
    	
        else if ("announceWinner".equals(req.getParameter("action"))) {
            String winner = getWinnerParty();
            
            res.setContentType("text/plain");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(winner);
        }
	}
	
	
	// getting party with majority seats
	private String getWinnerParty() {
		List<Result> resultList = getElectionResults();
		
		for (Result result : resultList) {
			if (result.getVotes() >= 3) {
				
				return result.getName();
			}			
		}

		return "Hung Parliament";
	}
	// checking if session email in database
	private boolean verifyEmail(String email) {

    	DatabaseUtility db = new DatabaseUtility();
    	// getting constituency name
    	String sql = "SELECT officer_id FROM officer WHERE officer_id = ?";
    	
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
    // setting whether election is active or not
    private void setElectionStatus(int value) {
		DatabaseUtility db = new DatabaseUtility();
    	String sql = "UPDATE election SET election_status = ? WHERE election_id = 'ele1'";
    	
    	try (
			Connection connect = db.getConnection();
			PreparedStatement pstmt = connect.prepareStatement(sql);
    		)
        {	
	    	 	pstmt.setInt(1, value); 		
	    	 	
	    	 	int rowsAffected = pstmt.executeUpdate();
	    	 	
	    	 	System.out.println("Set election status: " + rowsAffected);
	    	 	
	    	 	
    	}catch(SQLException ex){
			ex.printStackTrace();	
	    }
    }
    // getting all parties and their seat count
    private List<Result> getElectionResults() {

    	List<Integer> constituencyList = getConstituencyIDs();
    	// dictionary of party name and number of seats
    	List<Result> winnerList = new ArrayList<>();
    	// dictionary of party name and party id
    	
    	Map<Integer, String> partyNames = getPartyNames();
    	
    	// creating a list of party names and default value of 0
        for (Map.Entry<Integer, String> entry : partyNames.entrySet()) {
            
        	Result result = new Result();
            result.setName(entry.getValue());
            result.setVotes(0);
            
            winnerList.add(result);
        }
    	
    	for (Integer conID : constituencyList) {
    		Candidate candidate = getWinnerPerConst(conID);	
    		String partyName;
    		
    		if (candidate.getVoteCount() != 0) {
    			partyName = partyNames.get(candidate.getPartyID());
    			
        	    Optional<Result> partyResult = winnerList.stream()
        	            .filter(result -> result.getName().equals(partyName))
        	            .findFirst();

        	    partyResult.ifPresent(result -> result.setVotes(result.getVotes() + 1));
    		}
    	}
    	
    	return winnerList;
    	
    }
    // getting all existing constituency ID's   
    private List<Integer> getConstituencyIDs() {
    	DatabaseUtility db = new DatabaseUtility();
    	// getting constituency name
    	String sql = "SELECT constituency_id FROM constituency";
    	List<Integer> constituencies = new ArrayList<>();
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                while (rs.next()) {
    	                	constituencies.add(rs.getInt("constituency_id"));    	                	                    
    	                }
    	                return constituencies;
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	return constituencies;
    }
    // selecting winner of constituency
    private Candidate getWinnerPerConst(int conID){
    	   	
    	DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT * FROM candidate WHERE constituency_id = ? && vote_count > 0 ORDER BY vote_count DESC LIMIT 1";
    	
    	Candidate candidate = new Candidate();
    	candidate.setVoteCount(0);
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
        			pstmt.setInt(1, conID); 	
    		
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	            	if (rs.next()) {
   
    	                	candidate.setID(rs.getInt("canid"));    
    	                	candidate.setName(rs.getString("candidate"));
    	                	candidate.setPartyID(rs.getInt("party_id"));
    	                	candidate.setConstID(rs.getInt("constituency_id"));
    	                	candidate.setVoteCount(rs.getInt("vote_count"));


    	                	return candidate;
      	            	
		                } else {
		                	return candidate;
		                }
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	return candidate;

    }
    // getting names of parties
    private Map<Integer, String> getPartyNames(){
    	DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT party_id, party FROM party";
    	
    	Map<Integer, String> partyNames = new Hashtable<>();
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	
    	            try (ResultSet rs = pstmt.executeQuery()) {
    	                while (rs.next()) {
    	                	partyNames.put(rs.getInt("party_id"), rs.getString("party"));               
    	                }
    	                return partyNames;
    	            }
    	    	 	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	return partyNames;
    }
    // all canidate votes
    private List<Candidate> getCandidateVotes(){
    	DatabaseUtility db = new DatabaseUtility();
    	String sql = "SELECT * FROM candidate";
    		
    	List<Candidate> candidateList = new ArrayList<>();
    	
    	try (
    			Connection connect = db.getConnection();
    			PreparedStatement pstmt = connect.prepareStatement(sql);
        		)
        	
        	{	

    	            try (ResultSet rs = pstmt.executeQuery()) {
    	            	while (rs.next()) {
    	                	
    	            		Candidate candidate = new Candidate();
    	            		candidate.setID(rs.getInt("canid"));    
    	                	candidate.setName(rs.getString("candidate"));
    	                	candidate.setPartyID(rs.getInt("party_id"));
    	                	candidate.setConstID(rs.getInt("constituency_id"));
    	                	candidate.setVoteCount(rs.getInt("vote_count"));
    	                	
    	                	candidateList.add(candidate); 	
    	            	}
    	            	return candidateList;
    	            }
    	            	
        	}catch(SQLException ex){
    			ex.printStackTrace();	
    	    }	
        	
    	return candidateList;
        
    }
}

    