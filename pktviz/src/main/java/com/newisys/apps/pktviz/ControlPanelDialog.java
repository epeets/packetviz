/*
 * PacketViz packet visualization for the Java (TM) Platform
 * Copyright (C) 2007 Newisys, Inc. or its licensors, as applicable.
 * Java is a registered trademark of Sun Microsystems, Inc. in the U.S. or
 * other countries.
 *
 * Licensed under the Open Software License version 3.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-3.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.newisys.apps.pktviz;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.newisys.apps.pktviz.model.filter.AddrFilter;
import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.model.filter.TxnIdFilter;
import com.newisys.apps.pktviz.model.filter.TxnIdMultiFilter;
import com.newisys.apps.pktviz.model.xform.ActualTimeTransform;
import com.newisys.apps.pktviz.model.xform.PacketTimeTransform;
import com.newisys.apps.pktviz.model.xform.SeqTimeTransform;
import com.newisys.util.format.SizedIntegerFormat;

public class ControlPanelDialog
    extends JDialog
    implements PropertyChangeListener
{
    private static final int FILTER_NONE = 0;
    private static final int FILTER_TXN = 1;
    private static final int FILTER_LINE = 2;
    private static final int FILTER_ADDR = 3;

    private transient ViewSettings viewSettings;

    private JComboBox labelCombo;
    private JComboBox colorCombo;
    private JCheckBox autoStyleCheck;

    private JComboBox filterAttrCombo;
    private JLabel filterValueLabel;
    private JTextField filterValueField;
    private JLabel filterMaskLabel;
    private JTextField filterMaskField;

    private SpinnerNumberModel scaleModel;
    private JCheckBox seqTimeCheck;

    public ControlPanelDialog(Frame owner, ViewSettings viewSettings)
        throws HeadlessException
    {
        super(owner, "Control Panel");
        this.viewSettings = viewSettings;

        setResizable(false);

        JPanel cont = new JPanel();
        cont.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setContentPane(cont);

        GridBagLayout gridbag = new GridBagLayout();
        cont.setLayout(gridbag);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 2, 2, 2);

        ////////////////////////////////////////

        JPanel stylerPanel = new JPanel();
        stylerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Style"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        buildStylerPanel(stylerPanel);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(stylerPanel, c);
        cont.add(stylerPanel);

        showStyler();
        showAutoStyled();

        ////////////////////////////////////////

        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Filter"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        buildFilterPanel(filterPanel);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(filterPanel, c);
        cont.add(filterPanel);

        showFilter();

        ////////////////////////////////////////
        /*
         JPanel searchPanel = new JPanel();
         searchPanel.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createTitledBorder("Search"),
         BorderFactory.createEmptyBorder(2, 2, 2, 2)));
         buildSearchPanel(searchPanel);
         c.gridwidth = GridBagConstraints.REMAINDER;
         gridbag.setConstraints(searchPanel, c);
         cont.add(searchPanel);
         */
        ////////////////////////////////////////
        JPanel zoomPanel = new JPanel();
        zoomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Zoom"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        buildZoomPanel(zoomPanel);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(zoomPanel, c);
        cont.add(zoomPanel);

        ////////////////////////////////////////

        filterAttrChanged();
        //searchAttrChanged();
        pack();
    }

    private void buildStylerPanel(Container cont)
    {
        GridBagLayout panelGridbag = new GridBagLayout();
        cont.setLayout(panelGridbag);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.insets = new Insets(2, 2, 2, 2);

        ////////////////////////////////////////

        JLabel labelLabel = new JLabel("Label with:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(labelLabel, c);
        cont.add(labelLabel);

        String[] labelStrings = { "Command & Txn ID", "Command",
            "Txn ID" };
        labelCombo = new JComboBox(labelStrings);
        labelLabel.setLabelFor(labelCombo);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(labelCombo, c);
        cont.add(labelCombo);

        labelCombo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int labelIndex = labelCombo.getSelectedIndex();
                int colorIndex = colorCombo.getSelectedIndex();

                ControlPanelDialog.this.viewSettings.setPacketStyler(new PacketStyler(
                    labelIndex, colorIndex));
            }
        });

        ////////////////////////////////////////

        JLabel colorLabel = new JLabel("Color by:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(colorLabel, c);
        cont.add(colorLabel);

        String[] colorStrings = { "Txn ID", "Command" };
        colorCombo = new JComboBox(colorStrings);
        colorLabel.setLabelFor(colorCombo);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(colorCombo, c);
        cont.add(colorCombo);

        colorCombo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int labelIndex = labelCombo.getSelectedIndex();
                int colorIndex = colorCombo.getSelectedIndex();

                ControlPanelDialog.this.viewSettings.setPacketStyler(new PacketStyler(
                    labelIndex, colorIndex));
            }
        });

        ////////////////////////////////////////

        autoStyleCheck = new JCheckBox("Automatic");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        panelGridbag.setConstraints(autoStyleCheck, c);
        cont.add(autoStyleCheck);

        autoStyleCheck.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                JCheckBox autoStyleCheck = (JCheckBox) e.getSource();
                boolean autoStyled = autoStyleCheck.isSelected();

                viewSettings.setAutoStyled(autoStyled);
            }
        });
    }

    private void showStyler()
    {
        PacketStyler packetStyler = viewSettings.getPacketStyler();
        labelCombo.setSelectedIndex(packetStyler.getLabelMode());
        colorCombo.setSelectedIndex(packetStyler.getColorMode());
    }

    private void showAutoStyled()
    {
        boolean autoStyled = viewSettings.isAutoStyled();
        labelCombo.setEnabled(!autoStyled);
        colorCombo.setEnabled(!autoStyled);
        autoStyleCheck.setSelected(autoStyled);
    }

    private void buildFilterPanel(Container cont)
    {
        GridBagLayout panelGridbag = new GridBagLayout();
        cont.setLayout(panelGridbag);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.insets = new Insets(2, 2, 2, 2);

        ////////////////////////////////////////

        JLabel attrLabel = new JLabel("Attribute:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(attrLabel, c);
        cont.add(attrLabel);

        String[] attrStrings = { "(none)", "Txn ID", "Memory Line", "Address" };
        filterAttrCombo = new JComboBox(attrStrings);
        attrLabel.setLabelFor(filterAttrCombo);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(filterAttrCombo, c);
        cont.add(filterAttrCombo);

        filterAttrCombo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                filterAttrChanged();
            }
        });

        ////////////////////////////////////////

        filterValueLabel = new JLabel("Value:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(filterValueLabel, c);
        cont.add(filterValueLabel);

        filterValueField = new JTextField("0", 12);
        filterValueLabel.setLabelFor(filterValueField);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(filterValueField, c);
        cont.add(filterValueField);

        filterValueField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                applyFilter();
            }
        });

        ////////////////////////////////////////

        filterMaskLabel = new JLabel("Mask:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(filterMaskLabel, c);
        cont.add(filterMaskLabel);

        filterMaskField = new JTextField("'hFFFFFFFFFF", 12);
        filterMaskLabel.setLabelFor(filterMaskField);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(filterMaskField, c);
        cont.add(filterMaskField);

        filterMaskField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                applyFilter();
            }
        });

        ////////////////////////////////////////

        JButton filterApplyButton = new JButton("Apply");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        panelGridbag.setConstraints(filterApplyButton, c);
        cont.add(filterApplyButton);

        filterApplyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                applyFilter();
            }
        });
    }

    private void showFilter()
    {
        PacketFilter filter = viewSettings.getPacketFilter();
        if (filter == null)
        {
            filterAttrCombo.setSelectedIndex(FILTER_NONE);
        }
        else if (filter instanceof TxnIdFilter)
        {
            TxnIdFilter txnIDFilter = (TxnIdFilter) filter;
            int txnID = txnIDFilter.getTxnID();

            filterAttrCombo.setSelectedIndex(FILTER_TXN);
            filterValueField.setText(String.valueOf(txnID));
        }
        else if (filter instanceof AddrFilter)
        {
            AddrFilter addrFilter = (AddrFilter) filter;
            long addr = addrFilter.getAddr();
            long mask = addrFilter.getMask();
            int size = 40;

            if ((mask & 0x3f) == 0)
            {
                filterAttrCombo.setSelectedIndex(FILTER_LINE);
                addr >>= 6;
                mask >>= 6;
                size -= 6;
            }
            else
            {
                filterAttrCombo.setSelectedIndex(FILTER_ADDR);
            }

            filterValueField.setText(SizedIntegerFormat.format(addr, size, 16));
            filterMaskField.setText(SizedIntegerFormat.format(mask, size, 16));
        }
    }

    private void filterAttrChanged()
    {
        int attrIndex = filterAttrCombo.getSelectedIndex();
        boolean maskValid = (attrIndex == FILTER_LINE || attrIndex == FILTER_ADDR);

        filterMaskField.setEnabled(maskValid);
    }

    private void applyFilter()
    {
        int attrIndex = filterAttrCombo.getSelectedIndex();
        if (attrIndex == FILTER_TXN)
        {
            applyFilterList();
        }
        else
        {
            applyFilterScalar();
        }
    }

    private static Pattern splitCommaSpace = Pattern.compile("[, ]+");

    private void applyFilterList()
    {
        String txnIDListString = filterValueField.getText();
        String[] txnIDs = splitCommaSpace.split(txnIDListString);
        ArrayList<Integer> txnIDArrayList = new ArrayList<Integer>();
        for (String txnIDString : txnIDs)
        {
            try
            {
                int txnID = Integer.parseInt(txnIDString);
                txnIDArrayList.add(txnID);
            }
            catch (NumberFormatException numberFormatException)
            {
                JOptionPane.showMessageDialog(null, "Invalid filter value: "
                    + txnIDString, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        PacketFilter filter;
        filter = new TxnIdMultiFilter(txnIDArrayList.toArray(new Integer[0]));
        refreshFilter(filter);
    }

    private void applyFilterScalar()
    {
        int attrIndex = filterAttrCombo.getSelectedIndex();
        assert (attrIndex == FILTER_NONE || attrIndex == FILTER_LINE || attrIndex == FILTER_ADDR);
        long value;
        long mask;

        SizedIntegerFormat format = new SizedIntegerFormat();
        value = parseValue(attrIndex, format);

        mask = parseMask(attrIndex, format);

        PacketFilter filter;
        switch (attrIndex)
        {
            case FILTER_NONE:
                filter = null;
                break;
            case FILTER_LINE:
                filter = new AddrFilter(value << 6, mask << 6);
                break;
            case FILTER_ADDR:
                filter = new AddrFilter(value, mask);
                break;
            default:
                throw new RuntimeException("Invalid filter type");
        }

        refreshFilter(filter);
    }

    private void refreshFilter(PacketFilter filter)
    {
        viewSettings.setPacketFilter(filter);
        viewSettings.setSavedPacketFilter(null);
        viewSettings.setAutoFiltered(false);
    }

    private long parseMask(int attrIndex, SizedIntegerFormat format)
    {
        long mask;
        if (attrIndex == FILTER_LINE || attrIndex == FILTER_ADDR)
        {
            try
            {
                mask = format.parse(filterMaskField.getText());
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid filter mask",
                    "Error", JOptionPane.ERROR_MESSAGE);
                mask = -1;
            }
        }
        else
        {
            mask = -1;
        }
        return mask;
    }

    private long parseValue(int attrIndex, SizedIntegerFormat format)
    {
        long value;
        if (attrIndex != FILTER_NONE)
        {
            try
            {
                value = format.parse(filterValueField.getText());
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid filter value",
                    "Error", JOptionPane.ERROR_MESSAGE);
                value = -1;
            }
        }
        else
        {
            value = -1;
        }
        return value;
    }

    /*
     private void buildSearchPanel(Container cont)
     {
     GridBagLayout panelGridbag = new GridBagLayout();
     cont.setLayout(panelGridbag);

     GridBagConstraints c = new GridBagConstraints();
     c.anchor = GridBagConstraints.SOUTHWEST;
     c.insets = new Insets(2, 2, 2, 2);

     ////////////////////////////////////////

     JLabel attrLabel = new JLabel("Attribute:");
     c.gridwidth = 1;
     c.weightx = 0.0;
     panelGridbag.setConstraints(attrLabel, c);
     cont.add(attrLabel);

     String[] attrStrings = {
     "Txn ID",
     "Memory Line",
     "Address"
     };
     searchAttrCombo = new JComboBox(attrStrings);
     attrLabel.setLabelFor(searchAttrCombo);
     c.gridwidth = GridBagConstraints.REMAINDER;
     c.weightx = 1.0;
     panelGridbag.setConstraints(searchAttrCombo, c);
     cont.add(searchAttrCombo);

     searchAttrCombo.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
     searchAttrChanged();
     }
     });

     ////////////////////////////////////////

     searchValueLabel = new JLabel("Value:");
     c.gridwidth = 1;
     c.weightx = 0.0;
     panelGridbag.setConstraints(searchValueLabel, c);
     cont.add(searchValueLabel);

     searchValueField = new JTextField("0", 12);
     searchValueLabel.setLabelFor(searchValueField);
     c.gridwidth = GridBagConstraints.REMAINDER;
     c.weightx = 1.0;
     panelGridbag.setConstraints(searchValueField, c);
     cont.add(searchValueField);

     ////////////////////////////////////////

     searchMaskLabel = new JLabel("Mask:");
     c.gridwidth = 1;
     c.weightx = 0.0;
     panelGridbag.setConstraints(searchMaskLabel, c);
     cont.add(searchMaskLabel);

     searchMaskField = new JTextField("'hFFFFFFFFFF", 12);
     searchMaskLabel.setLabelFor(searchMaskField);
     c.gridwidth = GridBagConstraints.REMAINDER;
     c.weightx = 1.0;
     panelGridbag.setConstraints(searchMaskField, c);
     cont.add(searchMaskField);

     ////////////////////////////////////////

     JPanel buttonPanel = new JPanel();
     c.gridwidth = GridBagConstraints.REMAINDER;
     c.weightx = 1.0;
     panelGridbag.setConstraints(buttonPanel, c);
     cont.add(buttonPanel);

     JButton firstButton = new JButton("First");
     buttonPanel.add(firstButton);

     JButton nextButton = new JButton("Next");
     buttonPanel.add(nextButton);
     }

     private void searchAttrChanged()
     {
     int attrIndex = searchAttrCombo.getSelectedIndex();
     boolean maskValid = (attrIndex == SEARCH_LINE || 
     attrIndex == SEARCH_ADDR);

     searchMaskLabel.setEnabled(maskValid);
     searchMaskField.setEnabled(maskValid);
     }
     */
    private void buildZoomPanel(Container cont)
    {
        GridBagLayout panelGridbag = new GridBagLayout();
        cont.setLayout(panelGridbag);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.insets = new Insets(2, 2, 2, 2);

        ////////////////////////////////////////

        JLabel scaleLabel = new JLabel("Scale:");
        c.gridwidth = 1;
        c.weightx = 0.0;
        panelGridbag.setConstraints(scaleLabel, c);
        cont.add(scaleLabel);

        scaleModel = new SpinnerNumberModel(1.0, 0.001, 1000.0, 0.1);
        JSpinner scaleSpinner = new JSpinner(scaleModel);
        scaleLabel.setLabelFor(scaleSpinner);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        panelGridbag.setConstraints(scaleSpinner, c);
        cont.add(scaleSpinner);

        scaleModel.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                SpinnerNumberModel scaleModel = (SpinnerNumberModel) e.getSource();
                double scale = scaleModel.getNumber().doubleValue();
                viewSettings.setPixelsPerTick(scale);
            }
        });

        showScale();

        ////////////////////////////////////////

        seqTimeCheck = new JCheckBox("Sequential time");
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.0;
        panelGridbag.setConstraints(seqTimeCheck, c);
        cont.add(seqTimeCheck);

        seqTimeCheck.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                JCheckBox seqTimeCheck = (JCheckBox) e.getSource();
                boolean isSeqTime = seqTimeCheck.isSelected();

                PacketTimeTransform xform;
                if (isSeqTime)
                {
                    xform = new SeqTimeTransform();
                }
                else
                {
                    xform = new ActualTimeTransform();
                }

                viewSettings.setTimeTransform(xform);
            }
        });

        showTimeTransform();
    }

    private void showScale()
    {
        scaleModel.setValue(new Double(viewSettings.getPixelsPerTick()));
    }

    private void showTimeTransform()
    {
        PacketTimeTransform xform = viewSettings.getTimeTransform();
        seqTimeCheck.setSelected(xform instanceof SeqTimeTransform);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        if (name.equals("packetStyler"))
        {
            showStyler();
        }
        else if (name.equals("autoStyled"))
        {
            showAutoStyled();
        }
        else if (name.equals("packetFilter"))
        {
            showFilter();
        }
        else if (name.equals("pixelsPerTick"))
        {
            showScale();
        }
        else if (name.equals("timeTransform"))
        {
            showTimeTransform();
        }
    }

}
