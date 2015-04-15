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

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import com.newisys.apps.pktviz.model.PacketGraph;
import com.newisys.apps.pktviz.model.PacketGraphListener;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;
import com.newisys.apps.pktviz.model.TxnInfo;

public class PacketStyler
    implements PacketGraphListener
{

    public static final int LABEL_CMD_TXN = 0;
    public static final int LABEL_CMD = 1;
    public static final int LABEL_TXN = 2;

    public static final int COLOR_BY_TXN = 0;
    public static final int COLOR_BY_CMD = 1;

    private int labelMode;
    private int colorMode;

    public PacketStyler()
    {
        labelMode = LABEL_CMD_TXN;
        colorMode = COLOR_BY_TXN;
    }

    public PacketStyler(int _labelMode, int _colorMode)
    {
        labelMode = _labelMode;
        colorMode = _colorMode;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof PacketStyler))
        {
            return false;
        }

        PacketStyler other = (PacketStyler) obj;
        return (labelMode == other.labelMode && colorMode == other.colorMode);
    }

    public int hashCode()
    {
        return 31 * labelMode + colorMode;
    }

    public int getColorMode()
    {
        return colorMode;
    }

    public void setColorMode(int colorMode)
    {
        this.colorMode = colorMode;
    }

    public int getLabelMode()
    {
        return labelMode;
    }

    public void setLabelMode(int labelMode)
    {
        this.labelMode = labelMode;
    }

    public void updateStyle(PacketInfo info)
    {
        TxnInfo txn = info.getTxn();
        String cmd = info.getPacketName();

        String label = null;

        switch (labelMode)
        {
            case LABEL_CMD:
                label = cmd;
                break;
            case LABEL_TXN:
                label = (txn != null) ? String.valueOf(txn.getTxnID()) : "";
                break;
            case LABEL_CMD_TXN:
                label = cmd + (txn != null ? ":" + txn.getTxnID() : "");
                break;
        }

        Color color = null;
        switch (colorMode)
        {
            case COLOR_BY_TXN:
                color = (txn != null) ? getTxnColor(txn.getTxnID())
                    : Color.BLACK;
                break;
            case COLOR_BY_CMD:
                color = getTxnColor(hashTo6Bits(cmd.hashCode()));
                break;
        }

        info.disableUpdateEvents();
        try
        {
            if (label != null)
            {
                info.setLabel(label);
            }
            if (color != null)
            {
                info.setColor(color);
            }
        }
        finally
        {
            info.enableUpdateEvents();
        }
    }

    private static int hashTo6Bits(int h) {
        return ((h) ^ (h >>> 6) ^ (h >>> 12) ^ (h >>> 18) ^ (h >>> 24)) & 63;
    }

    public void updateAll(PacketGraph graph)
    {
        Collection c = graph.getPackets();
        Iterator i = c.iterator();
        while (i.hasNext())
        {
            PacketInfo info = (PacketInfo) i.next();
            updateStyle(info);
        }
    }

    private static Color getTxnColor(int id)
    {
        return getCmdColor(id & 63);
    }

    private static Color getCmdColor(int cmd)
    {
        int rw = (cmd >> 4) & 3;
        int gw = (cmd >> 2) & 3;
        int bw = (cmd >> 0) & 3;
        return new Color(rw * 50, gw * 50, bw * 50);
    }

    private static final Color DARK_GREEN = new Color(0, 128, 0);
    private static final Color DARK_MAGENTA = new Color(128, 0, 128);


    public void nodeAdded(PacketNode node, boolean topLevel)
    {
        // do nothing
    }

    public void nodeUpdated(PacketNode node)
    {
        // do nothing
    }

    public void packetAdded(PacketInfo packet)
    {
        updateStyle(packet);
    }

    public void packetUpdated(PacketInfo packet)
    {
        // do nothing
    }

    public void filterChanged()
    {
        // do nothing
    }
}
