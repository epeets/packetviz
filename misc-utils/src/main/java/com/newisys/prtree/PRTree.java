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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.newisys.bintree.BinaryTreeImpl;

public class PRTree extends BinaryTreeImpl {

    public static final long NEG_INF = Long.MIN_VALUE;
    public static final long POS_INF = Long.MAX_VALUE;

	public PRTree() {
		clear();
	}

	PRNode getRoot() {
		return (PRNode) root;
	}

	public void clear() {
		root = new RangeNode(NEG_INF, POS_INF);
	}

	public void add(Interval i) {
		addEndpoint(i.getLowerBound(), i);
        addEndpoint(i.getUpperBound(), i);
        updateCoverage(root, NEG_INF, POS_INF, i);
	}

	private void addEndpoint(long value, Interval i) {
        Node node = root;
        while (true) {
            if (node instanceof PointNode) {
                PointNode thisPoint = (PointNode) node;

                if (value < thisPoint.value) {
                    // add to left child
                    node = thisPoint.left;
                } else if (value > thisPoint.value) {
                    // add to right child
                    node = thisPoint.right;
                } else { // value == thisPoint.value
                    // tree already contains point; simply add to owner list
                    thisPoint.addOwnedBy(i);
                    break;
                }
            } else {
                RangeNode lowerRange = (RangeNode) node;

                // create new Point node at endpoint,
                // which will become parent of split range
                PointNode newPoint = new PointNode(value);
                newPoint.addOwnedBy(i);
                newPoint.swapCoveredBy(lowerRange);

                // create new upper Range node and adjust lower range
                RangeNode upperRange = new RangeNode(value, lowerRange.high);
                lowerRange.high = value;

                // update tree to make Point node parent of both Range nodes
                newPoint.parent = lowerRange.parent;
                newPoint.color = lowerRange.color;
                if (newPoint.parent == null) {
                    root = newPoint;
                } else if (newPoint.parent.left == lowerRange) {
                    newPoint.parent.left = newPoint;
                } else {
                    newPoint.parent.right = newPoint;
                }
                newPoint.left = lowerRange;
                newPoint.right = upperRange;
                lowerRange.parent = newPoint;
                upperRange.parent = newPoint;

                // fix up colors            
                fixAfterInsert(lowerRange);
                fixAfterInsert(upperRange);
                
                break;
            }
        }
	}

    protected void rotateLeft(Node n) {
        Node y = n.right;
        rotateCoverage(n, y, n.left, y.left, y.right);
        super.rotateLeft(n);
    }

    protected void rotateRight(Node n) {
        Node y = n.left;
        rotateCoverage(n, y, n.right, y.right, y.left);
        super.rotateRight(n);
    }

    private void rotateCoverage(Node xn, Node yn, Node an, Node bn, Node cn) {
        PointNode x = (PointNode) xn;
        PointNode y = (PointNode) yn;
        PRNode a = (PRNode) an;
        PRNode b = (PRNode) bn;
        PRNode c = (PRNode) cn;
        Set<Interval> xCoverage = x.getCoveredBy();
        Set<Interval> yCoverage = y.getCoveredBy();

        // move y coverage into y contained-by and child coverage
        y.getContainedBy().addAll(yCoverage);
        b.getCoveredBy().addAll(yCoverage);
        c.getCoveredBy().addAll(yCoverage);
        yCoverage.clear();
        
        // move x coverage into x contained-by and y-coverage
        x.getContainedBy().addAll(xCoverage);
        yCoverage.addAll(xCoverage);
        xCoverage.clear();
        
        // aggregate coverage for all intervals containing x and covering a & b
        Iterator iter = x.getContainedBy().iterator();
        while (iter.hasNext()) {
            Interval i = (Interval) iter.next();
            if (a.isCoveredBy(i) && b.isCoveredBy(i)) {
                x.addCoveredBy(i);
                a.removeCoveredBy(i);
                b.removeCoveredBy(i);
                iter.remove();
            }
        }
    }

    private void updateCoverage(Node node, long min, long max, Interval i) {
        if (node instanceof RangeNode) {
            RangeNode rangeNode = (RangeNode) node;

            // Range nodes should always be covered
            rangeNode.addCoveredBy(i);
        } else {
            PointNode pointNode = (PointNode) node;

            // check whether node is covered by interval
            Interval nodeRange = new SimpleInterval(min, false, max, false);
            if (IntervalUtil.containsInterval(i, nodeRange)) {
                pointNode.addCoveredBy(i);
            }
            else {
                int test = IntervalUtil.testPoint(i, pointNode.value);

                // check whether node is contained by interval
                if ((test & IntervalUtil.CONTAINS) != 0) {
                    pointNode.addContainedBy(i);
                }

                // update coverage for left and right subtrees
                if ((test & IntervalUtil.PRECEDES) != 0) {
                    updateCoverage(pointNode.getLeft(), min, pointNode.value, i);
                }
                if ((test & IntervalUtil.SUCCEEDS) != 0) {
                    updateCoverage(pointNode.getRight(), pointNode.value, max, i);
                }
            }
        }
    }

	public boolean isEmpty() {
		return root instanceof RangeNode;
	}

	public long firstPoint() {
		Node firstNode = firstNode();
		if (firstNode.parent == null) {
			throw new NoSuchElementException();
		}
		return ((PointNode) firstNode.parent).value;
	}

	public long lastPoint() {
		Node lastNode = lastNode();
		if (lastNode.parent == null) {
			throw new NoSuchElementException();
		}
		return ((PointNode) lastNode.parent).value;
	}

