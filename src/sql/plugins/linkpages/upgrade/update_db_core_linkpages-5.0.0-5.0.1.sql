-- liquibase formatted sql
-- changeset linkpages:update_db_core_linkpages-5.0.0-5.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_portlet_type
SET
    create_specific_form = '/admin/plugins/linkpages/create_portlet_linkpages_list.html',
    modify_specific_form = '/admin/plugins/linkpages/modify_portlet_linkpages_list.html'
WHERE
    id_portlet_type = 'LINK_PAGES_PORTLET';