<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.linkpages.web.portlet.LinkPagesPortletJspBean"%>

${ linkPagesPortletJspBean.init( pageContext.request, LinkPagesPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ linkPagesPortletJspBean.getModify( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
