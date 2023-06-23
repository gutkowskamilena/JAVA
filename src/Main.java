import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    DocumentService documentService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ekstraktor słów kluczowych TF-IDF");

        //Definicje obiektów w okienku
        FileChooser fileChooser = new FileChooser();
        Button openButton = new Button("Otwórz plik:");
        TextArea textArea = new TextArea();

        Label label = new Label("Podaj liczbę wyrazów do zwrócenia (N): ");
        TextField textField = new TextField(); // tutaj podajemy N

        // alert o podaniu N
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(null);
        alert.setContentText("Podaj liczbę wyrazów do zwrócenia (N).");

        // czytanie textField - przyjmuje tylko wartości int - pełne liczby
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // główny przycisk
        openButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            String input = textField.getText();
            if (file != null && !input.isEmpty()) {
                documentService = new DocumentService(file.getAbsolutePath());
                String summary = documentService.process(Integer.valueOf(input));
                textArea.setText(summary);
            } else {
                alert.showAndWait();
            }
        });

        VBox vbox = new VBox(label, textField, openButton, textArea);
        Scene scene = new Scene(vbox, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
