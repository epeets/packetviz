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

package com.newisys.apps.pktviz.view;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import com.newisys.apps.pktviz.model.PacketGraphSource;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;
import com.newisys.prtree.PointIterator;
import com.newisys.util.glyph.Connector;

public class JPacketGraph
    extends JAbstractPacketGraph
{
    private static final int DEFAULT_GRAPH_WIDTH = 200;
    private static final int GRAPH_HORIZ_MARGIN = 50;
    private static final int MINOR_PER_MAJOR = 10;
    private static final int PACKET_HIT_WIDTH = 4;
    private static final int PACKET_HIT_HEIGHT = 4;

    private JPacketGraphHeader header;
    private JPacketGraphRuler ruler;
    private JPacketGraphULCorner ulCorner;

    private JPacketPane packetPane;

    private long firstTick;

    private double pixelsPerTick;
    private double ticksPerMajor;

    private int minVisTick;
    private int maxVisTick;
    public boolean rotateLabels;

    public JPacketGraph()
    {
        nodePane.addComponentListener(new NodePaneComponentListener());

        header = new JPacketGraphHeader();
        ruler = new JPacketGraphRuler();
        ulCorner = new JPacketGraphULCorner();

        packetPane = new JPacketPane();
        add(packetPane, Integer.valueOf(50));
        rotateLabels = true;

        setPixelsPerTick(1.0);
    }

    public void setName(String name)
    {
        super.setName(name);
        header.setName(name + ".header");
        ruler.setName(name + ".ruler");
        ulCorner.setName(name + ".ulCorner");
        packetPane.setName(name + ".packetPane");
    }

    public void setSource(PacketGraphSource _source)
    {
        super.setSource(_source);

        if (source != null)
        {
            // fetch the first two times
            try
            {
                source.fetchUntil(0);
                firstTick = graph.getFirstTime();
                source.fetchUntil(firstTick + 1);
            }
            catch (IOException e)
            {
                // ignored
            }
        }

        autoScale();

        header.setSource(_source);
    }

    private void autoScale()
    {
        double newPixelsPerTick = 1.0;

        if (graph != null)
        {
            // calculate scale based on distance between first two times
            PointIterator i = graph.getPacketTimes();
            if (i.hasNext())
            {
                firstTick = i.next();
                if (i.hasNext())
                {
                    long secondTick = i.next();
                    newPixelsPerTick = 25.0 / (secondTick - firstTick);
                }
            }
        }
        else
        {
            firstTick = 0;
        }

        setPixelsPerTick(newPixelsPerTick);

        // in case firstTick changed but pixelsPerTick did not
        updateWidth();
    }

    public JComponent getColumnHeader()
    {
        return ruler;
    }

    public JComponent getRowHeader()
    {
        return header;
    }

    public JComponent getUpperLeftCorner()
    {
        return ulCorner;
    }

    public JPacketPane getPacketPane()
    {
        return packetPane;
    }

    public double getPixelsPerTick()
    {
        return pixelsPerTick;
    }

    public void setRotateLabels(boolean rotateLabels)
    {
        this.rotateLabels = rotateLabels;
    }

    public void setPixelsPerTick(double _pixelsPerTick)
    {
        if (pixelsPerTick != _pixelsPerTick)
        {
            double oldValue = pixelsPerTick;

            pixelsPerTick = _pixelsPerTick;
            ticksPerMajor = roundPrec(100.0 / pixelsPerTick, 10, 1);
            updateWidth();

            firePropertyChange("pixelsPerTick", new Double(oldValue),
                new Double(pixelsPerTick));
        }
    }

    private static double roundPrec(double d, int base, int digits)
    {
        double log = Math.log(d) / Math.log(base);
        double exp = Math.ceil(log);
        double mag = Math.pow(base, exp);
        double mant = d / mag;
        double scale = Math.pow(base, digits);
        double rmant = Math.rint(mant * scale) / scale;
        double res = mag * rmant;

        return res;
    }

    protected JAbstractPacketNode newNode(PacketNode node)
    {
        return new JPacketNode(node);
    }

    protected JAbstractPacketNode newGroup(PacketNode node)
    {
        JPacketGroup jPacketGroup = new JPacketGroup(node);
        return jPacketGroup;
    }

    public void nodeAdded(PacketNode node, boolean topLevel)
    {
        super.nodeAdded(node, topLevel);
        packetPane.invalidateNodePosMap();
    }

    public void nodeUpdated(PacketNode node)
    {
        super.nodeUpdated(node);
        packetPane.invalidateNodePosMap();
    }

    public void packetAdded(PacketInfo packet)
    {
        packetUpdated(packet);
    }

    public void packetUpdated(PacketInfo packet)
    {
        long fromTime = packet.getFromTime();
        long toTime = packet.getToTime();

        if (toTime >= minVisTick && fromTime <= maxVisTick)
        {
            packetPane.repaint();
        }
    }

    public void filterChanged()
    {
        autoScale();
        packetPane.repaint();
    }

    private double tickToPixel(double tick)
    {
        return ((tick - firstTick) * pixelsPerTick) + GRAPH_HORIZ_MARGIN;
    }

    private double pixelToTick(double pixel)
    {
        return ((pixel - GRAPH_HORIZ_MARGIN) / pixelsPerTick) + firstTick;
    }

    private void updateVisTicks()
    {
        Rectangle visRect = getVisibleRect();
        minVisTick = (int) Math.floor(pixelToTick(visRect.getMinX()));
        maxVisTick = (int) Math.ceil(pixelToTick(visRect.getMaxX()));
        //System.out.println("vis ticks=" + minVisTick + ":" + maxVisTick);
    }

    private void updateWidth()
    {
        int width;
        if (source != null)
        {
            long lastTick = graph.getLastTime();
            long tickWidth = lastTick - firstTick;
            //System.out.println("real tick width=" + tickWidth);

            // add a screenful of padding if not complete
            if (!source.isComplete())
            {
                updateVisTicks();
                tickWidth += (maxVisTick - minVisTick);
                //System.out.println("padded tick width=" + tickWidth);
            }

            width = (int) Math.ceil(tickWidth * pixelsPerTick)
                + GRAPH_HORIZ_MARGIN * 2;
        }
        else
        {
            width = DEFAULT_GRAPH_WIDTH;
        }

        setWidth(ruler, width);
        setWidth(packetPane, width);

        Container cont = getParent();
        if (cont != null) cont.validate();
    }

    private static void setWidth(JComponent comp, int w)
    {
        Dimension minSize = comp.getMinimumSize();
        if (minSize.width != w)
        {
            comp.setMinimumSize(new Dimension(w, minSize.height));
            comp.invalidate();
        }

        Dimension prefSize = comp.getPreferredSize();
        if (prefSize.width != w)
        {
            comp.setPreferredSize(new Dimension(w, prefSize.height));
            comp.invalidate();
        }
    }

    private static class JPacketGraphHeader
        extends JAbstractPacketGraph
    {
        public JPacketGraphHeader()
        {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        }

        protected JAbstractPacketNode newNode(PacketNode node)
        {
            return new JPacketNodeHeader(node);
        }

        protected JAbstractPacketNode newGroup(PacketNode node)
        {
            JPacketGroupHeader jPacketGroupHeader = new JPacketGroupHeader(node);
            return jPacketGroupHeader;
        }

    }

    private class JPacketGraphRuler
        extends JComponent
    {
        public JPacketGraphRuler()
        {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            setForeground(Color.BLACK);
            setOpaque(true);
            setPreferredSize(new Dimension(DEFAULT_GRAPH_WIDTH, 20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        }

        protected void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int h = getHeight();
            int hMajor = h / 2;
            int hMinor = h / 3;

            double ticksPerMinor = ticksPerMajor / MINOR_PER_MAJOR;

            updateVisTicks();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());

            Font f = g2d.getFont();
            FontRenderContext frc = g2d.getFontRenderContext();

            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(2);

            // loop through visible major ticks            
            double majorTick = Math.floor(minVisTick / ticksPerMajor)
                * ticksPerMajor;
            while (majorTick <= maxVisTick)
            {

                // draw major
                double x = tickToPixel(majorTick);
                g2d.drawLine((int) x, h - hMajor, (int) x, h);

                TextLayout layout = new TextLayout(format.format(majorTick), f,
                    frc);
                Rectangle2D bounds = layout.getBounds();
                layout.draw(g2d, (int) (x - bounds.getWidth() / 2), h - hMajor);

                double minorTick = majorTick;
                for (int i = 1; i < MINOR_PER_MAJOR; ++i)
                {
                    minorTick += ticksPerMinor;

                    // draw minor
                    x = tickToPixel(minorTick);
                    g2d.drawLine((int) x, h - hMinor, (int) x, h);
                }

                majorTick += ticksPerMajor;
            }
        }

    }

    private static class JPacketGraphULCorner
        extends JComponent
    {
        public JPacketGraphULCorner()
        {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
            setOpaque(true);
        }

        protected void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    public class JPacketPane
        extends JComponent
    {
        private HashMap<PacketNode, Integer> nodePosMap;
        private LinkedList<PacketEdge> visPacketEdges;
        private PacketInfo highlightedPacket;

        public JPacketPane()
        {
            setOpaque(false);

            nodePosMap = new HashMap<PacketNode, Integer>();
            visPacketEdges = new LinkedList<PacketEdge>();
        }

        public void invalidateNodePosMap()
        {
            nodePosMap.clear();
        }

        public PacketInfo getPacketAt(int x, int y)
        {
            double xc = x - PACKET_HIT_WIDTH / 2;
            double yc = y - PACKET_HIT_HEIGHT / 2;

            Iterator<PacketEdge> i = visPacketEdges.iterator();
            while (i.hasNext())
            {
                PacketEdge pe = i.next();
                Shape body = pe.edge.getBody();
                if (body.intersects(xc, yc, PACKET_HIT_WIDTH, PACKET_HIT_HEIGHT))
                {
                    return pe.packet;
                }
            }

            return null;
        }

        private PacketEdge findPacketEdge(PacketInfo packet)
        {
            Iterator<PacketEdge> i = visPacketEdges.iterator();
            while (i.hasNext())
            {
                PacketEdge pe = i.next();
                if (pe.packet == packet)
                {
                    return pe;
                }
            }
            return null;
        }

        public void highlightPacket(PacketInfo packet)
        {
            if (highlightedPacket == null)
            {
                xorPacket(packet);
                highlightedPacket = packet;
            }
        }

        public void restorePacket(PacketInfo packet)
        {
            if (highlightedPacket == packet)
            {
                xorPacket(packet);
                highlightedPacket = null;
            }
        }

        private void xorPacket(PacketInfo packet)
        {
            PacketEdge pe = findPacketEdge(packet);
            if (pe != null)
            {
                Graphics2D g2d = (Graphics2D) getGraphics();
                g2d.setXORMode(Color.WHITE);
                g2d.setStroke(edgeStroke);
                pe.edge.paint(g2d);
            }
        }

        private final Shape arrowEnd = Connector.makeArrowEnd();
        private final transient Stroke edgeStroke = new BasicStroke(2);

        private Connector drawEdge(
            Graphics2D g2d,
            double x1,
            double y1,
            double x2,
            double y2,
            String label,
            Color color)
        {
            if (color == null) color = Color.BLACK;
            g2d.setColor(color);
            g2d.setStroke(edgeStroke);

            Shape body = new Line2D.Double(x1, y1, x2, y2);
            Connector conn = new Connector(body, null, arrowEnd);
            conn.paint(g2d);

            if (label != null)
            {
                Font font = g2d.getFont();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                TextLayout layout = new TextLayout(label, font,
                    g2d.getFontRenderContext());

                Rectangle2D bounds = layout.getBounds();

                // Center the label over the line
                double tx = (x1 + x2) / 2 - bounds.getCenterX();
                double ty = (y1 + y2) / 2 - bounds.getCenterY();

                double centerx = (x1 + x2) / 2;
                double centery = (y1 + y2) / 2;

                double height = bounds.getHeight();

                AffineTransform originalTransform = g2d.getTransform();

                // Draw the label
                g2d.setColor(color);

                if (rotateLabels)
                {
                    // Get the angle of the line against the X-axis
                    double theta = Math.atan((y2 - y1) / (x2 - x1));

                    // Rotate the label parallel to the edge
                    g2d.transform(AffineTransform.getRotateInstance(theta,
                        centerx, centery));

                    double heightPadded = 1.1 * height;
                    // Shift the label perpendicular to the edge
                    g2d.transform(AffineTransform.getTranslateInstance(
                        heightPadded * Math.cos(theta), heightPadded
                            * Math.sin(theta)));
                }
                layout.draw(g2d, (float) tx, (float) ty);
                g2d.setTransform(originalTransform);
            }

            return conn;
        }

        protected void paintComponent(Graphics g)
        {
            visPacketEdges.clear();
            highlightedPacket = null;

            if (source == null) return;

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            updateVisTicks();

            if (!source.isComplete())
            {
                try
                {
                    source.fetchUntil(maxVisTick);
                }
                catch (IOException e)
                {
                    // ignored
                }
                updateWidth();
            }

            Collection visPackets = graph.findPackets(minVisTick, maxVisTick);
            Iterator i = visPackets.iterator();
            while (i.hasNext())
            {
                PacketInfo p = (PacketInfo) i.next();

                double fromX = tickToPixel(p.getFromTime());
                double toX = tickToPixel(p.getToTime());

                int fromY = getNodeY(p.getFromNode());
                int toY = getNodeY(p.getToNode());

                Connector edge = drawEdge(g2d, fromX, fromY, toX, toY,
                    p.getLabel(), p.getColor());
                visPacketEdges.add(new PacketEdge(p, edge));
            }
        }

        private int getNodeY(PacketNode node)
        {
            Integer y = nodePosMap.get(node);
            if (y == null)
            {
                JAbstractPacketNode nodeView = getViewForNode(node);
                y = Integer.valueOf(getRelY(nodeView, nodePane)
                    + nodeView.getYOffset());
                nodePosMap.put(node, y);
            }
            return y.intValue();
        }

        private int getRelY(Component comp, Component parent)
        {
            int y = 0;
            while (comp != null && comp != parent)
            {
                y += comp.getY();
                comp = comp.getParent();
            }
            return y;
        }

    }

    private static class PacketEdge
    {
        public PacketInfo packet;
        public Connector edge;

        public PacketEdge(PacketInfo _packet, Connector _edge)
        {
            packet = _packet;
            edge = _edge;
        }

    }

    private class NodePaneComponentListener
        implements ComponentListener
    {
        public void componentHidden(ComponentEvent e)
        {
            // do nothing
        }

        public void componentMoved(ComponentEvent e)
        {
            packetPane.invalidateNodePosMap();
        }

        public void componentResized(ComponentEvent e)
        {
            packetPane.invalidateNodePosMap();
        }

        public void componentShown(ComponentEvent e)
        {
            // do nothing
        }

    }

}
