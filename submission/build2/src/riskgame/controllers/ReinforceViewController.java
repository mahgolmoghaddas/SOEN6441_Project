package riskgame.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import riskgame.Main;
import riskgame.model.BasicClass.Card;
import riskgame.model.BasicClass.Country;
import riskgame.model.BasicClass.ObserverPattern.CardExchangeViewObserver;
import riskgame.model.BasicClass.Player;
import riskgame.model.Utils.InfoRetriver;
import riskgame.model.Utils.ListviewRenderer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import static riskgame.Main.*;
import static riskgame.controllers.StartViewController.reinforceInitCounter;

/**
 * controller class for ReinforceView.fxml
 *
 * @author WW, Zhanfan
 * @since build1
 **/
public class ReinforceViewController implements Initializable {
    /**
     * default factor for calculting standard number of army used for reinforce phase
     */
    private static final int DEFAULT_DIVISION_FACTOR = 3;
    /**
     * minimun army number that is assigned to each player
     */
    private static final int DEFAULT_MIN_REINFORCE_ARMY_NBR = 3;
    @FXML
    private Button btn_nextStep;
    @FXML
    private ListView<Country> lsv_ownedCountries;
    @FXML
    private ListView<Country> lsv_adjacentCountries;
    @FXML
    private Label lbl_undeployedArmy;
    @FXML
    private VBox vbx_worldDomiView;
    @FXML
    private ScrollBar scb_armyNbrAdjustment;
    @FXML
    private Label lbl_deployArmyCount;
    @FXML
    private Button btn_confirmDeployment;
    @FXML
    private Label lbl_undeployArmyPrompt;
    @FXML
    private Label lbl_deployCountPrompt;
    @FXML
    private Label lbl_adjacentCountriesInfo;
    @FXML
    private Label lbl_countriesInfo;
    @FXML
    private Label lbl_actionString;
    @FXML
    private Label lbl_playerName;
    @FXML
    private Label lbl_phaseViewName;
    @FXML
    private ListView lsv_cardsListView;
    @FXML
    private Button btn_skipCardsExchange;
    @FXML
    private Button btn_confirmExchangeCards;
    /**
     * current player in this phase
     */
    private int curPlayerIndex;
    private String curGamePhase;
    private String curActionString;
    private String curPlayerName;
    private Player curPlayer;
    private CardExchangeViewObserver cardExchangeViewObserver;
    private ArrayList<Card> playerCards;
    private int curUndeployedArmy = 0;
    private Alert alert = new Alert(Alert.AlertType.WARNING);

    /**
     * calculate army number for reinforcement with default value
     *
     * @param countryNum number of all countries a player owns
     * @return calculated number of army for reinforcement phase
     */
    public static int getStandardReinforceArmyNum(int countryNum) {
        int calResult = countryNum / DEFAULT_DIVISION_FACTOR;
        return calResult > DEFAULT_MIN_REINFORCE_ARMY_NBR ? calResult : DEFAULT_MIN_REINFORCE_ARMY_NBR;
    }

    /**
     * selected cards list will be validated with game rules
     *
     * @param selectedCardList selected cards arraylist
     * @return true for correct combination, false for incorrect combination
     */
    public static boolean validateCardsCombination(ObservableList<Card> selectedCardList) {
        if (selectedCardList.size() != 3) {
            //set true for testing
            return false;
        } else {
            int sum = 0;
            for (Card card : selectedCardList) {
                sum += card.ordinal() + 1;
            }
            return sum == 3 || sum == 6 || sum == 9;
        }
    }


        /**
         * init method for reinforce phase view
         *
         * @param location  default value
         * @param resources default value
         */
        @Override
        public void initialize (URL location, ResourceBundle resources){
            reinforceViewInit();
        }

