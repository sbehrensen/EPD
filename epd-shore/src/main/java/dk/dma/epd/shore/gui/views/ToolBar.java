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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.utils.ToolItemGroup;

/**
 * Class for setting up the toolbar of the application
 *
 * @author Steffen D. Sommer (steffendsommer@gmail.com)
 */
public class ToolBar extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private Boolean locked = false;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel buttonPanel;
    private static int moveHandlerHeight = 18;
    private static int toolItemSize = 35;
    private static int toolItemColumns = 2;
    private static int buttonPanelOffset = 4;
    private ArrayList<ToolItemGroup> toolItemGroups = new ArrayList<ToolItemGroup>();
    public int width;
    public int height;
    private static int iconWidth = 16;
    private static int iconHeight = 16;
    private Border toolPaddingBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(83, 83, 83));
    private Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
            new Color(37, 37, 37), new Color(52, 52, 52));

    private boolean routeCreation;
    private final JLabel newRoute;
    private final ToolItemGroup routeToolItems;
    private MainFrame mainFrame;
    private final ToolItemGroup mapToolItems;

    // private MouseDelegator mouseDelegator;

    /**
     * Constructor for setting up the toolbar
     *
     * @param mainFrame
     *            reference to the mainframe
     */
    public ToolBar(final MainFrame mainFrame) {

        this.mainFrame = mainFrame;

        // Setup location
        this.setLocation(10 + moveHandlerHeight, 10);
        this.setVisible(true);
        this.setResizable(false);

        // Strip off window looks
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Toolbar", SwingConstants.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(toolItemSize * toolItemColumns, moveHandlerHeight));
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

        // Create the grid for the toolitems
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setBackground(new Color(83, 83, 83));

        // Setup toolitems (add here for more toolitems)
        // Tool group: Map tools
        mapToolItems = new ToolItemGroup();

        // Tool: Select
        final JLabel select = new JLabel(toolbarIcon("images/toolbar/select.png"));
        select.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setActiveToolItem(select, mapToolItems);

                for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                    mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(2);
                }
                mainFrame.setMouseMode(2);
            }
        });
        mapToolItems.addToolItem(select);

        // Tool: Drag
        final JLabel drag = new JLabel(toolbarIcon("images/toolbar/drag.png"));
        drag.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setActiveToolItem(drag, mapToolItems);

                for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                    mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(1);
                }
                mainFrame.setMouseMode(1);
            }
        });
        mapToolItems.addToolItem(drag);

        // Tool: Zoom
        final JLabel zoom = new JLabel(toolbarIcon("images/toolbar/zoom.png"));
        zoom.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setActiveToolItem(zoom, mapToolItems);

                for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                    mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(0);
                }
                mainFrame.setMouseMode(0);
            }
        });
        mapToolItems.addToolItem(zoom);

        // Set that the map tools only can have 1 active tool item at a time
        mapToolItems.setSingleEnable(true);

        // Set default active tool item for this group
        setActiveToolItem(select, mapToolItems);

        toolItemGroups.add(mapToolItems);

        // Tool group: Layer tools
        final ToolItemGroup layerToolItems = new ToolItemGroup();

        // Tool: WMS layer
        final JLabel wms = new JLabel(toolbarIcon("images/toolbar/wms_small.png"));
        wms.setName("wms");
        wms.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (mainFrame.isWmsLayerEnabled()) {
                    mainFrame.setWmsLayerEnabled(false);
                    for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                        // if(mainFrame.getMapWindows().get(i).getChartPanel().getWmsLayer().isVisible()){
                        mainFrame.getMapWindows().get(i).getChartPanel().getWmsLayer().setVisible(false);
                        mainFrame.getMapWindows().get(i).getChartPanel().getBgLayer().setVisible(true);
                    }
                    setInactiveToolItem(wms);

                } else {
                    mainFrame.setWmsLayerEnabled(true);
                    for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {

                        mainFrame.getMapWindows().get(i).getChartPanel().getWmsLayer().setVisible(true);
                        mainFrame.getMapWindows().get(i).getChartPanel().getBgLayer().setVisible(false);

                        setActiveToolItem(wms, layerToolItems);
                    }
                }
            }
        });

        layerToolItems.addToolItem(wms);
        if (EPDShore.getSettings().getGuiSettings().useWMS()) {
            setActiveToolItem(wms, layerToolItems);
        }

        // Tool: MSI layer
        final JLabel msi = new JLabel(toolbarIcon("images/toolbar/msi_symbol_16.png"));
        msi.setName("msi");
        msi.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                if (mainFrame.isMsiLayerEnabled()) {
                    for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                        mainFrame.setMSILayerEnabled(false);
                        mainFrame.getMapWindows().get(i).getChartPanel().getMsiLayer().setVisible(false);
                    }
                    setInactiveToolItem(msi);
                } else {
                    for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                        mainFrame.setMSILayerEnabled(true);
                        mainFrame.getMapWindows().get(i).getChartPanel().getMsiLayer().setVisible(true);
                    }
                    setActiveToolItem(msi, layerToolItems);
                }
            }
        });
        layerToolItems.addToolItem(msi);

        // Set that the layer tools can have more than 1 active tool item at a
        // time
        layerToolItems.setSingleEnable(false);

        // Set default active tool(s) for this group
        setActiveToolItem(msi, layerToolItems);

        toolItemGroups.add(layerToolItems);



        // Tool group: Route tools
        routeToolItems = new ToolItemGroup();

        // Tool: Routes
        final JLabel routes = new JLabel(toolbarIcon("images/toolbar/routes.png"));
        routes.setName("routes");
        routes.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setActiveToolItem(routes, routeToolItems);
            }

            public void mouseReleased(MouseEvent e) {
                setInactiveToolItem(routes);
                mainFrame.getRouteManagerDialog().setVisible(!mainFrame.getRouteManagerDialog().isVisible());
            }
        });
        routeToolItems.addToolItem(routes);



        // Tool: New route
        newRoute = new JLabel(toolbarIcon("images/toolbar/routes_new.png"));
        newRoute.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (routeCreation){
                    setInactiveToolItem(newRoute);
                }else{
                    setActiveToolItem(newRoute, routeToolItems);
                }
            }

            public void mouseReleased(MouseEvent e) {
                newRoute();
            }
        });


        routeToolItems.addToolItem(newRoute);

        // Set that the layer tools can have more than 1 active tool item at a
        // time
        routeToolItems.setSingleEnable(false);

        toolItemGroups.add(routeToolItems);


        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(buttonPanel, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
                45, 45, 45)));
        this.getContentPane().add(masterPanel);

        // And finally refresh the toolbar
        repaintToolbar();

    }

    public void newRoute(){

        if (routeCreation){
            setInactiveToolItem(newRoute);
        }else{
            setActiveToolItem(newRoute, routeToolItems);
        }

        if (routeCreation){

            //Deactivate
            routeCreation = false;

            for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(mainFrame.getMouseMode());
            }

            //Save route?
            endRoute();

            //Re activate the tool options

            for (int j = 0; j < mapToolItems.getToolItems().size(); j++) {
                JLabel label = mapToolItems.getToolItems().get(j);
                label.setEnabled(true);
            }

        }else{
            routeCreation = true;

            for (int i = 0; i < mainFrame.getMapWindows().size(); i++) {
                mainFrame.getMapWindows().get(i).getChartPanel().setMouseMode(3);
            }

            //Deactivate other map tools

            for (int j = 0; j < mapToolItems.getToolItems().size(); j++) {
                JLabel label = mapToolItems.getToolItems().get(j);
                label.setEnabled(false);
            }

        }
    }

    public void endRoute(){

        NewRouteContainerLayer newRouteLayer = EPDShore.getMainFrame().getActiveMapWindow().getChartPanel().getNewRouteContainerLayer();

        //Route saved
        if (newRouteLayer.getRoute().getWaypoints().size() > 1) {
            Route route = new Route(newRouteLayer.getRoute());
            route.setName("New route");
            int i = 1;
            LinkedList<RouteWaypoint> waypoints = route.getWaypoints();
            for (RouteWaypoint routeWaypoint : waypoints) {
                if (routeWaypoint.getOutLeg() != null) {
                    RouteLeg outLeg = routeWaypoint.getOutLeg();

                    double xtd = EPDShore.getSettings().getNavSettings().getDefaultXtd();
                    outLeg.setXtdPort(xtd);
                    outLeg.setXtdStarboard(xtd);
                    outLeg.setHeading(Heading.RL);
                    outLeg.setSpeed(EPDShore.getSettings().getNavSettings().getDefaultSpeed());
                }
                routeWaypoint.setTurnRad(EPDShore.getSettings().getNavSettings().getDefaultTurnRad());
                routeWaypoint.setName(String.format("WP_%03d", i));
                i++;
            }
            route.calcValues(true);


            EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager().addRoute(route);
            EPDShore.getMainFrame().getRouteManagerDialog().getRouteManager().notifyListeners(null);
        }




        for (int i = 0; i < EPDShore.getMainFrame().getMapWindows().size(); i++) {
            EPDShore.getMainFrame().getMapWindows().get(i).getChartPanel().getRouteEditLayer().doPrepare();
            EPDShore.getMainFrame().getMapWindows().get(i).getChartPanel().getNewRouteContainerLayer().getWaypoints().clear();
            EPDShore.getMainFrame().getMapWindows().get(i).getChartPanel().getNewRouteContainerLayer().getRouteGraphics().clear();
            EPDShore.getMainFrame().getMapWindows().get(i).getChartPanel().getNewRouteContainerLayer().doPrepare();
        }


    }

    /**
     * Function for setting the active tool item in the toolbar
     *
     * @param tool
     *            reference to the active tool
     */
    public void setActiveToolItem(JLabel toolItem, ToolItemGroup toolItems) {

        if (toolItems.isSingleEnable()) {
            ArrayList<JLabel> items = toolItems.getToolItems();

            for (int i = 0; i < items.size(); i++) {
                items.get(i).setBorder(toolPaddingBorder);
                items.get(i).setOpaque(false);
            }
        }

        // Set active tool
        toolItem.setBackground(new Color(55, 55, 55));
        toolItem.setBorder(BorderFactory.createCompoundBorder(toolPaddingBorder, toolInnerEtchedBorder));
        toolItem.setOpaque(true);
    }

    public void setInactiveToolItem(JLabel toolItem) {
        toolItem.setBorder(toolPaddingBorder);
        toolItem.setOpaque(false);
    }

    /**
     * Function for resizing the icons for the toolbar
     *
     * @param imgpath
     *            path of the image
     * @return newimage the newly created and resized image
     */
    public ImageIcon toolbarIcon(String imgpath) {

        ImageIcon icon = new ImageIcon(EPDShore.class.getClassLoader().getResource(imgpath));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

    /**
     * Function for locking/unlocking the toolbar
     */
    public void toggleLock() {
        if (locked) {
            masterPanel.add(moveHandler, BorderLayout.NORTH);
            locked = false;
            repaintToolbar();

            // Align the toolbar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY - moveHandlerHeight);
            this.setLocation(new_location);

        } else {
            masterPanel.remove(moveHandler);
            locked = true;
            repaintToolbar();

            // Align the toolbar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY + moveHandlerHeight);
            this.setLocation(new_location);
        }
    }

    /**
     * Function for refreshing the toolbar after editing toolitems
     */
    public void repaintToolbar() {

        buttonPanel.removeAll();
        buttonPanel.updateUI();

        width = toolItemSize * toolItemColumns;
        height = 0;

        for (int i = 0; i < toolItemGroups.size(); i++) {

            // Add the tool item group
            JPanel group = new JPanel();
            group.setLayout(new GridLayout(0, 2));
            group.setOpaque(false);

            double temp = (double) toolItemGroups.get(i).getToolItems().size() / (double) toolItemColumns;
            int innerHeight = (int) (Math.ceil(temp) * (toolItemSize - 3));

            height = height + (int) (Math.ceil(temp) * (toolItemSize - 1));

            group.setSize(width, innerHeight);
            group.setPreferredSize(new Dimension(width, innerHeight));

            // Populate it with tool items
            ArrayList<JLabel> items = toolItemGroups.get(i).getToolItems();
            for (int t = 0; t < items.size(); t++) {
                JLabel item = items.get(t);
                group.add(item);
            }

            buttonPanel.add(group);

            // Add a separator
            if (i < toolItemGroups.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(65, 65, 65));
                sep.setBackground(new Color(83, 83, 83));
                buttonPanel.add(sep);

                height = height + 7;
            }
        }

        int innerHeight = height;

        if (!locked) {
            height = height + moveHandlerHeight;
        }

        // And finally set the size and repaint it
        buttonPanel.setSize(width, innerHeight - buttonPanelOffset);
        buttonPanel.setPreferredSize(new Dimension(width, innerHeight - buttonPanelOffset));
        this.setSize(width, height);
        this.revalidate();
        this.repaint();
    }

    /**
     * Function for getting the width of the toolbar
     *
     * @return width width of the toolbar
     */
    public int getWidth() {
        return width;
    }

    /**
     * Function for getting the height of the toolbar
     *
     * @return height height of the toolbar
     */
    public int getHeight() {
        return height;
    }
}