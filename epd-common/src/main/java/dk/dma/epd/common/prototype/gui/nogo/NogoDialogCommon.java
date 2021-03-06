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
package dk.dma.epd.common.prototype.gui.nogo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.mouse.NoGoMouseModeCommon;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.nogo.NogoHandlerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;

/**
 * The nogo dialog
 */
public class NogoDialogCommon extends JDialog implements ActionListener, Runnable, ItemListener {
    private static final long serialVersionUID = 1L;
    private JButton requestNogo;
    private JButton cancelButton;
    private JButton btnSelectArea;
    protected JSpinner spinnerDraught;
    private JSpinner spinnerTimeStart;
    private JSpinner spinnerTimeEnd;
    private JSpinner spinnerUKC;

    private JLabel depthTxt;

    private JLabel nwPtlbl;
    private JLabel nePtlbl;
    private JLabel swPtlbl;
    private JLabel sePtlbl;

    protected ChartPanelCommon chartPanel;
    protected NogoHandlerCommon nogoHandler;

    private Position northWestPoint;
    private Position southEastPoint;

    private JCheckBox chckbxUseSlices;
    private JComboBox<Integer> minuteSlices;

    private boolean useSlices;

    private int sliceInMinutes = 10;
    private JLabel slicesCount;
    private Double totalDepth = 0.0;
    private int sliceCount;

    public NogoDialogCommon(JFrame parent) {
        super(parent, "Request Nogo", true);
    }

    // public NogoDialogCommon() {
    // super(parent, "Request Nogo", true);
    //
    // mainFrame = (MainFrameCommon) parent;
    //
    // this.chartPanel = mainFrame.getChartPanel();
    // this.nogoHandler = nogoHandler;
    //
    // setSize(384, 445);
    // setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // setLocationRelativeTo(parent);
    // setResizable(false);
    //
    // initGUI();
    //
    // }

