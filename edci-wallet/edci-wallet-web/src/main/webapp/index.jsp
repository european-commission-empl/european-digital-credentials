<%@ page pageEncoding="UTF-8" import="java.io.File" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    try {
        File indexFile = new File(request.getServletContext().getResource("/index.html").getFile());
        long lastModified = indexFile.lastModified();
        // Ignore the milliseconds; there may be small differences because the time that is stored by the browser is in seconds (apparently):
        if ((lastModified - request.getDateHeader("If-Modified-Since")) / 1000 <= 0) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setDateHeader("Last-Modified", lastModified);
%>
<c:set var="baseHref" value="${base_href}" />
<c:set var="baseTag"><base href="${baseHref}/"></c:set>
<c:import url="index.html" var="html" />
${fn:replace(html, '<base href="/">', baseTag)}
<%
        }
    } catch (Exception e) {
%>
    EDCI - Wallet REST API is deployed in this server.
<%
    }
%>