<%@ page contentType="text/html;charset=UTF-8" session="true" %>
<iframe src=\"fileShow?nocache=<%=System.currentTimeMillis()%>\" frameborder=\"0\" width=100% height=80 scrolling=\"no\">
	<html>
		<head>
			<meta http-equiv="pragma" content="no-cache">
			<meta http-equiv="Cache-Control" content="no-cache, must-revalidate">
			<meta name="Expires" content="0">
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		</head>
		<body>
		  	<form id="upload" name="upload" target="answer" method="post" enctype="multipart/form-data" action="<%=request.getContextPath()%>/cms/fileUpload">
			<input type="file" id="file" name="file" size="20" onchange="document.upload.submit();"/>
			</form>
			<iframe id="answer" name="answer" src="<%=request.getContextPath()%>/net/anotheria/anosite/cms/jsp/filehandling/UploadFileResult.jsp" width="100%" height="20" scrolling="No" frameborder="0" marginwidth="0" marginheight="0" />
		</body>
	</html>
</iframe>