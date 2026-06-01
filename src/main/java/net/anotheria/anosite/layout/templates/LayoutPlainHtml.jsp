<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="http://www.anotheria.net/tags/anosite" prefix="as"
%><%@ page isELIgnored ="false" 
%><%
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%><ano:iterate name="page" property="headerBoxes" id="box">${box.content}</ano:iterate><ano:iterate 
name="page" property="column1" id="box">${box.content}</ano:iterate><ano:iterate 
name="page" property="column2" id="box">${box.content}</ano:iterate><ano:iterate 
name="page" property="column3" id="box">${box.content}</ano:iterate><ano:iterate 
name="page" property="footerBoxes" id="box">${box.content}</ano:iterate>