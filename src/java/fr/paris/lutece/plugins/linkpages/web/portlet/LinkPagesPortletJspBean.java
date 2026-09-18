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
package fr.paris.lutece.plugins.linkpages.web.portlet;

import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortlet;
import fr.paris.lutece.plugins.linkpages.business.portlet.LinkPagesPortletHome;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.page.PageResourceIdService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;


/**
 * Provides the user interface to manage LinkPages portlet
 */
@RequestScoped
@Named
public class LinkPagesPortletJspBean extends PortletJspBean
{

    private static final String MARK_COMBO_LINKPAGES = "combo_linkpages";
    private static final String MARK_LINKPAGE_ORDER = "linkpage_order";
    private static final String MARK_COMBO_LINKPAGES_ORDER = "combo_linkpages_order";
    private final static String MARK_LINKPAGE_ID = "linkpage_id";
    private final static String MARK_LINKPAGE_NAME = "linkpage_name";
    private final static String MARK_LINKPAGE_DESCRIPTION = "linkpage_description";
    private final static String MARK_NEW_LINKPAGE = "new_linkpage";
    private static final String MARK_PORTLET_ID = "portlet_id";
    private static final String MESSAGE_PORTLET_TYPE_NOT_FOUND = "linkpages.message.portletTypeNotFound";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "linkpages.message.portletNotFound";
    private static final String MESSAGE_INVALID_TOKEN = "linkpages.message.invalidToken";
    private static final String ACTION_CREATE_PORTLET = "linkpages.createPortlet";
    private static final String ACTION_MODIFY_PORTLET = "linkpages.modifyPortlet";
    private static final String ACTION_SELECT_LINKPAGE = "linkpages.selectLinkPage";
    private static final String ACTION_UNSELECT_LINKPAGE = "linkpages.unselectLinkPage";
    private static final String ACTION_MODIFY_ORDER = "linkpages.modifyOrderLinkPage";
    private static final String MARK_TOKEN_SELECT = "token_select";
    private static final String MARK_TOKEN_ORDER = "token_order";
    private static final String MARK_TOKEN_UNSELECT = "token_unselect";
    private static final String MARK_LINKPAGES_LIST = "linkpages_list";
    private static final String MARK_PAGE_ID = "page_id";

    private static final String PARAMETER_LINKPAGE = "linkpage";
    private static final String PARAMETER_LINKPAGE_ORDER = "linkpage_order";

    private static final String TEMPLATE_LINKPAGES_LIST = "admin/plugins/linkpages/linkpages_list.html";

    private static final String MESSAGE_LINKPAGE_NOT_EXIST = "linkpages.message.mandatory.linkpageNotExisted";
    private static final String MESSAGE_PORTLET_LINK_PAGE_SELECTED = "linkpages.message.portlet.linkpageAlreadySelected";

    private static final String JSP_DO_MODIFY_PORTLET = "../../DoModifyPortlet.jsp";

    @Inject
    private IPageService _pageService;


    /**
     * Returns portlet's properties prefix
     *
     * @return prefix
     */
    public String getPropertiesPrefix(  )
    {
        return "portlet.link.pages";
    }

    /**
     * Returns the Download portlet creation form
     *
     * @param request The http request
     * @return The HTML form
     */
    public String getCreate( HttpServletRequest request )
    {
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        if ( StringUtils.isEmpty( strPortletTypeId ) || PortletTypeHome.findByPrimaryKey( strPortletTypeId ) == null
                || PortletTypeHome.findByPrimaryKey( strPortletTypeId ).getDoCreateUrl( ) == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_TYPE_NOT_FOUND, getLocale( ) );
        }

        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        HashMap<String, Object> model = new HashMap<>( );
        ReferenceList linkPageAuthorized = new ReferenceList(  );
        ReferenceList linkPage = LinkPagesPortletHome.getLinkPagesList(  );

