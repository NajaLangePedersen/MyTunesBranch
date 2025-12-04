package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;
import dk.easv.mytunes.GUI.models.PlaylistModel;
import dk.easv.mytunes.GUI.models.SongModel;

//javaFX imports
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

//java imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MyTunesController implements Initializable {
    @FXML
    private TextField txtSearch;

    private PlaylistModel pm;
    private SongModel sm;

    private MediaPlayer mp;

    private boolean isPaused = false;

    private Song currentSong;

    private ObservableList<Playlist> observablePlaylist = FXCollections.observableArrayList();

    @FXML
    private TableView <Playlist> tblPlaylists;

    private ObservableList<Song> playlistSongObservable = FXCollections.observableArrayList();

    @FXML
    private TableView <Song> tblSongs;

    @FXML
    private ListView<Song> lstPlaylistSongs;

    @FXML
    private TableColumn<Playlist, String> colPlaylistName;
    @FXML
    private TableColumn<Playlist, String> colPlaylistTime;
    @FXML
    private TableColumn<Playlist, Integer> colPlaylistSongs;
    @FXML
    private TableColumn<Song, String> colSongTitle;
    @FXML
    private TableColumn<Song, String> colSongArtist;
    @FXML
    private TableColumn<Song, String> colSongCategory;
    @FXML
    private TableColumn<Song, String> colSongTime;

    @FXML
    private Label lblPlaylistName, lblPlayingSong;

    @FXML
    private Button btnPlay;
    @FXML
    private Slider volumeSlider;


    public MyTunesController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {
            pm = new PlaylistModel();
            sm = new SongModel();

            colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("Name"));
            colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("nrOfSongs"));
            colPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("totalLength"));

            colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
            colSongCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colSongTime.setCellValueFactory(new PropertyValueFactory<>("lengthString"));

            //connect tableviews to filtered list / observableList
            tblSongs.setItems(sm.getFilteredList());
            tblPlaylists.setItems(pm.getFilteredList());

            tblPlaylists.setItems(observablePlaylist);

            lstPlaylistSongs.setItems(playlistSongObservable);

            search();
            /**
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
             */

            //sort lists
            SortedList<Song> sortedSongData = new SortedList<>(sm.getFilteredList());
            sortedSongData.comparatorProperty().bind(tblSongs.comparatorProperty());
            tblSongs.setItems(sortedSongData);

            SortedList<Playlist> sortedPlaylistData = new SortedList<>(pm.getFilteredList());
            sortedPlaylistData.comparatorProperty().bind(tblPlaylists.comparatorProperty());
            tblPlaylists.setItems(sortedPlaylistData);

            onSelectedPlaylist();

            volumeSlider.setMin(0.0); //mute
            volumeSlider.setMax(1);
            volumeSlider.setValue(0.5);

            doubleClickHandlers();

        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    private void search(){
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
    }

    private void openPlaylistView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/PlaylistView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        PlaylistViewController pvc = fxmlLoader.getController();
        pvc.setModel(this.pm);
        stage.setTitle("Playlist View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onCreatePlaylist(ActionEvent actionEvent) throws IOException {
        openPlaylistView();
    }

    @FXML
    private void onEditPlaylist(ActionEvent actionEvent) throws IOException {
        openPlaylistView();
    }

    @FXML
    private void onDeletePlaylist(ActionEvent actionEvent) throws IOException {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            try{
                pm.deletePlaylist(selectedPlaylist);
            }
            catch (Exception err) {
                displayError(err);
            }
        }
    }

    private void openSongView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/SongView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        SongViewController svc = fxmlLoader.getController();
        svc.setModel(this.sm);
        stage.setTitle("Song View");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private void onCreateSong(ActionEvent actionEvent) throws IOException {
        openSongView();
    }

    @FXML
    private void onEditSong(ActionEvent actionEvent) throws IOException {
        /*Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null){
            openSongView(selectedSong);

        }
        else openSongView(null);*/

    }

    @FXML
    private void onDeleteSong(ActionEvent actionEvent) throws IOException {
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            try {
                sm.deleteSong(selectedSong);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onBtnUp(ActionEvent actionEvent) {
        int index = lstPlaylistSongs.getSelectionModel().getSelectedIndex();
        if(index > 0){
            Collections.swap(playlistSongObservable, index, index-1);
            lstPlaylistSongs.getSelectionModel().select(index-1);

            Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
            if(selectedPlaylist != null) {
                try {
                    pm.updateSongOrder(selectedPlaylist.getId(), new ArrayList<>(playlistSongObservable));

                } catch (Exception e) {
                    displayError(e);
                }
            }
        }

    }

    @FXML
    private void onBtnDown(ActionEvent actionEvent) {
        int index = lstPlaylistSongs.getSelectionModel().getSelectedIndex();
        if(index < playlistSongObservable.size()-1){
            Collections.swap(playlistSongObservable, index, index+1);
            lstPlaylistSongs.getSelectionModel().select(index+1);

            Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
            if(selectedPlaylist != null) {
                try {
                    pm.updateSongOrder(selectedPlaylist.getId(), new ArrayList<>(playlistSongObservable));

                } catch (Exception e) {
                    displayError(e);
                }
            }
        }

    }


    @FXML
    private void onDeletePlaylistSong(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedPlaylistSong = lstPlaylistSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedPlaylistSong != null ) {
            try{
                pm.deleteSongFromPlaylist(selectedPlaylist.getId(), selectedPlaylistSong.getId());
                playlistSongObservable.remove(selectedPlaylistSong);

                List<Song> updatedSongs = pm.getSongsForPlaylist(selectedPlaylist.getId());
                selectedPlaylist.setSongs(updatedSongs);

                playlistSongObservable.setAll(updatedSongs);

                tblPlaylists.refresh();
            }
            catch (Exception err) {
                displayError(err);
            }
        }

    }

    @FXML
    private void onBtnAddToPlaylist(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedSong != null ){
            try{
                //Adds song to model
                pm.addSongsToPlaylist(selectedPlaylist.getId(), selectedSong.getId());

                List<Song> updatedSongs = pm.getSongsForPlaylist(selectedPlaylist.getId());
                selectedPlaylist.setSongs(updatedSongs);

                playlistSongObservable.setAll(updatedSongs);

                tblPlaylists.refresh();

            }
            catch (Exception err) {
                displayError(err);
            }
        }
    }

    private void onSelectedPlaylist() {
        tblPlaylists.getSelectionModel().selectedItemProperty().addListener((obs,oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    List<Song> playlistSongs = pm.getSongsForPlaylist(newVal.getId());

                    newVal.setSongs(playlistSongs);
                    playlistSongObservable.setAll(playlistSongs);
                } catch (Exception e) {
                    displayError(e);
                    //e.printStackTrace();
                }
                lblPlaylistName.setText(newVal.getName());
            }
        });

        //initial selection
        if (!tblPlaylists.getItems().isEmpty()) {
            tblPlaylists.getSelectionModel().selectFirst();
        }

    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something is wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void onBtnClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }

    @FXML
    private void onBtnPlay(ActionEvent actionEvent) {
        //play song from tblSongs

        Song songToPlay = getSong();

        //if no song is selected, exit method
        if(songToPlay == null) {
            return;
        }
        //if nothing is playing, start playing
        if(mp == null) {
            playSong(songToPlay);
            btnPlay.setText("⏸");
            lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
            isPaused = false;
        }
        //if something is playing, and it isn't paused
        else if (!isPaused) {
            //stop song and play new song, if the selected song isn't the currently playing song
            if(!songToPlay.equals(currentSong)){
                mp.stop();
                playSong(songToPlay);
                btnPlay.setText("⏸");
                lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
                isPaused = false;
            }
            // pause if the selected song is the one playing
            else{
                mp.pause();
                isPaused=true;
                btnPlay.setText("▶");
            }
        }
        //if the song is paused
        else {
            //play new song, if selected song isn't the one currently playing
            if (!songToPlay.equals(currentSong)) {
                mp.stop();
                playSong(songToPlay);
                lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
            }
            //unpause current song
            else{
                mp.play();
            }
            btnPlay.setText("⏸");
            isPaused = false;
        }
    }

    private Song getSong() {
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        Song selectedPlaylistSong = lstPlaylistSongs.getSelectionModel().getSelectedItem();

        //If selectedSong is not null, then songToPlay = selectedSong; otherwise, songToPlay = selectedPlaylistSong
        //Song songToPlay = (selectedSong != null) ? selectedSong : selectedPlaylistSong;
        Song songToPlay;
        if(selectedSong != null) {
            songToPlay = selectedSong;
        } else { songToPlay = selectedPlaylistSong; }
        return songToPlay;
    }

    private void playSong(Song song) {
        try {

            String uri = sm.getMediaUriForSong(song); // via model, ikke direkte SongManager

            System.out.println(uri);
            if (uri == null) return;

            if (mp != null) {
                mp.stop();
            }

            Media media = new Media(uri);
            System.out.println(media.getSource());
            mp = new MediaPlayer(media);

            mp.setOnEndOfMedia(() -> {
                isPaused = false;
                btnPlay.setText("▶");
                lblPlayingSong.setText("");
            });
            // Binding our volume slider to the media player's volume property.
            mp.volumeProperty().bind(volumeSlider.valueProperty());
            mp.play();

            currentSong = song;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onPrevious(ActionEvent actionEvent) {
        Song prev = sm.previousSong();
        playSong(prev);
        lblPlayingSong.setText(prev.getTitle() + " is playing");
    }


    @FXML
    private void onNext(ActionEvent actionEvent) {
        Song next = sm.nextSong();
        playSong(next);
        lblPlayingSong.setText(next.getTitle() + " is playing");
    }

    private void doubleClickHandlers() {
        tblSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() ==2) {
                Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
                if(selectedSong != null) {
                    playSong(selectedSong);
                    btnPlay.setText("⏸");
                    isPaused = false;
                    currentSong = selectedSong;
                }
            }
        });

        lstPlaylistSongs.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                Song selectedPlaylistSong = lstPlaylistSongs.getSelectionModel().getSelectedItem();
                if(selectedPlaylistSong != null) {
                    playSong(selectedPlaylistSong);
                    btnPlay.setText("⏸");
                    isPaused = false;
                    currentSong = selectedPlaylistSong;
                }
            }
        });
    }


}

