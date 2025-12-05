
package dk.easv.mytunes.BLL;
//Project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.DAL.song.ISongDataAccess;
import dk.easv.mytunes.DAL.song.SongDAO_db;
import dk.easv.mytunes.BE.Song;
//java imports
import java.net.URL;
import java.util.List;

public class SongManager {
    private List<Song> songs;
    private List<Song> currentPlaylist;
    private int currentSongId = 0;
    private ISongDataAccess songDao;

    public SongManager() throws Exception {
        songDao = new SongDAO_db();
        songs = songDao.getAllSongs();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDao.getAllSongs();
    }

    public Song createSong(Song newSong) throws Exception {
        return songDao.createSong(newSong);
    }

    public void updateSong(Song song) throws Exception {
        songDao.updateSong(song);

    }

    public void deleteSong(Song song) throws Exception {
        songDao.deleteSong(song);
    }

    public void setCurrentPlaylist  (List<Song> playlistSongs){
        this.currentPlaylist = playlistSongs != null ? playlistSongs:List.of();
        this.currentSongId = 0;
    }


    public Song getCurrentSong() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            return currentPlaylist.get(currentSongId);
        }
        return songs.get(currentSongId);

    }

    public Song nextSong() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            currentSongId++;
            if (currentSongId >= currentPlaylist.size()) {
                currentSongId = 0;
            }
            return getCurrentSong();
        } else {
            currentSongId++;
            if (currentSongId >= songs.size()) {
                currentSongId = 0;
            }
            return songs.get(currentSongId);
        }
    }

    public Song previousSong() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            currentSongId--;
            if (currentSongId < 0) {
                currentSongId = currentPlaylist.size() - 1;
            }
            return getCurrentSong();
        } else {
            currentSongId--;
            if (currentSongId < 0) {
                currentSongId = songs.size() - 1;
            }
            return getCurrentSong();
        }
    }

    public String getMediaUriForSong(Song song) {
        if (song.isInternalResource()) {
            String resource= getClass().getResource("/dk/easv/mytunes/audio/" + song.getFilePath()).toExternalForm();

            return resource;
        } else {
            return song.getFilePath().toUri().toString();
        }
    }


    public void syncCurrentIndexTo(Song song) {
        if (song == null) return;
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            for (int i = 0; i < currentPlaylist.size(); i++) {
                if (currentPlaylist.get(i).getId() == song.getId()) {
                    currentSongId = i;
                    return;
                }
            }
            // if not found, keep currentSongId (or set to 0)
        } else if (songs != null && !songs.isEmpty()) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getId() == song.getId()) {
                    currentSongId = i;
                    return;
                }
            }
        }
    }


}
