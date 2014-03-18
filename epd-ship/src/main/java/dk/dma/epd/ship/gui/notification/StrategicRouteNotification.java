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
package dk.dma.epd.ship.gui.notification;

import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationCommon;

/**
 * A ship-specific strategic route implementation of the {@linkplain StrategicRouteNotificationCommon} class
 */
public class StrategicRouteNotification extends StrategicRouteNotificationCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param routeData the strategic route data
     */
    public StrategicRouteNotification(StrategicRouteNegotiationData routeData) {
        super(routeData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallerlName() {
        // TODO: Integrate with MC identity portfolio
        return "STCC " + get().getMmsi();
    }
}
