package dk.easv.mytunes.BE;


public class Song {
    private int id;
    private String title;
    private String artist;
    private double length;
    private String category;

    public Song(int id, String title, String artist, double length, String category) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.length = length;
        this.category = category;
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
}
