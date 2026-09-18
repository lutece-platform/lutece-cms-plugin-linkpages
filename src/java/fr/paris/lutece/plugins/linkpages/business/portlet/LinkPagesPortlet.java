/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.linkpages.business.portlet;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.AdminPageJspBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;


/**
 * This class represents business objects LinkPagesPortlet
 */
public class LinkPagesPortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_PORTLET = "skin/plugins/linkpages/portlet/linkpages_portlet.html";
    private static final String MARK_PORTLET = "portlet";
    private static final String MARK_LINK_PAGES = "link_pages";
    private static final String MARK_SITE_PATH = "site_path";

    /**
     * Sets the identifier of the portlet type to the value specified in the LinkPagesPortletHome class
     */
    public LinkPagesPortlet(  )
    {
        setPortletTypeId( LinkPagesPortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Returns the HTML content of the portlet
     *
     * @param request the HTTP request
     * @return the rendered portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PORTLET, this );
        model.put( MARK_SITE_PATH, AppPathService.getPortalUrl( ) );
        model.put( MARK_LINK_PAGES, getVisibleLinkPages( request ) );

        return AppTemplateService.getTemplate( TEMPLATE_PORTLET, request != null ? request.getLocale( ) : null, model )
                .getHtml( );
    }

    /**
     * Collects the linked pages visible to the current user
     *
     * @param request the HTTP request
     * @return the visible pages, with their image URL when they have one
     */
    private List<LinkPageItem> getVisibleLinkPages( HttpServletRequest request )
    {
        List<LinkPageItem> items = new ArrayList<>( );

        if ( request == null )
        {
            return items;
        }

        AdminPageJspBean adminPage = new AdminPageJspBean( );

        for ( Page page : LinkPagesPortletHome.getLinkPagesInPortletList( getId( ) ) )
        {
            if ( !page.isVisible( request ) )
            {
                continue;
            }

            String strImageUrl = null;

            if ( page.getImageContent( ) != null && page.getImageContent( ).length >= 1 )
            {
                strImageUrl = adminPage.getResourceImagePage( page, Integer.toString( page.getId( ) ) );
            }

            items.add( new LinkPageItem( page, strImageUrl ) );
        }

        return items;
    }

    /**
     * Updates the current instance of the LinkPage Portlet object
     */
    public void update(  )
    {
        LinkPagesPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the LinkPage Portlet object
     */
    public void remove(  )
    {
        LinkPagesPortletHome.getInstance(  ).remove( this );
    }

    /**
     * A linked page as the template consumes it
     */
    public static final class LinkPageItem
    {
        private final Page _page;
        private final String _strImageUrl;

        /**
         * Builds an item
         *
         * @param page the page
         * @param strImageUrl the image URL, null when the page has no image
         */
        LinkPageItem( Page page, String strImageUrl )
        {
            _page = page;
            _strImageUrl = strImageUrl;
        }

        /**
         * Returns the page identifier
         *
         * @return the page identifier
         */
        public int getId( )
        {
            return _page.getId( );
        }

        /**
         * Returns the page name
         *
         * @return the page name
         */
        public String getName( )
        {
            return _page.getName( );
        }

        /**
         * Returns the page description
         *
         * @return the page description
         */
        public String getDescription( )
        {
            return _page.getDescription( );
        }

        /**
         * Returns the image URL
         *
         * @return the image URL, null when the page has no image
         */
        public String getImageUrl( )
        {
            return _strImageUrl;
        }
    }
}
