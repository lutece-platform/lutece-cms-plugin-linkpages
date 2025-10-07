-- liquibase formatted sql
-- changeset linkpages:create_linkpages_portlet.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table linkpages_portlet
--

DROP TABLE IF EXISTS linkpages_portlet;
CREATE TABLE linkpages_portlet (
  id_portlet INT DEFAULT '0' NOT NULL,
  id_linkpage INT DEFAULT '0' NOT NULL,
  linkpage_order INT DEFAULT '0',
  PRIMARY KEY (id_portlet,id_linkpage)
);