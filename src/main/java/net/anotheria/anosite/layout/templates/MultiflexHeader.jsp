<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
%>
    <!-- A. HEADER -->      
    <div class="header">
      
      <!-- A.1 HEADER MIDDLE -->
      <div class="header-middle">    
   
        <!-- Sitelogo and sitename -->

        <a class="sitelogo" href="<ano:write name="site" property="linkToStartPage"/>" title="Go to Start page">
            <img src="<ano:write name="site" property="logo"/>"/>
        </a>
        <div class="sitename">

          <h1><a href="<ano:write name="site" property="linkToStartPage"/>" title="Go to Start page"><ano:write name="site" property="title"/></a></h1>
          <h2><ano:write name="site" property="subtitle"/></h2>
        </div>

        <!-- Navigation Level 0 -->

        <div class="nav0">
          <ano:equal name="site" property="languageSelector" value="true">
          <ul>

            <li><a href="<ano:write name="site" property="linkToStartPage"/>?lang=EN" title="English homepage"><img src="simg/flag_gb.gif" alt="English homepage" /></a></li>
            <li><a href="<ano:write name="site" property="linkToStartPage"/>?lang=DE" title="Homepage auf Deutsch"><img src="simg/flag_germany.gif" alt="German homepage" /></a></li>
          </ul>
          </ano:equal>
        </div>			

        <!-- Navigation Level 1 -->
        <div class="nav1">

          <ul>
          	<ano:iterate name="topNavi" id="item" type="net.anotheria.anosite.content.bean.NaviItemBean">
	            <li><a <ano:equal name="item" property="popup" value="true">target="_blank" </ano:equal>href="<ano:write name="item" property="link"/>" title="<ano:write name="item" property="title"/>"><ano:write name="item" property="name"/></a></li>
            </ano:iterate>
          </ul>
        </div>              
      </div>

      
      <!-- A.2 HEADER BOTTOM -->
      <div class="header-bottom">

      
        <!-- Navigation Level 2 (Drop-down menus) -->
        <div class="nav2">
	
	      <ano:iterate name="mainNavi" id="item" type="net.anotheria.anosite.content.bean.NaviItemBean">
          	  <!-- Navigation item -->
        	  <ul>
          	  <ano:equal name="item" property="hasChilds" value="false">
    	        <li><a <ano:equal name="item" property="popup" value="true">target="_blank" </ano:equal>href="<ano:write name="item" property="link"/>"><ano:write name="item" property="name"/></a></li>
	          </ano:equal>
          	  <ano:equal name="item" property="hasChilds" value="true">
	            <li><a <ano:equal name="item" property="popup" value="true">target="_blank" </ano:equal>href="<ano:write name="item" property="link"/>"><ano:write name="item" property="name"/><!--[if IE 7]><!--></a><!--<![endif]-->
	              <!--[if lte IE 6]><table><tr><td><![endif]-->
	                <ul>
				      <ano:iterate name="item" property="subNavi" id="subitem" type="net.anotheria.anosite.content.bean.NaviItemBean">
	                  <li><a <ano:equal name="subitem" property="popup" value="true">target="_blank" </ano:equal>href="<ano:write name="subitem" property="link"/>"><ano:write name="subitem" property="name"/></a></li>
	                  </ano:iterate>
	                </ul>
	              <!--[if lte IE 6]></td></tr></table></a><![endif]-->
	            </li>
	          </ano:equal>
	          </ul>
	      </ano:iterate>

        </div>
	  </div>

      <!-- A.3 HEADER BREADCRUMBS -->

      <!-- Breadcrumbs -->
      <div class="header-breadcrumbs">
        <ul>
      	<ano:iterate name="breadcrumbs" type="net.anotheria.anosite.content.bean.BreadCrumbItemBean" id="b">
      		<ano:equal name="b" property="clickable" value="true">
	          <li><a href="<ano:write name="b" property="link"/>"><ano:write name="b" property="title"/></a></li>
			</ano:equal>
      		<ano:notEqual name="b" property="clickable" value="true">
	          <li><ano:write name="b" property="title"/></li>
			</ano:notEqual>
        </ano:iterate>
        </ul>

		<ano:equal name="site" property="showSearchDialog" value="true">
        <!-- Search form -->                  
        <div class="searchform">
          <form action="<ano:write name="site" property="searchTarget"/>" method="get">

            <fieldset>
              <input name="criteria" class="field"  value=" Search..." />
              <input type="submit" name="button" class="button" value="GO!" />
            </fieldset>
          </form>
        </div>
        </ano:equal>
      </div>
    </div>