<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>HomeBudget</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<link href="css/style.css" rel="stylesheet" type="text/css" >
	</head>
	<body>
		<div class="container cont bg-1 shadow-1">
			<div id="header">
				<t:insertAttribute name="header" />
			</div>
			<div id="content">
				<t:insertAttribute name="body" />
			</div>
		</div>
	</body>
</html>
