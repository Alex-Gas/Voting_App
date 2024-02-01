<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>

	<script>
	    function navigateTo(url) {
	        window.location.href = url;
	    }
	</script>
</head>
<body>
	<h1>Register</h1>
    <form id="registerForm" action="Register" method="post">
        <label for="email">Email address:</label>
        <input type="text" id="email" name="email" required><br>
        
        <label for="name">Full Name:</label>
        <input type="text" id="name" name="name" required><br>
        
        <label for="dob">Date of Birth:</label>
        <input type="text" id="dob" name="dob" required><br>
        
        <label for="password">Password:</label>
        <input type="text" id="password" name="password" required><br>
        
        <label for="uvc">Your Unique Voter Code (UVC):</label>
        <input type="text" id="uvc" name="uvc" required><br>
        
        <label for="constituency">Constituency:</label>
        <input type="text" id="constituency" name="constituency" required><br>
        
        <button type="submit">Register</button>
    </form>
	
	<button onclick="navigateTo('Login')">Sign in</button>
	
</body>
</html>