
package dk.easv.mytunes.BLL;
//Project imports
import dk.easv.mytunes.DAL.song.ISongDataAccess;
import dk.easv.mytunes.DAL.song.SongDAO_db;
import dk.easv.mytunes.BE.Song;
//java imports
import java.util.List;

public class SongManager {
    private List<Song> songs;
    private int currentSongId = 0;
    private ISongDataAccess songDao;

    public SongManager() throws Exception{
        songDao = new SongDAO_db();
        songs = songDao.getAllSongs();
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
    public Song getCurrrentSong(){
        return songs.get(currentSongId);
    }

    public Song nextSong(){
        currentSongId++;
        if (currentSongId >= songs.size()){
            currentSongId = 0;
        }
        return getCurrrentSong();

    }

    public Song previousSong(){
        currentSongId--;
        if(currentSongId <= 0) {
            currentSongId = songs.size() - 1;
        }
        return getCurrrentSong();
    }

}
