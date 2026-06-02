<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%> 
<channel>
<ano:iterate name="piclenserssitems" type="net.anotheria.anosite.content.bean.RssFeedItem" id="item">
        <item>
            <title><ano:write name="item" property="title"/></title>
            <link><ano:write name="item" property="link"/></link>
            <media:thumbnail url="<ano:write name="item" property="thumbnailUrl"/>"/>
            <media:content url="<ano:write name="item" property="contentUrl"/>"/>
            <%--guid isPermaLink="<ano:write name="item" property="permalink"/>"><ano:write name="item" property="guid"/></guid--%>
        </item>
</ano:iterate> 
</channel>