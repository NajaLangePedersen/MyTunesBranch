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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

//java imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        //lav minutter og sekunder om til en double
        String time = txtTime.getText();
        double length = 0;
        try {
            String[] timeParts = time.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);

            length = minutes + seconds / 60.0; //laver længden om til decimal-minutter
        } catch (Exception e) {
            displayError(new Exception("Invalid time format"));
            return;
        }

        String category = comboBoxGenre.getSelectionModel().getSelectedItem();
        Path filePath = Paths.get(selectedFile.getPath());

        if(selectedFile == null){
            displayError(new Exception("No file selected"));
        }

        Song newSong = new Song(-1, title, artist, length, category, filePath);

        try {
            sm.createSong(newSong);
        } catch (Exception e) {
            displayError(e);
        }

        Node source = (Node) actionEvent.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
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

            //find length of file and display it in the time textfield
            try {
                Media media = new Media(selectedFile.toURI().toString());
                MediaPlayer tempPlayer = new MediaPlayer(media); //need temporary mediaplayer to get length of song

                //when media player is ready, display length of song
                tempPlayer.setOnReady(() -> {
                    Duration duration = media.getDuration();
                    int minutes = (int) duration.toMinutes(); //total length of song in minutes
                    int seconds = (int) (duration.toSeconds() % 60); //total length of song in seconds - but only what's left after diving with 60 (the minutes)

                    txtTime.setText(String.format("%02d:%02d", minutes, seconds)); //% = format specifier, 02 = min 2 cifre, fill with 0 in front if necessary, d = whole number
                });
            } catch (Exception e) {
                e.printStackTrace();
                txtTime.setText("Error");
            }
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
