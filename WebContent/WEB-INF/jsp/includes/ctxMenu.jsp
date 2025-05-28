<%@ include file="/jsp/includes/taglibs.jsp"%>
::
<span
	id="<%= ("importacao".equals(session.getAttribute("CONTEXT"))?"ctxSelected":"ctxUnselected") %>">
<a href="/users/?ctx=sup"><bean:message key="context.support" /></a> </span>
::
<%--
<span id="<%= ("importacao".equals(session.getAttribute("CONTEXT"))?"ctxSelected":"ctxUnselected") %>">
<a href="/users/?ctx=imp"><bean:message key="context.importacao" /></a>
</span>
::
--%>
<span
	id="<%= ("exportacao".equals(session.getAttribute("CONTEXT"))?"ctxSelected":"ctxUnselected") %>">
<a href="/users/?ctx=exp"><bean:message key="context.exportacao" /></a>
</span>
::
<span
	id="<%= ("sca".equals(session.getAttribute("CONTEXT"))?"ctxSelected":"ctxUnselected") %>">
<a href="/users/?ctx=sca"><bean:message key="context.sca" /></a> </span>
::
