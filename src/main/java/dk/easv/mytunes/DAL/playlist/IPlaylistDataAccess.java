package dk.easv.mytunes.DAL.playlist;

//project imports
import dk.easv.mytunes.BE.Playlist;

//java imports
import java.util.List;

public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist newPlaylist) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;
}
