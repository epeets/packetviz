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

package com.newisys.apps.pktviz.model.filter;

import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.TxnInfo;

public class TxnIdFilter
    implements PacketFilter
{

    private int txnID;

    public TxnIdFilter(int _txnID)
    {
        txnID = _txnID;
    }

    public boolean matches(PacketInfo packet)
    {
        TxnInfo txn = packet.getTxn();
        return (txn != null && txn.getTxnID() == txnID);
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof TxnIdFilter))
        {
            return false;
        }

        TxnIdFilter other = (TxnIdFilter) obj;
        return (txnID == other.txnID);
    }

    public int getTxnID()
    {
        return txnID;
    }

    /**
     * Override hashCode.
     *
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + txnID;
        return hashCode;
    }

}
