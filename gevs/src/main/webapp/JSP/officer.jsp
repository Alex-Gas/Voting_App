<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.Objects"%>
<%@ page import="java.util.Optional"%>
<%@ page import="java.util.function.Supplier"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Candidate" %>

<%
    Optional<HttpSession> userSession = Optional.ofNullable(request.getSession(false));
    Optional<List<Candidate>> candidates = Optional.ofNullable((List<Candidate>) request.getAttribute("candidates"));
    
    if (userSession.isPresent() && Objects.nonNull(userSession.get().getAttribute("email"))) {
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <title>Officer Page</title>
    
</head>
<body>
	<script>
	    function announceWinner() {
	        $.ajax({
	            type: "POST",
	            url: "Officer",
	            data: { action: "announceWinner" },
	            success: function (result) {
	                $("#winnerContainer").innerHTML = ""
	                $("#winnerContainer").text("Election Winner: " + result);
	            },
	            error: function (error) {
	                console.error("Error announcing winner: " + error);
	            }
	        });
	    }
	    

	    function navigateTo(url) {
	        window.location.href = url;
	    }

	    
	</script>

    <h1>Welcome, <%= userSession.get().getAttribute("email") %>!</h1>
	
	<div id="electionStatusContainer"></div>
	
	<div id="winnerContainer"></div>
	
    <form action="Officer" method="post">
        <button type="submit" name="action" value="startEle">Start Election</button>
        <button type="submit" name="action" value="endEle">End Election</button>
        <button type="submit" name="action" value="updateRes">Update Results</button>
    </form>
    
    <form action="Officer" method="post">
   	 	<button type="button" onclick="announceWinner()">Announce Winner</button>
    </form>
    
    <table>
    	<tr>
    		<td>Candidate Name</td>
    		<td>Vote Count</td>
    	</tr>
		<%
		if (candidates.isPresent()) {
			for (Candidate candidate : candidates.get())
			{
				%>
					<tr>
						<td><%= candidate.getName() %></td>			
						<td><%= candidate.getVoteCount() %></td>
					</tr>
				<%
			}
		}
		%>
	</table>
    <button onclick="navigateTo('Logout')">Logout</button> 
    
</body>
</html>

<%
    } else {
        // User is not logged in or constituency not present, redirect to the login page
        response.sendRedirect("Login");
    }
%>