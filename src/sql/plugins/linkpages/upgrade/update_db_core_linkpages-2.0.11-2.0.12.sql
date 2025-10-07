-- liquibase formatted sql
-- changeset linkpages:update_db_core_linkpages-2.0.11-2.0.12.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_portlet_type(id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES
('LINK_PAGES_PORTLET','linkpages.portlet.name','plugins/linkpages/CreatePortletLinkPages.jsp','plugins/linkpages/ModifyPortletLinkPages.jsp','fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortletHome','linkpages','plugins/linkpages/DoCreatePortletLinkPages.jsp','/admin/portlet/script_create_portlet.html','','','plugins/linkpages/DoModifyPortletLinkPages.jsp','/admin/portlet/script_modify_portlet.html','','/admin/plugins/linkpages/modify_portlet_linkpages_list.html');
