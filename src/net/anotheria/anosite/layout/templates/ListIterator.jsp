<%@ page
	contentType="text/html;charset=iso-8859-15" session="true"
%><%@ taglib uri="/tags/struts-bean" prefix="bean"
%><%@ taglib uri="/tags/struts-html" prefix="html"
%><%@ taglib uri="/tags/struts-logic" prefix="logic"
%><logic:iterate name="__boxlist" type="net.anotheria.anosite.content.bean.BoxBean" id="box">
   	<!-- generating box , box id: <bean:write name="box" property="id"/>, name: <bean:write name="box" property="name"/> , renderer: <bean:write name="box" property="renderer"/>, type: <bean:write name="box" property="typeName"/> -->
	<bean:define id="__box" toScope="request"  scope="page" name="box" type="net.anotheria.anosite.content.bean.BoxBean"/>
	<% String renderer = box.getRenderer(); %>
	<jsp:include page="<%=renderer%>" flush="false"/> 
</logic:iterate>
