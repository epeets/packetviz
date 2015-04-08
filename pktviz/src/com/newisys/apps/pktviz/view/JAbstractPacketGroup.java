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

import com.newisys.apps.pktviz.model.PacketNode;

abstract class JAbstractPacketGroup
    extends JAbstractPacketNode
{
    protected static final int TOP_MARGIN = 5;
    protected static final int BOTTOM_MARGIN = 15;

    public JAbstractPacketGroup(PacketNode node)
    {
        super(node);

        setLayout(new SimpleBoxLayout(this, SimpleBoxLayout.Y_AXIS));
    }

    public boolean isGroup()
    {
        return true;
    }

    public int getYOffset()
    {
        return getHeight() - (BOTTOM_MARGIN / 2);
    }

}