    protected void initGUI() {
        JPanel contentPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Area Selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(15, 30, 327, 115);

        JLabel lblNogoRequest = new JLabel("Nogo Request:");
        lblNogoRequest.setBounds(15, 5, 81, 14);
        lblNogoRequest.setFont(new Font("Tahoma", Font.BOLD, 11));
        contentPanel.setLayout(null);
        panel.setLayout(null);
        contentPanel.add(panel);

        btnSelectArea = new JButton("Select Area");
        btnSelectArea.setBounds(204, 81, 113, 23);
        panel.add(btnSelectArea);
        btnSelectArea.addActionListener(this);

        nwPtlbl = new JLabel("Select the area you want to request NoGo for");
        nwPtlbl.setBounds(10, 26, 300, 14);
        panel.add(nwPtlbl);

        nePtlbl = new JLabel("");
        nePtlbl.setBounds(176, 26, 141, 14);
        panel.add(nePtlbl);

        swPtlbl = new JLabel("");
        swPtlbl.setBounds(10, 51, 300, 14);
        panel.add(swPtlbl);

        sePtlbl = new JLabel("");
        sePtlbl.setBounds(176, 51, 127, 14);
        panel.add(sePtlbl);
        contentPanel.add(lblNogoRequest);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Time Selected", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(15, 156, 327, 94);
        contentPanel.add(panel_1);
        panel_1.setLayout(null);

        JLabel lblNogoBetween = new JLabel("NoGo valid between:");
        lblNogoBetween.setBounds(10, 24, 137, 14);
        panel_1.add(lblNogoBetween);

        
        
        DateTime dateTime = new DateTime(PntTime.getDate());
        dateTime = dateTime.withMillisOfSecond(0);
        dateTime = dateTime.withSecondOfMinute(0);
        dateTime = dateTime.withMinuteOfHour(0);

        Date date = new Date(dateTime.getMillis());
        Date date48hour = date;

        Calendar c = Calendar.getInstance();
        c.setTime(date48hour);
        c.add(Calendar.DATE, 2); // number of days to add
        date48hour = c.getTime();

        spinnerTimeStart = new JSpinner();
        spinnerTimeEnd = new JSpinner();

        spinnerTimeStart.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {

                if (((Date) spinnerTimeEnd.getValue()).getTime() < ((Date) spinnerTimeStart.getValue()).getTime()) {

                    spinnerTimeEnd.setValue(spinnerTimeStart.getValue());
                }

                calculateSlices();

            }
        });

        spinnerTimeEnd.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                calculateSlices();

            }
        });

        spinnerTimeStart.setModel(new SpinnerDateModel(date, null, date48hour, Calendar.HOUR));
        spinnerTimeStart.setBounds(10, 41, 98, 20);

        spinnerTimeEnd.setModel(new SpinnerDateModel(date, null, date48hour, Calendar.HOUR));
        spinnerTimeEnd.setBounds(10, 64, 98, 20);

        panel_1.add(spinnerTimeStart);
        panel_1.add(spinnerTimeEnd);

        spinnerTimeStart.setEditor(new JSpinner.DateEditor(spinnerTimeStart, "HH:mm-dd-MM-yy"));
        spinnerTimeEnd.setEditor(new JSpinner.DateEditor(spinnerTimeEnd, "HH:mm-dd-MM-yy"));

        chckbxUseSlices = new JCheckBox("Use Slices");
        chckbxUseSlices.setBounds(153, 24, 97, 23);
        chckbxUseSlices.addItemListener(this);

        panel_1.add(chckbxUseSlices);

        minuteSlices = new JComboBox<Integer>();
        minuteSlices.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 10, 20, 30, 40, 50, 60 }));
        minuteSlices.setEnabled(false);
        minuteSlices.setBounds(155, 49, 43, 20);
        panel_1.add(minuteSlices);
        minuteSlices.addItemListener(this);

        JLabel lblMinutes = new JLabel("Minutes");
        lblMinutes.setBounds(208, 49, 46, 14);
        panel_1.add(lblMinutes);

        slicesCount = new JLabel("");
        slicesCount.setBounds(153, 72, 165, 14);
        panel_1.add(slicesCount);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Depth Contour", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        panel_2.setBounds(15, 259, 327, 114);
        contentPanel.add(panel_2);
        panel_2.setLayout(null);

        JLabel lblNewLabel = new JLabel("Ship Draft in meters:");
        lblNewLabel.setBounds(12, 26, 139, 16);
        panel_2.add(lblNewLabel);

        SpinnerNumberModel m_numberSpinnerModel;
        Double current = new Double(5.50);
        Double min = new Double(0.00);
        Double max = new Double(1000.00);
        Double step = new Double(0.50);
        m_numberSpinnerModel = new SpinnerNumberModel(current, min, max, step);

        spinnerDraught = new JSpinner(m_numberSpinnerModel);
        spinnerDraught.setBounds(252, 24, 38, 20);

        spinnerDraught.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                updateTotalDepthLabel();
            }
        });

        panel_2.add(spinnerDraught);

        JLabel lblDesiredUnderKeel = new JLabel("Desired Under Keel Clearance (UKC) in meters:");
        lblDesiredUnderKeel.setBounds(12, 53, 224, 14);
        panel_2.add(lblDesiredUnderKeel);

        JLabel lblTotalDepthIn = new JLabel("Total Depth in meters:");
        lblTotalDepthIn.setBounds(10, 78, 226, 14);
        panel_2.add(lblTotalDepthIn);

        depthTxt = new JLabel("");
        depthTxt.setBounds(257, 78, 65, 14);
        panel_2.add(depthTxt);

        
        SpinnerNumberModel ukcSpinnerModel = new SpinnerNumberModel(1.0, 0.0, 1000.0, 0.50);
        
        spinnerUKC = new JSpinner();
        spinnerUKC.setModel(ukcSpinnerModel);
        spinnerUKC.setBounds(252, 55, 38, 20);

        spinnerUKC.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                updateTotalDepthLabel();
            }
        });

        panel_2.add(spinnerUKC);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                requestNogo = new JButton("Request NoGo");
                requestNogo.addActionListener(this);

                buttonPane.add(requestNogo);
                getRootPane().setDefaultButton(requestNogo);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                buttonPane.add(cancelButton);
            }
        }

        // if (ownShipHandler != null && ownShipHandler.getStaticData() != null) {
        //
        // int shipDraught = (int) ownShipHandler.getStaticData().getDraught();
        //
        // // System.out.println("Pure value is " + ownShipHandler.getStaticData().getDraught());
        // double draught = shipDraught / 10.0;
        // // System.out.println("Draught is " + draught);
        // spinnerDraught.setValue(draught);
        //
        // }

        updateTotalDepthLabel();
    }

    protected void updateTotalDepthLabel() {
        totalDepth = ((Double) spinnerDraught.getValue()).doubleValue() + ((Double) spinnerUKC.getValue()).doubleValue();

        depthTxt.setText(totalDepth.toString());

    }

    public void setSelectedArea(Point2D[] points) {

        // Find out what is the max of the selection made
        if (points[0].getY() > points[1].getY()) {
            // points 0 is the top left
            northWestPoint = Position.create(points[0].getY(), points[0].getX());
            southEastPoint = Position.create(points[1].getY(), points[1].getX());
        } else {
            northWestPoint = Position.create(points[1].getY(), points[1].getX());
            southEastPoint = Position.create(points[0].getY(), points[0].getX());
        }

        nwPtlbl.setText(Formatter.latToPrintable(northWestPoint.getLatitude())
                + Formatter.lonToPrintable(northWestPoint.getLongitude()));

        nePtlbl.setText(Formatter.latToPrintable(southEastPoint.getLatitude())
                + Formatter.lonToPrintable(northWestPoint.getLongitude()));

        swPtlbl.setText(Formatter.latToPrintable(northWestPoint.getLatitude())
                + Formatter.lonToPrintable(southEastPoint.getLongitude()));

        sePtlbl.setText(Formatter.latToPrintable(southEastPoint.getLatitude())
                + Formatter.lonToPrintable(southEastPoint.getLongitude()));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == requestNogo) {
            // Send off the request
            if (northWestPoint != null & southEastPoint != null) {

                double westEastDistance = northWestPoint.rhumbLineDistanceTo(Position.create(northWestPoint.getLatitude(),
                        southEastPoint.getLongitude()));
                double northSouthDistance = northWestPoint.rhumbLineDistanceTo(Position.create(southEastPoint.getLatitude(),
                        northWestPoint.getLongitude()));

                // 1300000000
                // 1300000
                if (!(westEastDistance * northSouthDistance / 1000 < 1300000)) {

                    nwPtlbl.setText("The area you have selected is too big");
                    nePtlbl.setText("");
                    swPtlbl.setText("");
                    sePtlbl.setText("");

                } else {

                    if (sliceCount > 20) {
                        nwPtlbl.setText("Your timeframe is too large, increase time between slices");
                        nePtlbl.setText("");
                        swPtlbl.setText("or request a smaller time period.");
                        sePtlbl.setText("");

                    } else {
                        this.setVisible(false);
                        nogoHandler.setNorthWestPoint(northWestPoint);
                        nogoHandler.setSouthEastPoint(southEastPoint);
                        double draught = this.totalDepth;
                        nogoHandler.setDraught(draught);
                        nogoHandler.setValidFrom((Date) spinnerTimeStart.getValue());
                        nogoHandler.setValidTo((Date) spinnerTimeEnd.getValue());

                        toggleMenuLayerSelection();

                        new Thread(this).start();

                    }

                }
            } else {
                nwPtlbl.setText("You must select an area");
                return;
            }

            // Set the mouse mode back to navigation.
            this.chartPanel.setMouseMode(this.chartPanel.getNogoMouseMode().getPreviousMouseModeID());
            // this.chartPanel.getMouseDelegator().getActiveMouseMode().s
        }
        if (e.getSource() == cancelButton) {
            // Cancel the request
            if (northWestPoint != null & southEastPoint != null) {
                this.chartPanel.setMouseMode(this.chartPanel.getNogoMouseMode().getPreviousMouseModeID());
            }

            this.dispose();
        }
        if (e.getSource() == btnSelectArea) {
            // Make a selection on the chartmap
            this.setVisible(false);

            chartPanel.setNogoDialog(this);

            // Set the previous active mouse mode.
            this.chartPanel.getNogoMouseMode().setPreviousMouseModeID(this.chartPanel.getMouseDelegator().getActiveMouseModeID());

            chartPanel.setMouseMode(NoGoMouseModeCommon.MODE_ID);
        }
    }

    protected void toggleMenuLayerSelection() {

    }

    @Override
    public void run() {
        nogoHandler.updateNogo(useSlices, sliceInMinutes);
        this.dispose();
    }

    @Override
    public void itemStateChanged(ItemEvent arg0) {

        if (arg0.getSource() == chckbxUseSlices) {
            if (chckbxUseSlices.isSelected()) {
                useSlices = true;
                minuteSlices.setEnabled(true);
                calculateSlices();
            } else {
                minuteSlices.setEnabled(false);
                useSlices = false;
                slicesCount.setText("");
            }
        }

        if (arg0.getSource() == minuteSlices) {
            sliceInMinutes = (int) minuteSlices.getSelectedItem();

            // slicesCount.setText(calculateSlicesAmount() + " slices");
            calculateSlices();
        }

    }

    private void calculateSlices() {

        if (useSlices) {

            sliceCount = 1;

            DateTime startDate = new DateTime(spinnerTimeStart.getValue());
            DateTime endDate = new DateTime(spinnerTimeEnd.getValue());

            DateTime currentVal = startDate.plusMinutes(sliceInMinutes);

            while (currentVal.isBefore(endDate)) {
                startDate = currentVal;
                currentVal = startDate.plusMinutes(sliceInMinutes);
                sliceCount = sliceCount + 1;
            }

            slicesCount.setText("Time Slices: " + sliceCount);
        }
    }
}
