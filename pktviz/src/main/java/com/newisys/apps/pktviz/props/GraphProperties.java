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
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.newisys.apps.pktviz.model.PacketGraphListener;
import com.newisys.apps.pktviz.model.PacketInfo;
import com.newisys.apps.pktviz.model.PacketNode;

public final class GraphProperties
    implements PacketGraphListener
{
    private List<NodeUpdateInfo> nodeUpdateList;
    private HashMap<String, NodeUpdateInfo> namedNodeUpdateMap;

    public GraphProperties()
    {
        nodeUpdateList = new LinkedList<NodeUpdateInfo>();
        namedNodeUpdateMap = new HashMap<String, NodeUpdateInfo>();
    }

    public List<NodeUpdateInfo> getNodeUpdateList()
    {
        return nodeUpdateList;
    }

    public NodeUpdateInfo getNodeUpdate(String id)
    {
        return namedNodeUpdateMap.get(id);
    }

    public int getNodePosition(PacketNode node, List siblings)
    {
        int pos = siblings.size();

        NodeUpdateInfo info = findMatchingNodeUpdateWithPos(node);
        While: while (info != null)
        {
            Switch: switch (info.posMode)
            {
                case NodeUpdateInfo.POS_MODE_FIRST:
                    pos = 0;
                    break While;
                case NodeUpdateInfo.POS_MODE_AFTER:
                    NodeUpdateInfo refInfo = getNodeUpdate(info.posRefID);
                    if (refInfo != null)
                    {
                        int index = findMatchingNodeIndex(refInfo, siblings);
                        if (index >= 0)
                        {
                            pos = index + 1;
                            break While;
                        }
                        info = refInfo;
                        break Switch;
                    }
                    else
                    {
                        // bad element reference
                        break While;
                    }
                default:
                    throw new RuntimeException("Unexpected case: "
                        + info.posMode);
            }
        }
        return pos;
    }

    private NodeUpdateInfo findMatchingNodeUpdateWithPos(PacketNode node)
    {
        Iterator<NodeUpdateInfo> i = nodeUpdateList.iterator();
        while (i.hasNext())
        {
            NodeUpdateInfo info = i.next();
            if (info.matches(node)
                && info.posMode != NodeUpdateInfo.POS_MODE_NONE)
            {
                return info;
            }
        }
        return null;
    }

    private static int findMatchingNodeIndex(NodeUpdateInfo info, List nodeList)
    {
        ListIterator i = nodeList.listIterator();
        while (i.hasNext())
        {
            PacketNode node = (PacketNode) i.next();
            if (info.matches(node))
            {
                return i.previousIndex();
            }
        }
        return -1;
    }

    public void applyNodeUpdates(PacketNode node)
    {
        String name = node.getName();
        Iterator<NodeUpdateInfo> i = nodeUpdateList.iterator();
        while (i.hasNext())
        {
            NodeUpdateInfo info = i.next();
            if (info.name == null)
            {
                applyNodeUpdates(node, info, null);
            }
            else if (info.pattern != null)
            {
                Matcher matcher = info.pattern.matcher(name);
                if (matcher.find())
                {
                    applyNodeUpdates(node, info, matcher);
                }
            }
            else if (info.name.equals(name))
            {
                applyNodeUpdates(node, info, null);
            }
        }
    }

    private void applyNodeUpdates(
        PacketNode node,
        NodeUpdateInfo info,
        Matcher nameMatcher)
    {
        node.disableUpdateEvents();
        try
        {
            if (info.label != null)
            {
                String label = replaceRefs(info.label, nameMatcher);
                node.setLabel(label);
            }
            if (info.bgColor != null)
            {
                node.setBgColor(info.bgColor);
            }
            if (info.fgColor != null)
            {
                node.setFgColor(info.fgColor);
            }
        }
        finally
        {
            node.enableUpdateEvents();
        }
    }

    private static Pattern refPattern = Pattern.compile("\\$([0-9]+)");

    private static String replaceRefs(String s, Matcher nameMatcher)
    {
        if (nameMatcher != null)
        {
            Matcher refMatcher = refPattern.matcher(s);
            StringBuffer sb = new StringBuffer();
            while (refMatcher.find())
            {
                String nStr = refMatcher.group(1);
                int n = Integer.parseInt(nStr);
                refMatcher.appendReplacement(sb, nameMatcher.group(n));
            }
            refMatcher.appendTail(sb);
            s = sb.toString();
        }
        return s;
    }

    public void readXml(File file)
    {
        Document doc;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.toString());
        }

        Node child = doc.getFirstChild();
        while (child != null)
        {
            short nodeType = child.getNodeType();
            String nodeName = child.getNodeName();
            if (nodeType == Node.ELEMENT_NODE && nodeName.equals("packetgraph"))
            {
                readPacketGraph(child);
            }
            else if (nodeType != Node.TEXT_NODE)
            {
                System.err.println("readXml: Unknown node: " + nodeName);
            }
            child = child.getNextSibling();
        }
    }

    private void readPacketGraph(Node node)
    {
        Node child = node.getFirstChild();
        while (child != null)
        {
            short nodeType = child.getNodeType();
            String nodeName = child.getNodeName();
            if (nodeType == Node.ELEMENT_NODE && nodeName.equals("nodes"))
            {
                readNodes(child);
            }
            else if (nodeType != Node.TEXT_NODE)
            {
                System.err.println("readPacketGraph: Unknown node: " + nodeName);
            }
            child = child.getNextSibling();
        }
    }

    private void readNodes(Node node)
    {
        Node child = node.getFirstChild();
        while (child != null)
        {
            short nodeType = child.getNodeType();
            String nodeName = child.getNodeName();
            if (nodeType == Node.ELEMENT_NODE && nodeName.equals("update"))
            {
                readNodeUpdate(child);
            }
            else if (nodeType != Node.TEXT_NODE)
            {
                System.err.println("readNodes: Unknown node: " + nodeName);
            }
            child = child.getNextSibling();
        }
    }

    private void readNodeUpdate(Node node)
    {
        NodeUpdateInfo info;
        {
            NamedNodeMap attrs = node.getAttributes();
            Node attr = attrs.getNamedItem("name");
            if (attr != null)
            {
                info = new NodeUpdateInfo(attr.getNodeValue(), false);
            }
            else
            {
                attr = attrs.getNamedItem("pattern");
                if (attr != null)
                {
                    info = new NodeUpdateInfo(attr.getNodeValue(), true);
                }
                else
                {
                    info = new NodeUpdateInfo();
                }
            }
        }
        nodeUpdateList.add(info);

        Node idAttr = node.getAttributes().getNamedItem("id");
        if (idAttr != null)
        {
            info.id = idAttr.getNodeValue();
            namedNodeUpdateMap.put(info.id, info);
        }

        Node child = node.getFirstChild();
        while (child != null)
        {
            short nodeType = child.getNodeType();
            String nodeName = child.getNodeName();
            if (nodeType == Node.ELEMENT_NODE && nodeName.equals("label"))
            {

                StringBuffer sb = new StringBuffer();
                Node labelChild = child.getFirstChild();
                while (labelChild != null)
                {
                    if (labelChild.getNodeType() == Node.TEXT_NODE)
                    {
                        sb.append(labelChild.getNodeValue());
                    }
                    labelChild = labelChild.getNextSibling();
                }
                info.label = sb.toString();

            }
            else if (nodeType == Node.ELEMENT_NODE
                && nodeName.equals("bgcolor"))
            {

                NamedNodeMap attrs = child.getAttributes();
                Node attr = attrs.getNamedItem("value");
                if (attr != null)
                {
                    info.bgColor = Color.decode(attr.getNodeValue());
                }
                else
                {
                    System.err.println("readNodeUpdate: No value specified for bgcolor");
                }

            }
            else if (nodeType == Node.ELEMENT_NODE
                && nodeName.equals("fgcolor"))
            {

                NamedNodeMap attrs = child.getAttributes();
                Node attr = attrs.getNamedItem("value");
                if (attr != null)
                {
                    info.fgColor = Color.decode(attr.getNodeValue());
                }
                else
                {
                    System.err.println("readNodeUpdate: No value specified for fgcolor");
                }

            }
            else if (nodeType == Node.ELEMENT_NODE
                && nodeName.equals("position"))
            {

                NamedNodeMap attrs = child.getAttributes();
                Node modeAttr = attrs.getNamedItem("absolute");
                if (modeAttr != null)
                {
                    String mode = modeAttr.getNodeValue();
                    if ("first".equals(mode))
                    {
                        info.posMode = NodeUpdateInfo.POS_MODE_FIRST;
                    }
                    else
                    {
                        System.err.println("readNodeUpdate: Invalid position mode: "
                            + mode);
                    }
                }
                else
                {
                    modeAttr = attrs.getNamedItem("relative");
                    if (modeAttr != null)
                    {
                        Node refIDAttr = attrs.getNamedItem("refid");
                        if (refIDAttr != null)
                        {
                            String mode = modeAttr.getNodeValue();
                            if ("after".equals(mode))
                            {
                                info.posMode = NodeUpdateInfo.POS_MODE_AFTER;
                                info.posRefID = refIDAttr.getNodeValue();
                            }
                            else
                            {
                                System.err.println("readNodeUpdate: Invalid position mode: "
                                    + mode);
                            }
                        }
                        else
                        {
                            System.err.println("readNodeUpdate: No refid specified for relative position");
                        }
                    }
                    else
                    {
                        System.err.println("readNodeUpdate: Position mode not specified");
                    }
                }

            }
            else if (nodeType != Node.TEXT_NODE)
            {
                System.err.println("readNodeUpdate: Unknown node: " + nodeName);
            }
            child = child.getNextSibling();
        }
    }

    public void nodeAdded(PacketNode node, boolean topLevel)
    {
        applyNodeUpdates(node);
    }

    public void nodeUpdated(PacketNode node)
    {
        // do nothing
    }

    public void packetAdded(PacketInfo packet)
    {
        // do nothing
    }

    public void packetUpdated(PacketInfo packet)
    {
        // do nothing
    }

    public void filterChanged()
    {
        // do nothing
    }

}
