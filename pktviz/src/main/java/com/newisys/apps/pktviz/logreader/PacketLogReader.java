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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newisys.apps.pktviz.model.PacketGraph;
import com.newisys.apps.pktviz.model.PacketGraphSource;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;
import com.newisys.apps.pktviz.model.TxnInfo;
import com.newisys.apps.pktviz.model.filter.PacketFilter;
import com.newisys.apps.pktviz.props.GraphProperties;

public final class PacketLogReader
    implements PacketGraphSource
{
    private BufferedReader bufReader;
    private PacketGraph packetGraph;
    private PacketFilter packetFilter;
    private GraphProperties graphProperties;
    private Map<Integer, TxnInfo> txnMap;
    private boolean doneReading;
    private long lastTimeRead;
    private long lineNumber;
    private int version;

    public PacketLogReader(File file, PacketGraph packetGraph)
        throws FileNotFoundException
    {
        this(getBufferedReaderForFile(file), packetGraph);
    }

    private static BufferedReader getBufferedReaderForFile(File file)
        throws FileNotFoundException
    {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        return new BufferedReader(isr);
    }

    public PacketLogReader(BufferedReader bufReader, PacketGraph packetGraph)
    {
        this.bufReader = bufReader;
        this.packetGraph = packetGraph;
        txnMap = new HashMap<Integer, TxnInfo>();
        doneReading = false;
        lastTimeRead = -1;
        lineNumber = 0;
        version = 2;
    }

    public PacketGraph getPacketGraph()
    {
        return packetGraph;
    }

    public PacketFilter getPacketFilter()
    {
        return packetFilter;
    }

    public void setPacketFilter(PacketFilter packetFilter)
    {
        this.packetFilter = packetFilter;
    }

    public GraphProperties getGraphProperties()
    {
        return graphProperties;
    }

    public void setGraphProperties(GraphProperties graphProperties)
    {
        this.graphProperties = graphProperties;
    }

    public boolean isComplete()
    {
        return doneReading;
    }

    public void fetchAll()
        throws IOException
    {
        fetchUntil(Long.MAX_VALUE);
    }

    public void fetchUntil(long untilTime)
        throws IOException
    {
        try
        {
            if (doneReading || lastTimeRead > untilTime) return;

            PacketLogEntry entry = new PacketLogEntry();
            entry.version = version;

            String record;
            while ((record = bufReader.readLine()) != null)
            {
                ++lineNumber;

                // On first line, detect version number header
                if (lineNumber == 1)
                {
                    Pattern versionPattern = Pattern.compile("# Packet Log Version (\\d+)");
                    Matcher versionMatcher = versionPattern.matcher(record);
                    if (versionMatcher.matches())
                    {
                        version = Integer.parseInt(versionMatcher.group(1));
                        System.out.println("Found log version " + version);
                        entry.version = version;
                        continue;
                    }
                    else
                    {
                        System.err.println("Warning: Log version not found; assuming "
                            + version);
                    }
                }

                // Remove comments from end of lines
                int n = record.indexOf('#');
                if (n >= 0)
                {
                    record = record.substring(0, n).trim();
                }
                if (record.length() > 0)
                {
                    try
                    {
                        entry.parseString(record);
                        if (entry.fieldsRead >= 6)
                        {
                            addLogEntry(entry);
                        }
                        else
                        {
                            System.err.println("Warning: Ignoring truncated log record at line "
                                + lineNumber + ": " + record);
                        }
                        if (lastTimeRead > untilTime) return;
                    }
                    catch (RuntimeException e)
                    {
                        doneReading = true;
                        e.printStackTrace();
                        throw new RuntimeException(
                            "Error parsing log record at line " + lineNumber
                                + ": " + record + "\n" + e.toString());
                    }
                }
            }
            packetGraph.allPacketsAddedEvent();
            doneReading = true;
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            throw new RuntimeException("Error on line " + lineNumber
                + " of input file");
        }
    }

    private void addLogEntry(PacketLogEntry entry)
    {
        int txnID = entry.txnID;
        TxnInfo txn;
        if (txnID >= -1)
        {
            Integer txnIDObj = Integer.valueOf(txnID);
            txn = txnMap.get(txnIDObj);
            if (txn == null)
            {
                txn = new TxnInfo(txnID);
                txnMap.put(txnIDObj, txn);
            }
        }
        else if (txnID == -1)
        {
            txn = null;
        }
        else
        {
            assert (false) : "txnID=" + txnID + " on line " + lineNumber;
            txn = null;
        }

        PacketNode fromNode = getNode(entry.fromNode);
        PacketNode toNode = getNode(entry.toNode);

        long sendTime = entry.sendTime;
        long recvTime = entry.recvTime;
        if (sendTime < 0) sendTime = recvTime;

        PacketInfo info = new PacketInfo(txn, fromNode, sendTime, toNode,
            recvTime, entry.packetBits, entry.hasRemoteBits, entry.remoteBits,
            entry.dataDwords, entry.packetName, entry.packetFieldList);

        if (version == 2)
        {
            throw new RuntimeException("Version 2 not supported");
        }
        else if (version == 3)
        {
            txn.setPacketName(entry.packetName);
        }

        // Note: in version == 4, packetName is set by
        // constructor PacketInfo()

        if (matchesFilter(info))
        {
            packetGraph.addPacket(info);
        }

        if (entry.recvTime > lastTimeRead)
        {
            lastTimeRead = entry.recvTime;
        }
    }

    private boolean matchesFilter(PacketInfo packet)
    {
        return packetFilter == null || packetFilter.matches(packet);
    }

    static Pattern namePattern = Pattern.compile("(.+)\\.(.+)");

    private PacketNode getNode(String name)
    {
        PacketNode node = packetGraph.getNode(name);

        if (node == null)
        {
            String baseName;
            PacketNode parent;
            Matcher matcher = namePattern.matcher(name);
            if (matcher.matches())
            {
                String parentName = matcher.group(1);
                baseName = matcher.group(2);
                parent = getNode(parentName);
            }
            else
            {
                baseName = name;
                parent = null;
            }

            node = new PacketNode(name);
            node.setLabel(baseName);
            if (parent != null)
            {
                if (graphProperties != null)
                {
                    int index = graphProperties.getNodePosition(node,
                        parent.getChildList());
                    parent.addChildNode(node, index);
                }
                else
                {
                    parent.addChildNode(node);
                }
            }
            if (graphProperties != null && parent == null)
            {
                int index = graphProperties.getNodePosition(node,
                    packetGraph.getTopLevelNodes());
                packetGraph.addNode(node, true, index);
            }
            else
            {
                packetGraph.addNode(node, parent == null);
            }
        }

        return node;
    }

}
