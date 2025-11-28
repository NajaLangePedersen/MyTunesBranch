package dk.easv.mytunes.BLL;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.DAL.playlist.IPlaylistDataAccess;
import dk.easv.mytunes.DAL.playlist.PlaylistDAO_db;

//java imports
import java.util.List;

public class PlaylistManager {

    private  IPlaylistDataAccess playlistDAO;


    public PlaylistManager() throws Exception {
        playlistDAO = new PlaylistDAO_db();
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
}
