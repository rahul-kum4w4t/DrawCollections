package in.zero.collection.link;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class DrawBinaryTree extends Application {

    private final static double HR_GAP = 40;
    private final static double VR_GAP = 50;

    public static void main(String[] args) {
        launch(args);
    }

    private List<Integer> getRandomSequenceInRange(int start, int end) {
        List<Integer> randomizedList = IntStream.range(start, end).boxed().collect(Collectors.toList());
        Collections.shuffle(randomizedList);
        return randomizedList;
    }

    @Override
    public void start(Stage stage) {
        //Integer[] data = new Integer[]{44, 40, 32, 38, 12, 37, 20, 8, 43, 15, 17, 3, 4, 5, 22, 45, 11, 10, 28, 6, 9, 30, 7, 46, 18, 48, 31, 21, 29, 34, 25, 19, 23, 16, 36, 42, 39, 35, 13, 2, 26, 14, 49, 1, 47, 33, 27, 41, 24};
        //int[] data = IntStream.range(0, 75).toArray();
        Integer[] data = getRandomSequenceInRange(1, 75).toArray(new Integer[]{});
        System.out.println(Arrays.toString(data));
        BinaryTree<Integer> tree = new Heap<>();
        Arrays.stream(data).forEach(tree::addAll);
        BinaryTreeTracer<Integer> tracer = new BinaryTreeTracer<>(tree);
        double min = 0, max = 0;

        for (DrawableNode<Integer> node : tracer.postOrder) {
            if (node.leftMargin < min) min = node.leftMargin;
            if (node.leftMargin > max) max = node.leftMargin;
        }

        final double pad = min < 0 ? Math.abs(min) : 0;

        Pane pane = new Pane();
        ObservableList<Node> children = pane.getChildren();

        pane.setPrefWidth(((max - min) * HR_GAP) + 2 * HR_GAP);
        pane.setMaxWidth(((max - min) * HR_GAP) + 2 * HR_GAP);
        pane.setPrefHeight((tracer.height + 1) * VR_GAP + VR_GAP);
        pane.setMaxHeight((tracer.height + 1) * VR_GAP + VR_GAP);
        pane.setBackground(Background.EMPTY);

        Arrays.stream(tracer.postOrder).forEach(node -> {
            double x = (HR_GAP * (node.leftMargin + pad)) + HR_GAP;
            double y = (node.level + 1) * VR_GAP;

            if (node.left != null) {
                children.add(getLine(x, y, node.left.leftMargin, node.left.level, pad));
            }
            if (node.right != null) {
                children.add(getLine(x, y, node.right.leftMargin, node.right.level, pad));
            }
        });

        Arrays.stream(tracer.postOrder).forEach(node -> {
            double x = (HR_GAP * (node.leftMargin + pad)) + HR_GAP;
            double y = (node.level + 1) * VR_GAP;

            createTreeNode(x, y, 20, String.valueOf(node.data), "black").forEach(children::add);
        });

        Scene scene = new Scene(new ScrollPane(pane));
        stage.setScene(scene);
        stage.setTitle(tree.getClass().getSimpleName() + " Map");
        stage.show();
    }

    private Line getLine(double x, double y, double leftMargin, int level, double pad) {
        double rx = (HR_GAP * (leftMargin + pad)) + HR_GAP;
        double ry = (level + 1) * VR_GAP;

        Line ln = new Line();
        ln.setStartX(x);
        ln.setStartY(y);
        ln.setEndX(rx);
        ln.setEndY(ry);
        ln.setStroke(Color.DARKSLATEGREY);
        return ln;
    }

    private Stream<Node> createTreeNode(double x, double y, double r, String text, String color) {
        Circle cr = new Circle();
        cr.setCenterX(x);
        cr.setCenterY(y);
        cr.setRadius(r);
        cr.setFill(Paint.valueOf(color));

        Label lbl = new Label();
        lbl.setText(text);
        lbl.setLayoutX(x - r);
        lbl.setLayoutY(y - r);
        lbl.setPrefHeight(r * 2);
        lbl.setPrefWidth(r * 2);
        lbl.setAlignment(Pos.BASELINE_CENTER);
        lbl.setTextFill(Paint.valueOf("white"));
        lbl.setFont(Font.font(12));

        return Stream.of(cr, lbl);
    }
}
