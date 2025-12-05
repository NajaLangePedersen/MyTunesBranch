package dk.easv.mytunes.DAL.playlist;
//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.DBConnector;
import javafx.collections.ObservableList;
//java imports
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String deleteSongsSql = "DELETE FROM PlaylistSongs WHERE PlaylistId = ?;";
        String deletePlaylistSql = "DELETE FROM dbo.Playlist WHERE PlaylistId = ?;";

        try (Connection conn = databaseConnector.getConnection()) {

            //use same connection for both statements (it only commits if both succeed)
            conn.setAutoCommit(false);

            //first delete all songs from playlist
            try (PreparedStatement stmtSongs = conn.prepareStatement(deleteSongsSql)) {
                stmtSongs.setInt(1, playlist.getId());
                stmtSongs.executeUpdate();
            }
            //then delete playlist
            try (PreparedStatement stmtPlaylist = conn.prepareStatement(deletePlaylistSql)) {
                stmtPlaylist.setInt(1, playlist.getId());
                stmtPlaylist.executeUpdate();
            }
            //if all succeed
            conn.commit();
        } catch (SQLException ex)
        {
            throw new Exception("Could not get Playlist from database", ex);
        }

    }

    @Override
    public List<Song> getSongsForPlaylist(int playlistId) throws Exception {
        List<Song> playlistSongs = new ArrayList<>();

        //Selects all songs from playlist
        String sql = "SELECT Songs.SongId, Songs.Title, Songs.Artist, Songs.Length, Songs.Category, Songs.FilePath " +
                "FROM Songs " +
                "JOIN PlaylistSongs ON Songs.SongId = PlaylistSongs.SongId " +
                "WHERE PlaylistSongs.PlaylistId = ? " +
                "ORDER BY PlaylistSongs.Position;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String filePathStr = rs.getString("FilePath");
                Path filePath = (filePathStr != null && !filePathStr.isEmpty()) ? Paths.get(filePathStr) : null;

                Song song = new Song(
                        rs.getInt("SongId"),
                        rs.getString("Title"),
                        rs.getString("Artist"),
                        rs.getDouble("Length"),
                        rs.getString("Category"),
                        filePath
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

    public void deleteSongFromPlaylist(int playlistId, int songId) throws Exception{
        String sql = "DELETE FROM PlaylistSongs WHERE PlaylistId = ? AND SongId = ?;";
        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);

            stmt.executeUpdate();
        }
        catch (SQLException ex){
            throw new Exception("Could not delete song from playlist", ex);
        }
    }

    @Override
    public void updateSongOrder(int playlistId, List<Song> newOrder) {
        String sql = "UPDATE PlaylistSongs SET Position = ? WHERE PlaylistId = ? AND SongId = ?";

        try(Connection conn = databaseConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            conn.setAutoCommit(false);
            
            int position = 1;
            for (Song song : newOrder) {
                stmt.setInt(1, position);
                stmt.setInt(2, playlistId);
                stmt.setInt(3, song.getId());
                stmt.addBatch();
                position++;
            }
            stmt.executeBatch();
            conn.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Song> getOrderedSongsForPlaylist(int playlistId) throws Exception{
    List<Song> songs = new ArrayList<>();
    String sql = "SELECT Songs.* " +
            "FROM PlayListSongs " +
            "JOIN Songs ON PlayListSongs.SongId = Songs.SongId " +
            "WHERE PlayListSongs.PlayListId = ? " +
            "ORDER BY PlayListSongs.Position ASC";

    try (Connection conn = databaseConnector.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)){

        stmt.setInt(1, playlistId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Song song = new Song(
                    rs.getInt("SongId"),
                    rs.getString("Title"),
                    rs.getString("Artist"),
                    rs.getDouble("Length"),
                    rs.getString("Category"),
                    Paths.get(rs.getString("filePath"))
            );
        }
    }
    return songs;
    }

}
