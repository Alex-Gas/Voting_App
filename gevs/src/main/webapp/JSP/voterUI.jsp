<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.Objects"%>
<%@ page import="java.util.Optional"%>
<%@ page import="java.util.function.Supplier"%>
<%@ page import="model.Candidate" %>
<%@ page import="model.Constituency" %>
<%@ page import="java.util.List" %>

<%
    Optional<HttpSession> userSession = Optional.ofNullable(request.getSession(false));
    Optional<Constituency> constituency = Optional.ofNullable((Constituency) request.getAttribute("constituency"));
    boolean isElection = (boolean) request.getAttribute("isElection");
    boolean hasVoted = (boolean) request.getAttribute("hasVoted");
    
    System.out.println(isElection);
   
    if (userSession.isPresent() && Objects.nonNull(userSession.get().getAttribute("email")) && constituency.isPresent()) {
        List<Candidate> candidates = constituency.get().getCandidates();
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <title>Voting Page</title>
</head>
<body>
	<script>
	    function navigateTo(url) {
	        window.location.href = url;
	    }
	</script>

    <h1>Welcome, <%= userSession.get().getAttribute("email") %>!</h1>
	
	<% if(isElection && !hasVoted){ %>
	
    <form id="voteForm" action="Vote" method="post">
        <p>Please select a candidate:</p>
        <% for (Candidate candidate : candidates) { %>
            <input type="radio" id="<%= candidate.getID() %>" name="candidate" value="<%= candidate.getID() %>">
            <label for="<%= candidate.getID() %>"><%= candidate.getName() %></label><br>
	        <% 
	        System.out.println("candidate added to form");
	        } %>

        <button type="submit">Vote</button>
    </form>
    
    <% } else if (isElection && hasVoted) { %>
	<h1> Vote Submitted </h1>
				
	
    
	<% } else { %>
	<h1> Election Closed </h1>
				
	<% }%>
	

	
    <button onclick="navigateTo('Logout')">Logout</button> 
</body>
</html>


<%
    } else {
        // User is not logged in or constituency not present, redirect to the login page
        response.sendRedirect("Login");
    }
%>