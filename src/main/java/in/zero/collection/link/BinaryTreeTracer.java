package in.zero.collection.link;

import java.util.Arrays;

public class BinaryTreeTracer<T> {

    private final static class DrawableNode<T> {
        T data;
        DrawableNode<T> parent;
        DrawableNode<T> left;
        DrawableNode<T> right;
        int level;
        double leftMargin;

        boolean dir;

        DrawableNode(BinaryTreeNode<T> node, DrawableNode<T> parent, int level, int leftMargin, boolean dir) {
            data = node.data;
            this.level = level;
            this.parent = parent;
            this.leftMargin = leftMargin;
            this.dir = dir;
            if (node.hasLeft()) left = new DrawableNode<>(node.left, this, level + 1, leftMargin - 1, false);
            if (node.hasRight()) right = new DrawableNode<>(node.right, this, level + 1, leftMargin + 1, true);
        }

        @Override
        public String toString() {
            return String.valueOf(data) + ":" + String.valueOf(level) + ":" + String.valueOf(leftMargin) + "->(" + (this.left != null ? "L:" + this.left.toString() : "") + (this.right != null ? "/R:" + this.right.toString() : "") + ")";
        }
    }

    private DrawableNode<T>[] levelOrder;
    private int[] levelIdx;

    private DrawableNode<T> root;

    private final int count;
    private int height;

    public static void main(String[] args) {
        BinarySearchTree<Integer> avl = new BinarySearchTree<>();
        avl.addAll(10, 1, 20, 15, 25, 17, 7, 21, 5, 33, 73, 71, 35, 42, 51, 28, 80, 11, 90);

        BinaryTreeTracer<Integer> tracer = new BinaryTreeTracer<>(avl);
        tracer.draw();
        System.out.println(tracer);
    }

    BinaryTreeTracer(BinaryTree<T> tree) {
        if (tree != null) {
            root = new DrawableNode<T>(tree.root, null, 0, 0, false);
            count = tree.nodesCount;
            levelOrder = getSubTreeNodes(root);
            height = levelOrder[levelOrder.length - 1].level;
            levelIdx = Arrays.stream(levelOrder).mapToInt(node -> node.level).toArray();
        } else {
            throw new IllegalArgumentException("Passed tree object can't be null");
        }
    }


    @SuppressWarnings("unchecked")
    public void draw() {
        int level = height;
        for (int i = levelOrder.length - 1; i > 0; i--) {
            if (levelOrder[i].level == level && levelOrder[i - 1].level == level) {
                if ((levelOrder[i - 1].leftMargin >= levelOrder[i].leftMargin) || (levelOrder[i].leftMargin - levelOrder[i - 1].leftMargin) == 1) {
                    updateTree(i, (levelOrder[i - 1].leftMargin - levelOrder[i].leftMargin) + 2, true);
                }
            } else {
                level = levelOrder[i - 1].level;
            }
        }
        this.updateMargin(this.root);
    }


    private void updateTree(int index, double gap, boolean updateParent) {

        if (levelOrder[index] != null) {
            this.propagateGapDownwards(levelOrder[index], gap);
            if (index + 1 < count && levelIdx[index] == levelIdx[index + 1]) {
                updateTree(index + 1, gap, true);
                if (updateParent) this.updateMargin(levelOrder[index].parent);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private DrawableNode<T>[] getSubTreeNodes(DrawableNode<T> node) {
        DrawableNode<T>[] nodes = new DrawableNode[count];
        int i, j;
        nodes[0] = node;
        for (i = 0, j = 1; i < j; i++) {
            if (nodes[i].left != null) {
                nodes[j++] = nodes[i].left;
            }
            if (nodes[i].right != null) {
                nodes[j++] = nodes[i].right;
            }
        }
        return nodes;
    }

    private void updateMargin(DrawableNode<T> node) {
        double margin = node.leftMargin;
        if (node.left != null) {
            margin = node.right != null ? node.left.leftMargin + (node.right.leftMargin - node.left.leftMargin) / 2 : node.left.leftMargin + 1d;
        } else if (node.right != null) {
            margin = node.right.leftMargin - 1d;
        }

        if (margin != node.leftMargin) {
            if (Math.ceil(margin) != margin) {
                node.leftMargin = Math.ceil(margin);
                if (node.parent != null) this.updateMargin(node.parent);
                //updateTree(ArrayUtils.indexOf(levelOrder, node.right), 1, false);
            } else {
                node.leftMargin = margin;
                if (node.parent != null) this.updateMargin(node.parent);
            }
        }
    }

    private void propagateGapDownwards(DrawableNode<T> node, double gap) {
        node.leftMargin += gap;
        if (node.left != null) propagateGapDownwards(node.left, gap);
        if (node.right != null) propagateGapDownwards(node.right, gap);
    }

    public String toString() {
        return root != null ? root.toString() : "";
    }
}
