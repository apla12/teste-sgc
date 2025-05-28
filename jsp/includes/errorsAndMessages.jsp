<%@ include file="/jsp/includes/taglibs.jsp"%>
<% String styleErr = "display:none;"; %>
<logic:messagesPresent>
	<% styleErr = "display:block;"; %>
</logic:messagesPresent>
<% String styleMsg = "display:none;"; %>
<logic:messagesPresent message="true">
	<% styleMsg = "display:block;"; %>
</logic:messagesPresent>
<div id="err_msgs"><script type="text/javascript">
      <%
      String clMsgUrl = new StringBuffer()
         .append( "http://" )
         .append( request.getServerName() )
         .append( ':' )
         .append( request.getServerPort() )
         .append( request.getContextPath() )
         .append( "/jsp/clearMessages.jsp" )
         .toString();
      %>
      function clearMessages(){
         var xmlhttp=false;
         /*@cc_on @*/
         /*@if (@_jscript_version >= 5)
          try {
               xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
          } catch (e) {
              try {
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
              } catch (E) {
                   xmlhttp = false;
              }
          }
         @end @*/
         if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
              xmlhttp = new XMLHttpRequest();
         }
         xmlhttp.open("GET", "<%=clMsgUrl%>",true);
         xmlhttp.send(null);
      }
      function blindUpDownErros(){
         if( getCookie("errors_hidden") ){
            new Effect.BlindDown('errors');
            deleteCookie("errors_hidden");
         } else {
            new Effect.BlindUp('errors');
            setCookie("errors_hidden", "true");
         }
      }
      function checkHidden(){
         if( getCookie("errors_hidden") ){
            e_errors = document.getElementById('errors');
            if( e_errors ){
               e_errors.style.display = 'none';
            }
         }
      }
      </script>
<div id="errors_cont" style="<%=styleErr%>">
<div id="errors_l"><img
	style="margin-right: 3px; margin-top: 2px; float: right"
	src="<c:url value="/html/img/Darrow.gif"/>" width="11"
	onclick="blindUpDownErros();" alt="" /> <img
	style="margin-right: 3px; margin-top: 2px; float: right"
	src="<c:url value="/html/img/Cancelar.gif"/>" width="11"
	onclick="new Effect.BlindUp('errors');new Effect.SwitchOff('errors_l');clearMessages();"
	alt="" /> erros</div>
<div id="errors"><img
	style="margin-right: 3px; margin-top: 2px; float: left"
	src="<c:url value="/html/img/exclamation.gif"/>" width="11" alt="" /> <span
	id="errors-container"><html:errors /></span></div>
</div>
<script type="text/javascript">checkHidden();</script>
<div id="messages_cont" style="<%=styleMsg%>">
<div id="messages_l"><%--
      <img style="margin-right: 3px; margin-top: 2px; float: right"
           src="<c:url value="/html/img/Darrow.gif"/>" width="11"
           onclick="new Effect.BlindUp('messages');">
      <img style="margin-right: 3px; margin-top: 2px; float: right"
           src="<c:url value="/html/img/Cancelar.gif"/>" width="11"
           onclick="new Effect.BlindUp('messages');new Effect.SwitchOff('messages_l');">
--%> info</div>
<div id="messages"><img style="margin-right: 5px;"
	src="<c:url value="/html/img/about_16.gif"/>" width="14" alt="" /> <span
	id="messages-container"> <html:messages message="true"
	id="message">
	<c:out value="${message}" />
</html:messages> </span></div>
</div>
</div>
