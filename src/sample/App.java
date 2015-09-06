package sample;

import com.sun.javafx.perf.PerformanceTracker;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application
{
	private static PerformanceTracker perfTracker;

	private class AppController implements Initializable
	{
		private Pane root;

		/**
		 * UI control binding
		 */
		@FXML private Pane contentPane;
		@FXML private Pane commandPane;
		@FXML private CheckBox showLinesCheckbox;
		@FXML private CheckBox showInnerCircleCheckbox;
		@FXML private CheckBox showRotateCircleCheckbox;
		@FXML private Button playButton;
		@FXML private Button aboutButton;
		@FXML private Label fpsLabel;

		/**
		 * Properties
		 */
		private SimpleDoubleProperty sceneWidth = new SimpleDoubleProperty(800);
		private SimpleDoubleProperty sceneHeight = new SimpleDoubleProperty(600);
		private SimpleBooleanProperty showLines = new SimpleBooleanProperty(false);
		private SimpleBooleanProperty showInnerCircle = new SimpleBooleanProperty(false);
		private SimpleBooleanProperty showRotateCircle = new SimpleBooleanProperty(false);

		ParallelTransition transition;

		private final long[] frameTimes = new long[100];
		private int frameTimeIndex = 0;
		private boolean arrayFilled = false;

		public void start(Stage primaryStage) {

			try {

				setUserAgentStylesheet(STYLESHEET_MODENA);

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(AppController.class.getResource("view/app-shell.fxml"));
				loader.setController(this);
				root = loader.load();
			}
			catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}

			initContentPane(contentPane);

			primaryStage.setScene(new Scene(root));
			primaryStage.setResizable(false);
			primaryStage.setTitle("Circle rotation animation");
			primaryStage.show();

			initFpsMeter(fpsLabel);
		}

		protected void initContentPane(Pane parent)
		{
			parent.setPrefSize(sceneWidth.get(), sceneHeight.get());
			parent.autosize();

			double outerRadius = Math.min(sceneHeight.get(), sceneHeight.get()) / 2 - 30;
			double innerRadius = outerRadius * 0.5;
			double xc = sceneWidth.get()  / 2 + 0.5;
			double yc = sceneHeight.get() / 2 + 0.5;

			Circle outerCircle = new Circle(xc, yc, outerRadius + 21);
			outerCircle.getStyleClass().add("outer-circle");

			Circle innerCircle = new Circle(xc, yc, innerRadius);
			innerCircle.visibleProperty().bindBidirectional(showInnerCircle);
			innerCircle.getStyleClass().add("inner-circle");

			parent.getChildren().addAll(outerCircle, innerCircle);

			Group lines = new Group();

			for (int i = 0; i < 24; ++i) {

				double f  = 2 * Math.PI / 24 * i;
				double x0 = xc + outerRadius * Math.cos(f);
				double y0 = yc + outerRadius * Math.sin(f);
				double x1 = xc + outerRadius * Math.cos(f + Math.PI);
				double y1 = yc + outerRadius * Math.sin(f + Math.PI);

				Line line = new Line(x0, y0, x1, y1);
				line.getStyleClass().add("radial-line");
				line.visibleProperty().bindBidirectional(showLines);
				lines.getChildren().add(line);
			}

			parent.getChildren().add(lines);

			Group rotationGroup = new Group();

			double rxc = xc + innerRadius;
			double rotateRadius = outerRadius - innerRadius;

			Circle rotateCircle = new Circle(rxc, yc, rotateRadius);
			rotateCircle.getStyleClass().add("rotate-circle");
			rotateCircle.visibleProperty().bindBidirectional(showRotateCircle);
			rotationGroup.getChildren().add(rotateCircle);

			for (int i = 0; i < 12; ++i) {

				double cxi = rxc + rotateRadius * Math.cos(2 * Math.PI / 12 * i);
				double cyi = yc  + rotateRadius * Math.sin(2 * Math.PI / 12 * i);

				Circle ball = new Circle(cxi, cyi, 20);
				ball.getStyleClass().add("rotate-ball");
				rotationGroup.getChildren().add(ball);
			}

			PathTransition pt = new PathTransition(Duration.millis(9000), innerCircle, rotationGroup);
			pt.setInterpolator(Interpolator.LINEAR);
			pt.setAutoReverse(false);

			RotateTransition rt = new RotateTransition(Duration.millis(9000), rotationGroup);
			rt.setInterpolator(Interpolator.LINEAR);
			rt.setFromAngle(360);
			rt.setToAngle(0);
			rt.setAutoReverse(false);

			transition = new ParallelTransition(rotationGroup, rt, pt);
			transition.setInterpolator(Interpolator.LINEAR);
			transition.setCycleCount(Timeline.INDEFINITE);
			transition.setAutoReverse(false);

			parent.getChildren().add(rotationGroup);
		}

		private float getFPS()
		{
			float fps = perfTracker.getAverageFPS();
			perfTracker.resetAverageFPS();
			return fps;
		}

		public void initFpsMeter(Label fpsLabel)
		{
			AnimationTimer animationTimer = new AnimationTimer()
			{
				@Override
				public void handle(long now)
				{
					long oldFrameTime = frameTimes[frameTimeIndex];
					frameTimes[frameTimeIndex] = now;
					frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;

					if (frameTimeIndex == 0) {
						arrayFilled = true;
					}

					if (arrayFilled) {
						long elapsedNanos = now - oldFrameTime;
						long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
						double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
						fpsLabel.setText(String.format("fps: %.2f", frameRate));
					}
				}
			};

			animationTimer.start();
		}

		@Override
		public void initialize(URL location, ResourceBundle resources) {

			showLinesCheckbox.selectedProperty().bindBidirectional(showLines);
			showInnerCircleCheckbox.selectedProperty().bindBidirectional(showInnerCircle);
			showRotateCircleCheckbox.selectedProperty().bindBidirectional(showRotateCircle);

			aboutButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("I have a great message for you!");
					alert.showAndWait();
				}
			});

			playButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (transition.getStatus() == Animation.Status.RUNNING) {
						stopAnimation();
						playButton.setText("Play");
					} else {
						playAnimation();
						playButton.setText("Stop");
					}
				}
			});
		}

		@FXML
		public void playAnimation() {
			transition.play();
		}

		@FXML
		public void stopAnimation() {
			transition.jumpTo(Duration.ZERO);
			transition.stop();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		new AppController().start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
