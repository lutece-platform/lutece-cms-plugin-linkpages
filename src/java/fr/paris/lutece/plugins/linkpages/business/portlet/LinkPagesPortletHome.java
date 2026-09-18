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
import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.util.ReferenceList;
import jakarta.enterprise.inject.spi.CDI;

import java.util.List;


/**
 * Provides instance management for LinkPagesPortlet objects
 */
public class LinkPagesPortletHome extends PortletHome
{
    private static ILinkPagesPortletDAO _dao = CDI.current( ).select( ILinkPagesPortletDAO.class ).get( );

    private static LinkPagesPortletHome _singleton = null;

    /**
     * Constructor
     */
    public LinkPagesPortletHome(  )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the identifier of the link pages portlet type
     *
     * @return the portlet type identifier
     */
    public String getPortletTypeId(  )
    {
        String strCurrentClassName = this.getClass(  ).getName(  );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of LinkPagesPortletHome
     *
     * @return the LinkPagesPortletHome instance
     */
    public static PortletHome getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new LinkPagesPortletHome(  );
        }

        return _singleton;
    }

    /**
    * Returns the instance of the LinkPagesPortletDAO singleton
    *
    * @return the instance of the LinkPagesPortletDAO
    */
    public IPortletInterfaceDAO getDAO(  )
    {
        return _dao;
    }

    /**
     * Returns the list of all the pages of the website
     *
     * @return the list in form of a ReferenceList object
     */
    public static ReferenceList getLinkPagesList(  )
    {
        return _dao.selectLinkPagesList(  );
    }

    /**
     * Return the max order of a portlet
     *
     * @param nPortletId The identifier of the portlet
     * @return The max order
     */
    public static int getMaxOrder( int nPortletId )
    {
        return _dao.selectMaxOrder( nPortletId );
    }

    /**
    * Returns the list of linkpages in a specified portlet
    *
    * @param nPortletId The identifier of the portlet to check
    * @return A List of linkpages object
    */
    public static List<Page> getLinkPagesInPortletList( int nPortletId )
    {
        return _dao.selectLinkPagesInPortletList( nPortletId );
    }

    /**
     * Return the order of a link page in a specified portlet
     *
     * @param nPortletId The identifier of the portlet to check
     * @param nLinkPageId The identifier of the page
     * @return The order of the page in the portlet
     */
    public static int getLinkPageOrder( int nPortletId, int nLinkPageId )
    {
        return _dao.selectLinkPageOrder( nPortletId, nLinkPageId );
    }

    /**
     * Return the Id of a link page by his order in a portlet
     *
     * @param nPortletId The identifier of the portlet in wich the linkpage is inscribed
     * @param nOrder The  order of the linkpage
     * @return The identifier of the linkpage
     */
    public static int getLinkPageIdByOrder( int nPortletId, int nOrder )
    {
        return _dao.selectLinkPageIdByOrder( nPortletId, nOrder );
    }

    /**
     * Update the order of a specified link page in a specified portlet
     *
     * @param nOrder The new order of the link page
     * @param nPortletId The identifier of the portlet to update
     * @param nLinkPageId The identifier of the link page who has a new order
     */
    public static void updateLinkPageOrder( int nOrder, int nPortletId, int nLinkPageId )
    {
        _dao.storeLinkPageOrder( nOrder, nPortletId, nLinkPageId );
        invalidate( nPortletId );
    }

    /**
     * Remove a link page of a portlet
     *
     * @param nPortletId The identifier of the portlet from wich the link page is removed
     * @param nLinkPageId The identifier of the link page to remove
     */
    public static void removeLinkPage( int nPortletId, int nLinkPageId )
    {
        _dao.deleteLinkPage( nPortletId, nLinkPageId );
        invalidate( nPortletId );
    }

    /**
     * Remove all the link pages of a portlet
     *
     * @param nPortletId The identifier of the portlet from wich the link page is removed
     */
    public static void removeAllLinkPages( int nPortletId )
    {
        _dao.deleteAllLinkPages( nPortletId );
        invalidate( nPortletId );
    }

    /**
     * Verify if a link page is already in a portlet
     *
     * @param nPortletId The identifier of the portlet to check
     * @param nLinkPageId The identifier of the link page to inscribe
     * @return A boolean of the result
     */
    public static boolean testDuplicate( int nPortletId, int nLinkPageId )
    {
        return _dao.testDuplicate( nPortletId, nLinkPageId );
    }

    /**
     * Insert a specified link page in a specified link page's portlet with a specified order
     *
     * @param nPortletId The identifier of the portlet to be inserted
     * @param nLinkPageId The identifier of the link page to insert
     * @param nOrder The order of the inserted link page
     */
    public static void insertLinkPage( int nPortletId, int nLinkPageId, int nOrder )
    {
        _dao.insertLinkPage( nPortletId, nLinkPageId, nOrder );
        invalidate( nPortletId );
    }
}
