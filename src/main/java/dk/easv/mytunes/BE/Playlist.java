package dk.easv.mytunes.BE;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int id;
    private String name;
    private List<Song> playlistSongs = new ArrayList<>();

    private IntegerProperty nrOfSongs = new SimpleIntegerProperty() {
    };
    private StringProperty totalLength = new SimpleStringProperty();


    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return playlistSongs;
    }

    public void setSongs(List<Song> playlistSongs) {
        this.playlistSongs = playlistSongs;
    }

    public int getNrOfSongs() {
        int nrSongs = 0;
        for ( Song song : playlistSongs) {
            nrSongs++;

        }
        return nrSongs;
    }

    public String getTotalLength() {
        int totalSeconds = 0;

        for (Song song : playlistSongs) {
            double length = song.getLength();

            //get total seconds of each song
            int minutes = (int) length;
            int seconds = (int) Math.round((length - minutes)*60);

            //put total seconds of all songs in a playlist together
            totalSeconds += minutes * 60 + seconds;
        }
        //convert to hours, minutes and seconds
        int totalHours = totalSeconds / 60 / 60;
        int totalMinutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", totalHours, totalMinutes, remainingSeconds); //% = format specifier, 02 = min 2 cifre, fill with 0 in front if necessary, d = whole number
    }

    private void updateCalculatedFields(){
        nrOfSongs.set(playlistSongs.size());
        totalLength.set(getTotalLength());
    }


}
