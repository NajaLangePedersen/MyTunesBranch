package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.models.PlaylistModel;
import dk.easv.mytunes.GUI.models.SongModel;

//javaFX imports
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

//java imports
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyTunesController implements Initializable {
    @FXML
    private TextField txtSearch;

    private PlaylistModel pm;
    private SongModel sm;

    private PlaylistViewController pc = new PlaylistViewController();
    private SongViewController sc = new SongViewController();

    @FXML
    private TableView <Playlist> tblPlaylists;

    @FXML
    private TableView <Song> tblSongs;

    @FXML
    private ListView<Song> lstPlaylistSongs;

    public MyTunesController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {
            pm = new PlaylistModel();
            sm = new SongModel();

            //connect tableviews to filtered list
            tblSongs.setItems(sm.getFilteredList());
            tblPlaylists.setItems(pm.getFilteredList());

            //live searching
            txtSearch.textProperty().addListener((observableValue, oldValue, newValue) ->
            {
                sm.getFilteredList().setPredicate(song -> {
                    //If filter text is empty, display all movies
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else {
                        return song.getCategory().toLowerCase().contains(lowerCaseFilter);
                    }

                });
            });

            //sort lists
            SortedList<Song> sortedSongData = new SortedList<>(sm.getFilteredList());
            sortedSongData.comparatorProperty().bind(tblSongs.comparatorProperty());
            tblSongs.setItems(sortedSongData);

            SortedList<Playlist> sortedPlaylistData = new SortedList<>(pm.getFilteredList());
            sortedPlaylistData.comparatorProperty().bind(tblPlaylists.comparatorProperty());
            tblPlaylists.setItems(sortedPlaylistData);

        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreatePlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/PlaylistView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Playlist View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onEditPlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/PlaylistView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Playlist View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onDeletePlaylist(ActionEvent actionEvent) throws IOException {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null){
            try{
                pm.deletePlaylist(selectedPlaylist);
            }
            catch (Exception err) {
                displayError(err);
            }
        }
    }

    @FXML
    private void onCreateSong(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/SongView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Song View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onEditSong(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/SongView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Song View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onDeleteSong(ActionEvent actionEvent) throws IOException {

        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null){
            try{
                sm.deleteSong(selectedSong);
            }
            catch (Exception err) {
                displayError(err);
            }
        }
    }

    @FXML
    private void onBtnUp(ActionEvent actionEvent) {

    }

    @FXML
    private void onBtnDown(ActionEvent actionEvent) {

    }

    @FXML
    private void onDeletePlaylistSong(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedPlaylistSong = lstPlaylistSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedPlaylistSong != null ){
            try{
                sm.deleteSong(selectedPlaylistSong);
            }
            catch (Exception err) {
                displayError(err);
            }
        }

    }

    @FXML
    private void onBtnAddToPlaylist(ActionEvent actionEvent) {

        //Song sele
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something is wrong");
    }
}


