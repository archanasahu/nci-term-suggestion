<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
  <body>
  <f:view>
    <form name="ntsubmit" method="post">
      Submit:
      <h:commandButton
        id="submit"
        value="submit"
        action="#{userSessionBean.changeRequest}"
        image="#{facesContext.externalContext.requestContextPath}/images/search.gif"
        alt="submit">
      </h:commandButton>
    </form>
  </f:view>
  </body>
</html>