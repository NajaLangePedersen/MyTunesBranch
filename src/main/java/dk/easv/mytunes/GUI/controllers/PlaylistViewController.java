package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.GUI.models.PlaylistModel;

//javaFX imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistViewController{
    private PlaylistModel pm;


    @FXML
    private TextField txtName;



    public PlaylistViewController(){

    }

    public void setModel(PlaylistModel pm){
        this.pm = pm;
    }


    private void displayError(Throwable t){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void onBtnSave(ActionEvent actionEvent) throws Exception {
        if(!txtName.getText().isEmpty()){
            String name = txtName.getText();

            Playlist newPlaylist = new Playlist(-1, name);

            pm.createPlaylist(newPlaylist);

            Node source = (Node) actionEvent.getSource();
            Stage window = (Stage) source.getScene().getWindow();
            window.close();

        }
    }

    public void setPlaylistName(String playlistName) {
        txtName.setText(playlistName);
    }


    @FXML
    private void onBtnCancel(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        Stage window = (Stage) source.getScene().getWindow();
        window.close();
    }


}
