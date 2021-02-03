package gui;
	
import javafx.application.Application;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class GUI extends Application {
	//some change on develop branch
	//I am making a change to the develop2 branch---
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			root.setId("root");
			Scene scene = new Scene(root,600,480);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			InputField temp = new InputField(primaryStage);
			TabPane inputControls = temp.getControls();
			
			VBox convertControls = this.getConvertControls();
			
			root.getChildren().addAll(inputControls, convertControls);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private VBox getConvertControls() {
		VBox parent = new VBox();
		parent.setId("convertControls");
		
		Label heading = new Label("Convert");
		Separator separator = new Separator();
		HBox convertBttns = new HBox();
		parent.getChildren().addAll(heading, separator, convertBttns);
		
		Button guitarBttn = new Button("Guitar");
		Button drumsBttn = new Button("Drums");
		convertBttns.getChildren().addAll(guitarBttn, drumsBttn);

		heading.getStyleClass().add("headingText");
		convertBttns.setId("convertBttnContainer");
		guitarBttn.getStyleClass().add("largeBttn");
		drumsBttn.getStyleClass().add("largeBttn");


		
		return parent;
	}
}
