package dk.easv.mytunes.BE;

import java.nio.file.Path;

public class Song {
    private int id;
    private String title;
    private String artist;
    private double length;
    private String category;
    private Path filePath;

    public Song(int id, String title, String artist, double length, String category, Path filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.length = length;
        this.category = category;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public double getLength(){
        return length;
    }

    public String getLengthString() {
        int minutes = (int) length; //int converts the number to a whole number - only the minute part of the decimal-minutes
        int seconds = (int) Math.round((length - minutes)*60); //subtracts the minute int from the whole decimal-minutes number - leaving only the decimal part of a minute. Then * 60 to make it into seconds instead of parts of a decimal-minute. Math.round rounds up in comparison to just int typecating which rounds down.

        return String.format("%02d:%02d", minutes, seconds); //% = format specifier, 02 = min 2 cifre, fill with 0 in front if necessary, d = whole number
    }

    public void setLength(double length){
        this.length = this.length;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String toString(){
        return title + " - " + artist;
    }

    public boolean isInternalResource() {
        return filePath != null && !filePath.isAbsolute();
    }
}
