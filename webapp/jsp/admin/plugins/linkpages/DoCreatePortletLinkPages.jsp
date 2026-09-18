<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.linkpages.web.portlet.LinkPagesPortletJspBean"%>

${ linkPagesPortletJspBean.init( pageContext.request, LinkPagesPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( linkPagesPortletJspBean.doCreate( pageContext.request ) ) }
