<%@ page
        contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%><%@ page isELIgnored ="false" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>LocalizationBundleMakeParents</title>
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
            <form method="post" id="makeParentLocalization" style="margin-left: 100px">
                <select name="child1" class="child1">
                    <ano:iterate name="localizationBundles" type="net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle" id="bundle">
                        <option value="${bundle.id}">${bundle.id}_${bundle.name}</option>
                    </ano:iterate>
                </select>
                <select name="child2" class="child2">
                    <ano:iterate name="localizationBundles" type="net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle" id="bundle">
                        <option value="${bundle.id}">${bundle.id}_${bundle.name}</option>
                    </ano:iterate>
                </select>
                <input type="text" class="parent-name" name="parent-name" value="" />
                <input type="submit" class="preview-parent" value="Preview" style="padding-left: 5px;padding-right: 5px;"/>
                <input type="button" class="make-parent" value="Create" style="padding-left: 5px;padding-right: 5px;"/>
            </form>
            <div class="clear"><!-- --></div>

            <table class="parentPreview" width="100%" cellspacing="0" cellpadding="0" border="0" style="display: none">
                <tbody class="parentPreviewBody">
                </tbody>
            </table>
            <input type="button" class="make-parent" value="Create" style="padding-left: 5px;padding-right: 5px; margin-left: 100px; display: none"/>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    $(".preview-parent").click(function (event) {
        event.preventDefault();
        $(".parentPreview").hide();
        $(".parentPreviewBody").html("");
        var data = {
            child1: $('.child1').val(),
            child2: $('.child2').val(),
            parentName: $('.parent-name').val()
        };
        var previewTableHeader = "<tr><td align=\"right\"></td><td align=\"left\">" + data.child1 + "</td><td align=\"left\">" + data.parentName + "</td><td align=\"left\">" + data.child2 + "</td></tr>";
        $.ajax({
            type: "POST",
            url: "/PreviewParentLocalizationBundle",
            data: data,
            success: function (data) {
                if (data.errors && data.errors.length != 0) {
                    if (data.errors["NAME"]) {
                        alert(data.errors["NAME"][0]);
                    }
                } else {
                    var localizations = data.data.localizations;

                    $(".parentPreviewBody").append(previewTableHeader);
                    for (var localization of localizations) {
                        $(".parentPreviewBody").append("<tr><td align=\"right\">" + localization.messageKey + "</td>" +
                            "<td align=\"left\"><textarea rows=\"16\" >" + localization.child1MessageValue + "</textarea></td>" +
                            "<td align=\"left\"><textarea rows=\"16\" >" + localization.parentMessageValue + "</textarea></td>" +
                            "<td align=\"left\"><textarea rows=\"16\" >" + localization.child2MessageValue + "</textarea></td>" +
                            "</tr>");
                    }
                    $(".parentPreview").show();
                    $(".make-parent").show();
                }
            },
            error: function (e) {
                alert("ERROR : " + e);
                console.log("ERROR : ", e);
            }
        });
    });

    $(".make-parent").click(function (event) {
        var data = {
            child1: $('.child1').val(),
            child2: $('.child2').val(),
            parentName: $('.parent-name').val()
        };
        $.ajax({
            type: "POST",
            url: "/MakeParentLocalizationBundle",
            data: data,
            success: function (data) {
                if (data.errors && data.errors.length != 0) {
                    if (data.errors["NAME"]) {
                        alert(data.errors["NAME"][0]);
                    }
                } else {
                    alert("Parent created");
                }
            },
            error: function (e) {
                alert("ERROR : " + e);
                console.log("ERROR : ", e);
            }
        });
    });
</script>
