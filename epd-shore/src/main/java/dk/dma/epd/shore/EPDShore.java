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
package dk.dma.epd.shore;

import java.beans.beancontext.BeanContextServicesSupport;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.PropertyConsumer;

import dk.dma.ais.reader.AisReader;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaFileSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSerialSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaStdinSensor;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaTcpSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.utils.StaticImages;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.msi.MsiHandler;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.ais.AisServices;
import dk.dma.epd.shore.services.shore.ShoreServices;
import dk.dma.epd.shore.settings.ESDSensorSettings;
import dk.dma.epd.shore.settings.ESDSettings;
import dk.dma.epd.shore.util.OneInstanceGuard;

/**
 * Main class with main method.
 *
 * Starts up components, bean context and GUI.
 *
 */
public class EPDShore {

    private static String VERSION;
    private static String MINORVERSION;
    private static Logger LOG;
    private static MainFrame mainFrame;

    private static BeanContextServicesSupport beanHandler;
    private static ESDSettings settings;
    static Properties properties = new Properties();

    private static AisHandler aisHandler;

    private static MsiHandler msiHandler;
    private static AisServices aisServices;

    private static NmeaSensor aisSensor;
    // private static NmeaSensor gpsSensor;

    private static AisReader aisReader;
    // private static GpsHandler gpsHandler;

    private static ShoreServices shoreServices;
    private static StaticImages staticImages;

    private static RouteManager routeManager;

    private static ExceptionHandler exceptionHandler = new ExceptionHandler();
    private static Path home = Paths.get(System.getProperty("user.home"), ".epd-shore");

    /**
     * Function called on shutdown
     */
    public static void closeApp() {
        closeApp(false);
    }

    public static Path getHomePath() {
        return home;
    }

    /**
     * Close app routine with possibility for restart - not implemented
     *
     * @param restart
     *            - boolean value for program restart
     */
    public static void closeApp(boolean restart) {
        // Shutdown routine

        // Chart panels

        // Window state

        // Window state has a
        // Name, Size, Location, Locked status, on top status
        // Chart panel has a zoom level, position

        // Main application

        mainFrame.saveSettings();
        settings.saveToFile();

        // GuiSettings
        // Handler settings
        routeManager.saveToFile();
        // msiHandler.saveToFile();
        // aisHandler.saveView();

        LOG.info("Closing ESD");
        System.exit(restart ? 2 : 0);
    }

    /**
     * Creates and shows the GUI
     */
    private static void createAndShowGUI() {
        // Set the look and feel.
        initLookAndFeel();

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the main window
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

    }

    /**
     * Create the plugin components and initialize the beanhandler
     */
    private static void createPluginComponents() {
        Properties props = getProperties();
        String componentsValue = props.getProperty("esd.plugin_components");
        if (componentsValue == null) {
            return;
        }
        String[] componentNames = componentsValue.split(" ");
        for (String compName : componentNames) {
            String classProperty = compName + ".class";
            String className = props.getProperty(classProperty);
            if (className == null) {
                LOG.error("Failed to locate property " + classProperty);
                continue;
            }
            // Create it if you do...
            try {
                Object obj = java.beans.Beans.instantiate(null, className);
                if (obj instanceof PropertyConsumer) {
                    PropertyConsumer propCons = (PropertyConsumer) obj;
                    propCons.setProperties(compName, props);
                }
                beanHandler.add(obj);
            } catch (IOException e) {
                LOG.error("IO Exception instantiating class \"" + className
                        + "\"");
            } catch (ClassNotFoundException e) {
                LOG.error("Component class not found: \"" + className + "\"");
            }
        }
    }

    /**
     * Function used to measure time
     *
     * @param start
     *            - Startime
     * @return - Elapsed time
     */
    public static double elapsed(long start) {
        double elapsed = System.nanoTime() - start;
        return elapsed / 1000000.0;
    }

    /**
     * Return the AisHandler
     *
     * @return - aisHandler
     */
    public static AisHandler getAisHandler() {
        return aisHandler;
    }

    /**
     * BeanHandler for program structure
     *
     * @return - beanHandler
     */
    public static BeanContextServicesSupport getBeanHandler() {
        return beanHandler;
    }

    // /**
    // * Return the GpsHandler
    // * @return - GpsHandler
    // */
    // public static GpsHandler getGpsHandler() {
    // return gpsHandler;
    // }

    /**
     * Return the mainFrame gui element
     *
     * @return - mainframe gui
     */
    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static RouteManager getRouteManager() {
        return routeManager;
    }

    /**
     * Return minor version
     *
     * @return - minor version
     */
    public static String getMinorVersion() {
        return MINORVERSION;
    }

    /**
     * Return the msiHandker
     *
     * @return - MsiHandler
     */
    public static MsiHandler getMsiHandler() {
        return msiHandler;
    }

    /**
     * Returns the properties
     *
     * @return - properties
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * Return the settings
     *
     * @return - settings
     */
    public static ESDSettings getSettings() {
        return settings;
    }

    /**
     * Return the shoreService used in shore connections like MSI
     *
     * @return - shoreServices
     */
    public static ShoreServices getShoreServices() {
        return shoreServices;
    }

