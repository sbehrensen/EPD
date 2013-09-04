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
package dk.dma.epd.shore.voct;

import java.util.LinkedList;
import java.util.List;

import dk.dma.epd.shore.EPDShore;

public class SRUManager implements Runnable {

    private List<SRU> srus = new LinkedList<SRU>();

    public SRUManager() {
        EPDShore.startThread(this, "sruManager");
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            EPDShore.sleep(1000000);

        }

    }

    public int getSRUCount() {
        synchronized (srus) {
            return srus.size();
        }
    }

    /**
     * @return the sru
     */
    public List<SRU> getSRUs() {
        return srus;
    }

    
    public SRU getSRUs(int index) {
        return getSRUs().get(index);
    }
    
    public static SRUManager loadSRUManager() {
        SRUManager manager = new SRUManager();

        // try {
        // FileInputStream fileIn = new FileInputStream(VOYAGESFILE);
        // ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        // VoyageStore voyageStore = (VoyageStore) objectIn.readObject();
        // objectIn.close();
        // fileIn.close();
        // manager.setVoyages(voyageStore.getVoyages());
        //
        // } catch (FileNotFoundException e) {
        // // Not an error
        // } catch (Exception e) {
        // LOG.error("Failed to load routes file: " + e.getMessage());
        // // Delete possible corrupted or old file
        // new File(VOYAGESFILE).delete();
        // }
        
        manager.addStaticData();

        return manager;
    }
    
    
    public void addStaticData(){
        
        SRU bopa = new SRU("MHV BOPA", 1, SRU.sru_type.SHIP, SRU.sru_status.UNKNOWN, true);
        SRU plane = new SRU("Plane 001", 1, SRU.sru_type.PLANE, SRU.sru_status.UNKNOWN, true);
        SRU helicopter = new SRU("Helicopter", 1, SRU.sru_type.HELICOPTER, SRU.sru_status.UNKNOWN, true);
        
        srus.add(bopa);
        srus.add(plane);
        srus.add(helicopter);
        
    }

}
