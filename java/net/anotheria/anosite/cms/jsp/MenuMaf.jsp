<%@ page
        contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%><%@ taglib uri="/WEB-INF/tlds/anosite.tld" prefix="as"
%><%@ page isELIgnored ="false" %>
<div class="left">
    <div class= "scroll_left">
        <img class="logo" src="../cms_static/img/${currentSystem eq 'PROD' ? 'logo2.gif' : 'logo.gif'}" alt="CMS Logo"/>
        <ano:notEmpty name="currentApplication">
            <div class="currentApplication">App: ${currentApplication}</div>
        </ano:notEmpty>
        <div class="currentSystem">Environment: ${currentSystem eq 'PROD' ? '<font color="red">PROD</font>' : currentSystem}</div>
        <ano:notEqual name="disabledSearchFlag" value="true">
            <form name="Search" action="cmssearch">
                <input class="search" name="criteria" type="text" value="Search..." />
                <a href="#" class="adv_search ">Advanced search</a>
                <div class="clear"><!-- --></div>
                <div class="open adv_s_open">
                    <div class="top">
                        <div><!-- --></div>
                    </div>
                    <div class="in">
                        <span class="f_14">Search</span>
                        <ul>
                            <li><input type="radio" id="r1" value="view" <ano:equal name="searchScope" value="view">CHECKED</ano:equal> name="searchArea"/><label for="r1">Current page</label></li>
                            <li><input type="radio" id="r2" value="section" <ano:equal name="searchScope" value="section">CHECKED</ano:equal> name="searchArea"/><label for="r2">Current section</label></li>
                        </ul>
                        <input type="hidden" value="<ano:write name="moduleName" />" name="module"/>
                        <input type="hidden" value="<ano:write name="documentName" />" name="document"/>
                        <a href="#" class="button" onClick="document.Search.submit();return false"><span>Search</span></a>
                    </div>
                    <div class="bot">
                        <div><!-- --></div>
                    </div>
                </div>
            </form>
            <a href="#" class="lang_open">Languages</a>
        </ano:notEqual>
        <div class="clear"><!-- --></div>
        <div class="open lang_s_open">
            <div class="top">
                <div><!-- --></div>
            </div>
            <div class="in">
                <input type="checkbox" id="all_check" class="all_check"/><label for="c1">Select All</label>
                <div class="clear"><!-- --></div>
                <ul>
                    <li>
                        <input type="checkbox" id="lang_EN"/><label for="lang_EN">EN</label>
                    </li>
                    <li>
                        <input type="checkbox" id="lang_DE"/><label for="lang_DE">DE</label>
                    </li>
                </ul>
                <a href="#" class="button"><span>Apply</span></a>
                <div class="clear"><!-- --></div>
            </div>
            <div class="bot">
                <div><!-- --></div>
            </div>
        </div>
        <ul class="main_navigation">
            <ano:iterate name="mainNavigation" type="net.anotheria.anosite.cms.bean.NavigationItemBean" id="NaviItem">
                <ano:equal name="NaviItem" property="active" value="true">
                    <li class="opened"><a><ano:write name="NaviItem" property="caption"/></a>
                        <ul>
                            <ano:iterate id="subNaviItem" name="NaviItem" property="subNavi">
                                <li><a <ano:equal name="subNaviItem" property="active" value="true"> class="active" href="<ano:write name="subNaviItem" property="link"/>"</ano:equal><ano:notEqual name="subNaviItem" property="active" value="true">href="<ano:write name="subNaviItem" property="link"/>"</ano:notEqual>><ano:write name="subNaviItem" property="caption"/></a></li>
                            </ano:iterate>
                        </ul>
                    </li>
                </ano:equal>
                <ano:notEqual name="NaviItem" property="active" value="true">
                    <li><a href="<ano:tslink><ano:write name="NaviItem" property="link"/></ano:tslink>"><ano:write name="NaviItem" property="caption"/></a></li>
                </ano:notEqual>
            </ano:iterate>
        </ul>
        <a href="<ano:tslink>changePass</ano:tslink>" class="change_pass">Change password</a>
        <a href="<ano:tslink>logout</ano:tslink>" class="logout">Logout</a>
        <div class="clear"><!-- --></div>
        <a href="http://www.anotheria.net" class="powered"><img src="../cms_static/img/powered_conf.gif" alt=""/></a>
    </div>
</div>
