/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.shore.gui.views;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.bbn.openmap.BufferedLayerMapBean;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.MapBean;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.menuitems.AisIntendedRouteToggle;
import dk.dma.epd.shore.gui.views.menuitems.GeneralHideIntendedRoutes;
import dk.dma.epd.shore.gui.views.menuitems.GeneralNewRoute;
import dk.dma.epd.shore.gui.views.menuitems.GeneralShowIntendedRoutes;
import dk.dma.epd.shore.gui.views.menuitems.IMapMenuAction;
import dk.dma.epd.shore.gui.views.menuitems.MsiAcknowledge;
import dk.dma.epd.shore.gui.views.menuitems.MsiDetails;
import dk.dma.epd.shore.gui.views.menuitems.MsiZoomTo;
import dk.dma.epd.shore.gui.views.menuitems.RouteAppendWaypoint;
import dk.dma.epd.shore.gui.views.menuitems.RouteCopy;
import dk.dma.epd.shore.gui.views.menuitems.RouteDelete;
import dk.dma.epd.shore.gui.views.menuitems.RouteEditEndRoute;
import dk.dma.epd.shore.gui.views.menuitems.RouteHide;
import dk.dma.epd.shore.gui.views.menuitems.RouteLegInsertWaypoint;
import dk.dma.epd.shore.gui.views.menuitems.RouteMetocProperties;
import dk.dma.epd.shore.gui.views.menuitems.RouteProperties;
import dk.dma.epd.shore.gui.views.menuitems.RouteRequestMetoc;
import dk.dma.epd.shore.gui.views.menuitems.RouteReverse;
import dk.dma.epd.shore.gui.views.menuitems.RouteShowMetocToggle;
import dk.dma.epd.shore.gui.views.menuitems.RouteWaypointActivateToggle;
import dk.dma.epd.shore.gui.views.menuitems.RouteWaypointDelete;
import dk.dma.epd.shore.gui.views.menuitems.SetRouteExchangeAIS;
import dk.dma.epd.shore.gui.views.menuitems.SetRouteExchangeRoute;
import dk.dma.epd.shore.layers.ais.AisLayer;
import dk.dma.epd.shore.layers.msi.MsiLayer;
import dk.dma.epd.shore.msi.MsiHandler;
import dk.dma.epd.shore.route.RouteManager;


/**
 * Right click map menu
 */
public class MapMenu extends JPopupMenu implements ActionListener, LightMapHandlerChild, BeanContextChild,
        BeanContextMembershipListener {

    private static final long serialVersionUID = 1L;

    private IMapMenuAction action;
    private MsiHandler msiHandler;

    // menu items
    private GeneralHideIntendedRoutes hideIntendedRoutes;
    private GeneralShowIntendedRoutes showIntendedRoutes;
    private GeneralNewRoute newRoute;
    private JMenu scaleMenu;
    private AisIntendedRouteToggle aisIntendedRouteToggle;

//    private NogoRequest nogoRequest;
    private MsiAcknowledge msiAcknowledge;
    private MsiDetails msiDetails;
    private MsiZoomTo msiZoomTo;

    private RouteAppendWaypoint routeAppendWaypoint;
    private RouteHide routeHide;
    private RouteCopy routeCopy;
    private RouteReverse routeReverse;
    private RouteDelete routeDelete;
    private RouteProperties routeProperties;
    private RouteMetocProperties routeMetocProperties;
    private RouteRequestMetoc routeRequestMetoc;
    private RouteShowMetocToggle routeShowMetocToggle;
    private RouteLegInsertWaypoint routeLegInsertWaypoint;
    private RouteWaypointActivateToggle routeWaypointActivateToggle;
    private RouteWaypointDelete routeWaypointDelete;
    private RouteEditEndRoute routeEditEndRoute;
    private SetRouteExchangeAIS setRouteExchangeAIS;
    private SetRouteExchangeRoute setRouteExchangeRoute;

    // bean context
    protected String propertyPrefix;
    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);
    protected boolean isolated;
    private RouteManager routeManager;
    private Route route;
//    private SendRouteDialog sendRouteDialog;

    //Route suggest?
//    private RouteSuggestionDialog routeSuggestionDialog;


    private MapBean mapBean;
    private Map<Integer, String> map;
//    private NewRouteContainerLayer newRouteLayer;
    private AisLayer aisLayer;
    private AisHandler aisHandler;

