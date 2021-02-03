package gui;

import javafx.application.Application;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

public class GUI extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			root.setId("root");
			Scene scene = new Scene(root,600,560);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			InputField temp = new InputField(primaryStage);
			TabPane inputControls = temp.getControls();

			VBox convertControls = this.getConvertControls(primaryStage);

			root.getChildren().addAll(inputControls, convertControls);

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private VBox getConvertControls(Stage primaryStage) {
		VBox parent = new VBox();
		parent.setId("convertControls");

		/**
		 The first group of input elements, bpm/key signature/time signature.
		 */
		HBox musicalFields = new HBox();
		musicalFields.getStyleClass().add("container");
		Button convertBttn = new Button("Convert");
		convertBttn.getStyleClass().add("largeBttn");

		TextField bpmField = new TextField();
		bpmField.setPromptText("BPM (optional)");
		bpmField.setPrefColumnCount(10);
		bpmField.getText();

		final ComboBox timeSignatureComboBox = new ComboBox();
		timeSignatureComboBox.getItems().addAll(
				"4/4",
				"2/2",
				"2/4",
				"3/4",
				"3/8",
				"6/8",
				"9/8",
				"12/8",
				"Unknown"
		);

		final ComboBox keySignatureComboBox = new ComboBox();
		keySignatureComboBox.getItems().addAll(
				"A",
				"B",
				"C",
				"D",
				"E",
				"F",
				"G",
				"Unknown"
		);
		musicalFields.getChildren().addAll(bpmField, timeSignatureComboBox, keySignatureComboBox);

		/**
		 *  The second group of input elements, for file name/output folder
		 */
		HBox outputFields = new HBox();

		TextField fileNameField = new TextField();
		fileNameField.setPromptText("Output File Name");
		fileNameField.setPrefColumnCount(10);
		fileNameField.getText();

		TextField folderNameField = new TextField();
		folderNameField.setPromptText("Output Folder");
		folderNameField.setPrefColumnCount(10);
		folderNameField.getText();


		Button openFileDialog = new Button("Select Output Folder");
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("src"));
		openFileDialog.setOnAction(e -> {
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			folderNameField.setText(selectedDirectory.getAbsolutePath());
			System.out.println(selectedDirectory.getAbsolutePath());
		});
		outputFields.getChildren().addAll(fileNameField, folderNameField, openFileDialog);
		outputFields.getStyleClass().add("container");

		//change
		parent.getChildren().addAll(musicalFields, outputFields, convertBttn);

		return parent;
	}
}
