/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortlet;
import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortletHome;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Renders the link pages screens under HTML auto-escaping (LUT-33153): the rows of the management list, rendered by the
 * application, are written as HTML, and the page name and description are written as text.
 */
public class LinkPagesPortletRenderTest extends LuteceTestCase
{
    private static final String PAGE_NAME = "Horaires <b>\"l'accueil\"</b>";
    private static final String PAGE_NAME_ESCAPED = "Horaires &lt;b&gt;&quot;l&#39;accueil&quot;&lt;/b&gt;";
    private static final String PAGE_DESCRIPTION = "Accueil & <i>information</i>";
    private static final String PAGE_DESCRIPTION_ESCAPED = "Accueil &amp; &lt;i&gt;information&lt;/i&gt;";

    private LinkPagesPortlet _portlet;
    private Page _page;

    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _page = new Page( );
        _page.setParentPageId( PortalService.getRootPageId( ) );
        _page.setPageTemplateId( 1 );
        _page.setName( PAGE_NAME );
        _page.setDescription( PAGE_DESCRIPTION );
        _page.setRole( Page.ROLE_NONE );
        _page.setDateUpdate( new Timestamp( System.currentTimeMillis( ) ) );
        PageHome.create( _page );

        _portlet = new LinkPagesPortlet( );
        _portlet.setName( "LinkPagesPortletRenderTest" );
        _portlet.setPageId( PortalService.getRootPageId( ) );
        _portlet.setStyleId( 0 );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        LinkPagesPortletHome.getInstance( ).create( _portlet );
        LinkPagesPortletHome.insertLinkPage( _portlet.getId( ), _page.getId( ), 1 );
    }

    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            LinkPagesPortletHome.removeAllLinkPages( _portlet.getId( ) );
            LinkPagesPortletHome.getInstance( ).remove( _portlet );
        }
        if ( _page != null )
        {
            PageHome.remove( _page.getId( ) );
        }
        super.tearDown( );
    }

    /**
     * The modification screen writes the rows of the linked pages as HTML, with the page name and description as text and
     * the action URL escaped once.
     *
     * @throws AccessDeniedException
     *             if the test user is refused
     */
    @Test
    public void testModifyWritesTheRowsAsHtml( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), PortletJspBean.RIGHT_MANAGE_ADMIN_SITE );
        LinkPagesPortletJspBean bean = CDI.current( ).select( LinkPagesPortletJspBean.class ).get( );
        bean.init( request, PortletJspBean.RIGHT_MANAGE_ADMIN_SITE );
        request.addParameter( "portlet_id", String.valueOf( _portlet.getId( ) ) );

        String strHtml = bean.getModify( request );

        assertTrue( strHtml.contains( PAGE_NAME_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( PAGE_DESCRIPTION_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( "DoUnselectLinkPage.jsp?portlet_id=" + _portlet.getId( ) + "&amp;page_id=" + _page.getId( ) + "&amp;token=" ),
                strHtml );
        assertTrue( strHtml.contains( "name=\"page_id\"" ) || strHtml.contains( "name='page_id'" ), strHtml );
        assertFalse( strHtml.contains( "&lt;tr" ), strHtml );
        assertFalse( strHtml.contains( "&amp;amp;" ), strHtml );
        assertFalse( strHtml.contains( PAGE_NAME ), strHtml );
        // the page name heads its row and names the row actions
        assertTrue( strHtml.contains( "scope=\"row\"" ), strHtml );
        assertTrue( strHtml.contains( " " + PAGE_NAME_ESCAPED + " " ) || strHtml.contains( " " + PAGE_NAME_ESCAPED + "\"" ), strHtml );
    }

    /**
     * The portlet writes the name and the description of a linked page as text, and its link with the page identifier.
     */
    @Test
    public void testPortletWritesThePageAsText( )
    {
        String strHtml = _portlet.getHtmlContent( new MockHttpServletRequest( ) );

        assertTrue( strHtml.contains( PAGE_NAME_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( PAGE_DESCRIPTION_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( "page_id=" + _page.getId( ) + "\"" ) || strHtml.contains( "page_id=" + _page.getId( ) + "'" ), strHtml );
        assertFalse( strHtml.contains( PAGE_NAME ), strHtml );
        assertFalse( strHtml.contains( "<i>" ), strHtml );
    }
}
