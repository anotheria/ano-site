<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ page isELIgnored ="false" 
%><%@ taglib uri="/WEB-INF/tld/anosite.tld" prefix="as"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><ano:iterate name="page" property="mediaLinks" type="net.anotheria.anosite.content.bean.MediaLinkBean" id="mediaLink"
	><ano:equal name="anosite.verbose" value="true"><!-- AS MediaLinks: MediaLink id: <ano:write name="mediaLink" property="id"/>, name: <ano:write name="mediaLink" property="name"/> --></ano:equal
   	><link<ano:notEmpty name="mediaLink" property="href"> href="${pageContext.request.contextPath}/${mediaLink.href}?<as:random/>"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="type"> type="${mediaLink.type}"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="media"> media="${mediaLink.media}"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="rel"> rel="${mediaLink.rel}"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="rev"> rev="${mediaLink.rev}"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="charset"> charset="${mediaLink.charset}"</ano:notEmpty
   	><ano:notEmpty name="mediaLink" property="hreflang"> hreflang="${mediaLink.hreflang}"</ano:notEmpty
   	>/>
</ano:iterate>
