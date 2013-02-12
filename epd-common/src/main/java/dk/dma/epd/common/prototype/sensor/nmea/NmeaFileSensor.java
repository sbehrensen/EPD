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
package dk.dma.epd.common.prototype.sensor.nmea;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.util.Util;


/**
 * NMEA sensor reading from file
 */
public class NmeaFileSensor extends NmeaSensor {
    
    private static final Logger LOG = LoggerFactory.getLogger(NmeaFileSensor.class);
    
    private String filename;
    private Frame frame;
    
    public NmeaFileSensor(String filename, SensorSettings sensorSettings) {
        LOG.info("Using AIS replay file: " + filename);
        this.filename = filename;
        setReplay(true);
        setReplaySpeedup(sensorSettings.getReplaySpeedup());
        setReplayStartDate(sensorSettings.getReplayStartDate());
        LOG.info("Replay start date: " + sensorSettings.getReplayStartDate());
    }

    @Override
    public void run() {
        // Open file
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (IOException e) {
            LOG.error("Failed to open replay file: " + filename + ": " + e.getMessage());
            return;
        }
        
        // Wait for frame and confirmation
        while (frame == null) {
            Util.sleep(1000);
        }
        Util.sleep(5000);
        JOptionPane.showMessageDialog(frame, "Start replay");        
        
        
        // Read
        try {
            readLoop(in);
        } catch (IOException e) {
            LOG.error("Error while reading replay file: " + filename + ": " + e.getMessage());
        }
        
        long dataElapsed = getDataEnd().getTime() - getDataStart().getTime();
        long realElapsed = (getReplayEnd().getTime() - getReplayStart().getTime()) * getReplaySpeedup();
                
        LOG.info("Replay data start: " + getDataStart() + " end: " + getDataEnd() + " elapsed: " + dataElapsed / 1000);
        LOG.info("Replay real start: " + getReplayStart() + " end: " + getReplayEnd() + " elapsed: " + realElapsed / 1000);
        
        if (frame != null) {
            JOptionPane.showMessageDialog(frame, "Replay finished");
        }

    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof Frame) {
            frame = (Frame)obj;
        }
    }

    @Override
    public void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException {
        throw new SendException("Cannot send to file sensor");
    }

}