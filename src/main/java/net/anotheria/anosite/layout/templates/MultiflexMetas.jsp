<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%> 
  
<title><ano:write name="page" scope="request" property="title" filter="false"/></title>

<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="description" content="<ano:write name="page" scope="request" property="description" filter="false"/>">
<meta name="keywords" content="<ano:write name="page" scope="request" property="keywords" filter="false"/>">
<meta name="verify-v1" content="ByKKpjaJVvtDJjxH5AxNjRpbhCKJAq1EuOuIbWB0SjY=">
<jsp:include page="MediaLinks.jsp" flush="false"/>
<jsp:include page="Scripts.jsp" flush="false"/>

<ano:define id="__boxlist" toScope="request" name="page" property="metaBoxes"/>
<jsp:include page="ListIterator.jsp" flush="false"/>

<link href="<ano:write name="stylesheet" property="link"/>" rel="stylesheet" type="text/css">