<%@ page pageEncoding="UTF-8" import="java.io.File" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="frontEndContext" value="${frontend_context}"/>
<%
    try {
%>
<c:set var="baseHref" value="${base_href}" />
<c:set var="baseTag">base href="${baseHref}/"</c:set>
<c:set var="baseHrefString">base href="/"</c:set>
<c:import url="index.html" var="html" />
<c:set var="currentHtml" value="${fn:replace(html, baseHrefString, baseTag)}" />
${fn:replace(currentHtml, '$FRONTEND_CONTEXT', frontEndContext)}
<%
     //   }
    } catch (Exception e) {
        System.out.println("#JSP EXCEPTION");
       e.printStackTrace();
%>
    No index.html file present.<br/>
    You may have forgotten to build the Angular application before deploying to the server.
<%
    }
%>