        for ( ReferenceItem item : linkPage )
        {
            if ( _pageService.isAuthorizedAdminPage( Integer.parseInt( item.getCode(  ) ),
                    PageResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
            {
                linkPageAuthorized.add( item );
            }
        }

        model.put( MARK_COMBO_LINKPAGES, linkPageAuthorized );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_CREATE_PORTLET ) );

        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType , model);

        return template.getHtml(  );
    }

    /**
     * Returns the modified portlet in HTML form
     *
     * @param request The http request
     * @return The HTML form
     */
    public String getModify( HttpServletRequest request )
    {
        LinkPagesPortlet portlet = findPortlet( request );

        if ( portlet == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) );
        }

        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );
        int nIdPortlet = portlet.getId( );
        int nPageId = portlet.getPageId(  );

        HashMap<String, Object> model = new HashMap<>(  );
        model.put( MARK_LINKPAGES_LIST, getLinkPagesInPortletList( request, nIdPortlet ) );
        model.put( MARK_PORTLET_ID, strIdPortlet );
        model.put( MARK_PAGE_ID, nPageId );

        int nMax = LinkPagesPortletHome.getMaxOrder( nIdPortlet );
        nMax = nMax + 1;
        model.put( MARK_LINKPAGE_ORDER, Integer.toString( nMax ) );
        model.put( MARK_COMBO_LINKPAGES_ORDER, getNewLinkPageOrdersList( nIdPortlet ) );

        ReferenceList linkPageAuthorized = new ReferenceList(  );
        ReferenceList linkPage = LinkPagesPortletHome.getLinkPagesList(  );

        for ( ReferenceItem item : linkPage )
        {
            if ( _pageService.isAuthorizedAdminPage( Integer.parseInt( item.getCode(  ) ),
                        PageResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
            {
                linkPageAuthorized.add( item );
            }
        }

        model.put( MARK_COMBO_LINKPAGES, linkPageAuthorized );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_PORTLET ) );
        model.put( MARK_TOKEN_SELECT, getSecurityTokenService( ).getToken( request, ACTION_SELECT_LINKPAGE ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml(  );
    }

    /**
     * Process portlet's creation
     *
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doCreate( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );

        if ( StringUtils.isEmpty( strIdPage ) || !StringUtils.isNumeric( strIdPage ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        LinkPagesPortlet portlet = new LinkPagesPortlet(  );
        int nIdPage = Integer.parseInt( strIdPage );

        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nIdPage );

        LinkPagesPortletHome.getInstance(  ).create( portlet );

        String strLinkPageId = request.getParameter( PARAMETER_LINKPAGE );

        if ( ( strLinkPageId == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_LINKPAGE_NOT_EXIST, AdminMessage.TYPE_ERROR );
        }

        int nLinkPageId = Integer.parseInt( strLinkPageId );

        LinkPagesPortletHome.insertLinkPage( portlet.getId(), nLinkPageId, 1 );

        return JSP_DO_MODIFY_PORTLET + "?" + PARAMETER_PORTLET_ID + "=" + portlet.getId();
    }

    /**
     * Process portlet's modification
     *
     * @param request The http request
     * @return Management's Url
     */
    public String doModify( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        LinkPagesPortlet portlet = findPortlet( request );

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.update(  );

        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
    * Returns an orders list for a new linkpage in a specified portlet
    *
    * @param nPortletId The identifier of the portlet
    * @return A list of orders
    */
    private ReferenceList getNewLinkPageOrdersList( int nPortletId )
    {
        int nMax = LinkPagesPortletHome.getMaxOrder( nPortletId );
        ReferenceList list = new ReferenceList(  );

        for ( int i = 1; i < ( nMax + 2 ); i++ )
        {
            list.addItem( i, Integer.toString( i ) );
        }

        return list;
    }

    /**
     * Returns The list of linkpages wich belong to a specified portlet
     *
     * @param request The http request
     * @param nPortletId The identifier of the portlet
     * @return A list of linkpages
     */
    private String getLinkPagesInPortletList( HttpServletRequest request, int nPortletId )
    {
        StringBuilder strLinkPagesList = new StringBuilder(  );

        for ( Page page : LinkPagesPortletHome.getLinkPagesInPortletList( nPortletId ) )
        {
            if ( _pageService.isAuthorizedAdminPage( page.getId(  ), PageResourceIdService.PERMISSION_VIEW,
                        getUser(  ) ) )
            {
                HashMap<String, Object> model = new HashMap<>(  );
                int nIdPage = page.getId(  );
                model.put( MARK_LINKPAGE_ID, page.getId(  ) );
                model.put( MARK_LINKPAGE_NAME, page.getName(  ) );
                model.put( MARK_LINKPAGE_DESCRIPTION, page.getDescription(  ) );
                model.put( MARK_NEW_LINKPAGE, "0" );
                model.put( MARK_COMBO_LINKPAGES_ORDER, getOrdersList( nPortletId ) );
                model.put( MARK_PORTLET_ID, nPortletId );
                model.put( MARK_TOKEN_ORDER, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_ORDER ) );
                model.put( MARK_TOKEN_UNSELECT, getSecurityTokenService( ).getToken( request, ACTION_UNSELECT_LINKPAGE ) );

                Integer nOrderLinkPage = Integer.valueOf( LinkPagesPortletHome.getLinkPageOrder( nPortletId, page.getId(  ) ) );
                model.put( MARK_LINKPAGE_ORDER, nOrderLinkPage.toString(  ) );

                strLinkPagesList.append( AppTemplateService.getTemplate( TEMPLATE_LINKPAGES_LIST, getLocale(  ), model )
                                                           .getHtml(  ) );
            }
        }

        return strLinkPagesList.toString(  );
    }

    /**
     * Returns an orders list
     *
     * @param nPortletId The identifier of the portlet
     * @return A list of orders
     */
    private ReferenceList getOrdersList( int nPortletId )
    {
        int nMax = LinkPagesPortletHome.getMaxOrder( nPortletId );
        ReferenceList list = new ReferenceList(  );

        for ( int i = 1; i < ( nMax + 1 ); i++ )
        {
            list.addItem( i, Integer.toString( i ) );
        }

        return list;
    }

    /**
     * Process link pages order modification
     *
     * @param request request
     * @return Portlet's modification url
     */
    public String doModifyOrderLinkPage( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_ORDER ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strLinkPageId = request.getParameter( PARAMETER_PAGE_ID );

        if ( !StringUtils.isNumeric( strPortletId ) || !StringUtils.isNumeric( strLinkPageId ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nPortletId = Integer.parseInt( strPortletId );
        int nLinkPageId = Integer.parseInt( strLinkPageId );
        int nOldOrder = LinkPagesPortletHome.getLinkPageOrder( nPortletId, nLinkPageId );
        String strOrder = request.getParameter( PARAMETER_LINKPAGE_ORDER );
        int nOrder = Integer.parseInt( strOrder );

        if ( nOrder < nOldOrder )
        {
            for ( int i = nOldOrder - 1; i > ( nOrder - 1 ); i-- )
            {
                int nIdTemp = LinkPagesPortletHome.getLinkPageIdByOrder( nPortletId, i );
                LinkPagesPortletHome.updateLinkPageOrder( i + 1, nPortletId, nIdTemp );
            }

            LinkPagesPortletHome.updateLinkPageOrder( nOrder, nPortletId, nLinkPageId );
        }
        else if ( nOrder > nOldOrder )
        {
            for ( int i = nOldOrder; i < ( nOrder + 1 ); i++ )
            {
                int nIdTemp = LinkPagesPortletHome.getLinkPageIdByOrder( nPortletId, i );
                LinkPagesPortletHome.updateLinkPageOrder( i - 1, nPortletId, nIdTemp );
            }

            LinkPagesPortletHome.updateLinkPageOrder( nOrder, nPortletId, nLinkPageId );
        }

        return JSP_DO_MODIFY_PORTLET + "?" + PARAMETER_PORTLET_ID + "=" + nPortletId;
    }

    /**
     * Process link page's unselecting
     *
     * @param request request
     * @return Portlet's modification url
     */
    public String doUnselectLinkPage( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_UNSELECT_LINKPAGE ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        String strLinkPageId = request.getParameter( PARAMETER_PAGE_ID );
        int nLinkPageId = Integer.parseInt( strLinkPageId );
        int nOrder = LinkPagesPortletHome.getLinkPageOrder( nPortletId, nLinkPageId );
        int nMax = LinkPagesPortletHome.getMaxOrder( nPortletId );

        LinkPagesPortletHome.removeLinkPage( nPortletId, nLinkPageId );

        for ( int i = nOrder + 1; i < ( nMax + 1 ); i++ )
        {
            int nLinkPageIdTemp = LinkPagesPortletHome.getLinkPageIdByOrder( nPortletId, i );
            LinkPagesPortletHome.updateLinkPageOrder( i - 1, nPortletId, nLinkPageIdTemp );
        }

        return JSP_DO_MODIFY_PORTLET + "?" + PARAMETER_PORTLET_ID + "=" + nPortletId;
    }

    /**
     * Process link page's selecting
     *
     * @param request request
     * @return Portlet's modification url
     */
    public String doSelectLinkPage( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_SELECT_LINKPAGE ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        String strOrder = request.getParameter( PARAMETER_LINKPAGE_ORDER );
        int nOrder = Integer.parseInt( strOrder );

        String strLinkPageId = request.getParameter( PARAMETER_LINKPAGE );

        if ( ( strLinkPageId == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_LINKPAGE_NOT_EXIST, AdminMessage.TYPE_ERROR );
        }

        int nLinkPageId = Integer.parseInt( strLinkPageId );

        if ( LinkPagesPortletHome.testDuplicate( nPortletId, nLinkPageId ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_LINK_PAGE_SELECTED,
                AdminMessage.TYPE_ERROR );
        }

        int nMax = LinkPagesPortletHome.getMaxOrder( nPortletId );

        for ( int i = nOrder; i < ( nMax + 1 ); i++ )
        {
            int nLinkPageIdTemp = LinkPagesPortletHome.getLinkPageIdByOrder( nPortletId, i );
            LinkPagesPortletHome.updateLinkPageOrder( i + 1, nPortletId, nLinkPageIdTemp );
        }

        LinkPagesPortletHome.insertLinkPage( nPortletId, nLinkPageId, nOrder );

        return JSP_DO_MODIFY_PORTLET + "?" + PARAMETER_PORTLET_ID + "=" + nPortletId;
    }

    /**
     * Process the selection of all link pages
     *
     * @param request request
     * @return Portlet's modification url
     */
    public String doSelectAllLinkPage( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );

        LinkPagesPortlet portlet = (LinkPagesPortlet) PortletHome.findByPrimaryKey( nPortletId );

        int nMax = LinkPagesPortletHome.getMaxOrder( nPortletId );

        int nOrder = nMax + 1;

        Collection<Page> linkPagesList = PageHome.getChildPages( portlet.getPageId(  ) );

        Iterator<Page> i = linkPagesList.iterator(  );

        while ( i.hasNext(  ) )
        {
            Page linkPage = i.next(  );

            if ( !LinkPagesPortletHome.testDuplicate( nPortletId, linkPage.getId(  ) ) )
            {
                LinkPagesPortletHome.insertLinkPage( nPortletId, linkPage.getId(  ), nOrder );
                nOrder++;
            }
        }

        return JSP_DO_MODIFY_PORTLET + "?" + PARAMETER_PORTLET_ID + "=" + nPortletId;
    }

    /**
     * Finds the portlet named by the request, without throwing on a bad identifier.
     *
     * PortletHome.findByPrimaryKey of the core dereferences the row it loaded without checking it exists, so an
     * unknown identifier raises a NullPointerException there rather than returning null.
     *
     * @param request the HTTP request
     * @return the portlet, null when the identifier is missing, malformed or unknown
     */
    private LinkPagesPortlet findPortlet( HttpServletRequest request )
    {
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );

        if ( StringUtils.isEmpty( strIdPortlet ) || !StringUtils.isNumeric( strIdPortlet ) )
        {
            return null;
        }

        Portlet portlet;

        try
        {
            portlet = PortletHome.findByPrimaryKey( Integer.parseInt( strIdPortlet ) );
        }
        catch( NullPointerException e )
        {
            AppLogService.info( "Unknown portlet {}", strIdPortlet );
            return null;
        }

        return portlet instanceof LinkPagesPortlet ? (LinkPagesPortlet) portlet : null;
    }
}
