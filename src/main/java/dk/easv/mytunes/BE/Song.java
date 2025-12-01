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

    public void setLength(){
        this.length = length;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString(){
        return title + " - " + artist;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
