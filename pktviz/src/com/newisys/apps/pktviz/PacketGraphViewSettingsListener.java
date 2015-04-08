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

import com.newisys.apps.pktviz.model.PacketGraph;
import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.model.xform.PacketTimeTransform;
import com.newisys.apps.pktviz.view.JPacketGraph;

public class PacketGraphViewSettingsListener
    implements PropertyChangeListener
{

    private JPacketGraph packetGraphView;
    private PacketGraph graph;

    public PacketGraphViewSettingsListener()
    {
    }

    public void setPacketGraphView(JPacketGraph jpg)
    {
        packetGraphView = jpg;
    }

    public void setPacketGraph(PacketGraph pg)
    {
        graph = pg;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {

        String name = evt.getPropertyName();
        if (name.equals("packetStyler"))
        {
            if (graph == null) return;
            PacketStyler styler = (PacketStyler) evt.getNewValue();
            styler.updateAll(graph);
        }
        else if (name.equals("packetFilter"))
        {
            if (graph == null) return;
            PacketFilter filter = (PacketFilter) evt.getNewValue();
            graph.setPacketFilter(filter);
        }
        else if (name.equals("pixelsPerTick"))
        {
            if (packetGraphView == null) return;
            double pixelsPerTick = ((Double) evt.getNewValue()).doubleValue();
            packetGraphView.setPixelsPerTick(pixelsPerTick);
        }
        else if (name.equals("timeTransform"))
        {
            if (graph == null) return;
            PacketTimeTransform xform = (PacketTimeTransform) evt.getNewValue();
            graph.setTimeTransform(xform);
        }
    }

}
