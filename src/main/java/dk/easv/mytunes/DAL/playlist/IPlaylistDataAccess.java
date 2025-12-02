package dk.easv.mytunes.DAL.playlist;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;

//java imports
import java.util.List;

public interface IPlaylistDataAccess {

    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist newPlaylist) throws Exception;

    void updatePlaylist(Playlist playlist) throws Exception;

    void deletePlaylist(Playlist playlist) throws Exception;

    void addSongsToPlaylist(int playlistId, int songId) throws Exception;

    List<Song> getSongsForPlaylist(int playlistId) throws Exception;

    Playlist getPlaylist(int playlistId) throws Exception;

    void deleteSongFromPlaylist(int playlistId, int songId) throws Exception;

    void updateSongOrder(int playlistId, List<Song> newOrder);
}
