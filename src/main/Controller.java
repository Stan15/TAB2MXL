package main;

import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import parser.Parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class Controller {
    @FXML private AnchorPane anchorPane;

    @FXML private TextArea inputField;

    @FXML
    private void convertButtonHandle() {
        Parser p = new Parser(inputField.getText());
        String output = p.parse();

        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extFilter);


        File file = fc.showSaveDialog( anchorPane.getScene().getWindow() );

        if (file != null) {
            saveToFile(output, file);
        }
    }

    private void saveToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
