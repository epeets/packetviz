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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.BorderFactory;

import com.newisys.apps.pktviz.model.PacketNode;

class JPacketGroupHeader
    extends JAbstractPacketGroup
{
    protected static final int LEFT_MARGIN = 15;

    public JPacketGroupHeader(PacketNode node)
    {
        super(node);

        visibleBorder = BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(visibleBorder,
                BorderFactory.createEmptyBorder(TOP_MARGIN, LEFT_MARGIN,
                    BOTTOM_MARGIN, 0)), BorderFactory.createMatteBorder(1, 0,
                0, 0, Color.GRAY)));
    }

    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        Rectangle clientRect = getClientRect();

        g2d.setColor(getBackground());
        g2d.fill(clientRect);

        if (label != null)
        {
            Font f = g2d.getFont();
            FontRenderContext frc = g2d.getFontRenderContext();
            TextLayout layout = new TextLayout(label, f, frc);

            g2d.setColor(getForeground());
            layout.draw(g2d, 2.0f, (float) clientRect.getMaxY() - 2.0f);
        }
    }
}
