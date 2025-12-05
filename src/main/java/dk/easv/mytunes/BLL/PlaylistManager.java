package dk.easv.mytunes.BLL;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.playlist.IPlaylistDataAccess;
import dk.easv.mytunes.DAL.playlist.PlaylistDAO_db;
import javafx.collections.ObservableList;

//java imports
import java.util.List;

public class PlaylistManager {

    private  IPlaylistDataAccess playlistDAO;


    public PlaylistManager() throws Exception {
        playlistDAO = new PlaylistDAO_db();
    }

    public List<Song> getOrderedSongsForPlaylist(int playlistId) throws Exception {
            return playlistDAO.getOrderedSongsForPlaylist(playlistId);

    }

    public List<Playlist> getAllPlaylists() throws Exception{
        return playlistDAO.getAllPlaylists();
    }

    public Playlist createPlaylist(Playlist newPlaylist) throws Exception {
        return playlistDAO.createPlaylist(newPlaylist);
    }

    public void updatePlaylist(Playlist playlist) throws Exception {
        playlistDAO.updatePlaylist(playlist);
    }

    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistDAO.deletePlaylist(playlist);
    }

    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        return playlistDAO.getSongsForPlaylist(playlistId);
    }

    public Playlist getPlaylist(int playlistId) throws Exception {
        return playlistDAO.getPlaylist(playlistId);
    }

    public void addSongsToPlaylist(int playlistId, int songId) throws Exception {
        playlistDAO.addSongsToPlaylist(playlistId, songId);
    }

    public void deleteSongFromPlaylist(int playlistId, int songId) throws Exception {
        playlistDAO.deleteSongFromPlaylist(playlistId, songId);
    }

    public void updateSongOrder(int playlistId, List<Song> newOrder) {
        playlistDAO.updateSongOrder(playlistId, newOrder);
    }
}
