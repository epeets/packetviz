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

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.border.Border;

import com.newisys.apps.pktviz.model.PacketNode;

abstract class JAbstractPacketNode
    extends JComponent
{
    protected PacketNode node;
    protected String label;
    protected Border visibleBorder;

    public JAbstractPacketNode(PacketNode node)
    {
        setOpaque(true);

        this.node = node;
        updateFromModel();
    }

    public boolean isGroup()
    {
        return false;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String _label)
    {
        if (!_label.equals(label))
        {
            label = _label;
            repaint();
        }
    }

    public int getYOffset()
    {
        return getHeight() / 2;
    }

    public void updateFromModel()
    {
        setBackground(node.getBgColor());
        setForeground(node.getFgColor());
        setLabel(node.getLabel());
        setName(node.getName());
    }

    protected Rectangle getClientRect()
    {
        if (visibleBorder != null)
        {
            Insets insets = visibleBorder.getBorderInsets(this);
            int x1 = insets.left;
            int x2 = getWidth() - insets.right;
            int y1 = insets.top;
            int y2 = getHeight() - insets.bottom;
            return new Rectangle(x1, y1, x2 - x1, y2 - y1);
        }
        else
        {
            return new Rectangle(getWidth(), getHeight());
        }
    }

}
