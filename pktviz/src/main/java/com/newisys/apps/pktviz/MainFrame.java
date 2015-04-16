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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.newisys.apps.pktviz.logreader.PacketLogReader;
import com.newisys.apps.pktviz.model.PacketGraph;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.TxnInfo;
import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.model.filter.TxnIdFilter;
import com.newisys.apps.pktviz.props.GraphProperties;
import com.newisys.apps.pktviz.view.JPacketGraph;

public class MainFrame
    extends JFrame
{
    private transient ViewSettings viewSettings;
    private transient PacketGraphViewSettingsListener viewListener;
    private JPacketGraph packetGraphView;
    private JPacketGraph.JPacketPane packetPane;
    private ControlPanelDialog controlPanelDialog;
    private PacketDumpDialog packetDumpDialog;

    private File graphFile;
    private File propsFile;

    private PacketInfo highlightedPacket;

    public MainFrame()
    {
        super("PacketViz");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        viewSettings = new ViewSettings();
        viewListener = new PacketGraphViewSettingsListener();
        viewSettings.addPropertyChangeListener(viewListener);

        addMenuBar();
        addContentPane();

        pack();

        controlPanelDialog = new ControlPanelDialog(this, viewSettings);
        viewSettings.addPropertyChangeListener(controlPanelDialog);

        packetDumpDialog = new PacketDumpDialog(this);
    }

    private void addMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = new JMenuItem("Open...", KeyEvent.VK_O);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                choosePacketGraph();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Reload", KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                openPacketGraph(graphFile);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Close", KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                closePacketGraph();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        menu = new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(menu);

        menuItem = new JMenuItem("Set properties file...", KeyEvent.VK_S);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                choosePropsFile();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Clear properties file", KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                setPropsFile(null);
            }
        });
        menu.add(menuItem);

        menu = new JMenu("Window");
        menu.setMnemonic(KeyEvent.VK_W);
        menuBar.add(menu);

        menuItem = new JMenuItem("Control panel", KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                controlPanelDialog.setVisible(!controlPanelDialog.isVisible());
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Packet dump", KeyEvent.VK_P);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                packetDumpDialog.setVisible(!packetDumpDialog.isVisible());
            }
        });
        menu.add(menuItem);
    }

    public ControlPanelDialog getControlPanelDialog()
    {
        return controlPanelDialog;
    }

    public PacketDumpDialog getPacketDumpDialog()
    {
        return packetDumpDialog;
    }

    public void choosePacketGraph()
    {
        File defaultFile = getDefaultPathFile();
        JFileChooser fc = new JFileChooser(defaultFile);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            openPacketGraph(fc.getSelectedFile());
        }
    }

    private File getDefaultPathFile()
    {
        File defaultFile;
        if (graphFile == null)
        {
            if (System.getProperties().containsKey("user.dir"))
            {
                defaultFile = new File(System.getProperty("user.dir"));
                if (!defaultFile.exists())
                {
                    assert (false) : "user.dir system property specified but not "
                        + "defined to a valid path: "
                        + defaultFile.getAbsolutePath();
                }
            }
            else
            {
                defaultFile = null;
            }
        }
        else
        {
            defaultFile = graphFile.getParentFile();
            assert (defaultFile.isDirectory());
        }
        return defaultFile;
    }

    public void openPacketGraph(File file)
    {
        closePacketGraph();

        if (file != null)
        {
            try
            {
                PacketGraph g = new PacketGraph();
                g.setPacketFilter(viewSettings.getPacketFilter());
                g.setTimeTransform(viewSettings.getTimeTransform());
                g.addListener(viewSettings.getPacketStyler());

                GraphProperties props = viewSettings.getGraphProps();
                if (props != null)
                {
                    g.addListener(props);
                }

                PacketLogReader plr = new PacketLogReader(file, g);
                plr.setGraphProperties(props);
                plr.fetchAll();

                packetGraphView.setSource(plr);
                viewListener.setPacketGraph(g);
                graphFile = file;
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void closePacketGraph()
    {
        packetGraphView.setSource(null);
        viewListener.setPacketGraph(null);
        graphFile = null;
    }

    public void choosePropsFile()
    {
        JFileChooser fc = new JFileChooser(propsFile);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            setPropsFile(fc.getSelectedFile());
        }
    }

    public void setPropsFile(File file)
    {
        boolean equal = (file != null) ? file.equals(propsFile)
            : (propsFile == null);
        if (!equal)
        {
            File saveGraphFile = graphFile;
            closePacketGraph();

            propsFile = file;

            GraphProperties props;
            if (propsFile != null)
            {
                props = new GraphProperties();
                props.readXml(propsFile);
            }
            else
            {
                props = null;
            }
            viewSettings.setGraphProps(props);

            openPacketGraph(saveGraphFile);
        }
    }

    private void addContentPane()
    {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        packetGraphView = new JPacketGraph();
        packetGraphView.setName("packetGraphView");
        packetGraphView.addPropertyChangeListener(viewSettings);
        viewListener.setPacketGraphView(packetGraphView);

        packetPane = packetGraphView.getPacketPane();
        packetPane.addMouseListener(new PacketPaneMouseListener());
        packetPane.addMouseMotionListener(new PacketPaneMouseMotionListener());

        JScrollPane graphScrollPane = new JScrollPane(packetGraphView);
        graphScrollPane.setPreferredSize(new Dimension(500, 300));
        graphScrollPane.setColumnHeaderView(packetGraphView.getColumnHeader());
        graphScrollPane.setRowHeaderView(packetGraphView.getRowHeader());
        graphScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
            packetGraphView.getUpperLeftCorner());
        graphScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        graphScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        contentPane.add(graphScrollPane, BorderLayout.CENTER);
    }

    public class PacketPaneMouseListener
        extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (SwingUtilities.isLeftMouseButton(e))
            {
                if (e.getClickCount() == 2)
                {
                    if (highlightedPacket != null
                        && !viewSettings.isAutoFiltered())
                    {
                        TxnInfo txnInfo = highlightedPacket.getTxn();
                        if (txnInfo != null)
                        {
                            int txnID = txnInfo.getTxnID();
                            TxnIdFilter filter = new TxnIdFilter(txnID);

                            PacketFilter savedFilter = viewSettings.getPacketFilter();
                            viewSettings.setSavedPacketFilter(savedFilter);

                            viewSettings.setPacketFilter(filter);
                            viewSettings.setAutoFiltered(true);
                        }
                    }
                }
            }
            else if (SwingUtilities.isRightMouseButton(e))
            {
                if (viewSettings.isAutoFiltered())
                {
                    viewSettings.revertPacketFilter();
                    viewSettings.setAutoFiltered(false);
                }
            }
        }

    }

    public class PacketPaneMouseMotionListener
        extends MouseMotionAdapter
    {
        public void mouseMoved(MouseEvent e)
        {
            int x = e.getX(), y = e.getY();
            PacketInfo packet = packetPane.getPacketAt(x, y);
            if (packet != highlightedPacket)
            {
                if (highlightedPacket != null)
                {
                    packetPane.restorePacket(highlightedPacket);
                    highlightedPacket = null;
                }
                if (packet != null)
                {
                    packetPane.highlightPacket(packet);
                    highlightedPacket = packet;

                    packetDumpDialog.clear();
                    packet.dumpTo(packetDumpDialog.getFieldDumpListener());
                }
            }
        }

    }

}
