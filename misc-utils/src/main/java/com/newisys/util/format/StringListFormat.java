/*
 * Misc-Utils - Miscellaneous Utility Classes
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

package com.newisys.util.format;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class StringListFormat
{
    /**
     * Converts a comma-delimited string into a list of strings.
     *
     * @param   s   a String containing a comma-delimited list of items
     *
     * @return  a List containing the substrings
     */
    public static List parse(String s)
    {
        List<String> v = new LinkedList<String>();
        if (s != null) {
            // remove enclosing [], if present
            int len = s.length();
            if (len >= 2 &&
                s.charAt(0) == '[' &&
                s.charAt(len - 1) == ']') {
                s = s.substring(1, len - 1);
            }

            // add trimmed comma-separated substrings to vector
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                v.add(token.trim());
            }
        }
        return v;
    }

    /**
     * Converts a list of strings into a comma-delimited string.
     *
     * @param   v   the List to convert
     *
     * @return  a String containing the comma-delimited list of items
     */
    public static String format(List v)
    {
        StringBuffer buf = new StringBuffer();
        if (v != null) {
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                if (buf.length() > 0) buf.append(',');
                buf.append(i.next().toString());
            }
        }
        return buf.toString();
    }
}

