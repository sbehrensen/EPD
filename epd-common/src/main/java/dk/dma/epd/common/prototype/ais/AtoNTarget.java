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
package dk.dma.epd.common.prototype.ais;

import java.util.Date;

import dk.dma.ais.message.AisMessage21;
import dk.dma.enav.model.geometry.Position;

/**
 * Class representing a AtoN target
 */
public class AtoNTarget extends AisTarget {
    
    private static final long serialVersionUID = 1L;
    
    private Position pos;
    private int atonType;
    private String name;
    private int posAcc;
    private int dimBow;
    private int dimStern;
    private int dimPort;
    private int dimStarboard;
    private int posType;
    private int offPosition;
    private int regional;
    private int raim;
    private int virtual;
    private int assigned;
    private String nameExt;
    
    /**
     * Empty constructor
     */
    public AtoNTarget() {
        super();
    }
    
    /**
     * Copy constructor
     * @param atoNTarget
     */
    public AtoNTarget(AtoNTarget atoNTarget) {
        super(atoNTarget);
        pos = atoNTarget.pos;
        atonType = atoNTarget.atonType;
        name = atoNTarget.name;
        posAcc = atoNTarget.posAcc;
        dimBow = atoNTarget.dimBow;
        dimStern = atoNTarget.dimStern;
        dimPort = atoNTarget.dimPort;
        dimStarboard = atoNTarget.dimStarboard;
        posType = atoNTarget.posType;
        offPosition = atoNTarget.offPosition;
        regional = atoNTarget.regional;
        raim = atoNTarget.raim;
        virtual = atoNTarget.virtual;
        assigned = atoNTarget.assigned;
        nameExt = atoNTarget.nameExt;
    }
    
    /**
     * Update AtoN target given AIS message #21
     * @param msg21
     */
    public void update(AisMessage21 msg21) {
        pos = msg21.getPos().getGeoLocation();
        atonType = msg21.getAtonType();
        name = msg21.getName();
        posAcc = msg21.getPosAcc();
        dimBow = msg21.getDimBow();
        dimStern = msg21.getDimStern();
        dimPort = msg21.getDimPort();
        dimStarboard = msg21.getDimStarboard();
        posType = msg21.getPosType();
        offPosition = msg21.getOffPosition();
        regional = msg21.getRegional();
        raim = msg21.getRaim();
        virtual = msg21.getVirtual();
        assigned = msg21.getAssigned();
        nameExt = msg21.getNameExt();
    }
    
    /**
     * Determine if AtoN target has gone
     */
    @Override
    public boolean hasGone(Date now, boolean strict) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;        
        // Base gone "loosely" on ITU-R Rec M1371-4 4.2.1  (3 minutes)
        long tol = 600; // 10 minutes
        return elapsed > tol;
    }
    
    public Position getPos() {
        return pos;
    }

    public int getAtonType() {
        return atonType;
    }

    public void setAtonType(int atonType) {
        this.atonType = atonType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosAcc() {
        return posAcc;
    }

    public void setPosAcc(int posAcc) {
        this.posAcc = posAcc;
    }

    public int getDimBow() {
        return dimBow;
    }

    public void setDimBow(int dimBow) {
        this.dimBow = dimBow;
    }

    public int getDimStern() {
        return dimStern;
    }

    public void setDimStern(int dimStern) {
        this.dimStern = dimStern;
    }

    public int getDimPort() {
        return dimPort;
    }

    public void setDimPort(int dimPort) {
        this.dimPort = dimPort;
    }

    public int getDimStarboard() {
        return dimStarboard;
    }

    public void setDimStarboard(int dimStarboard) {
        this.dimStarboard = dimStarboard;
    }

    public int getPosType() {
        return posType;
    }

    public void setPosType(int posType) {
        this.posType = posType;
    }

    public int getOffPosition() {
        return offPosition;
    }

    public void setOffPosition(int offPosition) {
        this.offPosition = offPosition;
    }

    public int getRegional() {
        return regional;
    }

    public void setRegional(int regional) {
        this.regional = regional;
    }

    public int getRaim() {
        return raim;
    }

    public void setRaim(int raim) {
        this.raim = raim;
    }

    public int getVirtual() {
        return virtual;
    }

    public void setVirtual(int virtual) {
        this.virtual = virtual;
    }

    public int getAssigned() {
        return assigned;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    public String getNameExt() {
        return nameExt;
    }

    public void setNameExt(String nameExt) {
        this.nameExt = nameExt;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
    
}