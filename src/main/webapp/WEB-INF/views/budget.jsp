<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<div>
	Witaj <security:authentication property="principal.username" />
</div>
<form:form class="col-sm-4" role="form" method="POST" commandName="budget" >
	<spring:bind path="name">
		<div class="form-group" ${status.error ? 'has-error' : ''}">
			<form:label path="name">Nazwa budżetu:</form:label>
			<form:input path="name" class="form-control" size="45" required="required" autofocus="autofocus" />
			<form:errors path="name" cssClass="control-label" />
		</div>
	</spring:bind>
		<spring:bind path="description">
		<div class="form-group" ${status.error ? 'has-error' : ''}">
			<form:label path="description">Opis budżetu:</form:label>
			<form:input path="description" class="form-control" size="100" />
			<form:errors path="description" cssClass="control-label" />
		</div>
	</spring:bind>
	<button type="submit" class="btn btn-default">Stwórz budżet</button>
</form:form>