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

package com.newisys.apps.pktviz.model;

import com.newisys.util.packet.FieldDumpListener;
import com.newisys.util.packet.StringFieldDumpListener;

public final class TxnInfo
{
    private int txnID;
    private Address addr;
    private String packetName;

    public TxnInfo(int _txnID)
    {
        txnID = _txnID;
        addr = null;
    }

    public int getTxnID()
    {
        return txnID;
    }

    public String getPacketName()
    {
        return packetName;
    }

    public void setPacketName(String packetName_)
    {
        packetName = packetName_;
    }

    public Address getAddr()
    {
        return addr;
    }

    public long getAddrValue()
    {
        return addr != null ? addr.getAddr() : 0;
    }

    public void setAddrValue(long _addr)
    {
        addr = Address.makeAddress(_addr);
    }

    public boolean isAddrSet()
    {
        return addr != null;
    }

    public void dumpTo(FieldDumpListener fdl)
    {
        fdl.dumpInt("TxnID", txnID);
        fdl.dumpAddr("TxnAddr", getAddrValue());
    }

    public String toString()
    {
        FieldDumpListener fdl = new StringFieldDumpListener();
        dumpTo(fdl);
        return fdl.toString();
    }

}
