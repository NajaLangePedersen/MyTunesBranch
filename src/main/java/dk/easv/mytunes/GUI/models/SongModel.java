package dk.easv.mytunes.GUI.models;

//project imports
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.SongManager;

//javaFX imports

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

public class SongModel {

    private FilteredList<Song> filteredList;

    private ObservableList<Song> songsToBeViewed;

    private SongManager sMan;

    public SongModel() throws Exception {
        sMan = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(sMan.getAllSongs());
        filteredList = new FilteredList<>(songsToBeViewed);
    }

    public FilteredList<Song> getFilteredList() {
        return filteredList;
    }

    public Song createSong(Song newSong) throws Exception{

        Song songCreated = sMan.createSong(newSong);

        songsToBeViewed.add(songCreated);

        return songCreated;
    }

    public void updateSong(Song song) throws Exception{
        sMan.updateSong(song);
        int index = songsToBeViewed.indexOf(song);
        songsToBeViewed.set(index,song);

    }

    public void deleteSong(Song song) throws Exception{
        sMan.deleteSong(song);

        songsToBeViewed.remove(song);
    }

    public void setActivePlaylist(List<Song> playlistSongs) {
        sMan.setCurrentPlaylist(playlistSongs);
    }

    public Song getCurrentSong() {
        return sMan.getCurrrentSong();
    }

    public Song nextSong(){
        return sMan.nextSong();
    }
    public Song previousSong() {
        return sMan.previousSong();
    }

}
