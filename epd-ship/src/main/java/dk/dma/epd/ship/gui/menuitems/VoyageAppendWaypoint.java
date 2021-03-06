/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.ship.EPDShip;

/**
 * @author Janus Varmarken
 */
public class VoyageAppendWaypoint extends JMenuItem implements IMapMenuAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Route to which waypoint is to be appended.
     */
    private Route route;

    /**
     * Index that specifies the type of the voyage associated with this menu
     * item (e.g. a modified STCC route).
     */
    private int routeIndex;

    public VoyageAppendWaypoint(String menuItemText) {
        super(menuItemText);
    }

    @Override
    public void doAction() {
//        System.out.println("VoyageAppendWaypoint clicked!");
        this.route.appendWaypoint();
        // Notify listeners of the new waypoint
        EPDShip.getInstance().getVoyageEventDispatcher().notifyListenersOfVoyageUpdate(
                VoyageUpdateEvent.WAYPOINT_APPENDED, this.route,
                this.routeIndex);
    }

    /**
     * Set the route that this menu item will append waypoint(s) to.
     * 
     * @param r
     *            The route to which waypoints will be appended.
     */
    public void setRoute(Route r) {
        this.route = r;
    }

    /**
     * Set the route index that specifies the "type" of the route associated
     * with this menu item (e.g. if it is a modified STCC rotue)
     * 
     * @param routeIndex
     *            The new route index.
     */
    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
}
