package dk.easv.mytunes.DAL.playlist;
//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.DBConnector;
import javafx.collections.ObservableList;
//java imports
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_db implements IPlaylistDataAccess {

    private DBConnector databaseConnector;

    public PlaylistDAO_db() throws IOException {
        databaseConnector = new DBConnector();
    }

    public List<Playlist> getAllPlaylists() throws Exception {
        ArrayList<Playlist> allPlaylists = new ArrayList<>();

        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM dbo.Playlist";

            ResultSet rs = stmt.executeQuery(sql);

            // Loop through the result set
            while (rs.next()) {

                int id = rs.getInt("PlaylistId");
                String name = rs.getString("Name");

                Playlist playlist = new Playlist(id, name);
                allPlaylists.add(playlist);
            }

            for (Playlist p : allPlaylists) {
                List<Song> playlistSongs = getSongsForPlaylist(p.getId());
                p.setSongs(playlistSongs);
            }

            return allPlaylists;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not get playlists from database", ex);
        }
    }

    public Playlist createPlaylist(Playlist newPlaylist) throws Exception {
        // sql command
        String sql = "INSERT INTO dbo.Playlist (Name) VALUES (?);";

        try(Connection conn = databaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // Bind parameters
            stmt.setString(1, newPlaylist.getName());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()){
                id=rs.getInt(1);
            }

            // Create Playlist object and send up the layers
            Playlist createdPlaylist = new Playlist(id, newPlaylist.getName());

            return  createdPlaylist;

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new Exception("Could not create newPlaylist", ex);
        }

    }

    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {
        // sql commands
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE PlaylistId = ?";

        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            // bind parameters
            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());

            stmt.executeUpdate();
        }

        catch (SQLException ex)
        {
            throw new Exception("Could not get playlists from database", ex);
        }

    }

    @Override
    public void deletePlaylist(Playlist playlist) throws Exception {

        // sql commands
        String sql = "DELETE FROM dbo.Playlist WHERE PlaylistId = ?;";
        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, playlist.getId());

            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get Playlist from database", ex);
        }

    }

    @Override
    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        List<Song> playlistSongs = new ArrayList<>();

        String sql = "SELECT * FROM Songs JOIN PlaylistSongs ON Songs.SongId = PlaylistSongs.SongId WHERE PlaylistSongs.PlaylistId = ?;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("SongId"),
                        rs.getString("Artist"),
                        rs.getString("Title"),
                        rs.getDouble("Length"),
                        rs.getString("Category")
                );
                playlistSongs.add(song);
            }

        } catch (SQLException ex) {
            throw new Exception("Could not get songs from database", ex);
        }

        return playlistSongs;
    }

    @Override
    public Playlist getPlaylist(int playlistId) throws Exception {
        Playlist playlist = null;

        String sql = "SELECT * FROM Playlist WHERE PlaylistId = ?;";

        try (Connection conn = databaseConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                playlist = new Playlist(
                        rs.getInt("PlaylistId"),
                        rs.getString("Name")
                );

                List<Song> songs = getSongsForPlaylist(playlistId);
                playlist.setSongs(songs);
            }

        } catch (SQLException ex) {
            throw new Exception("Could not get playlist from database", ex);
        }
        return playlist;
    }

    @Override
    public void addSongsToPlaylist(int playlistId, int songId) throws Exception{
        String sql = "INSERT INTO PlayListSongs (playlistId, songId) VALUES (?, ?);";
        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);

            stmt.executeUpdate();
        }
        catch (SQLException ex){
            throw new Exception("Could not add song to playlist", ex);
        }

    }
}
