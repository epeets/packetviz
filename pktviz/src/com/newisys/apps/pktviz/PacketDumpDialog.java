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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.newisys.util.packet.FieldDumpListener;

public class PacketDumpDialog
    extends JDialog
{

    private FieldDumpTableModel tableModel;

    public PacketDumpDialog(Frame owner) throws HeadlessException
    {
        super(owner, "Packet Dump");
        setResizable(true);

        tableModel = new FieldDumpTableModel();

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 300));

        setContentPane(scrollPane);
    }

    public void clear()
    {
        tableModel.clear();
    }

    public FieldDumpListener getFieldDumpListener()
    {
        return tableModel.getFieldDumpListener();
    }

}
