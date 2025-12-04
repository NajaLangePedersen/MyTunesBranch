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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class SongViewController implements Initializable {
    private SongModel sm;
    private File selectedFile;
    private Song songToEdit;
    private double length = 0;

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

        txtFile.setEditable(true);
    }

    public void setSongToEdit(Song song){
        this.songToEdit = song;
        if(song != null){
            txtTitle.setText(song.getTitle());
            txtArtist.setText(song.getArtist());
            txtTime.setText(song.getLengthString());
            txtFile.setText(song.getFilePath().toString());
            comboBoxGenre.getSelectionModel().select(song.getCategory());
        }
    }

    private double getLength(String time) {
        //Make String of minutes and seconds into double decimal-minutes
        try {
            String[] timeParts = time.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);

            length = minutes + seconds / 60.0; //laver længden om til decimal-minutter
        } catch (Exception e) {
            displayError(new Exception("Invalid time format"));
        }
        return length;
    }

    @FXML
    private void onBtnSave(ActionEvent actionEvent){
        String title = txtTitle.getText();
        String artist = txtArtist.getText();
        double length = getLength(txtTime.getText());
        String category = comboBoxGenre.getSelectionModel().getSelectedItem();

        //Prioritize txtFile for path; fall back to selectedFile if available
        String filePathStr = txtFile.getText();
        if(selectedFile != null){
            filePathStr = selectedFile.getPath();
        }

        if(filePathStr == null || filePathStr.trim().isEmpty()){
            displayError(new Exception("No file selected"));
            return;
        }
        Path filePath = Paths.get(filePathStr);

        //Make path portable.
        String projectRoot = System.getProperty("user.dir");
        String audioDir = projectRoot + "/src/main/resources/dk/easv/mytunes/audio/";
        if (filePath.startsWith(audioDir)){
            filePathStr = filePathStr.substring(audioDir.length());
        }

        //For absolute paths, check if the file exists
        if(filePath.isAbsolute()) {
            File checkFile = new File(filePathStr);
            if(!checkFile.exists()){
                displayError(new Exception("File does not exist"));
                return;
            }
        }

        try{
            if(songToEdit != null){
                //Update existing song
                songToEdit.setTitle(title);
                songToEdit.setArtist(artist);
                songToEdit.setLength(length);
                songToEdit.setCategory(category);
                songToEdit.setFilePath(filePath);
                sm.updateSong(songToEdit);
            } else{
                //Create a new song
                Song newSong = new Song(-1, title, artist, length, category, filePath);
                sm.createSong(newSong);
            }
        } catch (Exception e) {
            displayError(e);
            return;
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
            // Copy the selected file to the project's audio package if it's not already there
            String projectRoot = System.getProperty("user.dir");
            Path audioDirPath = Paths.get(projectRoot, "src", "main", "resources", "dk", "easv", "mytunes", "audio");
            File audioDir = audioDirPath.toFile();
            if (!audioDir.exists()) {
                boolean created = audioDir.mkdirs(); // Create the directory if it doesn't exist
                if (!created) {
                    displayError(new Exception("Could not create audio directory"));
                    return;
                }
            }

            Path sourcePath = selectedFile.toPath();
            String fileName = selectedFile.getName();
            Path targetPath = audioDirPath.resolve(fileName);

            // Handle name conflicts by appending a number
            int counter = 1;
            while (Files.exists(targetPath)) {
                String newFileName = fileName.replace(".mp3", "-" + counter + ".mp3");
                targetPath = audioDirPath.resolve(newFileName);
                counter++;
            }

            // Copy the file
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

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

    public void setSongTitle(String songTitle){
        txtTitle.setText(songTitle);
    }

    public void setSongArtist(String artist){
        txtArtist.setText(artist);
    }

    public void setSongTime(String time){
        txtTime.setText(time);
    }

    public void setSongFile(String file){
        txtFile.setText(file);
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
        alert.setHeaderText("Error");
        alert.setContentText(t.getMessage());
        alert.showAndWait();
    }
}
