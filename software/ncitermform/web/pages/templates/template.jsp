<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-suggestion/LICENSE.txt for details.
L--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.webapp.*" %>
<%@ page import="gov.nih.nci.evs.utils.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  String basePath = FormUtils.getBasePath(request);
 
  String imagesPath = FormUtils.getImagesPath(request);
  String cssPath = FormUtils.getCSSPath(request);
  String content_title = HTTPUtils.cleanXSS((String) request.getParameter("content_title"));
  if (content_title == null || content_title.trim().length() <= 0)
      content_title = "Suggest New Term";
  String content_quickLink = HTTPUtils.cleanXSS((String) request.getParameter("content_quickLink"));
  if (content_quickLink == null)
      content_quickLink = "";
      
  String content_page = HTTPUtils.cleanXSS((String) request.getParameter("content_page"));
  
  String buildDate = AppProperties.getInstance().getBuildDate();
  String application_version = AppProperties.getInstance().getAppVersion();
  String anthill_build_tag_built = AppProperties.getInstance().getAnthillBuildTagBuilt();  
  //String version = (String) 
  //  request.getSession().getAttribute(FormRequest.VERSION);
  
  String version = null;
  Object version_obj = request.getSession().getAttribute(FormRequest.VERSION);
  
  if (version == null) {
      version = "Default";
  } else {
      version = version_obj.toString();
  }
  
  String logoUrl = basePath + "/" + BaseRequest.getUrlParameter(version);
%>
<!--
   Build info: <%=buildDate%>
 Version info: <%=application_version%>
          Tag: <%=anthill_build_tag_built%>
  -->
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title><%=content_title%></title>
    <link rel="stylesheet" type="text/css" href="<%=cssPath%>/styleSheet.css" />
    <link rel="shortcut icon" href="<%=basePath%>/favicon.ico" type="image/x-icon" />
    
    
    <script>

    function getContextPath() {
	return "<%=request.getContextPath()%>";
    }

    function loadAudio() {
        var path = getContextPath() + "/audio.wav?bogus=";
        document.getElementById("audioCaptcha").src = path + new Date().getTime();
        document.getElementById("audioSupport").innerHTML = document.createElement('audio').canPlayType("audio/wav");
    }
    </script>    
    
    
  </head>
  <body>
  
    <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->      
  
    <jsp:include page="/pages/templates/header.jsp" />
    <div class="center-page">
      <jsp:include page="/pages/templates/sub_header.jsp" />
      <div class="mainbox-top"><img src="<%=imagesPath%>/mainbox-top.gif"
        width="745" height="5" alt="Mainbox Top" /></div>
      <div id="main-area">
        <div class="bannerarea">
          <a href="<%=logoUrl%>"><img src="<%=imagesPath%>/banner.gif"
            alt="Suggest Term Logo" border="0"/></a>
        </div>
        <div class="bluebar">
          <% if (content_quickLink.length() > 0) { %>
            <jsp:include page="<%=content_quickLink%>" />
          <% } %>
        </div>
        
        
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <jsp:include page="<%=content_page%>" />
          <jsp:include page="/pages/templates/footer.jsp" />
        </div>
      </div>
      <div class="mainbox-bottom"><img src="<%=imagesPath%>/mainbox-bottom.gif"
        width="745" height="5" alt="Mainbox Bottom" /></div>
    </div>
  </body>
  <br>
</html>