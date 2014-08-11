package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application
{
	private class AppController
	{
		private Pane root;

		@FXML private Pane contentPane;
		@FXML private Pane commandPane;

		public void start(Stage primaryStage) {

			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(AppController.class.getResource("view/app-shell.fxml"));
				loader.setController(this);

				root = loader.load();
			}
			catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}

			setUserAgentStylesheet(STYLESHEET_MODENA);

			Scene scene = new Scene(root);

			initContentPane(contentPane);

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Circle rotation animation");
			primaryStage.show();
		}

		protected void initContentPane(Pane parent) {

			parent.autosize();

			double stageWidth  = parent.getWidth();
			double stageHeight = parent.getHeight();

			double xc = stageWidth / 2 + 0.5;
			double yc = stageHeight / 2 + 0.5;
			double outerRadius = Math.min(stageWidth, stageHeight) / 2 - 30;
			double innerRadius = outerRadius * 0.5;

			Circle outerCircle = new Circle(xc, yc, outerRadius +21);
			outerCircle.setFill(Color.web("#dd0000", 0.3));
			outerCircle.setStroke(Color.web("#ffffff", 0.4));
			outerCircle.setStrokeWidth(3);
			parent.getChildren().add(outerCircle);
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
