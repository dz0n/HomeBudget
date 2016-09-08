<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2>Zakładanie konta</h2>
<form:form class="col-sm-4" role="form" method="POST" commandName="user" >
	<spring:bind path="username">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<form:label path="username">Nazwa użytkownika:</form:label>
			<form:input class="form-control" path="username" size="30" required="required" autofocus="autofocus" />
			<form:errors path="username" cssClass="control-label"/>
		</div>
	</spring:bind>
	<spring:bind path="email">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<form:label path="email">Email:</form:label>
			<form:input class="form-control" path="email" size="50" required="required" />
			<form:errors path="email" cssClass="control-label"/>
		</div>
	</spring:bind>
	<spring:bind path="password">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<form:label path="password">Hasło:</form:label>
			<form:password class="form-control" path="password" size="30" required="required" />
			<form:errors path="password" cssClass="control-label"/>
		</div>
	</spring:bind>
	<button type="submit" class="btn btn-default">Załóż konto</button>
</form:form>