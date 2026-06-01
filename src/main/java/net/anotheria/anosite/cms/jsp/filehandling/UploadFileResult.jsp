<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano" %>
<html>
	<head>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache, must-revalidate">
		<meta name="Expires" content="0">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="<%=request.getContextPath()%>/css/admin.css" rel="stylesheet" type="text/css">
		<title>@Upload file</title>
	</head>
	<body topmargin="0" marginwidth="0" marginheight="0" onLoad="parent.stopState();">
		<ano:equal name="ano-web.file.fileBean" property="valid" value="true">
			<table  cellspacing="0" cellpadding="0" width=100% >
				<tr class="hellerblau">
					<td>
						Filename:&nbsp;
						<ano:equal name="ano-web.file.fileBean" property="filePresent" value="false">
							<ano:write name="ano-web.file.fileBean" property="name"/>
						</ano:equal>
						<ano:equal name="ano-web.file.fileBean" property="filePresent" value="true">
							<a target="_blank" href="<%=request.getContextPath()%>/cms/showTmpFile"><ano:write name="ano-web.file.fileBean" property="name"/></a>
						</ano:equal>
						&nbsp;|&nbsp;
						Filesize:&nbsp;<ano:write name="ano-web.file.fileBean" property="size"/>
						&nbsp;|&nbsp;
						<ano:write name="ano-web.file.fileBean" property="message"/>
					</td>
				</tr>
				<tr class="hellerblau">
					<td colspan=5>&nbsp;</td>
				</tr>
			</table>
		</ano:equal>
	</body>
</html>