        /**
         * init UI controls and corresponding varaibles
         * <p>
         * param playerIndex initialize UI controls and display information of current player
         */
        private void reinforceViewInit () {
            initPhaseView();
            initCurPlayerCardListView();
            addUndeployedArmy();
            InfoRetriver.updateDominationView("From reinforcement initial", vbx_worldDomiView);
            btn_confirmDeployment.setVisible(false);

            int cardsNbr = curPlayer.getCardsList().size();

            System.out.println("\n\ncards number: " + cardsNbr);

            if (cardsNbr <= 2) {
                btn_confirmExchangeCards.setVisible(false);
                btn_skipCardsExchange.setVisible(false);
                btn_confirmDeployment.setVisible(true);
            }
            if (cardsNbr >= 5) {
                btn_skipCardsExchange.setVisible(false);
            }
            Color curPlayerColor = curPlayer.getPlayerColor();
            lbl_countriesInfo.setTextFill(curPlayerColor);
            lbl_adjacentCountriesInfo.setTextFill(curPlayerColor);
            lbl_undeployedArmy.setText(Integer.toString(curUndeployedArmy));
            lsv_ownedCountries.setItems(InfoRetriver.getObservableCountryList(curPlayer));

            System.out.println("country display index: " + curPlayer.getPlayerIndex());

            ListviewRenderer.renderCountryItems(lsv_ownedCountries);

            scb_armyNbrAdjustment.setMax(curUndeployedArmy);
            scb_armyNbrAdjustment.setMin(1);
            scb_armyNbrAdjustment.adjustValue(curUndeployedArmy);
            lbl_deployArmyCount.setText(Integer.toString(curUndeployedArmy));

            scb_armyNbrAdjustment.valueProperty().addListener((observable, oldValue, newValue) -> lbl_deployArmyCount.setText(Integer.toString(newValue.intValue())));

        }

        /**
         * init card obserser pattern
         */
        private void initCurPlayerCardListView () {
            initObserver("CardView");
            ArrayList<Card> cardsList = playerCards;
            cardsList.sort(Collections.reverseOrder());
            ObservableList<Card> cardObservableList = FXCollections.observableList(cardsList);
            lsv_cardsListView.setItems(cardObservableList);
            ListviewRenderer.renderCardsListView(lsv_cardsListView, curPlayer);
            lsv_cardsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        }

        /**
         * set contents to phase view labels
         * contents are obtained via phaseViewObserver
         */
        private void initPhaseView () {
            initObserver("PhaseView");
            curPlayer = playersList.get(curPlayerIndex);
            Color curPlayerColor = curPlayer.getPlayerColor();
            lbl_phaseViewName.setText(curGamePhase);
            lbl_phaseViewName.setTextFill(curPlayerColor);
            lbl_playerName.setText(curPlayerName);
            lbl_playerName.setTextFill(curPlayerColor);
            lbl_actionString.setText(curActionString);
            lbl_actionString.setWrapText(true);
        }

        /**
         * retrieving newest data from observer
         * it will be called every time the view initializes
         *
         * @param whichObs observer name, two available so far
         */
        private void initObserver (String whichObs){
            switch (whichObs) {
                case "PhaseView":
                    curGamePhase = phaseViewObserver.getPhaseName();
                    curPlayerIndex = phaseViewObserver.getPlayerIndex();
                    curPlayerName = "Player_" + curPlayerIndex;
                    curActionString = phaseViewObserver.getActionString();
                    break;
                case "CardView":
                    this.cardExchangeViewObserver = new CardExchangeViewObserver();
                    curPlayer.addObserver(this.cardExchangeViewObserver);
                    //init cards if player already has some
                    curPlayer.initObservableCard();
                    curPlayer.notifyObservers("from reinforce view: initial cards!");
                    this.playerCards = this.cardExchangeViewObserver.getPlayerCards();
                    //TODO: phaseViewObservable will keep adding new cardObserver without removing the old one!
                    phaseViewObservable.deleteObserver(cardExchangeViewObserver);
                    phaseViewObservable.addObserver(this.cardExchangeViewObserver);
                    phaseViewObservable.initObservableExchangeTime();
                    phaseViewObservable.notifyObservers("keeping exchange time up to date.");
                    break;
            }

        }

