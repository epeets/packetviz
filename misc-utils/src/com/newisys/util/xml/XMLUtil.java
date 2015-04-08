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

package com.newisys.util.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * A collection of static utility methods for working with XML DOM objects.
 */
public final class XMLUtil
{
    private XMLUtil()
    {
        // prevent instantiation
    }

    /**
	 * Returns the String value of the unqualified attribute with the given
	 * local name belonging to the given element, or null if the attribute is
	 * not present.
	 * 
	 * @param elem an element
	 * @param localName an unqualified attribute name
	 * @return the String value of the attribute, or null if the attribute is
	 *         not present
	 */
    public static String getAttrString(Element elem, String localName)
    {
        Attr attr = elem.getAttributeNodeNS(null, localName);
        String value = (attr != null) ? attr.getValue() : null;
        return value;
    }

    /**
	 * Returns the Boolean value of the unqualified attribute with the given
	 * local name belonging to the given element, or null if the attribute is
	 * not present.
	 * 
	 * @param elem an element
	 * @param localName an unqualified attribute name
	 * @return the Boolean value of the attribute, or null if the attribute is
	 *         not present
	 */
    public static Boolean getAttrBoolean(Element elem, String localName)
    {
        String str = getAttrString(elem, localName);
        if (str != null)
        {
            // valid XML Schema boolean values are 0, 1, false, true
            return new Boolean(str.equals("true") || str.equals("1"));
        }
        else
        {
            return null;
        }
    }

    private static final DateFormat dateFormat = DateFormat.getDateInstance();

    /**
	 * Returns the Date value of the unqualified attribute with the given local
	 * name belonging to the given element, or null if the attribute is not
	 * present. The date format must match the default for the locale, as
	 * returned by DateFormat.getDateInstance().
	 * 
	 * @param elem an element
	 * @param localName an unqualified attribute name
	 * @return the Date value of the attribute, or null if the attribute is not
	 *         present
	 */
    public static Date getAttrDate(Element elem, String localName)
    {
        String str = getAttrString(elem, localName);
        if (str != null)
        {
            try
            {
                return dateFormat.parse(str);
            }
            catch (ParseException ignored)
            {
            }
        }
        return null;
    }

    /**
	 * Returns the value of the first child node of the given node, and asserts
	 * that the child node is a Text node.
	 * 
	 * @param node a node containing a single Text child node
	 * @return the value of the child Text node
	 */
    public static String getChildText(Node node)
    {
        Node child = node.getFirstChild();
        assert(child.getNodeType() == Node.TEXT_NODE);
        return child.getNodeValue();
    }

    /**
	 * Returns a String containing the concatenation of text contained in the
	 * given node. This includes Text node children, and Text node children of
	 * Element nodes and their Element node children.
	 * 
	 * @param node a Node object
	 * @return a concatenation of the descendent text
	 */
    public static String getChildrenText(Node node)
    {
        StringBuffer buf = new StringBuffer();
        getChildrenText(node.getChildNodes(), buf);
        return buf.toString();
    }

    /**
	 * Returns a String containing the concatenation of text contained in the
	 * given node list. This includes Text nodes in the list, and Text node
	 * children of Element nodes and their Element node children.
	 * 
	 * @param nodeList a NodeList object
	 * @return a concatenation of the descendent text
	 */
    public static String getChildrenText(NodeList nodeList)
    {
        StringBuffer buf = new StringBuffer();
        getChildrenText(nodeList, buf);
        return buf.toString();
    }

    private static void getChildrenText(NodeList nodeList, StringBuffer buf)
    {
        int len = nodeList.getLength();
        for (int i = 0; i < len; ++i)
        {
            Node child = nodeList.item(i);
            while (child != null)
            {
                short nodeType = child.getNodeType();
                switch (nodeType)
                {
                    case Node.TEXT_NODE :
                        buf.append(child.getNodeValue());
                        break;
                    case Node.ELEMENT_NODE :
                        getChildrenText(child.getChildNodes(), buf);
                        break;
                }
                child = child.getNextSibling();
            }
        }
    }

    /**
	 * Dumps a debug listing of the child nodes of the given node to
	 * System.out.
	 * 
	 * @param node the node to dump the children of
	 */
    public static void dumpChildren(Node node)
    {
        System.out.println(
            "Children of "
                + node.getNodeName()
                + ", NS: "
                + node.getNamespaceURI()
                + ", Type: " + node.getClass());
        Node child = node.getFirstChild();
        while (child != null)
        {
            short nodeType = child.getNodeType();
            String nodeName = child.getNodeName();
            String nodeValue = child.getNodeValue();
            String nsURI = child.getNamespaceURI();
            System.out.println(
                "  Type: "
                    + nodeType
                    + ", Name: "
                    + nodeName
                    + ", Value: "
                    + nodeValue
                    + ", NS: "
                    + nsURI
                    + ", Type: " + node.getClass()
                    );
            child = child.getNextSibling();
        }
    }

    /**
	 * Dumps a debug listing of the attributes of the given element to
	 * System.out.
	 * 
	 * @param elem the element to dump the attributes of
	 */
    public static void dumpAttrs(Element elem)
    {
        System.out.println(
            "Attributes of "
                + elem.getNodeName()
                + ", NS: "
                + elem.getNamespaceURI());
        NamedNodeMap attrs = elem.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; ++i)
        {
            Node attr = attrs.item(i);
            System.out.println(
                "  Name: "
                    + attr.getNodeName()
                    + ", Value: "
                    + attr.getNodeValue()
                    + ", NS: "
                    + attr.getNamespaceURI());
        }
    }
    
    /**
     * Print out the node in XML format
     * 
     * @param node
     * @param out
     */
    public static void printNode(Node node, PrintStream out)
    {
        Document doc = new DocumentImpl();
        Element topLevel = doc.createElement("xml");
        doc.appendChild(topLevel);
        topLevel.appendChild(doc.importNode(node, true));
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(out, format);
        try
        {
            serializer.serialize(doc);
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }

    }
}
