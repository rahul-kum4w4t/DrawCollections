package in.zero.collection.link;

class BinaryTreeTracer<T> {

    private final int nodesCount;
    private int index = 0;

    final int height;
    final DrawableNode<T>[] postOrder;

    BinaryTreeTracer(BinaryTree<T> tree) {
        if (tree != null) {
            nodesCount = tree.nodesCount;
            postOrder = new DrawableNode[tree.nodesCount];
            height = tree.getHeight();
            process(new DrawableNode<>(tree.root, 0, 0), height);
        } else {
            throw new IllegalArgumentException("Passed tree object can't be null");
        }
    }

    private void propagateGapDownwards(DrawableNode<?> node, double gap) {
        node.leftMargin += gap;
        if (node.left != null) propagateGapDownwards(node.left, gap);
        if (node.right != null) propagateGapDownwards(node.right, gap);
    }

    private void process(DrawableNode<?> node, final int ht) {

        DrawableNode<?>[] rightContour = null, leftContour = null;
        if (node.left != null) {
            process(node.left, ht - 1);
            rightContour = getRightContour(node.left, ht);
        }
        if (node.right != null) {
            process(node.right, ht - 1);
            leftContour = getLeftContour(node.right, ht);
        }
        postOrder[index++] = (DrawableNode<T>) node;
        if (leftContour != null && rightContour != null) {
            double gap, maxGap = Double.NEGATIVE_INFINITY;
            for (int i = 1; i < ht && leftContour[i] != null && rightContour[i] != null; i++) {
                if (Math.round(leftContour[i].leftMargin - rightContour[i].leftMargin) <= 1d) {
                    gap = rightContour[i].leftMargin - leftContour[i].leftMargin;
                    if (maxGap < gap) maxGap = gap;
                }
            }
            if (maxGap != Double.NEGATIVE_INFINITY) {
                maxGap = Math.round(maxGap) % 2 == 0d ? (Math.round(maxGap) + 2d) / 2d : (Math.round(maxGap) + 3d) / 2d;
                propagateGapDownwards(node.right, maxGap);
                propagateGapDownwards(node.left, -1 * maxGap);
            }
        }
    }

    private DrawableNode<?>[] getRightContour(DrawableNode<?> node, final int subTreeHeight) {
        DrawableNode<?>[] contour = new DrawableNode[subTreeHeight];
        DrawableNode<?>[] queue = new DrawableNode[nodesCount];
        queue[0] = node;
        int count = 1, rightContourCounter = 0;
        for (int i = 0; i < count; i++) {
            if (queue[i].left != null) queue[count++] = queue[i].left;
            if (queue[i].right != null) queue[count++] = queue[i].right;
            if (i + 1 < count && queue[i].level != queue[i + 1].level) {
                contour[rightContourCounter++] = queue[i];
            }
        }
        if (rightContourCounter > 0 && queue[count - 1].level != contour[rightContourCounter - 1].level) {
            contour[rightContourCounter] = queue[count - 1];
        }
        return contour;
    }

    private DrawableNode<?>[] getLeftContour(DrawableNode<?> node, final int subTreeHeight) {
        DrawableNode<?>[] contour = new DrawableNode[subTreeHeight];
        DrawableNode<?>[] stack = new DrawableNode[nodesCount];
        stack[0] = node;
        contour[0] = node;
        for (int i = 0, count = 1, leftCoutourCounter = 1; i < count; i++) {
            if (stack[i].left != null) stack[count++] = stack[i].left;
            if (stack[i].right != null) stack[count++] = stack[i].right;
            if (i - 1 >= 0 && stack[i].level != stack[i - 1].level) {
                contour[leftCoutourCounter++] = stack[i];
            }
        }
        return contour;
    }
}

final class DrawableNode<T> {
    T data;
    DrawableNode<T> left;
    DrawableNode<T> right;
    int level;
    double leftMargin;

    DrawableNode(BinaryTreeNode<T> node, int level, int leftMargin) {
        data = node.data;
        this.level = level;
        this.leftMargin = leftMargin;
        if (node.hasLeft()) left = new DrawableNode<>(node.left, level + 1, leftMargin - 1);
        if (node.hasRight()) right = new DrawableNode<>(node.right, level + 1, leftMargin + 1);
    }

    public String toString() {
        return String.valueOf(data);
    }
}
