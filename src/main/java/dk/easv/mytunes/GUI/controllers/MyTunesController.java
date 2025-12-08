package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.GUI.models.PlaylistModel;
import dk.easv.mytunes.GUI.models.SongModel;

//javaFX imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

//java imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class MyTunesController implements Initializable {

    private PlaylistModel pm;
    private SongModel sm;
    private MediaPlayer mp;

    private boolean isPaused = false;
    private Song currentSong;
    private Song lastSelectedSong;

    private ObservableList<Playlist> observablePlaylist = FXCollections.observableArrayList();
    private ObservableList<Song> playlistSongObservable = FXCollections.observableArrayList();

    @FXML
    private TableView<Playlist> tblPlaylists;
    @FXML
    private TableView<Song> tblSongs;

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
    private ListView<Song> lstPlaylistSongs;

    @FXML
    private Label lblPlaylistName, lblPlayingSong;
    @FXML
    private Button btnPlay;
    @FXML
    private Slider volumeSlider;
    @FXML
    private TextField txtSearch;


    public MyTunesController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            pm = new PlaylistModel();
            sm = new SongModel();

            setColumns();

            //connect tableviews to filteredlist / observableList
            tblSongs.setItems(sm.getFilteredList());
            tblPlaylists.setItems(pm.getFilteredList());

            tblPlaylists.setItems(observablePlaylist);
            lstPlaylistSongs.setItems(playlistSongObservable);

            search();

            sortLists();

            onSelectedPlaylist();

            setVolumeSlider();

            doubleClickHandlers();

            getLastSelectedSong();

        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    /**
     * Initializes the search functionality.
     */
    private void search() {
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

    /**
     * Initializes the columns for the tableviews.
     */
    private void setColumns() {
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("nrOfSongs"));
        colPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("totalLength"));

        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("lengthString"));
    }

    /**
     * Initializes the sorting of the tableviews.
     */
    private void sortLists() {
        //sort lists
        SortedList<Song> sortedSongData = new SortedList<>(sm.getFilteredList());
        sortedSongData.comparatorProperty().bind(tblSongs.comparatorProperty());
        tblSongs.setItems(sortedSongData);

        SortedList<Playlist> sortedPlaylistData = new SortedList<>(pm.getFilteredList());
        sortedPlaylistData.comparatorProperty().bind(tblPlaylists.comparatorProperty());
        tblPlaylists.setItems(sortedPlaylistData);
    }

    /**
     * Initializes playlistSongs listview to be able to show songs on playlist
     */
    private void onSelectedPlaylist() {
        tblPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    List<Song> playlistSongs = pm.getSongsForPlaylist(newVal.getId());

                    newVal.setSongs(playlistSongs);
                    playlistSongObservable.setAll(playlistSongs);

                    sm.setCurrentPlaylist(playlistSongs);

                } catch (Exception e) {
                    displayError(e);
                }
                lblPlaylistName.setText(newVal.getName());
            }
        });

        //initial selection
        if (!tblPlaylists.getItems().isEmpty()) {
            tblPlaylists.getSelectionModel().selectFirst();
        }

    }

    private void setVolumeSlider() {
        volumeSlider.setMin(0.0); //mute
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
    }

    /**
     * Initializes double click handlers for tableviews.
     */
    private void doubleClickHandlers() {
        // Double-click in the Songs table -> use current table order (sorted & filtered)
        tblSongs.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Song songToPlay = tblSongs.getSelectionModel().getSelectedItem();
                    if (songToPlay != null) {
                        // Build a playback queue from the UI's current order
                        sm.setCurrentPlaylist(new ArrayList<>(tblSongs.getItems())); // SortedList -> List
                        playSong(songToPlay);
                        btnPlay.setText("⏸");
                        lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
                        isPaused = false;
                        currentSong = songToPlay;
                        lastSelectedSong = songToPlay;
                    }
                }
            });

            // Double-click in the Playlist list -> use playlist's visible order
            lstPlaylistSongs.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Song songToPlay = lstPlaylistSongs.getSelectionModel().getSelectedItem();
                    if (songToPlay != null) {
                        sm.setCurrentPlaylist(new ArrayList<>(playlistSongObservable));
                        playSong(songToPlay);
                        btnPlay.setText("⏸");
                        lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
                        isPaused = false;
                        currentSong = songToPlay;
                        lastSelectedSong = songToPlay;
                    }
                }
            });

        }

    /**
     * Gets the newest selected song no matter which list
     */
    private void getLastSelectedSong() {
        tblSongs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lastSelectedSong = newVal;
            }
        });

        lstPlaylistSongs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lastSelectedSong = newVal;
            }
        });
    }

    /**
     * Opens the playlist-view and sets the controller for the new view
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void onCreatePlaylist(ActionEvent actionEvent) throws IOException {
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

    /**
     * Opens the playlist-view and sets the controller for the new view
     * Sets the selected playlist
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void onEditPlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/PlaylistView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        PlaylistViewController pvc = fxmlLoader.getController();
        pvc.setModel(this.pm);
        stage.setTitle("Playlist View");
        stage.setScene(scene);

        //set playlist name in new view
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        pvc.setPlaylistName(selectedPlaylist);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * deletes selcted playlist from database
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void onDeletePlaylist(ActionEvent actionEvent) throws IOException {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            try {
                pm.deletePlaylist(selectedPlaylist);
            } catch (Exception err) {
                displayError(err);
            }
        }
    }

    /**
     * Opens song-view and sets the controller for the new view
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void onCreateSong(ActionEvent actionEvent) throws IOException {
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

    /**
     * Opens song-view and sets the controller for the new view
     * Sets
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void onEditSong(ActionEvent actionEvent) throws IOException {
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/SongView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        SongViewController svc = fxmlLoader.getController();
        svc.setModel(this.sm);
        svc.setSongToEdit(selectedSong);
        stage.setTitle("Edit song");
        stage.setScene(scene);

        //Gør så du ikke kan åbne et til vindue før du har lukket det første du åbnede.
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();


    }

    /**
     * Deletes selected song from database
     *
     * @param actionEvent
     * @throws IOException
     */
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

    /**
     * Moves songs placement in a playlist (move selected song a place up)
     *
     * @param actionEvent
     */
    @FXML
    private void onBtnUp(ActionEvent actionEvent) {
        int index = lstPlaylistSongs.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            Collections.swap(playlistSongObservable, index, index - 1);
            lstPlaylistSongs.getSelectionModel().select(index - 1);

            Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                try {
                    pm.updateSongOrder(selectedPlaylist.getId(), new ArrayList<>(playlistSongObservable));

                } catch (Exception e) {
                    displayError(e);
                }
            }
        }

    }

    /**
     * Moves songs placement in a playlist (move selected song a place down)
     *
     * @param actionEvent
     */
    @FXML
    private void onBtnDown(ActionEvent actionEvent) {
        int index = lstPlaylistSongs.getSelectionModel().getSelectedIndex();
        if (index < playlistSongObservable.size() - 1) {
            Collections.swap(playlistSongObservable, index, index + 1);
            lstPlaylistSongs.getSelectionModel().select(index + 1);

            Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                try {
                    pm.updateSongOrder(selectedPlaylist.getId(), new ArrayList<>(playlistSongObservable));

                } catch (Exception e) {
                    displayError(e);
                }
            }
        }

    }

    /**
     * deletes a song from selected playlist and only from the playlist
     *
     * @param actionEvent
     */
    @FXML
    private void onDeletePlaylistSong(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedPlaylistSong = lstPlaylistSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedPlaylistSong != null) {
            try {
                pm.deleteSongFromPlaylist(selectedPlaylist.getId(), selectedPlaylistSong.getId());
                playlistSongObservable.remove(selectedPlaylistSong);

                List<Song> updatedSongs = pm.getSongsForPlaylist(selectedPlaylist.getId());
                selectedPlaylist.setSongs(updatedSongs);

                playlistSongObservable.setAll(updatedSongs);

                tblPlaylists.refresh();
            } catch (Exception err) {
                displayError(err);
            }
        }

    }

    /**
     * Adds selected song to selected playlist
     *
     * @param actionEvent
     */
    @FXML
    private void onBtnAddToPlaylist(ActionEvent actionEvent) {
        Playlist selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedSong != null) {
            try {
                //Adds song to model
                pm.addSongsToPlaylist(selectedPlaylist.getId(), selectedSong.getId());

                List<Song> updatedSongs = pm.getSongsForPlaylist(selectedPlaylist.getId());
                selectedPlaylist.setSongs(updatedSongs);

                playlistSongObservable.setAll(updatedSongs);

                tblPlaylists.refresh();

            } catch (Exception err) {
                displayError(err);
            }
        }
    }

    /**
     * displays the error to the user
     *
     * @param t
     */
    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something is wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    /**
     * Closes the program
     *
     * @param actionEvent
     */
    @FXML
    private void onBtnClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }

    /**
     * Controls playing and pausing
     * @param actionEvent
     */
    @FXML
    private void onBtnPlay(ActionEvent actionEvent) {
        //play song from tblSongs

        Song songToPlay = lastSelectedSong;

        //if no song is selected, exit method
        if (songToPlay == null) {
            return;
        }


        // Pick queue based on where the selection lives
        if (songToPlay.equals(tblSongs.getSelectionModel().getSelectedItem())) {
            sm.setCurrentPlaylist(new ArrayList<>(tblSongs.getItems())); // library queue
        } else if (songToPlay.equals(lstPlaylistSongs.getSelectionModel().getSelectedItem())) {
            sm.setCurrentPlaylist(new ArrayList<>(playlistSongObservable)); // playlist queue
        } else {
            // Fallback: if in doubt, use library queue
            sm.setCurrentPlaylist(new ArrayList<>(tblSongs.getItems()));
        }



        //if nothing is playing, start playing
        if (mp == null) {
            playSong(songToPlay);
            btnPlay.setText("⏸");
            lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
            isPaused = false;
        }
        //if something is playing, and it isn't paused
        else if (!isPaused) {
            //stop song and play new song, if the selected song isn't the currently playing song
            if (!songToPlay.equals(currentSong)) {
                mp.stop();
                playSong(songToPlay);
                btnPlay.setText("⏸");
                lblPlayingSong.setText(songToPlay.getTitle() + " is playing");
                isPaused = false;
            }
            // pause if the selected song is the one playing
            else {
                mp.pause();
                isPaused = true;
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
            else {
                mp.play();
            }
            btnPlay.setText("⏸");
            isPaused = false;
        }
    }

    /**
     * handles mediaplayer
     * @param song
     */
    private void playSong(Song song) {
        try {

            String uri = sm.getMediaUriForSong(song); // via model, ikke direkte SongManager

            if (uri == null) return;

            if (mp != null) {
                mp.stop();
            }

            sm.syncCurrentIndexTo(song);

            Media media = new Media(uri);
            mp = new MediaPlayer(media);

            mp.setOnEndOfMedia(() -> {
                Song nextSong = sm.nextSong();
                playSong(nextSong);
                isPaused = false;
                btnPlay.setText("⏸");
                lblPlayingSong.setText(nextSong.getTitle() + " is playing");
            });
            // Binding our volume slider to the media player's volume property.
            mp.volumeProperty().bind(volumeSlider.valueProperty());
            mp.play();

            currentSong = song;
            lastSelectedSong = song;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onPrevious(ActionEvent actionEvent) {
        Song prev = sm.previousSong();
        playSong(prev);
        lblPlayingSong.setText(prev.getTitle() + " is playing");
        System.out.println(currentSong.getTitle());
    }


    @FXML
    private void onNext(ActionEvent actionEvent) {
        Song next = sm.nextSong();
        playSong(next);
        lblPlayingSong.setText(next.getTitle() + " is playing");
        System.out.println(currentSong.getTitle());
        System.out.println(lastSelectedSong.getTitle());

    }
}