//    private NogoHandler nogoHandler;


    public MapMenu() {
        super();

        // general menu items
        hideIntendedRoutes = new GeneralHideIntendedRoutes("Hide all intended routes");
        hideIntendedRoutes.addActionListener(this);
        showIntendedRoutes = new GeneralShowIntendedRoutes("Show all intended routes");
        showIntendedRoutes.addActionListener(this);
        newRoute = new GeneralNewRoute("Add new route");
        newRoute.addActionListener(this);

        scaleMenu = new JMenu("Scale");

        // using treemap so scale levels are always sorted
        map = new TreeMap<Integer, String>();

        // ais menu items
        aisIntendedRouteToggle = new AisIntendedRouteToggle();
        aisIntendedRouteToggle.addActionListener(this);

        // msi menu items
        msiDetails = new MsiDetails("Show MSI details");
        msiDetails.addActionListener(this);
        msiAcknowledge = new MsiAcknowledge("Acknowledge MSI");
        msiAcknowledge.addActionListener(this);
        msiZoomTo = new MsiZoomTo("Zoom to MSI");
        msiZoomTo.addActionListener(this);

        // route general items
        setRouteExchangeRoute = new SetRouteExchangeRoute("Send Route");
        setRouteExchangeRoute.addActionListener(this);

        routeHide = new RouteHide("Hide route");
        routeHide.addActionListener(this);

        routeCopy = new RouteCopy("Copy route");
        routeCopy.addActionListener(this);

        routeReverse = new RouteReverse("Reverse route");
        routeReverse.addActionListener(this);

        routeDelete = new RouteDelete("Delete route");
        routeDelete.addActionListener(this);



        routeRequestMetoc = new RouteRequestMetoc("Request METOC");
        routeRequestMetoc.addActionListener(this);
        routeShowMetocToggle = new RouteShowMetocToggle();
        routeShowMetocToggle.addActionListener(this);
        routeProperties = new RouteProperties("Route properties");
        routeProperties.addActionListener(this);
        routeMetocProperties = new RouteMetocProperties("METOC properties");
        routeMetocProperties.addActionListener(this);
        routeAppendWaypoint = new RouteAppendWaypoint("Append waypoint");
        routeAppendWaypoint.addActionListener(this);

        // route leg menu
        routeLegInsertWaypoint = new RouteLegInsertWaypoint("Insert waypoint here");
        routeLegInsertWaypoint.addActionListener(this);

        // route waypoint menu
        routeWaypointActivateToggle = new RouteWaypointActivateToggle("Activate waypoint");
        routeWaypointActivateToggle.addActionListener(this);
        routeWaypointDelete = new RouteWaypointDelete("Delete waypoint");
        routeWaypointDelete.addActionListener(this);

        // route edit menu
        routeEditEndRoute = new RouteEditEndRoute("End route");
        routeEditEndRoute.addActionListener(this);

        // ais menu items
        setRouteExchangeAIS = new SetRouteExchangeAIS("Send Route to vessel");
        setRouteExchangeAIS.addActionListener(this);

    }

    /**
     * Adds the general menu to the right-click menu. Remember to always add this first, when creating specific menus.
     * @param alone TODO
     */
    public void generalMenu(boolean alone){

        scaleMenu.removeAll();

        // clear previous map scales
        map.clear();
        // Initialize the scale levels, and give them name (this should be done from settings later...)
        map.put(5000,     "Berthing      (1 : 5.000)");
        map.put(10000,    "Harbour       (1 : 10.000)");
        map.put(70000,    "Approach      (1 : 70.000)");
        map.put(300000,   "Coastal       (1 : 300.000)");
        map.put(2000000,  "Overview      (1 : 2.000.000)");
        map.put(20000000, "Ocean         (1 : 20.000.000)");
        // put current scale level
        Integer currentScale = (int) mapBean.getScale();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        map.put(currentScale, "Current scale (1 : " + formatter.format(currentScale) + ")");

        // Iterate through the treemap, adding the menuitems and assigning actions
        Set<Integer> keys = map.keySet();
        for (final Integer key : keys) {
            String value = map.get(key);
            JMenuItem menuItem = new JMenuItem(value);
            menuItem.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    mapBean.setScale(key);
                }
            });
            scaleMenu.add(menuItem);
        }

        hideIntendedRoutes.setAisHandler(aisHandler);
        showIntendedRoutes.setAisHandler(aisHandler);

        newRoute.setToolBar(EPDShore.getMainFrame().getToolbar());

