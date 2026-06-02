<%@ page
	contentType="text/html;charset=UTF-8" session="true"%><%@ taglib 
	uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:iterate name="__boxlist" type="net.anotheria.anosite.content.bean.BoxBean" id="box"><ano:equal 
	name="anosite.verbose" value="true"><%="\n"%><!-- AS ListIterator: generating box, box id: <ano:write name="box" property="id"/>, name: <ano:write name="box" property="name"/> , renderer: <ano:write name="box" property="renderer"/>, type: <ano:write name="box" property="typeName"/> --></ano:equal>
<ano:define 
	id="__box" toScope="request"  scope="page" name="box" type="net.anotheria.anosite.content.bean.BoxBean"/><% String renderer = box.getRenderer(); %><ano:present 
	name="anosite.sa..editModeFlag" scope="session"><a style="padding-left:3px;padding-right:3px; color:white; background-color: red; font-variant: small-caps; font-size:tiny" href="cms/aswebdataBoxEdit?pId=<ano:write name="box" property="id"/>" target="_blank"><ano:write name="box" property="name"/></a><div style="border-top: solid red; border-left: solid red; border-right: solid red; border-bottom: solid red; border-width: 1px; "></ano:present><jsp:include
	page="<%=renderer%>" flush="false"/><ano:present name="anosite.sa..editModeFlag" scope="session"></div></ano:present></ano:iterate>