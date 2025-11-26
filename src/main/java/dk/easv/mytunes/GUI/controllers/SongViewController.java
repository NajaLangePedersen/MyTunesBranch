package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.models.SongModel;

//javaFX imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

//java imports
import java.net.URL;
import java.util.ResourceBundle;

public class SongViewController { //implements Initializable {
    private SongModel sm;

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

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle){

    }*/

    @FXML
    private void onBtnSave(ActionEvent actionEvent){
        String title = txtTitle.getText();
        String artist = txtArtist.getText();
        double length = Double.parseDouble(txtTime.getText());
        String category = comboBoxGenre.getValue();

        //file???

        Song newSong = new Song(-1, title, artist, length, category);

        try {
            sm.createSong(newSong);
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void onBtnChooseFile(ActionEvent actionEvent){

    }

    @FXML
    private void onBtnCancel(ActionEvent actionEvent){

    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something is wrong");
    }
}
