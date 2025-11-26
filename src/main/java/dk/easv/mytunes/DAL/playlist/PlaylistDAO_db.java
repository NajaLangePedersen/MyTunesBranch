package dk.easv.mytunes.DAL.playlist;

import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.DAL.DBConnector;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_db implements IPlaylistDataAccess{

    private DBConnector databaseConnector;

    public PlaylistDAO_db() throws IOException{
        databaseConnector = new DBConnector();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        ArrayList<Playlist> allPlaylists = new ArrayList<>();

        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM dbo.Playlist";

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                int id = rs.getInt("Id");
                String name = rs.getString("Name");

                Playlist playlist = new Playlist(id, name);
                allPlaylists.add(playlist);
            }
            return allPlaylists;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not get playlists from database", ex);
        }
    }

    @Override
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
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE ID = ?";

        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            // bind parameters
            stmt.setString(1, playlist.getName());

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
        String sql = "DELETE FROM dbo.Playlist WHERE ID = ?;";
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
}