//        newRoute.setMouseDelegator(mouseDelegator);
//        newRoute.setMainFrame(mainFrame);

        if(alone){
            removeAll();
            add(hideIntendedRoutes);
            add(showIntendedRoutes);
            add(newRoute);
            add(scaleMenu);
            return;
        }

        addSeparator();
        add(hideIntendedRoutes);
        add(scaleMenu);
    }


    /**
     * Builds ais target menu
     */
    public void aisMenu(VesselTarget vesselTarget){
        removeAll();

        setRouteExchangeAIS.setMSSI(vesselTarget.getMmsi());
        setRouteExchangeAIS.setSendRouteDialog(EPDShore.getMainFrame().getSendRouteDialog());

        add(setRouteExchangeAIS);

        aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget.getSettings());
        aisIntendedRouteToggle.setAisLayer(aisLayer);
        aisIntendedRouteToggle.setVesselTarget(vesselTarget);

        if(vesselTarget.getAisRouteData() != null && vesselTarget.getAisRouteData().hasRoute()){
            aisIntendedRouteToggle.setEnabled(true);
        } else {
            aisIntendedRouteToggle.setEnabled(false);
        }
        if(vesselTarget.getSettings().isShowRoute()){
            aisIntendedRouteToggle.setText("Hide intended route");
        } else {
            aisIntendedRouteToggle.setText("Show intended route");
        }
        add(aisIntendedRouteToggle);

        generalMenu(false);
    }

    /**
     * Options for suggested route
     */
    public void aisSuggestedRouteMenu(VesselTarget vesselTarget) {
        removeAll();

        aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget.getSettings());
        aisIntendedRouteToggle.setAisLayer(aisLayer);
        aisIntendedRouteToggle.setVesselTarget(vesselTarget);

        if(vesselTarget.getAisRouteData() != null && vesselTarget.getAisRouteData().hasRoute()){
            aisIntendedRouteToggle.setEnabled(true);
        } else {
            aisIntendedRouteToggle.setEnabled(false);
        }
        if(vesselTarget.getSettings().isShowRoute()){
            aisIntendedRouteToggle.setText("Hide intended route");
        } else {
            aisIntendedRouteToggle.setText("Show intended route");
        }
        add(aisIntendedRouteToggle);

        generalMenu(false);
    }

    /**
     * Builds the maritime safety information menu
     * @param selectedGraphic The selected graphic (containing the msi message)
     */
    public void msiMenu(MsiSymbolGraphic selectedGraphic){
        removeAll();

        msiDetails.setMsiMessage(selectedGraphic.getMsiMessage());
        msiDetails.setNotCenter(EPDShore.getMainFrame().getNotificationCenter());

        add(msiDetails);

        Boolean isAcknowledged = msiHandler.isAcknowledged(selectedGraphic.getMsiMessage().getMessageId());
        msiAcknowledge.setMsiHandler(msiHandler);
        msiAcknowledge.setEnabled(!isAcknowledged);
        msiAcknowledge.setMsiMessage(selectedGraphic.getMsiMessage());
        add(msiAcknowledge);

        generalMenu(false);
    }

    public void msiDirectionalMenu(MsiDirectionalIcon selectedGraphic, MsiLayer msiLayer) {
        removeAll();

        msiDetails.setMsiMessage(selectedGraphic.getMessage().msiMessage);
        add(msiDetails);

        msiZoomTo.setMsiLayer(msiLayer);
        msiZoomTo.setMsiMessageExtended(selectedGraphic.getMessage());
        add(msiZoomTo);

        generalMenu(false);
    }

    public void generalRouteMenu(int routeIndex){

        routeManager = EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager();
        route = routeManager.getRoute(routeIndex);

        routeAppendWaypoint.setRouteManager(routeManager);
        routeAppendWaypoint.setRouteIndex(routeIndex);
        add(routeAppendWaypoint);

        addSeparator();

        setRouteExchangeRoute.setRoute(route);
        setRouteExchangeRoute.setSendRouteDialog(EPDShore.getMainFrame().getSendRouteDialog());
        add(setRouteExchangeRoute);


        routeHide.setRouteManager(routeManager);
        routeHide.setRouteIndex(routeIndex);
        add(routeHide);

        routeDelete.setRouteManager(routeManager);
        routeDelete.setRouteIndex(routeIndex);
        add(routeDelete);

        routeCopy.setRouteManager(routeManager);
        routeCopy.setRouteIndex(routeIndex);
        add(routeCopy);

        routeReverse.setRouteManager(routeManager);
        routeReverse.setRouteIndex(routeIndex);
        add(routeReverse);



        routeRequestMetoc.setRouteManager(routeManager);
        routeRequestMetoc.setRouteIndex(routeIndex);
        add(routeRequestMetoc);

        if(routeManager.hasMetoc(route)){
            routeShowMetocToggle.setEnabled(true);
        } else {
            routeShowMetocToggle.setEnabled(false);
        }

        if(route.getRouteMetocSettings().isShowRouteMetoc() && routeManager.hasMetoc(route)){
            routeShowMetocToggle.setText("Hide METOC");
        } else {
            routeShowMetocToggle.setText("Show METOC");
        }

        routeShowMetocToggle.setRouteManager(routeManager);
        routeShowMetocToggle.setRouteIndex(routeIndex);
        add(routeShowMetocToggle);

        routeMetocProperties.setRouteManager(routeManager);
        routeMetocProperties.setRouteIndex(routeIndex);
        add(routeMetocProperties);

        routeProperties.setRouteManager(routeManager);
        routeProperties.setRouteIndex(routeIndex);
        add(routeProperties);

        generalMenu(false);
    }

    public void routeLegMenu(int routeIndex, RouteLeg routeLeg, Point point){
        routeManager = EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager();

        removeAll();

        if(routeManager.getActiveRouteIndex() == routeIndex){
            routeLegInsertWaypoint.setEnabled(false);
        } else {
            routeLegInsertWaypoint.setEnabled(true);
        }

        routeLegInsertWaypoint.setMapBean(mapBean);
        routeLegInsertWaypoint.setRouteManager(routeManager);
        routeLegInsertWaypoint.setRouteLeg(routeLeg);
        routeLegInsertWaypoint.setRouteIndex(routeIndex);
        routeLegInsertWaypoint.setPoint(point);

        add(routeLegInsertWaypoint);

        generalRouteMenu(routeIndex);
        //TODO: add leg specific items
    }

    public void routeWaypointMenu(int routeIndex, int routeWaypointIndex){
        routeManager = EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager();

        removeAll();

        routeWaypointDelete.setEnabled(true);



        routeWaypointDelete.setRouteWaypointIndex(routeWaypointIndex);
        routeWaypointDelete.setRouteIndex(routeIndex);
        routeWaypointDelete.setRouteManager(routeManager);
        add(routeWaypointDelete);

        generalRouteMenu(routeIndex);
    }

    public void routeEditMenu(){
        removeAll();
        routeManager = EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager();

        routeEditEndRoute.setToolBar(EPDShore.getMainFrame().getToolbar());

        add(routeEditEndRoute);


        generalMenu(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action = (IMapMenuAction) e.getSource();
        action.doAction();
    }

    // Allows MapMenu to be added to the MapHandler (eg. use the find and init)
    @Override
    public void findAndInit(Object obj) {
        if(obj instanceof MsiHandler){
            msiHandler = (MsiHandler) obj;
        }
        if(obj instanceof BufferedLayerMapBean){
            mapBean = (BufferedLayerMapBean) obj;
        }
        if(obj instanceof NewRouteContainerLayer){
//            newRouteLayer = (NewRouteContainerLayer) obj;
        }
        if(obj instanceof AisLayer){
            aisLayer = (AisLayer) obj;
        }
        if(obj instanceof AisHandler){
            aisHandler = (AisHandler) obj;
        }

    }

    public void findAndInit(Iterator<?> it) {
        while (it.hasNext()) {
            findAndInit(it.next());
        }
    }

    @Override
    public void findAndUndo(Object obj) {
    }

    @Override
    public void childrenAdded(BeanContextMembershipEvent bcme) {
        if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
            findAndInit(bcme.iterator());
        }
    }

    @Override
    public void childrenRemoved(BeanContextMembershipEvent bcme) {
        Iterator<?> it = bcme.iterator();
        while (it.hasNext()) {
            findAndUndo(it.next());
        }
    }

    @Override
    public BeanContext getBeanContext() {
        return beanContextChildSupport.getBeanContext();
    }

    @Override
    public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

        if (in_bc != null) {
            if (!isolated || beanContextChildSupport.getBeanContext() == null) {
                in_bc.addBeanContextMembershipListener(this);
                beanContextChildSupport.setBeanContext(in_bc);
                findAndInit(in_bc.iterator());
            }
        }
    }

    @Override
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
    }

    @Override
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
    }

}