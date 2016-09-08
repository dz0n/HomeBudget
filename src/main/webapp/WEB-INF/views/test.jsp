<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
testowy budżet:<br>
<c:out value="${budget.name}" />
<c:out value="${budget.description}" />
Kategoria: <c:out value="${category}" />
Konto: <c:out value="${account}" />
<a href="<c:url value="/test2" />" >przejdź do test2</a>