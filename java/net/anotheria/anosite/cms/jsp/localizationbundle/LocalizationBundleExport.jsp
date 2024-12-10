<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%><%@ page isELIgnored ="false" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>LocalizationBundleExport</title>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-cache, must-revalidate"/>
    <meta name="Expires" content="0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript" src="/cms_static/js/jquery-1.4.min.js"></script>
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
            <select name="language" class="locale">
                <ano:iterate name="languages" type="java.lang.String" id="option">
                    <option value="<ano:write name="option"/>" <ano:equal name="option" value="${selectedLanguage}">selected</ano:equal>><ano:write name="option"/></option>
                </ano:iterate>
            </select>
            <a href="#" class="button export-txt"><span>Export</span></a>
            <div class="clear"><!-- --></div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    $('.export-txt').click( function(event) {
        event.preventDefault();
        event.stopPropagation();

        var locale = $('.locale').val();
        var data = {
            locale: locale
        };
        let fileNameDate = new Date().toISOString().replace("T","_").substring(0, 19).replaceAll(":","_");
        $.ajax({
            url: 'exportLocalizationBundlesToTxt',
            data: data,
            success: function (data) {
                var element = document.createElement('a');
                element.setAttribute('href', 'data:attachment/txt;charset=utf-8,' + encodeURIComponent(data));
                element.setAttribute('download', fileNameDate + '_bundles_export_' + locale + '.txt');

                element.style.display = 'none';
                document.body.appendChild(element);
                element.click();
                document.body.removeChild(element);
            }
        });
    });
</script>
