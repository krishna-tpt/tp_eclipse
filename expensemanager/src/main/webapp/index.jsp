<%@ page contentType="text/html;charset=UTF-8"%>
<% out.println("Context Path: " + request.getContextPath());%>
<% response.sendRedirect(request.getContextPath() + "/dashboard"); %>
