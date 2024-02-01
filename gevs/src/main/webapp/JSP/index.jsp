<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Landing page</title>
	<script>
	    function navigateTo(url) {
	        window.location.href = url;
	    }
	</script>
</head>
<body>
	<h1>Welcome to GEVS</h1>

    <button onclick="navigateTo('Register')">Register</button>
    <button onclick="navigateTo('Login')">Login</button>

	<p>http://localhost:8080/gevs/</p>
</body>
</html>