        /**
         * onClick event for moving to next player if reinforcement phase is not finished or attack view from the first player
         *
         * @param actionEvent next player's turn or proceed to attackview if all players finish reinforcement
         * @throws IOException reinforcement.fxml or attakview.fxml are not found
         */
        @FXML
        public void clickNextStep (ActionEvent actionEvent) throws IOException {
//      curRoundPlayerIndex = (curRoundPlayerIndex + 1) % totalNumOfPlayers;
            Stage curStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            System.out.println("\n???????????????????????????" + reinforceInitCounter);

            if (reinforceInitCounter > 1) {
                checkNextViewNeedChange(false);

                reinforceInitCounter--;

                Pane reinforcePane = new FXMLLoader(getClass().getResource("../view/ReinforceView.fxml")).load();
                Scene reinforceScene = new Scene(reinforcePane, 1200, 900);

                curStage.setScene(reinforceScene);
                curStage.show();

            } else {
                checkNextViewNeedChange(true);

                Pane attackPane = new FXMLLoader(getClass().getResource("../view/AttackView.fxml")).load();
                Scene attackScene = new Scene(attackPane, 1200, 900);

                curStage.setScene(attackScene);
                curStage.show();
            }
        }

        /**
         * notify all phase view observer that game stage changed.
         * Changes can be next player's reinforcement or going to attack phase.
         *
         * @param isAttackPhase true for going to attack phase otherwise, next player's turn
         */
        private void checkNextViewNeedChange ( boolean isAttackPhase){
            if (!isAttackPhase) {
                int nextPlayerIndex = (curPlayerIndex + 1) % Main.totalNumOfPlayers;
                phaseViewObservable.setAllParam("Reinforcement Phase", nextPlayerIndex, curActionString);
                phaseViewObservable.notifyObservers("continue reinforce");

            } else {
                phaseViewObservable.setAllParam("Attack Phase", curRoundPlayerIndex, "Attack Action");
                phaseViewObservable.notifyObservers("From ReinforceView to AttackView");
            }
        }

        /**
         * onClick event when a country item in the ListView is selected and display its adjacent country data in adjacent ListView
         *
         * @param mouseEvent clicking one country name in the listview will display its adjacent countries
         */
        @FXML
        public void selectOneCountry (MouseEvent mouseEvent){
            int countryIndex = lsv_ownedCountries.getSelectionModel().getSelectedIndex();
            ObservableList datalist = InfoRetriver.getAdjacentCountryObservablelist(curPlayerIndex, countryIndex);

            lsv_adjacentCountries.setItems(datalist);
            ListviewRenderer.renderCountryItems(lsv_adjacentCountries);

            System.out.println(">>>select owned country: " + lsv_ownedCountries.getSelectionModel().getSelectedItem());
        }

        /**
         * onClick event for confirming army deployment
         *
         * @param actionEvent deploy a selected number of army to the selected country
         */
        @FXML
        public void clickConfirmDeployment (ActionEvent actionEvent){
            Country selectedCountry = lsv_ownedCountries.getSelectionModel().getSelectedItem();
            int selectedCountryIndex = lsv_ownedCountries.getSelectionModel().getSelectedIndex();
            int undeloyedArmyCount = Integer.parseInt(lbl_undeployedArmy.getText());

            if (selectedCountryIndex == -1) {
                alert.setHeaderText(null);
                alert.setContentText("Please select a country for army deployment");
                alert.showAndWait();
            } else {
                int deployArmyCount = Integer.parseInt(lbl_deployArmyCount.getText());
                selectedCountry.addToCountryArmyNumber(deployArmyCount);

                int remainUndeployedArmyCount = undeloyedArmyCount - deployArmyCount;
                lbl_undeployedArmy.setText(Integer.toString(remainUndeployedArmyCount));

                lsv_ownedCountries.refresh();

                if (remainUndeployedArmyCount == 0) {
                    btn_confirmDeployment.setVisible(false);
                    lbl_undeployedArmy.setVisible(false);
                    lbl_deployArmyCount.setVisible(false);
                    lbl_deployCountPrompt.setVisible(false);
                    scb_armyNbrAdjustment.setVisible(false);
                    lbl_undeployArmyPrompt.setVisible(false);

                    btn_nextStep.setVisible(true);
                    lsv_ownedCountries.getSelectionModel().select(-1);
                } else {
                    scb_armyNbrAdjustment.setMax(remainUndeployedArmyCount);
                    scb_armyNbrAdjustment.adjustValue(remainUndeployedArmyCount);
                    lbl_deployArmyCount.setText(Integer.toString(remainUndeployedArmyCount));
                }

                System.out.println("\nfinish deployment to country " + selectedCountry.getCountryName() + ": " + selectedCountry.getCountryArmyNumber());
            }
            InfoRetriver.updateDominationView("deploy new army", vbx_worldDomiView);
        }

