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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;

import com.newisys.apps.pktviz.model.PacketNode;

class JPacketGroup
    extends JAbstractPacketGroup
{
    public JPacketGroup(PacketNode node)
    {
        super(node);

        visibleBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                visibleBorder,
                BorderFactory.createEmptyBorder(TOP_MARGIN, 0, BOTTOM_MARGIN, 0)),
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY)));
    }

    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(getBackground());
        g2d.fill(getClientRect());
    }
}
