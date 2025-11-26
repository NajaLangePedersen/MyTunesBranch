package dk.easv.mytunes.GUI.controllers;

//project imports
import dk.easv.mytunes.BE.Playlist;
import dk.easv.mytunes.GUI.models.PlaylistModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistViewController implements Initializable {
    private PlaylistModel pm;

    @FXML
    private TextField txtName;


    public PlaylistViewController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try{
            pm = new PlaylistModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
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
        }
    }

    @FXML
    private void onBtnCancel(ActionEvent actionEvent){

    }


}
