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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.newisys.util.packet.AbstractFieldDumpListener;
import com.newisys.util.packet.FieldDumpListener;

public class FieldDumpTableModel
    extends AbstractTableModel
{
    private MyFieldDumpListener listener;
    private ArrayList<String> cells;

    public FieldDumpTableModel()
    {
        listener = new MyFieldDumpListener();
        cells = new ArrayList<String>();
    }

    public void clear()
    {
        int rowCount = getRowCount();
        if (rowCount > 0)
        {
            cells.clear();
            fireTableRowsDeleted(0, rowCount - 1);
        }
    }

    public FieldDumpListener getFieldDumpListener()
    {
        return listener;
    }

    public int getRowCount()
    {
        return cells.size() / 2;
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName(int columnIndex)
    {
        return (columnIndex == 0) ? "Field" : "Value";
    }

    public Class< ? > getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return cells.get(rowIndex * 2 + columnIndex);
    }

    private class MyFieldDumpListener
        extends AbstractFieldDumpListener
    {
        public void dumpString(String name, String value)
        {
            int rowCount = getRowCount();
            cells.add(name);
            cells.add(value);
            fireTableRowsInserted(rowCount, rowCount);
        }

    }

}
