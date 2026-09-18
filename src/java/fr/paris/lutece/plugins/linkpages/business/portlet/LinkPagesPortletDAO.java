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
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides data access for LinkPagesPortlet objects
 */
@ApplicationScoped
public final class LinkPagesPortletDAO implements ILinkPagesPortletDAO
{
    private static final String SQL_QUERY_SELECT_LINKPAGES_LIST = "SELECT id_page, name FROM core_page ORDER BY name";
    private static final String SQL_QUERY_SELECT_MAX_ORDER = "SELECT max( linkpage_order ) FROM linkpages_portlet WHERE id_portlet=?";
    private static final String SQL_QUERY_SELECT_LINKPAGE_IN_PORTLET = " SELECT a.id_page, a.name, a.description, a.page_order, a.status, a.role, a.code_theme, a.image_content " +
        " FROM core_page a, linkpages_portlet b  WHERE a.id_page = b.id_linkpage AND b.id_portlet = ? " +
        " ORDER BY b.linkpage_order";
    private static final String SQL_QUERY_SELECT_LINKPAGE_ORDER = "SELECT linkpage_order FROM linkpages_portlet WHERE id_portlet = ? AND id_linkpage = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM linkpages_portlet WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet, id_page FROM core_portlet WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT_LINKPAGE_ID_BY_ORDER = "SELECT id_linkpage FROM linkpages_portlet WHERE id_portlet = ? AND linkpage_order = ?";
    private static final String SQL_QUERY_UPDATE_LINKPAGE_ORDER = "UPDATE linkpages_portlet SET linkpage_order = ? WHERE id_portlet = ? AND id_linkpage = ? ";
    private static final String SQL_QUERY_DELETE_LINKPAGE = "DELETE FROM linkpages_portlet WHERE id_portlet=? AND id_linkpage =?";
    private static final String SQL_QUERY_DELETE_LINKPAGE_ALL = "DELETE FROM portlet_link_pages WHERE id_portlet=?";
    private static final String SQL_QUERY_CHECK_DUPLICATE = "SELECT id_linkpage FROM linkpages_portlet WHERE  id_portlet = ? AND  id_linkpage = ?";
    private static final String SQL_QUERY_INSERT_LINKPAGE = "INSERT INTO linkpages_portlet ( id_portlet, id_linkpage, linkpage_order ) VALUES (?,?,?)";

    /**
     * Inserts a portlet record
     *
     * @param portlet the portlet to insert
     */
    public void insert( Portlet portlet )
    {
    }

    /**
     * Deletes a portlet record
     *
     * @param nPortletId the portlet identifier
     */
    public void delete( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Loads a portlet by identifier
     *
     * @param nPortletId the portlet identifier
     * @return the portlet object
     */
    public Portlet load( int nPortletId )
    {
        LinkPagesPortlet portlet = new LinkPagesPortlet( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setPageId( daoUtil.getInt( 2 ) );
            }
        }

        return portlet;
    }

    /**
     * Stores a portlet record
     *
     * @param portlet the portlet to store
     * @throws AppException on storage error
     */
    public void store( Portlet portlet ) throws AppException
    {
    }

    /**
     * Loads all website pages
     *
     * @return the page list
     */
    public ReferenceList selectLinkPagesList( )
    {
        ReferenceList list = new ReferenceList( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKPAGES_LIST ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }

        return list;
    }

