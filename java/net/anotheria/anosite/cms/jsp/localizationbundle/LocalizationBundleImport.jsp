<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%><%@ page isELIgnored ="false" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>LocalizationBundleImport</title>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-cache, must-revalidate"/>
    <meta name="Expires" content="0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript" src="/cms_static/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="/cms_static/js/anofunctions.js"></script>
    <link href="/cms_static/css/newadmin.css" rel="stylesheet" type="text/css">
</head>
<body>
<jsp:include page="MenuMaf.jsp" flush="true"/>
<div class="right">
    <div class="r_w">
        <div class="main_area">
            <div class="c_l"><!-- --></div>
            <div class="c_r"><!-- --></div>
            <div class="c_b_l"><!-- --></div>
            <div class="c_b_r"><!-- --></div>
            <form method="post" id="uploadLocalization" enctype="multipart/form-data"
            >
                <input type="file" name="file" />

                <select name="locale" class="locale">
                    <ano:iterate name="languages" type="java.lang.String" id="option">
                        <option value="<ano:write name="option"/>" <ano:equal name="option" value="${selectedLanguage}">selected</ano:equal>><ano:write name="option"/></option>
                    </ano:iterate>
                </select>
                <input type="submit" class="import-txt" value="Import" style="padding-left: 5px;padding-right: 5px;"/>
            </form>
            <div class="clear"><!-- --></div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    $(".import-txt").click(function (event) {
        event.preventDefault();
        var form = $('#uploadLocalization')[0];
        var data = new FormData(form);
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/ImportLocalizationBundle",
            data: data,
            processData: false,
            contentType: false,
            success: function (data) {
                alert("Localization imported");
                console.log("SUCCESS : ", data);
            },
            error: function (e) {
                alert("ERROR : " + e);
                console.log("ERROR : ", e);
            }
        });
    });
</script>
