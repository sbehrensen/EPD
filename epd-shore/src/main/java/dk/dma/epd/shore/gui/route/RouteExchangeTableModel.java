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
package dk.dma.epd.shore.gui.route;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.service.ais.AisServices;
import dk.dma.epd.shore.service.ais.RouteSuggestionData;
import dk.dma.epd.shore.service.ais.AisServices.AIS_STATUS;

/**
 * Table model for Route Exchange Notifications
 */
public class RouteExchangeTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] AREA_COLUMN_NAMES = { "ID", "MMSI", "Route Name", "Date", "Status", "Application Recieve Date" };
    private static final String[] COLUMN_NAMES = { "ID", "MMSI", "Route Name", "Status" };

//    private AisServices aisService;

    private List<RouteSuggestionData> messages = new ArrayList<RouteSuggestionData>();

    /**
     * Constructor for creating the msi table model
     *
     * @param msiHandler
     */
    public RouteExchangeTableModel(AisServices aisService) {
        super();
//        this.aisService = aisService;
        updateMessages();
    }

    /**
     * Get column class at specific index
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        if (value == null) {
            return String.class;
        }
        return value.getClass();
    }

    /**
     * Get the column count
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int areaGetColumnCount() {
        return AREA_COLUMN_NAMES.length;
    }

    /**
     * Return the column names
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public String areaGetColumnName(int column) {
        return AREA_COLUMN_NAMES[column];
    }

    /**
     * Return messages
     *
     * @return
     */
    public List<RouteSuggestionData> getMessages() {
        return messages;

    }

    /**
     * Get the row count
     */
    @Override
    public int getRowCount() {
        return messages.size();
    }

    /**
     * Get the value at a specific row and colum index
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rowIndex == -1) {
            return "";
        }
        RouteSuggestionData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.getId();
        case 1:
            return "" + message.getMmsi();
        case 2:
            return message.getRoute().getName();
        case 3:
            return interpetStatusShort(message.getStatus());
        default:
            return "";

        }
    }

    public String interpetStatusShort(AIS_STATUS status) {

        if (status == AIS_STATUS.RECIEVED_APP_ACK) {
            return "Sent";
        } else {
            if (status == AIS_STATUS.FAILED) {
                return "Failed";
            } else {
                if (status == AIS_STATUS.NOT_SENT) {
                    return "Not sent";
                } else {
                    if (status == AIS_STATUS.RECIEVED_ACCEPTED) {
                        return "Accepted";
                    } else {
                        if (status == AIS_STATUS.RECIEVED_NOTED) {
                            return "Noted";
                        } else {
                            if (status == AIS_STATUS.RECIEVED_REJECTED) {
                                return "Rejected";
                            } else {
                                if (status == AIS_STATUS.SENT_NOT_ACK) {
                                    return "Sent but not recieved";
                                } else {
                                    return "Unknown: " + status;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String interpetStatusLong(AIS_STATUS status) {

        if (status == AIS_STATUS.RECIEVED_APP_ACK) {
            return "Sent and acknowleged by application but not user";
        } else {
            if (status == AIS_STATUS.FAILED) {
                return "Failed to send to target";
            } else {
                if (status == AIS_STATUS.NOT_SENT) {
                    return "Not sent - check AIS status";
                } else {
                    if (status == AIS_STATUS.RECIEVED_ACCEPTED) {
                        return "Route Suggestion Accepted by ship";
                    } else {
                        if (status == AIS_STATUS.RECIEVED_NOTED) {
                            return "Route Suggestion Noted by user";
                        } else {
                            if (status == AIS_STATUS.RECIEVED_REJECTED) {
                                return "Route Suggestion Rejected by user";
                            } else {
                                if (status == AIS_STATUS.SENT_NOT_ACK) {
                                    return "Sent but no answer from route aplication";
                                } else {
                                    return "Unknown: " + status;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Object areaGetValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == -1 || this.getRowCount() < 1) {
            return "";
        }
        RouteSuggestionData message = messages.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return message.getId();
        case 1:
            return message.getMmsi();
        case 2:
            return message.getRoute().getName();
        case 3:
            return Formatter.formatShortDateTime(message.getTimeSent());
        case 4:
            return interpetStatusLong(message.getStatus());
        case 5:
            if (message.getAppAck() != null){
                return Formatter.formatShortDateTime(message.getAppAck());
            }else{
                return "Not recieved by application";
            }

        default:
            return "";
        }
    }

    /**
     * Update messages
     */
    public void updateMessages() {
        messages.clear();

//        for (Iterator<RouteSuggestionData> it = aisService.getRouteSuggestions().values().iterator(); it.hasNext();) {
//            messages.add(it.next());
//        }
    }

    public boolean isAwk(int rowIndex) {
        if (rowIndex == -1 || this.getRowCount() < 1) {
            return false;
        }
        return messages.get(rowIndex).isAcknowleged();
    }

}