    /**
     * Returns the version
     *
     * @return - version
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Set the used theme using lookAndFeel
     */
    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.error("Failed to set look and feed: " + e.getMessage());
        }

        // Uncomment for fancy look and feel
        /**
         * try { for (LookAndFeelInfo info :
         * UIManager.getInstalledLookAndFeels()) { if
         * ("Nimbus".equals(info.getName())) {
         * UIManager.setLookAndFeel(info.getClassName()); break; } } } catch
         * (Exception e) { // If Nimbus is not available, you can set the GUI to
         * another look and feel. }
         **/

    }

    /**
     * Load the properties file
     */
    static void loadProperties() {
        InputStream in = EPDShore.class.getResourceAsStream("/epd-shore.properties");
        try {
            if (in == null) {
                throw new IOException("Properties file not found");
            }
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOG.error("Failed to load resources: " + e.getMessage());
        }
    }

    /**
     * Starts the program by initializing the various threads and spawning the
     * main GUI
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {

        new Bootstrap().run();

        // Set up log4j logging
        // DOMConfigurator.configure("log4j.xml");
        LOG = LoggerFactory.getLogger(EPDShore.class);

        // Set default exception handler
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        VERSION = "5.0";
        LOG.info("Starting eNavigation Prototype Display Shore - version " + VERSION);
        LOG.info("Copyright (C) 2012 Danish Maritime Authority");
        LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
        LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
        LOG.info("For details see LICENSE file.");

        // Create the bean context (map handler)
        // mapHandler = new MapHandler();
        beanHandler = new BeanContextServicesSupport();

        // Enable GPS timer by adding it to bean context
        GnssTime.init();
        beanHandler.add(GnssTime.getInstance());

        // Start position handler and add to bean context
        // gpsHandler = new GpsHandler();
        // beanHandler.add(gpsHandler);

        // Load settings or get defaults and add to bean context
        if (args.length > 0) {
            settings = new ESDSettings(args[0]);
        } else {
            settings = new ESDSettings();
        }

        // Create shore services
        shoreServices = new ShoreServices(getSettings().getEnavSettings());
        beanHandler.add(shoreServices);

        // // Create AIS services
        // aisServices = new AisServices();
        // beanHandler.add(aisServices);

        LOG.info("Using settings file: " + settings.getSettingsFile());
        settings.loadFromFile();
        beanHandler.add(settings);

        // Determine if instance already running and if that is allowed
        OneInstanceGuard guard = new OneInstanceGuard("esd.lock");
        if (guard.isAlreadyRunning()) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "One application instance already running. Stop instance or restart computer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Start sensors
        startSensors();

        // ESDAisSettings sensorSettings = settings.getAisSettings();
        //
        // RoundRobinAisTcpReader reader = new RoundRobinAisTcpReader();
        // reader.setCommaseparatedHostPort(sensorSettings.getAisHostOrSerialPort()
        // + ":" + sensorSettings.getAisTcpPort());

        // reader.registerHandler(aisHandler);

        // aisReader = reader;
        // beanHandler.add(aisReader);
        //
        // aisReader.start();

        // aisHandler = new AisHandler();
        aisHandler = new AisHandler();
        // aisHandler.loadView();
        beanHandler.add(aisHandler);

        // Add StaticImages handler
        staticImages = new StaticImages();
        beanHandler.add(staticImages);

        // Load routeManager and register as GPS data listener
        routeManager = RouteManager.loadRouteManager();
        beanHandler.add(routeManager);

        // Create AIS services
        aisServices = new AisServices();
        beanHandler.add(aisServices);

        // reader.setTimeout(getInt("ais_source_timeout." + name, "10"));
        // reader.setReconnectInterval(getInt("ais_source_reconnect_interval." +
        // name, "5") * 1000);

        // // Register proprietary handlers
        // reader.addProprietaryFactory(new GatehouseFactory());
        //
        //
        //
        // Create plugin components
        createPluginComponents();

        // Create and show GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

        // Create MSI handler
        msiHandler = new MsiHandler(getSettings().getEnavSettings());
        beanHandler.add(msiHandler);

    }

    private static void startSensors() {
        ESDSensorSettings sensorSettings = settings.getSensorSettings();
        switch (sensorSettings.getAisConnectionType()) {
        case NONE:
            aisSensor = new NmeaStdinSensor();
            break;
        case TCP:
            aisSensor = new NmeaTcpSensor(
                    sensorSettings.getAisHostOrSerialPort(),
                    sensorSettings.getAisTcpPort());
            break;
        case SERIAL:
            aisSensor = new NmeaSerialSensor(
                    sensorSettings.getAisHostOrSerialPort());
            break;
        case FILE:
            aisSensor = new NmeaFileSensor(sensorSettings.getAisFilename(),
                    sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: "
                    + sensorSettings.getAisConnectionType());
        }

        if (aisSensor != null) {
            aisSensor.addSensorType(SensorType.AIS);
        }

        if (aisSensor != null) {
            aisSensor.setSimulateGps(sensorSettings.isSimulateGps());
            aisSensor.setSimulatedOwnShip(sensorSettings.getSimulatedOwnShip());
            aisSensor.start();
            // Add ais sensor to bean context
            beanHandler.add(aisSensor);
        }

    }

    public static StaticImages getStaticImages() {
        return staticImages;
    }

    /**
     * Function used to call sleep on a thread
     *
     * @param ms
     *            - time in ms of how long to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Function used to create a thread
     *
     * @param t
     *            - class to create thread on
     * @param name
     *            - Thread name
     */
    public static void startThread(Runnable t, String name) {
        Thread thread = new Thread(t);
        thread.setName(name);
        thread.start();
    }

    public static AisReader getAisReader() {
        return aisReader;
    }

    public static AisServices getAisServices() {
        return aisServices;
    }

}