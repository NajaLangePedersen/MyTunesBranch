
package dk.easv.mytunes.BLL;
//Project imports
import dk.easv.mytunes.DAL.song.ISongDataAccess;
import dk.easv.mytunes.DAL.song.SongDAO_db;
import dk.easv.mytunes.BE.Song;
//java imports
import java.util.List;

public class SongManager {

    private ISongDataAccess songDao;

    public SongManager() throws Exception{
        songDao = new SongDAO_db();
    }

    public List<Song> getAllSongs() throws Exception{
        return songDao.getAllSongs();
    }

    public Song createSong(Song newSong) throws Exception {
        return songDao.createSong(newSong);
    }

    public void updateSong(Song song) throws Exception{
        songDao.updateSong(song);

    }

    public void deleteSong(Song song) throws Exception{
        songDao.deleteSong(song);
    }
}
