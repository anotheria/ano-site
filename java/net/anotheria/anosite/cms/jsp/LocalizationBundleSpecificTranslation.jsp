<%@ page
        contentType="text/html;charset=UTF-8" session="true"
%>
<%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
<%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%>
<%@ page isELIgnored="false" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>LocalizationBundleSpecificTranslation</title>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-cache, must-revalidate"/>
    <meta name="Expires" content="0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript" src="/cms_static/js/jquery-1.4.min.js"></script>
    <script type="text/javascript" src="/cms_static/js/anofunctions.js"></script>
    <link href="/cms_static/css/newadmin.css" rel="stylesheet" type="text/css">

    <style>
        .lds-ring {
            display: inline-block;
            position: relative;
            width: 40px;
            height: 40px;
        }

        .lds-ring div {
            box-sizing: border-box;
            display: block;
            position: absolute;
            width: 32px;
            height: 32px;
            margin: 8px;
            border: 3px solid #fff;
            border-radius: 50%;
            animation: lds-ring 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
            border-color: #000 transparent transparent transparent;
        }

        .lds-ring div:nth-child(1) {
            animation-delay: -0.45s;
        }

        .lds-ring div:nth-child(2) {
            animation-delay: -0.3s;
        }

        .lds-ring div:nth-child(3) {
            animation-delay: -0.15s;
        }

        @keyframes lds-ring {
            0% {
                transform: rotate(0deg);
            }
            100% {
                transform: rotate(360deg);
            }
        }
    </style>
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
            <div style="margin-bottom: 1em">
                <p>Input: </p>
                <textarea class="input" rows="10" style="width: 100%"></textarea>
            </div>
            <div style="display: flex; flex-direction: column">
                <div style="width: 350px; display: flex">
                    <p style="width: 30%">Source language: </p>
                    <select style="width: 65%" name="language" class="localeFrom">
                        <ano:iterate name="languages" type="java.lang.String" id="option">
                            <option value="<ano:write name="option"/>"><ano:write name="option"/></option>
                        </ano:iterate>
                    </select>
                </div>
                <div style="width: 350px; display: flex">
                    <p style="width: 30%">Bundle: </p>
                    <select style="width: 65%" name="language" class="bundleId">
                        <ano:iterate name="localizationBundles"
                                     type="net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle"
                                     id="bundle">
                            <option value="${bundle.id}">${bundle.id}_${bundle.name}</option>
                        </ano:iterate>
                    </select>
                </div>
            </div>
            <div style="display: flex; flex-direction: column; margin-bottom: 2em">
                <div><a href="#" class="button translate"><span>Translate</span></a></div>
                <div id="translateRing" class="lds-ring" style="display: none">
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                </div>
            </div>
            <div id="translatedResult" style="margin-bottom: 1em">

            </div>
            <div><a href="#" class="button submit" style="display: none"><span>Submit</span></a></div>
            <div id="submitRing" class="lds-ring" style="display: none">
                <div></div>
                <div></div>
                <div></div>
                <div></div>
            </div>
            <div class="clear"><!-- --></div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    var counter = 1;

    $('.translate').click(function (event) {
        event.preventDefault();
        event.stopPropagation();

        var bundleId = $('.bundleId').val();
        var localeFrom = $('.localeFrom').val();
        var input = $('.input').val();

        var payload = {
            bundleId: bundleId,
            localeFrom: localeFrom,
            input: input,
            method: "translate"
        };
        $.ajax({
            url: '/SpecificTranslateLocalizationBundle',
            type: "POST",
            data: payload,
            beforeSend: function () {
                $('#translateRing').show();
                $('.submit').hide();
                for (let i = 1; i < counter; i++) {
                    document.getElementById("locale" + i).remove();
                    document.getElementById("textarea" + i).remove();
                }
                counter = 1;
            },
            complete: function () {
                $('#translateRing').hide();
                $('.result').show();
                $('.submit').show();
            },
            success: function (data) {
                if (data.errors && data.errors.length != 0) {
                    if (data.errors["INPUT_ERROR"]) {
                        alert(data.errors["INPUT_ERROR"][0]);
                    } else if (data.errors["CONFIG_ERROR"]) {
                        alert(data.errors["CONFIG_ERROR"][0]);
                    } else if (data.errors["CANNOT_TRANSLATE"]) {
                        alert(data.errors["CANNOT_TRANSLATE"][0]);
                    } else if (data.errors["SERVER_ERROR"]) {
                        alert(data.errors["SERVER_ERROR"][0]);
                    }
                } else {
                    var map = data.data.results;

                    for (let key in map) {
                        if (map.hasOwnProperty(key)) {
                            const localeElement = document.createElement("p");
                            localeElement.textContent = key + ":";
                            localeElement.setAttribute("id", "locale" + counter);

                            const textAreaElement = document.createElement("textarea");
                            textAreaElement.textContent = map[key];
                            textAreaElement.setAttribute("rows", "10");
                            textAreaElement.setAttribute("style", "width: 100%;")
                            textAreaElement.setAttribute("id", "textarea" + counter);

                            var resultDiv = document.getElementById("translatedResult");
                            resultDiv.append(localeElement);
                            resultDiv.append(textAreaElement);
                            console.log(key + " -> " + map[key]);
                            counter++;
                        }
                    }

                }
            }
        });
    });
    $('.submit').click(function (event) {
        event.preventDefault();
        event.stopPropagation();

        var bundleId = $('.bundleId').val();

        const map = new Map();
        for (let i = 1; i < counter; i++) {
            var textAreaId = "textarea" + i;
            console.log(textAreaId);
            const textArea = document.getElementById(textAreaId);
            console.log(textArea);
            var localeId = "locale" + i;
            console.log(localeId);
            const locale = document.getElementById(localeId);
            console.log(locale);
            map.set(locale.textContent, textArea.textContent);
        }
        console.log(JSON.stringify(Array.from(map.entries())));

        var payload = {
            bundleId: bundleId,
            map: JSON.stringify(Array.from(map.entries())),
            method: "save"
        };
        $.ajax({
            url: '/SpecificTranslateLocalizationBundle',
            type: "POST",
            data: payload,
            beforeSend: function () {
                $('#submitRing').show();
            },
            complete: function () {
                $('#submitRing').hide();
            },
            success: function (data) {
                if (data.errors && data.errors.length != 0) {
                    if (data.errors["INPUT_ERROR"]) {
                        alert(data.errors["INPUT_ERROR"][0]);
                    } else if (data.errors["CONFIG_ERROR"]) {
                        alert(data.errors["CONFIG_ERROR"][0]);
                    } else if (data.errors["CANNOT_TRANSLATE"]) {
                        alert(data.errors["CANNOT_TRANSLATE"][0]);
                    } else if (data.errors["SERVER_ERROR"]) {
                        alert(data.errors["SERVER_ERROR"][0]);
                    }
                } else {
                    alert("Translation saved");
                }
            }
        });
    });
</script>
