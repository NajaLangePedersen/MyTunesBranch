package dk.easv.mytunes.GUI.models;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.BLL.PlaylistManager;

//javaFX imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

public class PlaylistModel {
    private FilteredList<Playlist> filteredList;

    private ObservableList<Playlist> playlistsToBeViewed;

    private PlaylistManager pMan;

    public PlaylistModel() throws Exception {
        pMan = new PlaylistManager();
        playlistsToBeViewed = FXCollections.observableArrayList();
        playlistsToBeViewed.addAll(pMan.getAllPlaylists());
        filteredList = new FilteredList<>(playlistsToBeViewed);
    }

    public List<Song> getOrderedSongsForPlaylist(int playlistId) throws Exception {
        return pMan.getOrderedSongsForPlaylist(playlistId);
    }

    public FilteredList<Playlist> getFilteredList(){
        return filteredList;
    }


    public Playlist createPlaylist(Playlist newPlaylist) throws Exception {
        Playlist playlistCreated = pMan.createPlaylist(newPlaylist);

        playlistsToBeViewed.add(playlistCreated);

        return playlistCreated;
    }

    public void updatePlaylist(Playlist playlist) throws Exception {
        pMan.updatePlaylist(playlist);

        int index = playlistsToBeViewed.indexOf(playlist);
        playlistsToBeViewed.set(index, playlist);
    }

    public void deletePlaylist(Playlist playlist)throws Exception{
        pMan.deletePlaylist(playlist);

        playlistsToBeViewed.remove(playlist);

    }

    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        return pMan.getSongsForPlaylist(playlistId);
    }

    public void addSongsToPlaylist(int playlistId, int songId) throws Exception {
        pMan.addSongsToPlaylist(playlistId, songId);
    }

    public void deleteSongFromPlaylist(int playlistId, int songId) throws Exception {
        pMan.deleteSongFromPlaylist(playlistId, songId);
    }
    
    public void updateSongOrder(int playlistId, List<Song> newOrder) throws Exception{
        pMan.updateSongOrder(playlistId, newOrder);
    }


}
