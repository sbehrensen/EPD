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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.gui.views.SendRouteDialog;



public class SendRouteToShip extends JMenuItem implements IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long MMSI;
    private SendRouteDialog sendRouteDialog;

    public SendRouteToShip(String text) {
        super();
        this.setText(text);
    }

    @Override
    public void doAction() {

        sendRouteDialog.setSelectedMMSI(MMSI);
        sendRouteDialog.setVisible(true);
    }


    public void setMSSI(long MSSI) {
        this.MMSI = MSSI;
    }

    public void setSendRouteDialog(SendRouteDialog sendRouteDialog){
        this.sendRouteDialog = sendRouteDialog;
    }

}
