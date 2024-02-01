<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Page</title>
    
    <script>
	    function navigateTo(url) {
	        window.location.href = url;
	    }
	</script>
	
</head>
<body>
	<h1> Sign in </h1>
    <form id="loginForm" action="Login" method="post">
        <label for="email">Email address:</label>
        <input type="text" id="email" name="email" required><br>
        
        <label for="password">Password:</label>
        <input type="text" id="password" name="password" required><br>
        
        <button type="submit">Login</button>
    </form>


	<button onclick="navigateTo('Register')">Sign up</button> 
  
  
</body>
</html>