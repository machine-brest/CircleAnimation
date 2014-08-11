package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	public static final double STAGE_WIDTH  = 1024;
	public static final double STAGE_HEIGHT = 600;

	@Override
	public void start(Stage primaryStage) throws Exception {

		Group root  = new Group();
		Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, Color.BLACK);

		double xc = STAGE_WIDTH  / 2 + 0.5;
		double yc = STAGE_HEIGHT / 2 + 0.5;
		double outerRadius = Math.min(STAGE_WIDTH, STAGE_HEIGHT) / 2 - 30;
		double innerRadius = outerRadius * 0.5;

		Circle outerCircle = new Circle(xc, yc, outerRadius +21);
		outerCircle.setFill(Color.web("#dd0000", 0.3));
		outerCircle.setStroke(Color.web("#ffffff", 0.4));
		outerCircle.setStrokeWidth(3);
		root.getChildren().add(outerCircle);

		Circle innerCircle = new Circle(xc, yc, innerRadius);
		innerCircle.setStroke(Color.web("#00dcff", 0.2));
		innerCircle.setVisible(false);
		innerCircle.setStrokeWidth(1);
		root.getChildren().add(innerCircle);

		Group lines = new Group();

		for (int i = 0; i < 24; ++i) {

			double f  = 2 * Math.PI / 24 * i;
			double x0 = xc + outerRadius * Math.cos(f);
			double y0 = yc + outerRadius * Math.sin(f);
			double x1 = xc + outerRadius * Math.cos(f + Math.PI);
			double y1 = yc + outerRadius * Math.sin(f + Math.PI);

			Line line = new Line(x0, y0, x1, y1);

			line.setStroke(Color.web("#000000", 0.3));
			line.setStrokeType(StrokeType.CENTERED);
			line.setSmooth(false);
			line.setStrokeWidth(1);
			line.setVisible(true);
			lines.getChildren().add(line);
		}

		root.getChildren().add(lines);

		Group rotationGroup = new Group();

		double rxc = xc + innerRadius;
		double rotationRadius = outerRadius - innerRadius;

		Circle rotationCircle = new Circle(rxc, yc, rotationRadius);
		rotationCircle.setFill(Color.web("#ff0000", 0.1));
		rotationCircle.setStroke(Color.web("#ffffff", 0.2));
		rotationCircle.setStrokeWidth(1);
		rotationCircle.setVisible(false);
		rotationGroup.getChildren().add(rotationCircle);

		for (int i = 0; i < 12; ++i) {

			double cxi = rxc + rotationRadius * Math.cos(2 * Math.PI / 12 * i);
			double cyi = yc  + rotationRadius * Math.sin(2 * Math.PI / 12 * i);

			Circle circle = new Circle(cxi, cyi, 20);
			circle.setFill(Color.web("#dd0000", 0.3));
			circle.setStroke(Color.web("white", 0.4));
			circle.setStrokeWidth(1);

			Circle center = new Circle(cxi, cyi, 2);
			center.setFill(Color.web("#dd0000", 0.3));
			center.setStroke(Color.web("white", 0.4));
			center.setStrokeWidth(1);

			rotationGroup.getChildren().add(circle);
			rotationGroup.getChildren().add(center);
		}

		root.getChildren().add(rotationGroup);

		PathTransition pathTransition = new PathTransition(Duration.millis(14000), innerCircle, rotationGroup);
		pathTransition.setInterpolator(Interpolator.LINEAR);
		pathTransition.setAutoReverse(false);

		RotateTransition rotateTransition = new RotateTransition(Duration.millis(14000), rotationGroup);
		rotateTransition.setInterpolator(Interpolator.LINEAR);
		rotateTransition.setFromAngle(360);
		rotateTransition.setToAngle(0);
		rotateTransition.setAutoReverse(false);

		rotateTransition.setAxis(innerCircle.getRotationAxis());

		ParallelTransition parallelTransition = new ParallelTransition(rotationGroup, rotateTransition, pathTransition);
		parallelTransition.setInterpolator(Interpolator.LINEAR);
		parallelTransition.setCycleCount(Timeline.INDEFINITE);
		parallelTransition.setAutoReverse(false);
		parallelTransition.play();

		Pane overlay = getOverlay();
		overlay.setLayoutX(STAGE_WIDTH - 200);
		overlay.setLayoutY(20);

		root.getChildren().add(overlay);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Pane getOverlay()
	{
		StackPane pane  = new StackPane();
		Rectangle frame = RectangleBuilder.create()
				.height(200).width(180)
				.fill(Color.web("gray", 0.3)).stroke(Color.GRAY)
				.build();

		pane.getChildren().add(frame);

		VBox vbox = VBoxBuilder.create()
				.padding(new Insets(10, 10, 10, 10))
				.fillWidth(true)
				.spacing(5)
				.build();

		CheckBox checkBoxShowLines1 = CheckBoxBuilder.create()
				.text("Show lines").selected(true)
				.build();

		CheckBox checkBoxShowLines2 = CheckBoxBuilder.create()
				.text("Show lines").selected(true)
				.build();

		vbox.getChildren().addAll(checkBoxShowLines1, checkBoxShowLines2);
		pane.getChildren().add(vbox);

		return pane;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