    /**
     * Loads the max order of a portlet
     *
     * @param nPortletId the portlet identifier
     * @return the max order
     */
    public int selectMaxOrder( int nPortletId )
    {
        int nOrder = 0;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_ORDER ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nOrder = daoUtil.getInt( 1 );
            }
        }

        return nOrder;
    }

    /**
     * Loads the link pages of a portlet
     *
     * @param nPortletId the portlet identifier
     * @return the page list
     */
    public List<Page> selectLinkPagesInPortletList( int nPortletId )
    {
        List<Page> list = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKPAGE_IN_PORTLET ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Page page = new Page( );
                page.setId( daoUtil.getInt( 1 ) );
                page.setName( daoUtil.getString( 2 ) );
                page.setDescription( daoUtil.getString( 3 ) );
                page.setOrder( daoUtil.getInt( 4 ) );
                page.setStatus( daoUtil.getInt( 5 ) );
                page.setRole( daoUtil.getString( 6 ) );
                page.setCodeTheme( daoUtil.getString( 7 ) );
                page.setImageContent( daoUtil.getBytes( 8 ) );
                list.add( page );
            }
        }

        return list;
    }

    /**
     * Loads the order of a link page in a portlet
     *
     * @param nPortletId the portlet identifier
     * @param nPageId the page identifier
     * @return the page order
     */
    public int selectLinkPageOrder( int nPortletId, int nPageId )
    {
        int nOrder = 0;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKPAGE_ORDER ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.setInt( 2, nPageId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nOrder = daoUtil.getInt( 1 );
            }
        }

        return nOrder;
    }

    /**
     * Loads a link page identifier by order
     *
     * @param nPortletId the portlet identifier
     * @param nOrder the page order
     * @return the page identifier
     */
    public int selectLinkPageIdByOrder( int nPortletId, int nOrder )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKPAGE_ID_BY_ORDER ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.setInt( 2, nOrder );
            daoUtil.executeQuery( );

            if ( !daoUtil.next( ) )
            {
                throw new AppException( DAOUtil.MSG_EXCEPTION_SELECT_ERROR + "(PortletId = " + nPortletId + "; Order = " +
                    nOrder + ")" );
            }

            return daoUtil.getInt( 1 );
        }
    }

    /**
     * Updates the order of a link page
     *
     * @param nOrder the new order
     * @param nPortletId the portlet identifier
     * @param nLinkPageId the link page identifier
     */
    public void storeLinkPageOrder( int nOrder, int nPortletId, int nLinkPageId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_LINKPAGE_ORDER ) )
        {
            daoUtil.setInt( 1, nOrder );
            daoUtil.setInt( 2, nPortletId );
            daoUtil.setInt( 3, nLinkPageId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Removes a link page from a portlet
     *
     * @param nPortletId the portlet identifier
     * @param nLinkPageId the link page identifier
     */
    public void deleteLinkPage( int nPortletId, int nLinkPageId )
    {
        if ( ( nLinkPageId != 0 ) )
        {
            try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINKPAGE ) )
            {
                daoUtil.setInt( 1, nPortletId );
                daoUtil.setInt( 2, nLinkPageId );
                daoUtil.executeUpdate( );
            }
        }
    }

    /**
     * Removes all link pages from a portlet
     *
     * @param nPortletId the portlet identifier
     */
    public void deleteAllLinkPages( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINKPAGE_ALL ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Checks a link page duplicate in a portlet
     *
     * @param nPortletId the portlet identifier
     * @param nLinkPageId the link page identifier
     * @return true when already registered
     */
    public boolean testDuplicate( int nPortletId, int nLinkPageId )
    {
        boolean bDuplicate = false;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_DUPLICATE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.setInt( 2, nLinkPageId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                bDuplicate = true;
            }
        }

        return bDuplicate;
    }

    /**
     * Inserts a link page in a portlet
     *
     * @param nPortletId the portlet identifier
     * @param nLinkPageId the link page identifier
     * @param nOrder the page order
     */
    public void insertLinkPage( int nPortletId, int nLinkPageId, int nOrder )
    {
        if ( ( nLinkPageId != 0 ) )
        {
            try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LINKPAGE ) )
            {
                daoUtil.setInt( 1, nPortletId );
                daoUtil.setInt( 2, nLinkPageId );
                daoUtil.setInt( 3, nOrder );
                daoUtil.executeUpdate( );
            }
        }
    }
}
