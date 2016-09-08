<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2>Zaloguj się</h2>
<form name="f" class="col-sm-4" role="form" method="POST" >
	<c:if test="${param=='{error=}'}" >
		<div class="alert alert-warning">
			Niepoprawny login lub hasło.
		</div>
	</c:if>
	<c:if test="${param=='{logout=}'}" >
		<div class="alert alert-info">
			Zostałeś wylogowany.
		</div>
	</c:if>
	<div class="form-group">
		<label for="username">Nazwa użytkownika:</label>
		<input type="text" class="form-control" name="username" id="username" size="30" required="required" autofocus="autofocus" />
	</div>
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label for="password">Hasło:</label>
		<input type="password" class="form-control" name="password" id="password" size="30" required="required" />
	</div>
	<div class="checkbox">
		<label>
			<input type="checkbox" name="remember-me" id="remember_me">
			Pamiętaj mnie
		</label>
	</div>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	<button type="submit" class="btn btn-default">Zaloguj się</button>
	<a href="<c:url value="/register" />" >
		<button type="button" class="btn btn-default">Załóż konto</button>
	</a>
</form>