// project structure
package dk.easv.mytunes.DAL.song;
import dk.easv.mytunes.BE.Song;
import dk.easv.mytunes.DAL.DBConnector;
// java imports
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO_db implements ISongDataAccess{
    private DBConnector databaseConnector;

    public  SongDAO_db()throws IOException{
        databaseConnector = new DBConnector();
    }

    public List<Song> getAllSongs() throws Exception{
        ArrayList<Song> allSongs = new ArrayList<>();

        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement())
        {
            String sql = "SELECT * FROM dbo.Songs;";
            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {

                String filePathStr = rs.getString("FilePath");
                Path filePath = (filePathStr != null && !filePathStr.isEmpty()) ? Paths.get(filePathStr) : null;

                //Map Database row to Song object
                int id = rs.getInt("SongId");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                double length = rs.getDouble("Length");
                String category = rs.getString("Category");


                Song song = new Song(id, title, artist, length, category, filePath);
                allSongs.add(song);
            }
            return allSongs;

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new Exception("Could not get Songs from the database", ex);
        }
    }

    public Song createSong(Song newSong) throws Exception {
        String sql = "INSERT INTO dbo.Songs (title, artist, length, category, filePath) VALUES (?,?,?,?,?);";

        try (Connection conn = databaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //Bind parameters
            stmt.setString(1, newSong.getTitle());
            stmt.setString(2, newSong.getArtist());
            stmt.setDouble(3, newSong.getLength());
            stmt.setString(4, newSong.getCategory());
            stmt.setString(5, newSong.getFilePath().toString());

            //Run the SQL statement
            stmt.executeUpdate();

            //Get generated ID
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            //Create song object and send up layers
            Song createdSong = new Song(id, newSong.getTitle(), newSong.getArtist(), newSong.getLength(), newSong.getCategory(), newSong.getFilePath());

            return createdSong;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not create Song", e);
        }
    }

    @Override
    public void updateSong(Song song) throws Exception {

        //sql command
        String sql = "UPDATE dbo.Songs SET Title = ?, Artist = ?, Length = ?, Category = ?, FilePath = ? WHERE SongId = ?";

        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement   stmt = conn.prepareStatement(sql))
        {
            // Bind parameters
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setDouble(3, song.getLength());
            stmt.setString(4, song.getCategory());
            stmt.setString(5, song.getFilePath().toString());

            //Run the sql statement
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get Songs from database", ex);
        }

    }

    public void deleteSong(Song song) throws Exception {

        // sql command
        String sql = "DELETE FROM dbo.Songs WHERE SongId = ?;";

        try(Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            // bind parameters
            stmt.setInt(1, song.getId());

            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new Exception("Could not get songs from database", ex);
        }


    }



}
