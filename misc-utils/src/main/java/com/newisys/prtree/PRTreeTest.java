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

package com.newisys.prtree;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PRTreeTest {

	private static class NamedInterval extends SimpleInterval {

		private String name;

		public NamedInterval(String name, long lowerBound, long upperBound) {
			super(lowerBound, upperBound);
			this.name = name;
		}

		public NamedInterval(
			String name,
			long lowerBound,
			boolean lowerClosed,
			long upperBound,
			boolean upperClosed) {
			super(lowerBound, lowerClosed, upperBound, upperClosed);
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static void main(String[] args) {
		System.out.println("digraph MPRTreeTest {");
		System.out.println(
			"node [shape=record,fontname=\"Helvetica\",fontsize=10,height=0.2,width=0.4,style=solid];");
		System.out.println(
			"edge [color=\"midnightblue\",fontname=\"Helvetica\",fontsize=10,style=solid];");

		PRTree tree = new PRTree();

		if (true) {
			dumpTree(tree.getRoot(), "Start");
			tree.add(new NamedInterval("A", 9, 19));
			dumpTree(tree.getRoot(), "Insert A");
			tree.add(new NamedInterval("B", 2, 7));
			dumpTree(tree.getRoot(), "Insert B");
			tree.add(new NamedInterval("C", 1, true, 3, false));
			dumpTree(tree.getRoot(), "Insert C");
			tree.add(new NamedInterval("D", 17, false, 20, true));
			dumpTree(tree.getRoot(), "Insert D");
			tree.add(new NamedInterval("E", 8, 12));
			dumpTree(tree.getRoot(), "Insert E");
			tree.add(new NamedInterval("F", 18, 18));
			dumpTree(tree.getRoot(), "Insert F");
			tree.add(new NamedInterval("G", PRTree.NEG_INF, false, 17, true));
			dumpTree(tree.getRoot(), "Insert G");
            //System.out.println("# " + getIntervalNames(tree.queryInterval(new SimpleInterval(1, 10))));
		}

		if (false) {
			tree.add(new NamedInterval("A", 1, 3));
			tree.add(new NamedInterval("B", 2, 4));
			tree.add(new NamedInterval("C", 3, 5));
			tree.add(new NamedInterval("D", 4, 6));
			tree.add(new NamedInterval("E", 5, 7));
			tree.add(new NamedInterval("F", 6, 8));
			tree.add(new NamedInterval("G", 7, 9));
			tree.add(new NamedInterval("H", 8, 10));
			dumpTree(tree.getRoot(), "Insert A-H");
		}

		if (false) {
			Random r = new Random();
			for (int i = 0; i < 200; ++i) {
				int low = r.nextInt(200) - 100;
				int high = low + r.nextInt(100);
				tree.add(new NamedInterval("I" + i, low, high));
			}
			dumpTree(tree.getRoot(), "Insert 200 nodes");
		}

		System.out.println("}");
	}

	private static int subgraphID = 0;

	private static String getSubgraphName() {
		return "Tree" + subgraphID;
	}

	private static void dumpTree(PRTree.PRNode tree, String title) {
		System.out.println("subgraph " + getSubgraphName() + " {");
		System.out.println(
			getSubgraphName()
				+ "_Title [style=filled,color=gray80,label=\""
				+ title
				+ "\"];");
		dumpNode(tree);
		System.out.println("}");
		++subgraphID;
	}

	private static void dumpNode(PRTree.PRNode node) {
		String nodeName = getNodeName(node);

		StringBuffer nodeDecl = new StringBuffer(80);
		nodeDecl.append(nodeName);
        nodeDecl.append(" [color=");
        nodeDecl.append(node.color ? "firebrick" : "black");
		nodeDecl.append(",label=\"{");
		if (node instanceof PRTree.PointNode) {
			PRTree.PointNode pointNode = (PRTree.PointNode) node;
			nodeDecl.append(getValueString(pointNode.value));
			appendNodeIntervalSet(nodeDecl, "=", pointNode.getContainedBy());
			appendNodeIntervalSet(nodeDecl, "c", pointNode.getCoveredBy());
			appendNodeIntervalSet(nodeDecl, "ob", pointNode.getOwnedBy());
		} else {
			PRTree.RangeNode rangeNode = (PRTree.RangeNode) node;
			nodeDecl.append('(');
			nodeDecl.append(getValueString(rangeNode.low));
			nodeDecl.append(',');
			nodeDecl.append(getValueString(rangeNode.high));
			nodeDecl.append(')');
			appendNodeIntervalSet(nodeDecl, "c", rangeNode.getCoveredBy());
		}
		nodeDecl.append("}\"];");
		System.out.println(nodeDecl.toString());

		PRTree.PRNode leftNode = node.getLeft();
		if (leftNode != null) {
			System.out.println(nodeName + " -> " + getNodeName(leftNode) + ";");
			dumpNode(leftNode);
		}

		PRTree.PRNode rightNode = node.getRight();
		if (rightNode != null) {
			System.out.println(
				nodeName + " -> " + getNodeName(rightNode) + ";");
			dumpNode(rightNode);
		}
	}

	private static String getNodeName(PRTree.PRNode node) {
		return getSubgraphName()
			+ "_Node"
			+ Integer.toHexString(node.hashCode());
	}

	private static String getValueString(long value) {
		if (value == PRTree.NEG_INF) {
			return "-inf";
		} else if (value == PRTree.POS_INF) {
			return "+inf";
		} else {
			return String.valueOf(value);
		}
	}

	private static void appendNodeIntervalSet(
		StringBuffer nodeDecl,
		String string,
		Set set) {
		String intervalNames = getIntervalNames(set);
		if (intervalNames.length() > 0) {
			nodeDecl.append('|');
			nodeDecl.append(string);
			nodeDecl.append(": ");
			nodeDecl.append(intervalNames);
		}
	}

	private static String getIntervalNames(Set set) {
		StringBuffer buf = new StringBuffer();
		Iterator i = set.iterator();
		boolean first = true;
		while (i.hasNext()) {
			NamedInterval ni = (NamedInterval) i.next();
			if (first) {
				first = false;
			} else {
				buf.append(',');
			}
			buf.append(ni.getName());
		}
		return buf.toString();
	}
}
