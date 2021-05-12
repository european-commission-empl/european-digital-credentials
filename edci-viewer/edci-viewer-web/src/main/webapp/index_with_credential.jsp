<%@ page pageEncoding="UTF-8" import="java.io.File" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String xml = (String) request.getAttribute("xml");
    try {
        File indexFile = new File(request.getServletContext().getResource("/index.html").getFile());
        long lastModified = indexFile.lastModified();
        // Ignore the milliseconds; there may be small differences because the time that is stored by the browser is in seconds (apparently):
        /*
        if ((lastModified - request.getDateHeader("If-Modified-Since")) / 1000 <= 0) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            response.setDateHeader("Last-Modified", lastModified);
            */
%>
<%--<c:set var="baseHref" value="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}" />--%>
<c:set var="baseHref" value="${pageContext.request.contextPath}" />
<c:set var="baseTag">base href="${baseHref}/"</c:set>
<c:set var="baseHrefString">base href="/"</c:set>
<c:import url="index.html" var="html" />
<c:set var="currentHtml" value="${fn:replace(html, baseHrefString, baseTag)}" />


<%--${currentHtml}--%>
${fn:replace(currentHtml, '<myXml></myXml>', xml)}
<%
      //  }
    } catch (Exception e) {
%>
    <%=e.getMessage()%>
    No index.html file present.<br/>
    You may have forgotten to build the Angular application before deploying to the server...
<%
    }
%>
