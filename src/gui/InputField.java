package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputField {
	public static Set<String> files = new HashSet<>();
	private static final TextArea textArea = new TextArea();
	private final Tab fileSelectField = new Tab();
	private final String[] supportedTypes = {"txt"};
	private Stage stage;
	private final int maxFileCount = 7;

	public InputField(Stage stage) {
		this.stage = stage;
	}


	public TabPane getControls() {
		TabPane tabPane = new TabPane();

		Tab textInputTab = new Tab();
		textInputTab.setText("Text Field");
		textInputTab.setContent(textArea);

		fileSelectField.setText("SelectFile");
		this.refreshFileSelectControls();	//sets the content of dropArea

		tabPane.getTabs().addAll(textInputTab, fileSelectField);
		return tabPane;
	}

	private VBox getControlsNoFileSelected() {
		VBox parent = new VBox();
		parent.setPadding(new Insets(20));
		parent.setStyle("    -fx-alignment: center;");
		VBox dropZone = new VBox();

		Label titleText = new Label("Drag a text file here");
		Label subtitleText = new Label("Or if you prefer...");
		Button fileSelectBttn = getFileSelectBttnAction();

		dropZone.getChildren().addAll(titleText, subtitleText, fileSelectBttn);
		parent.getChildren().add(dropZone);

		setDropZoneAction(dropZone);

		dropZone.setId("fileDropZoneSmall");
		titleText.setTextFill(Color.web("#707070"));
		subtitleText.setTextFill(Color.web("#707070"));

		titleText.setFont(Font.font(20));
		subtitleText.setFont(Font.font(10));
		return parent;
	}

	private VBox getControlsListFiles() {
		VBox parent = new VBox();
		parent.setPadding(new Insets(20));
		parent.setStyle("    -fx-alignment: center;");
		VBox dropZone = new VBox();
		VBox fileCards = new VBox();
		ScrollPane scrollView = new ScrollPane();
		scrollView.setContent(fileCards);

		for (String file : files) {
			dropZone.getChildren().add(this.fileCard(file));
		}

		VBox bttnContainer = new VBox();
		bttnContainer.styleProperty().setValue("-fx-padding: 1em 0 0 0;");
		Button fileSelectBttn = getFileSelectBttnAction();
		bttnContainer.getChildren().add(fileSelectBttn);

		dropZone.getChildren().addAll(scrollView, bttnContainer);
		parent.getChildren().add(dropZone);

		setDropZoneAction(dropZone);

		dropZone.setId("fileDropZoneLarge");
		return parent;
	}

	private void refreshFileSelectControls() {
		if (files.isEmpty()) {
			fileSelectField.setContent(this.getControlsNoFileSelected());
		}else {
			fileSelectField.setContent(this.getControlsListFiles());
		}
	}

	private void setDropZoneAction(VBox dropZone) {
		dropZone.setOnDragOver(new EventHandler<>() {

			@Override
			public void handle(DragEvent event) {
				String color = "#70aaff";
				if (event.getGestureSource() != dropZone
						&& event.getDragboard().hasFiles() && fileIsValid(event.getDragboard().getFiles())) {
					/* allow for both copying and moving, whatever user chooses */
					event.acceptTransferModes(TransferMode.COPY);
				}else {
					//set color to red
					color = "#ff0000";
				}

				if (files.size()>=maxFileCount) //set color to red
					color = "#ff0000";

				dropZone.styleProperty().setValue("-fx-border-color: "+color+";");
				event.consume();
			}
		});

		dropZone.setOnDragDropped(new EventHandler<>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					addFiles(db.getFiles());
					success = true;
				}
				/* let the source know whether the string was successfully
				 * transferred and used */
				event.setDropCompleted(success);

				event.consume();
			}
		});

		dropZone.setOnDragExited(new EventHandler<>() {

			@Override
			public void handle(DragEvent event) {
				dropZone.styleProperty().setValue("-fx-border-color: #707070;");
				event.consume();
			}
		});
	}

	private void addFiles(List<File> fileList) {
		for (File file : fileList) {
			if (!files.contains(file.getAbsolutePath())) {
				if (files.size()>=this.maxFileCount) {return;}
				files.add(file.getAbsolutePath());
			}
		}
		this.refreshFileSelectControls();
	}

	private void removeFile(String filePath) {
		files.remove(filePath);
		this.refreshFileSelectControls();
	}

	private Button getFileSelectBttnAction() {
		Button fileSelectBttn = new Button("Pick a file from your device");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		fileSelectBttn.setOnAction(new EventHandler<>() {
			public void handle(ActionEvent event) {
				//Opening a dialog box
				fileChooser.showOpenDialog(stage);
			}}
		);

		fileSelectBttn.setId("fileSelectButton");
		return fileSelectBttn;
	}

	private boolean fileIsValid(List<File> files) {
		for (File file : files) {
			if (file==null) {
				return false;
			}

			String fileName = file.getName();
			int dotIndex = fileName.lastIndexOf('.');
			if (dotIndex == -1) {
				return false;
			}
			for (String ext : this.supportedTypes) {
				if (fileName.substring(dotIndex + 1).equals(ext)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private HBox fileCard(String filePath) {
		HBox parent = new HBox();
		parent.setStyle("-fx-pref-width: 100%;");

		String fileName = new File(filePath).getName();
		int charLen = 50;
		if (fileName.length()>charLen) {
			fileName = fileName.substring(0,charLen) + "...";
		}
		Label fileLabel = new Label(fileName);
		fileLabel.setAlignment(Pos.TOP_LEFT);

		Button removeFileBttn = new Button("x");
		removeFileBttn.setAlignment(Pos.TOP_RIGHT);
		removeFileBttn.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				removeFile(filePath);
			}
		});

		parent.getChildren().addAll(fileLabel, removeFileBttn);
		return parent;
	}

}
