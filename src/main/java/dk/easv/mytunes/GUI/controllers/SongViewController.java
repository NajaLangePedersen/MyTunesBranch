package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.models.SongModel;

//javaFX imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//java imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SongViewController implements Initializable {
    private SongModel sm;
    private File selectedFile;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtArtist;

    @FXML
    private ComboBox<String> comboBoxGenre;

    @FXML
    private TextField txtTime;

    @FXML
    private TextField txtFile;

    public void setModel(SongModel sm){
        this.sm = sm;
    }

    public void initialize(URL location, ResourceBundle resources){
        comboBoxGenre.getItems().addAll("Rock", "Pop", "Jazz", "Classical", "Hip Hop", "Sound effects");

        comboBoxGenre.getSelectionModel().selectFirst();
    }

    @FXML
    private void onBtnSave(ActionEvent actionEvent){
        String title = txtTitle.getText();
        String artist = txtArtist.getText();
        double length = Double.parseDouble(txtTime.getText());
        String category = comboBoxGenre.getSelectionModel().getSelectedItem();

        if(selectedFile == null){
            displayError(new Exception("No file selected"));
        }

        Song newSong = new Song(-1, title, artist, length, category);

        try {
            sm.createSong(newSong);
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void onBtnChooseFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3 File");

        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 Files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(mp3Filter);

        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome + "/Music");
        if(initialDirectory.exists()){
            fileChooser.setInitialDirectory(initialDirectory);
        }

        Stage stage = (Stage) txtFile.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile != null) {
            txtFile.setText(selectedFile.getAbsolutePath());
        }

    }

    @FXML
    private void onBtnCancel(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something is wrong");
    }
}
