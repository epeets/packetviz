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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.model.filter.TxnIdFilter;
import com.newisys.apps.pktviz.model.xform.PacketTimeTransform;
import com.newisys.apps.pktviz.model.xform.SeqTimeTransform;
import com.newisys.apps.pktviz.props.GraphProperties;

public class ViewSettings
    implements PropertyChangeListener
{

    private PacketStyler packetStyler;
    private boolean autoStyled;
    private GraphProperties graphProps;
    private PacketFilter packetFilter;
    private PacketFilter savedPacketFilter;
    private boolean autoFiltered;
    private double pixelsPerTick;
    private PacketTimeTransform timeTransform;

    private PropertyChangeSupport pcs;

    public ViewSettings()
    {
        packetStyler = new PacketStyler();
        autoStyled = true;
        graphProps = null;
        packetFilter = null;
        autoFiltered = false;
        timeTransform = new SeqTimeTransform();

        pcs = new PropertyChangeSupport(this);
    }

    public PacketStyler getPacketStyler()
    {
        return packetStyler;
    }

    public void setPacketStyler(PacketStyler _packetStyler)
    {
        if (!objEquals(_packetStyler, packetStyler))
        {
            PacketStyler oldStyler = packetStyler;
            packetStyler = _packetStyler;
            pcs.firePropertyChange("packetStyler", oldStyler, _packetStyler);
        }
    }

    public boolean isAutoStyled()
    {
        return autoStyled;
    }

    public void setAutoStyled(boolean _autoStyled)
    {
        if (_autoStyled != this.autoStyled)
        {
            boolean oldValue = autoStyled;
            autoStyled = _autoStyled;
            pcs.firePropertyChange("autoStyled", Boolean.valueOf(oldValue),
                Boolean.valueOf(_autoStyled));
            if (autoStyled)
            {
                updateAutoStyle();
            }
        }
    }

    public GraphProperties getGraphProps()
    {
        return graphProps;
    }

    public void setGraphProps(GraphProperties _graphProps)
    {
        if (!objEquals(_graphProps, graphProps))
        {
            GraphProperties oldProps = graphProps;
            graphProps = _graphProps;
            pcs.firePropertyChange("graphProps", oldProps, _graphProps);
        }
    }

    public PacketFilter getPacketFilter()
    {
        return packetFilter;
    }

    public void setPacketFilter(PacketFilter _packetFilter)
    {
        if (!objEquals(_packetFilter, packetFilter))
        {
            PacketFilter oldFilter = packetFilter;
            packetFilter = _packetFilter;
            pcs.firePropertyChange("packetFilter", oldFilter, _packetFilter);
            if (autoStyled) updateAutoStyle();
        }
    }

    public PacketFilter getSavedPacketFilter()
    {
        return savedPacketFilter;
    }

    public void setSavedPacketFilter(PacketFilter _savedPacketFilter)
    {
        savedPacketFilter = _savedPacketFilter;
    }

    public void revertPacketFilter()
    {
        setPacketFilter(savedPacketFilter);
        setSavedPacketFilter(null);
    }

    public boolean isAutoFiltered()
    {
        return autoFiltered;
    }

    public void setAutoFiltered(boolean autoFiltered)
    {
        this.autoFiltered = autoFiltered;
    }

    public double getPixelsPerTick()
    {
        return pixelsPerTick;
    }

    public void setPixelsPerTick(double _pixelsPerTick)
    {
        if (_pixelsPerTick != pixelsPerTick)
        {
            double oldValue = pixelsPerTick;
            pixelsPerTick = _pixelsPerTick;
            pcs.firePropertyChange("pixelsPerTick", new Double(oldValue),
                new Double(_pixelsPerTick));
        }
    }

    public PacketTimeTransform getTimeTransform()
    {
        return timeTransform;
    }

    public void setTimeTransform(PacketTimeTransform _timeTransform)
    {
        if (!objEquals(_timeTransform, timeTransform))
        {
            PacketTimeTransform oldXform = timeTransform;
            timeTransform = _timeTransform;
            pcs.firePropertyChange("timeTransform", oldXform, _timeTransform);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.removePropertyChangeListener(listener);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        if (name.equals("pixelsPerTick"))
        {
            double pixelsPerTick = ((Double) evt.getNewValue()).doubleValue();
            setPixelsPerTick(pixelsPerTick);
        }
    }

    private void updateAutoStyle()
    {
        if (packetFilter instanceof TxnIdFilter)
        {
            setPacketStyler(new PacketStyler(PacketStyler.LABEL_CMD,
                PacketStyler.COLOR_BY_CMD));
        }
        else
        {
            setPacketStyler(new PacketStyler(PacketStyler.LABEL_CMD_TXN,
                PacketStyler.COLOR_BY_TXN));
        }
    }

    private static boolean objEquals(Object a, Object b)
    {
        return (a != null) ? a.equals(b) : (b == null);
    }

}
