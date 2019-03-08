package riskgame.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import riskgame.Main;
import riskgame.model.BasicClass.Country;
import riskgame.model.BasicClass.ObserverPattern.ListViewObserver;
import riskgame.model.BasicClass.Player;
import riskgame.model.Utils.InfoRetriver;
import riskgame.model.Utils.ListviewRenderer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * controller class for AttackView.fxml
 **/
public class AttackViewController implements Initializable {

    @FXML
    private ListView lsv_adjacentCountries;
    @FXML
    private ListView lsv_ownedCountries;
    @FXML
    private Button btn_nextStep;
    @FXML
    private Label lbl_playerInfo;
    @FXML
    private Button btn_finishAttack;
    @FXML
    private Button btn_confirmAttack;

    /**
     * curent player index
     */
    private final int curPlayerIndex = Main.curRoundPlayerIndex;

    /**
     * Observer ListView for storing owned country list.
     */
    private ListViewObserver ownedCountryListViewObserver;

    /**
     * Observer ListView for storing adjacent country list.
     */
    private ListViewObserver adjacentCountryListViewObserver;

    /**
     * Alert object
     */
    private Alert alert;

    /**
     * init method for attack phase view
     *
     * @param location  default value
     * @param resources default value
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Player curPlayer = Main.playersList.get(Main.curRoundPlayerIndex);
        String playerInfo = "Player: " + Main.curRoundPlayerIndex;
        lbl_playerInfo.setText(playerInfo);
        alert = new Alert(Alert.AlertType.WARNING);

        initCountryListviewDisplay(curPlayer);

    }

    /**
     * display name and army number of countries the player owns
     *
     * @param curPlayer display all country names of the current player
     */
    private void initCountryListviewDisplay(Player curPlayer) {
        ObservableList<Country> ownedObservevableCountryList = InfoRetriver.getObservableCountryList(curPlayer);
        ownedCountryListViewObserver = new ListViewObserver(lsv_ownedCountries, ownedObservevableCountryList);
        adjacentCountryListViewObserver = new ListViewObserver(lsv_adjacentCountries, null);
    }

    /**
     * onClick event when a country item is selected from country ListView
     *
     * @param mouseEvent display its adjacent countries of the selected country
     */
    @FXML
    public void selectOneCountry(MouseEvent mouseEvent) {
        int countryIndex = ownedCountryListViewObserver
                .getListView()
                .getSelectionModel()
                .getSelectedIndex();

        System.out.println("#############selected country index: " + countryIndex);

        ObservableList<Country> datalist = InfoRetriver.getAttackableAdjacentCountryList(Main.curRoundPlayerIndex, countryIndex);

        adjacentCountryListViewObserver.setObservableList(datalist);
        ListviewRenderer.renderCountryItems(adjacentCountryListViewObserver.getListView());
    }

    /**
     * onClick event for moving to fortification phase of the game
     *
     * @param actionEvent button is clicked
     * @throws IOException FotificationView.fxml is not found
     */
    public void clickNextStep(ActionEvent actionEvent) throws IOException {
        Stage curStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Pane fortificationPane = new FXMLLoader(getClass().getResource("../view/FortificationView.fxml")).load();
        Scene fortificationScene = new Scene(fortificationPane, 1200, 900);

        curStage.setScene(fortificationScene);
        curStage.show();
    }

    /**
     * onClick event for confirming attack
     *
     * @param actionEvent button clicked
     */
    public void clickAttack(ActionEvent actionEvent) {
        int defendingCountryIndex = adjacentCountryListViewObserver
                .getListView()
                .getSelectionModel()
                .getSelectedIndex();

        if (defendingCountryIndex == -1) {
            alert.setContentText("Please select an adjacent country to attack!");
            alert.showAndWait();
        } else {
            Player attacker = Main.playersList.get(curPlayerIndex);

            Country attackingCountry = (Country) ownedCountryListViewObserver
                    .getListView()
                    .getSelectionModel()
                    .getSelectedItem();

            Country defendingCountry = (Country) adjacentCountryListViewObserver
                    .getListView()
                    .getSelectionModel()
                    .getSelectedItem();

            if (attackingCountry.getCountryArmyNumber() < 2) {
                alert.setContentText("No enough army for attacking! Please select another country!");
                alert.showAndWait();
            } else {
                attacker.attckCountry(attackingCountry, defendingCountry);

                System.out.println("!!!!!!!!!!!attacking!!!!!!!!!!!!!!!!");
            }
        }
    }

    /**
     * Click the button to finish this round of attack.
     *
     * @param actionEvent onClick event
     */
    public void clickFinishAttack(ActionEvent actionEvent) {
        btn_confirmAttack.setVisible(false);
        btn_finishAttack.setVisible(false);
        btn_nextStep.setVisible(true);
    }
}
