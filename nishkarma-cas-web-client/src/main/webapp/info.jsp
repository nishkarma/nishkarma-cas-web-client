<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.jasig.cas.client.authentication.AttributePrincipal" %>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
 
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CAS Test</title>
		<style>
		table, th, td {
		    border: 1px solid black;
		    border-collapse: collapse;
		}
		</style>
    </head>
    <body>
 
    <h1>CAS User Info</h1>
 
    <p>request.getRemoteUser() : <%= request.getRemoteUser() %></p>
    
    <p>request.isUserInRole("admin") : <%= request.isUserInRole("admin") %></p>
    <p>request.isUserInRole("staff") : <%= request.isUserInRole("staff") %></p>
    <p>request.isUserInRole("staff2") : <%= request.isUserInRole("staff2") %></p>
    <p>request.isUserInRole("user") : <%= request.isUserInRole("user") %></p>
	<%
	AttributePrincipal principal = (AttributePrincipal)request.getUserPrincipal();

	if (principal != null)	{
	
		%>
	
	
	    <p>principal.getName() : <%= principal.getName() %></p>
	
		<%
	
		System.out.println("principal.getName()="+principal.getName());
	
		Map attributes = principal.getAttributes();
		 
		Iterator attributeNames = attributes.keySet().iterator();
		%>	 
	
		<p>---principal.getAttributes---</p>
		<table >
		<tr><th>attributeName</th><td>attributeValue</td></tr>
		 
		<%	
		for (; attributeNames.hasNext();) {
			out.println("<tr><th>");
			
			String attributeName = (String) attributeNames.next();
		    out.println(attributeName);
		    out.println("</th><td>");
		    
		    Object attributeValue = attributes.get(attributeName);
		    out.println(attributeValue);
		    out.println("</td></tr>");
		}
		 
		out.println("</table>");
	
	}
 
	%>
    </body>
</html>