	public Set queryPoint(long value) {
		Set<Interval> result = new HashSet<Interval>();

		PRNode cur = (PRNode) root;
		while (true) {
			// accumulate all covering intervals in path to final Point/Range node
			result.addAll(cur.getCoveredBy());

			if (cur instanceof PointNode) {
				PointNode curPoint = (PointNode) cur;
				if (value < curPoint.value) {
					cur = (PRNode) curPoint.left;
				} else if (value > curPoint.value) {
					cur = (PRNode) curPoint.right;
				} else {
					// accumulate containing intervals of matching Point node
					result.addAll(curPoint.getContainedBy());
					// stop when we hit matching Point node
					break;
				}
			} else {
				// stop when we hit a Range node
				break;
			}
		}

		return result;
	}

	public Set queryInterval(Interval i) {
		Set<Interval> result = new HashSet<Interval>();

		// find node representing lower bound
		long value = i.getLowerBound();
		PRNode cur = (PRNode) root;
		while (true) {
			if (cur instanceof PointNode) {
				PointNode curPoint = (PointNode) cur;
				if (value < curPoint.value) {
					cur = (PRNode) curPoint.left;
				} else if (value > curPoint.value) {
					cur = (PRNode) curPoint.right;
				} else { // value == curPoint.value
					// accumulate containing intervals of endpoint Point node
					if (i.isLowerBoundClosed()) {
						result.addAll(curPoint.getContainedBy());
					}
					// stop when we hit matching Point node
					break;
				}
			} else {
				// stop when we hit a Range node
				break;
			}
		}

		// traverse tree until upper bound is reached
		value = i.getUpperBound();
		while (true) {
			// accumulate all covering intervals in traversal to upper bound
			result.addAll(cur.getCoveredBy());

			cur = (PRNode) successor(cur);
			if (cur == null) {
				// stop when last node is reached
				// (query upper bound was greater than largest endpoint)
				break;
			} else { 
                if (cur instanceof PointNode) {
                    PointNode curPoint = (PointNode) cur;
                    if (value < curPoint.value) {
                        // stop when upper bound is exceeded
                        break;
                    } else if (value > curPoint.value) {
                        // accumulate containing intervals of contained Point nodes
                        result.addAll(curPoint.getContainedBy());
                    } else { // value == curPoint.value
                        // accumulate all covering intervals in traversal to upper bound
                        result.addAll(cur.getCoveredBy());
                        // accumulate containing intervals of endpoint Point node
                        if (i.isUpperBoundClosed()) {
                            result.addAll(curPoint.getContainedBy());
                        }
                        // stop when we hit matching Point node
                        break;
                    }
                }
			}
		}

		return result;
	}

	public PointIterator pointIterator() {
		return new MyPointIterator();
	}

	private class MyPointIterator implements PointIterator {

		private Node curNode;

		public MyPointIterator() {
			curNode = firstNode();
			while (curNode instanceof RangeNode) {
				curNode = successor(curNode);
			}
		}

		public boolean hasNext() {
			return curNode != null;
		}

		public long next() {
			if (curNode == null) {
				throw new NoSuchElementException();
			}
			long value = ((PointNode) curNode).value;
			do {
				curNode = successor(curNode);
			} while (curNode != null && curNode instanceof RangeNode);
			return value;
		}
	}

	static abstract class PRNode extends Node {

		private Set<Interval> coveredBy;

		public PRNode() {
			coveredBy = new HashSet<Interval>();
		}

		public PRNode getParent() {
			return (PRNode) parent;
		}

		public PRNode getLeft() {
			return (PRNode) left;
		}

		public PRNode getRight() {
			return (PRNode) right;
		}

		public Set<Interval> getCoveredBy() {
			return coveredBy;
		}

		public void addCoveredBy(Interval i) {
			coveredBy.add(i);
		}

		public void removeCoveredBy(Interval i) {
			coveredBy.remove(i);
		}

		public boolean isCoveredBy(Interval i) {
			return coveredBy.contains(i);
		}
        
        public void swapCoveredBy(PRNode other) {
            Set<Interval> temp = coveredBy;
            coveredBy = other.coveredBy;
            other.coveredBy = temp;
        }
	}

	static class PointNode extends PRNode {

		public long value;
		private Set<Interval> containedBy;
		private Set<Interval> ownedBy;

		public PointNode(long value) {
			this.value = value;
			containedBy = new HashSet<Interval>();
			ownedBy = new HashSet<Interval>();
		}

		public Set<Interval> getContainedBy() {
			return containedBy;
		}

		public void addContainedBy(Interval i) {
			containedBy.add(i);
		}

		public void removeContainedBy(Interval i) {
			containedBy.remove(i);
		}

		public boolean isContainedBy(Interval i) {
			return containedBy.contains(i);
		}

		public Set getOwnedBy() {
			return ownedBy;
		}

		public void addOwnedBy(Interval i) {
			ownedBy.add(i);
		}

		public void removeOwnedBy(Interval i) {
			ownedBy.remove(i);
		}

		public boolean isOwnedBy(Interval i) {
			return ownedBy.contains(i);
		}
        
        public String toString() {
            return "PointNode[" + value + "]";
        }
	}

	static class RangeNode extends PRNode implements Interval {

		public long low;
		public long high;

		public RangeNode(long low, long high) {
			this.low = low;
			this.high = high;
		}

		public long getLowerBound() {
			return low;
		}

		public boolean isLowerBoundClosed() {
			return false;
		}

		public long getUpperBound() {
			return high;
		}

		public boolean isUpperBoundClosed() {
			return false;
		}
        
        public String toString() {
            return "RangeNode[" + low + "," + high + "]";
        }
	}
}
