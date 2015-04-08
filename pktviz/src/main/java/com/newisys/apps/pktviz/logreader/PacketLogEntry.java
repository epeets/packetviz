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

package com.newisys.apps.pktviz.logreader;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newisys.util.format.SizedIntegerFormat;

/** 
 * <PRE>
 * Field formats: Version 2
 * # Packet Log Version 2
 * # Recv Time,Send Time,Txn,From,To,Packet Bits,Remote Bits,Data
 * 
 * Version 3 - Packets as string rather than data - used for "drawing" packet flows
 * # Packet Log Version 3
 * # Recv Time,Send Time,Txn,From,To,Packet Name
 *
 * Version 4 - Packets as "name1=value1; name2=value2"
 * </PRE>
 */
public class PacketLogEntry
{
    int version = 1;
    long recvTime;
    long sendTime;
    int txnID;
    String fromNode;
    String toNode;
    String packetName; // Version 3
    long packetBits;
    String packetFieldList; // Version 4
    boolean hasRemoteBits;
    short remoteBits;
    int[] dataDwords;
    int fieldsRead;

    //                                                            #1       #2      #3     #4      #5   #6
    private static Pattern logLineExprVer4 = Pattern.compile("([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),(.*)");

    public void parseString(String record)
    {
        recvTime = -1;
        sendTime = -1;
        txnID = -1;
        fromNode = null;
        toNode = null;
        packetBits = -1;
        hasRemoteBits = false;
        remoteBits = -1;
        dataDwords = null;
        fieldsRead = 0;
        packetName = null;
        packetFieldList = null;

        assert (version == 4);
        if (version == 4)
        {
            Matcher fieldMatcher = logLineExprVer4.matcher(record);
            if (fieldMatcher.matches())
            {
                recvTime = Long.parseLong(fieldMatcher.group(1));
                sendTime = Long.parseLong(fieldMatcher.group(2));
                txnID = Integer.parseInt(fieldMatcher.group(3));
                fromNode = fieldMatcher.group(4);
                toNode = fieldMatcher.group(5);
                packetFieldList = fieldMatcher.group(6);
                fieldsRead = 6;
            }
            else
            {
                throw new RuntimeException("Could not parse: " + record);
            }
        }
        else
        {
            //#0       #1       #2                   #3                  #4               #5   #6       #7
            //21491000,21489600,0, system.box0.cpu0,system.box0.cpu0,a5a5a5a5a5a5a5a5a5a5,    ,00000000
            int fieldID = 0;
            StringTokenizer tokenizer = new StringTokenizer(record, ",", true);
            while (tokenizer.hasMoreTokens())
            {

                // field 1 was introduced in version 2
                if (fieldID == 1 && version < 2)
                {
                    ++fieldID;
                }

                String field = tokenizer.nextToken();
                if (field.equals(","))
                {
                    ++fieldID;
                }
                else
                {
                    switch (fieldID)
                    {
                        case 0:
                            if (!field.equals("X"))
                            {
                                recvTime = Long.parseLong(field);
                            }
                            break;
                        case 1:
                            if (!field.equals("X"))
                            {
                                sendTime = Long.parseLong(field);
                            }
                            break;
                        case 2:
                            if (!field.equals("X"))
                            {
                                txnID = Integer.parseInt(field);
                            }
                            break;
                        case 3:
                            fromNode = field;
                            break;
                        case 4:
                            toNode = field;
                            break;
                        case 5:
                            switch (version)
                            {
                                case 2:
                                    packetBits = SizedIntegerFormat.parseUnsignedLong(
                                        field, 16);
                                    break;
                                case 3:
                                    packetName = field;
                                    break;
                                case 4:
                                    packetFieldList = field;
                                    break;
                            }
                            break;
                        case 6:
                            hasRemoteBits = true;
                            remoteBits = Short.parseShort(field, 16);
                            break;
                        case 7:
                            int dwordCount = field.length() / 8;
                            dataDwords = new int[dwordCount];
                            for (int i = 0; i < dwordCount; ++i)
                            {
                                long l = Long.parseLong(field.substring(i * 8,
                                    (i + 1) * 8), 16);
                                dataDwords[i] = (int) l;
                            }
                            break;
                        default:
                            throw new RuntimeException("Unhandled case: "
                                + fieldID);
                    }
                    fieldsRead = fieldID + 1;
                }
            }
        }
    }
}
