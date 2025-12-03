
package dk.easv.mytunes.BLL;
//Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.DAL.song.ISongDataAccess;
import dk.easv.mytunes.DAL.song.SongDAO_db;
import dk.easv.mytunes.BE.Song;
//java imports
import javafx.scene.media.Media;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SongManager {
    private List<Song> songs;
    private List<Song> currentPlaylist;
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

    public void setCurrentPlaylist(List<Song> playlistSongs) {
        this.currentPlaylist = playlistSongs;
        this.currentSongId = 0;
    }


    public Song getCurrrentSong(){
        if(currentPlaylist != null && !currentPlaylist.isEmpty()) {
            return songs.get(currentSongId);
        }
        return songs.get(currentSongId);

    }

    public Song nextSong() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            currentSongId++;
            if (currentSongId >= currentPlaylist.size()) {
                currentSongId = 0;
            }
            return getCurrrentSong();
        } else {
            currentSongId++;
            if (currentSongId >= songs.size()) {
                currentSongId = 0;
            }
            return getCurrrentSong();
        }
    }

    public Song previousSong() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            currentSongId--;
            if (currentSongId < 0) {
                currentSongId = currentPlaylist.size() - 1;
            }
            return getCurrrentSong();
        } else {
            currentSongId--;
            if (currentSongId < 0) {
                currentSongId = songs.size() - 1;
            }
            return getCurrrentSong();
        }
    }

    public String getMediaUriForSong(Song song) {
        if (song.isInternalResource()) {
            URL resource = getClass().getResource("/dk/easv/mytunes/audio/hype-drill-music-438398.mp3");

            return resource != null ? resource.toString() : null;
        } else {
            return song.getFilePath().toUri().toString();
        }
    }

    /*public String getMediaUriForSong(Song song) {
        try {
            Path filePath;

            if (song.isInternalResource()) {
                filePath = Path.of("audio", String.valueOf(song.getFilePath().getFileName()));
            } else {
                filePath = song.getFilePath();
            }

            if (!Files.exists(filePath)) {
                System.err.println("Filen findes ikke: " + filePath);
                return null;
            }
            return filePath.toUri().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }*/
}
