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

package com.newisys.bintree;

public class BinaryTreeImpl {

    protected static class Node {

        public static final boolean BLACK = false;
        public static final boolean RED = true;

        public Node parent;
        public Node left;
        public Node right;
        public boolean color;

        public Node() {
            color = BLACK;
        }
    }

    protected Node root;

    protected Node firstNode()
    {
        Node n = root;
        if (n != null) {
            // first entry is leftmost descendent
            while (n.left != null) {
                n = n.left;
            }
        }
        return n;
    }

    protected Node lastNode()
    {
        Node n = root;
        if (n != null) {
            // last entry is rightmost descendent
            while (n.right != null) {
                n = n.right;
            }
        }
        return n;
    }

    protected Node predecessor(Node n)
    {
        if (n.left != null) {
            // predecessor is left child's leftmost descendent
            n = n.left;
            while (n.right != null) {
                n = n.right;
            }
        }
        else {
            // predecessor is first ancestor of which we are a right descendent
            Node child = n;
            n = n.parent;
            while (n != null && child == n.left) {
                child = n;
                n = n.parent;
            }
        }
        return n;
    }

    protected Node successor(Node n)
    {
        if (n.right != null) {
            // successor is right child's leftmost descendent
            n = n.right;
            while (n.left != null) {
                n = n.left;
            }
        }
        else {
            // successor is first ancestor of which we are a left descendent
            Node child = n;
            n = n.parent;
            while (n != null && child == n.right) {
                child = n;
                n = n.parent;
            }
        }
        return n;
    }

    protected void fixAfterInsert(Node n)
    {
        n.color = Node.RED;
        while (n != null && n != root && n.parent.color == Node.RED) {
            if (parentOf(n) == leftOf(parentOf(parentOf(n)))) {
                Node y = rightOf(parentOf(parentOf(n)));
                if (colorOf(y) == Node.RED) {
                    setColor(parentOf(n), Node.BLACK);
                    setColor(y, Node.BLACK);
                    setColor(parentOf(parentOf(n)), Node.RED);
                    n = parentOf(parentOf(n));
                }
                else {
                    if (n == rightOf(parentOf(n))) {
                        n = parentOf(n);
                        rotateLeft(n);
                    }
                    setColor(parentOf(n), Node.BLACK);
                    setColor(parentOf(parentOf(n)), Node.RED);
                    if (parentOf(parentOf(n)) != null) {
                        rotateRight(parentOf(parentOf(n)));
                    }
                }
            }
            else {
                // symmetric
                Node y = leftOf(parentOf(parentOf(n)));
                if (colorOf(y) == Node.RED) {
                    setColor(parentOf(n), Node.BLACK);
                    setColor(y, Node.BLACK);
                    setColor(parentOf(parentOf(n)), Node.RED);
                    n = parentOf(parentOf(n));
                }
                else {
                    if (n == leftOf(parentOf(n))) {
                        n = parentOf(n);
                        rotateRight(n);
                    }
                    setColor(parentOf(n),  Node.BLACK);
                    setColor(parentOf(parentOf(n)), Node.RED);
                    if (parentOf(parentOf(n)) != null) {
                        rotateLeft(parentOf(parentOf(n)));
                    }
                }
            }
        }
        root.color = Node.BLACK;
    }

    protected void fixAfterDelete(Node n)
    {
        while (n != root && colorOf(n) == Node.BLACK) {
            if (n == leftOf(parentOf(n))) {
                Node sib = rightOf(parentOf(n));
                if (colorOf(sib) == Node.RED) {
                    setColor(sib, Node.BLACK);
                    setColor(parentOf(n), Node.RED);
                    rotateLeft(parentOf(n));
                    sib = rightOf(parentOf(n));
                }
                if (colorOf(leftOf(sib))  == Node.BLACK &&
                    colorOf(rightOf(sib)) == Node.BLACK) {
                    setColor(sib,  Node.RED);
                    n = parentOf(n);
                }
                else {
                    if (colorOf(rightOf(sib)) == Node.BLACK) {
                        setColor(leftOf(sib), Node.BLACK);
                        setColor(sib, Node.RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(n));
                    }
                    setColor(sib, colorOf(parentOf(n)));
                    setColor(parentOf(n), Node.BLACK);
                    setColor(rightOf(sib), Node.BLACK);
                    rotateLeft(parentOf(n));
                    n = root;
                }
            }
            else {
                // symmetric
                Node sib = leftOf(parentOf(n));
                if (colorOf(sib) == Node.RED) {
                    setColor(sib, Node.BLACK);
                    setColor(parentOf(n), Node.RED);
                    rotateRight(parentOf(n));
                    sib = leftOf(parentOf(n));
                }
                if (colorOf(rightOf(sib)) == Node.BLACK &&
                    colorOf(leftOf(sib)) == Node.BLACK) {
                    setColor(sib,  Node.RED);
                    n = parentOf(n);
                }
                else {
                    if (colorOf(leftOf(sib)) == Node.BLACK) {
                        setColor(rightOf(sib), Node.BLACK);
                        setColor(sib, Node.RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(n));
                    }
                    setColor(sib, colorOf(parentOf(n)));
                    setColor(parentOf(n), Node.BLACK);
                    setColor(leftOf(sib), Node.BLACK);
                    rotateRight(parentOf(n));
                    n = root;
                }
            }
        }
        setColor(n, Node.BLACK);
    }

    protected void rotateLeft(Node n)
    {
        Node r = n.right;
        n.right = r.left;
        if (r.left != null) {
            r.left.parent = n;
        }
        r.parent = n.parent;
        if (n.parent == null) {
            root = r;
        }
        else if (n.parent.left == n) {
            n.parent.left = r;
        }
        else {
            n.parent.right = r;
        }
        r.left = n;
        n.parent = r;
    }

    protected void rotateRight(Node n)
    {
        Node l = n.left;
        n.left = l.right;
        if (l.right != null) {
            l.right.parent = n;
        }
        l.parent = n.parent;
        if (n.parent == null) {
            root = l;
        }
        else if (n.parent.right == n) {
            n.parent.right = l;
        }
        else {
            n.parent.left = l;
        }
        l.right = n;
        n.parent = l;
    }

    private static Node parentOf(Node n)
    {
        return (n != null) ? n.parent : null;
    }

    private static Node leftOf(Node n)
    {
        return (n != null) ? n.left : null;
    }

    private static Node rightOf(Node n)
    {
        return (n != null) ? n.right : null;
    }

    private static boolean colorOf(Node n)
    {
        return (n != null) ? n.color : Node.BLACK;
    }

    private static void setColor(Node n, boolean color)
    {
        if (n != null) n.color = color;
    }

}
