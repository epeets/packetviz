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

package com.newisys.apps.pktviz.props;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newisys.apps.pktviz.model.PacketNode;

class NodeUpdateInfo
{
    public static final int POS_MODE_NONE = 0;
    public static final int POS_MODE_FIRST = 1;
    public static final int POS_MODE_AFTER = 2;

    public String id;
    public String name;
    public Pattern pattern;
    public String label;
    public Color bgColor;
    public Color fgColor;
    public int posMode;
    public String posRefID;

    public NodeUpdateInfo()
    {
    }

    public NodeUpdateInfo(String _name, boolean isPattern)
    {
        name = _name;
        if (isPattern)
        {
            pattern = Pattern.compile(name);
        }
    }

    public boolean matches(PacketNode node)
    {
        boolean matches = false;

        if (name == null)
        {
            matches = true;
        }
        else
        {
            String nodeName = node.getName();

            if (pattern != null)
            {
                Matcher matcher = pattern.matcher(nodeName);
                if (matcher.find())
                {
                    matches = true;
                }
            }
            else if (name.equals(nodeName))
            {
                matches = true;
            }
        }

        return matches;
    }

}
