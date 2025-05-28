<script type="text/javascript"
	src="<c:url value="/html/js/application/alerts.js"/>"></script>

<c:if test="${sessionScope.USER_INFO != null}">
	<input id='userId' type='hidden'
		value='<c:out value="${sessionScope.USER_INFO.userId}"/>' />
	<% String style = "display:none;"; %>
	<common:ifHasMessages>
		<% style = "display:block;"; %>
	</common:ifHasMessages>
	<div id="als_msg">
	<div id="alerts_l"><img id="alertsDown"
		style="margin-right: 3px; margin-top: 2px; float: right"
		src="<c:url value="/html/img/Darrow.gif"/>" width="11" /> <img
		id="alertsClose"
		style="margin-right: 3px; margin-top: 2px; float: right"
		src="<c:url value="/html/img/Cancelar.gif"/>" width="11" /> alertas</div>
	<!-- /alerts_l -->
	<div id="alerts" style="<%=style%>"><img
		style="margin-right: 3px;"
		src="<c:url value="/html/img/light_small.gif"/>" /> <common:userMessages />
	</div>
	<!-- /alerts --></div>
	<!-- /als_msg -->
</c:if>