        /**
         * click button for confirming exchange cards
         *
         * @param actionEvent click this button
         */
        public void clickExchangeCards (ActionEvent actionEvent){
            ObservableList<Card> selectedCardList =
                    FXCollections.observableArrayList(lsv_cardsListView.getSelectionModel().getSelectedItems());

            System.out.println("selected cards: " + selectedCardList);

            if (selectedCardList.isEmpty()) {
                alert.setContentText("No card selected!");
                alert.showAndWait();
            } else if (validateCardsCombination(selectedCardList)) {
                int exchangedArmyNbr = getExchangedArmyNbr();
                addUndeployedArmyAfterExchangeCards(exchangedArmyNbr);

                System.out.printf("GET NEW %d ARMY!\n", exchangedArmyNbr);

                removeCardsFromList(selectedCardList);

                lsv_cardsListView.refresh();
                lsv_cardsListView.getSelectionModel().select(-1);

                lbl_undeployedArmy.setText(Integer.toString(curUndeployedArmy));
                scb_armyNbrAdjustment.setMax(curUndeployedArmy);
                scb_armyNbrAdjustment.adjustValue(curUndeployedArmy);
                lbl_deployArmyCount.setText(Integer.toString(curUndeployedArmy));

                btn_confirmDeployment.setVisible(true);
                btn_skipCardsExchange.setVisible(false);
                btn_confirmExchangeCards.setVisible(false);

                phaseViewObservable.addOneExchangeTime();
                phaseViewObservable.notifyObservers("Add Exchange Time");

            } else {
                alert.setContentText("Wrong cards combination, please try again!");
                alert.showAndWait();
            }
        }

        /**
         * @param exchangedArmyNbr army number exchanged to be added to the player
         */
        private void addUndeployedArmyAfterExchangeCards ( int exchangedArmyNbr){
            curUndeployedArmy += exchangedArmyNbr;
            curPlayer.addArmy(exchangedArmyNbr);
        }

        private void addUndeployedArmy () {
            int ownedCountryNum = curPlayer.getOwnedCountryNameList().size();
            int newArmyPerRound = getStandardReinforceArmyNum(ownedCountryNum) + curPlayer.getContinentBonus();
            curUndeployedArmy += newArmyPerRound;
        }

        /**
         * Selected cards will be removed from cards list if the exchange succeeds.
         *
         * @param selectedCardList exchanged card list
         */
        private void removeCardsFromList (ObservableList < Card > selectedCardList) {
            curPlayer.removeObservableCards(selectedCardList);
        }

        /**
         * if the selected cards list satisfies the game rule, in this method it will be calculated to army number the player can exchange.
         *
         * @return exchanged army number
         */
        private int getExchangedArmyNbr () {
            //get exchange time from card observer
            return 5 * cardExchangeViewObserver.getExchangeTime();
        }

        /**
         * skip exchanging cards in the round
         *
         * @param actionEvent click this button
         */
        public void clickSkipCardsExchange (ActionEvent actionEvent){
            btn_skipCardsExchange.setVisible(false);
            btn_confirmExchangeCards.setVisible(false);
            btn_confirmDeployment.setVisible(true);
        }
    }

