/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.linkpages.web.portlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortlet;
import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortletHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The order of the pages of a link pages portlet when a page is inserted
 */
public class LinkPagesPortletOrderTest extends LuteceTestCase
{
    private LinkPagesPortlet _portlet;
    private static final int PAGE_A = 987651;
    private static final int PAGE_B = 987652;
    private static final int PAGE_C = 987653;

    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _portlet = new LinkPagesPortlet( );
        _portlet.setName( "LinkPagesPortletOrderTest" );
        // the root page of the test database
        _portlet.setPageId( 1 );
        _portlet.setStyleId( 0 );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        LinkPagesPortletHome.getInstance( ).create( _portlet );
    }

    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            for ( int nPage : new int [ ] { PAGE_A, PAGE_B, PAGE_C } )
            {
                LinkPagesPortletHome.removeLinkPage( _portlet.getId( ), nPage );
            }
            LinkPagesPortletHome.getInstance( ).remove( _portlet );
        }
        super.tearDown( );
    }

    /**
     * Inserting a page at the first rank of [A, B] gives [C, A, B]
     */
    @Test
    public void testInsertAtFirstRankKeepsTheOrder( )
    {
        LinkPagesPortletHome.insertLinkPage( _portlet.getId( ), PAGE_A, 1 );
        LinkPagesPortletHome.insertLinkPage( _portlet.getId( ), PAGE_B, 2 );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strToken = CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, "linkpages.selectLinkPage" );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN, strToken );
        request.addParameter( "portlet_id", String.valueOf( _portlet.getId( ) ) );
        request.addParameter( "linkpage", String.valueOf( PAGE_C ) );
        request.addParameter( "linkpage_order", "1" );
        CDI.current( ).select( LinkPagesPortletJspBean.class ).get( ).doSelectLinkPage( request );

        assertEquals( 1, LinkPagesPortletHome.getLinkPageOrder( _portlet.getId( ), PAGE_C ), "order of C" );
        assertEquals( 2, LinkPagesPortletHome.getLinkPageOrder( _portlet.getId( ), PAGE_A ), "order of A" );
        assertEquals( 3, LinkPagesPortletHome.getLinkPageOrder( _portlet.getId( ), PAGE_B ), "order of B" );
    }

    /**
     * Removing all the pages of a portlet empties it
     */
    @Test
    public void testRemoveAllLinkPages( )
    {
        LinkPagesPortletHome.insertLinkPage( _portlet.getId( ), PAGE_A, 1 );
        LinkPagesPortletHome.insertLinkPage( _portlet.getId( ), PAGE_B, 2 );

        LinkPagesPortletHome.removeAllLinkPages( _portlet.getId( ) );

        assertTrue( !LinkPagesPortletHome.testDuplicate( _portlet.getId( ), PAGE_A ), "page A removed" );
        assertTrue( !LinkPagesPortletHome.testDuplicate( _portlet.getId( ), PAGE_B ), "page B removed" );
    }
}
