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
    <title>LocalizationBundlesCompare</title>
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
<jsp:include page="../../../gen/shared/jsp/MenuMaf.jsp" flush="true"/>
<div class="right">
    <div class="r_w">
        <div class="main_area">
            <div class="c_l"><!-- --></div>
            <div class="c_r"><!-- --></div>
            <div class="c_b_l"><!-- --></div>
            <div class="c_b_r"><!-- --></div>
            <div style="display: flex; flex-direction: column">
                <div style="width: 350px; display: flex">
                    <p style="width: 40%">First  Bundle: </p>
                    <select style="width: 65%" name="language" class="firstBundle">
                        <ano:iterate name="localizationBundles"
                                     type="net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle"
                                     id="bundle">
                            <option value="${bundle.id}">${bundle.id}_${bundle.name}</option>
                        </ano:iterate>
                    </select>
                </div>
                <div style="width: 350px; display: flex">
                    <p style="width: 40%">Second  Bundle: </p>
                    <select style="width: 65%" name="language" class="secondBundle">
                        <ano:iterate name="localizationBundles"
                                     type="net.anotheria.anosite.gen.asresourcedata.data.LocalizationBundle"
                                     id="bundle">
                            <option value="${bundle.id}">${bundle.id}_${bundle.name}</option>
                        </ano:iterate>
                    </select>
                </div>
            </div>
            <div><a href="#" class="button getCompare"><span>Get compare</span></a></div>
            <div id="submitRing" class="lds-ring" style="display: none">
                <div></div>
                <div></div>
                <div></div>
                <div></div>
            </div>
            <div class="clear"><!-- --></div>
        </div>
        <div id="result-div" class="main_area" style="display: none">
            <div class="c_l"><!-- --></div>
            <div class="c_r"><!-- --></div>
            <div class="c_b_l"><!-- --></div>
            <div class="c_b_r"><!-- --></div>
            <div style="margin-bottom: 1em">
                <p>Result: </p>
                <textarea class="result-area" rows="10" style="width: 100%; padding: .25em"></textarea>
            </div>
            <div class="clear"><!-- --></div>
        </div>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    const getCompare = function () {
        const firstBundle = $('.firstBundle').val();
        const secondBundle = $('.secondBundle').val();

        const payload = {
            firstBundle: firstBundle,
            secondBundle: secondBundle,
        };

        $.ajax({
            url: '/LocalizationBundlesCompare',
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
                    } else if (data.errors["SERVER_ERROR"]) {
                        alert(data.errors["SERVER_ERROR"][0]);
                    }
                } else {
                    $('#result-div').show();
                    const result = data.data.result;
                    $('.result-area').val(result);
                }
            }
        });
    };

    $('.getCompare').click(function (event) {
        event.preventDefault();
        event.stopPropagation();

        getCompare();
    });
</script>
