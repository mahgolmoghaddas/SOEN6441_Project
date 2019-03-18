package riskgame.model.Utils;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import riskgame.Main;
import riskgame.model.BasicClass.Continent;
import riskgame.model.BasicClass.Country;
import riskgame.model.BasicClass.Dice;
import riskgame.model.BasicClass.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static riskgame.controllers.AttackViewController.MAX_ATTACKING_ARMY_NUMBER;
import static riskgame.controllers.AttackViewController.MAX_DEFENDING_ARMY_NUMBER;

/**
 * This class calculates number of dice thrown
 * compares the attacker's and defender's dice
 * checks if country,than continent and then whole worldmap is conquered or not
 */

public class AttackProcess {

    public static void alloutAttackSimulate(Country attackingCountry, Country defendingCountry, int attackArmyNbr, int defendArmyNbr,
                                            TextArea txa_attackInfoDisplay) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Attacker: ")
                .append(attackingCountry.getCountryName())
                .append(", Defender: ")
                .append(defendingCountry.getCountryName())
                .append("\n\n");

        recursiveAttack(attackingCountry, defendingCountry, attackArmyNbr, defendArmyNbr, stringBuilder);

        if (isCountryConquered(defendingCountry)) {
            int undelopyedAttackerArmyNbr = attackingCountry.getCountryArmyNumber() - 1;
            Player attacker = Main.playersList.get(attackingCountry.getCountryOwnerIndex());
            Player defender = Main.playersList.get(defendingCountry.getCountryOwnerIndex());

            updateConqueredCountry(attackingCountry, defendingCountry, undelopyedAttackerArmyNbr, attacker, defender);
        }
        txa_attackInfoDisplay.setText(stringBuilder.toString());
    }

    /**
     * gets the results for attacks untill all the attackes are finished by attacker
     * @param attackingCountry country which is going to attack
     * @param defendingCountry country getting attacked
     * @param attackArmyNbr army number of attacking country
     * @param defendArmyNbr army number of defending country
     * @param stringBuilder
     */
    private static void recursiveAttack(Country attackingCountry, Country defendingCountry, int attackArmyNbr, int defendArmyNbr, StringBuilder stringBuilder) {
        if (attackArmyNbr == 0 || defendArmyNbr == 0) {
            return;
        }
        int avaliableForAttackNbr = attackArmyNbr > MAX_ATTACKING_ARMY_NUMBER ? MAX_ATTACKING_ARMY_NUMBER : attackArmyNbr;
        int avaliableForDefendNbr = defendArmyNbr > MAX_DEFENDING_ARMY_NUMBER ? MAX_DEFENDING_ARMY_NUMBER : defendArmyNbr;

        getOneAttackResult(attackingCountry, defendingCountry, avaliableForAttackNbr, avaliableForDefendNbr, stringBuilder);

        attackArmyNbr = attackingCountry.getCountryArmyNumber() - 1;
        defendArmyNbr = defendingCountry.getCountryArmyNumber();
        stringBuilder.append("\n");
        recursiveAttack(attackingCountry, defendingCountry, attackArmyNbr, defendArmyNbr, stringBuilder);
    }

    public static void oneAttackSimulate(Country attackingCountry, Country defendingCountry, int attackArmyNbr, int defendArmyNbr,
                                         TextArea txa_attackInfoDisplay) {
        int avaliableForAttackNbr = attackArmyNbr > MAX_ATTACKING_ARMY_NUMBER ? MAX_ATTACKING_ARMY_NUMBER : attackArmyNbr;
        int avaliableForDefendNbr = defendArmyNbr > MAX_DEFENDING_ARMY_NUMBER ? MAX_DEFENDING_ARMY_NUMBER : defendArmyNbr;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Attacker: ")
                .append(attackingCountry.getCountryName())
                .append(", Defender: ")
                .append(defendingCountry.getCountryName())
                .append("\n\n");

        int attackerRemainArmyNbr = getOneAttackResult(attackingCountry, defendingCountry, avaliableForAttackNbr, avaliableForDefendNbr, stringBuilder);

        txa_attackInfoDisplay.setText(stringBuilder.toString());

        attackResultProcess(attackingCountry, defendingCountry, attackerRemainArmyNbr);

    }


    private static int getOneAttackResult(Country attackingCountry, Country defendingCountry, int attackableArmyNbr, int defendableArmyNbr,
                                          StringBuilder stringBuilder) {
        int result = 0;

        ArrayList<Integer> attackerDiceResultList = getDiceResultList(attackableArmyNbr);
        ArrayList<Integer> defenderDiceResultList = getDiceResultList(defendableArmyNbr);

        stringBuilder
                .append("attacker dices: ")
                .append(attackerDiceResultList)
                .append(", defender dices: ")
                .append(defenderDiceResultList)
                .append("\n");

        System.out.println("\nattackerDiceList: " + attackerDiceResultList);
        System.out.println("defenderDiceResult:" + defenderDiceResultList + "\n");

        int attackArmyCount = attackerDiceResultList.size();
        int defendArmyCount = defenderDiceResultList.size();

        int compareTimes = defenderDiceResultList.size() < attackerDiceResultList.size() ? defenderDiceResultList.size() :
                attackerDiceResultList.size();

        for (int i = 0; i < compareTimes; i++) {
            System.out.println("\nattacker: " + attackerDiceResultList.size()
                    + ", defender: " + defenderDiceResultList.size());

            int bestAttackerDice = attackerDiceResultList.remove(0);
            int bestDefenderDice = defenderDiceResultList.remove(0);

            if (bestAttackerDice > bestDefenderDice) {
                defendingCountry.reduceFromCountryArmyNumber(1);

                System.out.println("Round: " + i + ", >>>Attacker win! attacker: " + attackingCountry.getCountryArmyNumber()
                        + ", defender: " + defendingCountry.getCountryArmyNumber() + "\n");

                defendArmyCount--;

                stringBuilder
                        .append("Round: ")
                        .append(i)
                        .append("\n>>>[ ")
                        .append(attackingCountry.getCountryName())
                        .append(" ]<<< WINS ")
                        .append(">>>[ ")
                        .append(defendingCountry.getCountryName())
                        .append(" ]<<<\nattacker remaining army num: ")
                        .append(attackingCountry.getCountryArmyNumber())
                        .append(", defender remaining army num: ")
                        .append(defendingCountry.getCountryArmyNumber())
                        .append("\n")
                        .append("defend army count after reduction: ")
                        .append(defendArmyCount)
                        .append(", attack army count: ")
                        .append(attackArmyCount)
                        .append("\n");
            } else {
                attackingCountry.reduceFromCountryArmyNumber(1);

                System.out.println("Round: " + i + ", >>>Defender win! attacker: " + attackingCountry.getCountryArmyNumber()
                        + ", defender: " + defendingCountry.getCountryArmyNumber() + "\n");

                attackArmyCount--;

                stringBuilder
                        .append("Round: ")
                        .append(i)
                        .append("\n>>>[ ")
                        .append(attackingCountry.getCountryName())
                        .append(" ]<<< LOSES to ")
                        .append(">>>[ ")
                        .append(defendingCountry.getCountryName())
                        .append(" ]<<<\nattacker remaining army num: ")
                        .append(attackingCountry.getCountryArmyNumber())
                        .append(", defender remaining army num: ")
                        .append(defendingCountry.getCountryArmyNumber())
                        .append("\n")
                        .append("attack army count after reduction: ")
                        .append(attackArmyCount)
                        .append(", defend army count: ")
                        .append(defendArmyCount)
                        .append("\n");
            }
        }
        result = attackArmyCount;

        return result;
    }

    /**
     * Checks if country,continent or whole world is conquered
     * @param attackingCountry
     * @param defendingCountry
     * @param remainingArmyNbr army number left after dice throw and attack process
     */

    private static void attackResultProcess(Country attackingCountry, Country defendingCountry, int remainingArmyNbr) {
        int defenderIndex = defendingCountry.getCountryOwnerIndex();
        int attackerIndex = attackingCountry.getCountryOwnerIndex();

        Player attackPlayer = Main.playersList.get(attackerIndex);
        Player defendPlayer = Main.playersList.get(defenderIndex);

        String continentName = defendingCountry.getContinentName();
        Continent curContinent = Main.worldContinentMap.get(continentName);

        if (AttackProcess.isCountryConquered(defendingCountry)) {
            updateConqueredCountry(attackingCountry, defendingCountry, remainingArmyNbr, attackPlayer, defendPlayer);

            String defendCountryName = defendingCountry.getCountryName();
            int attackCountryArmyNbr = attackingCountry.getCountryArmyNumber();

            if (attackCountryArmyNbr > 1) {
                List<Integer> choices = IntStream.range(0, attackCountryArmyNbr).boxed().collect(Collectors.toList());

                System.out.println("attacking remaining army: " + attackCountryArmyNbr + ", remaining: " + remainingArmyNbr + ", choices: " + choices);

                ChoiceDialog<Integer> dialog = new ChoiceDialog<>(0, choices);
                dialog.setTitle("Army deployment");
                dialog.setHeaderText("Please input number of army for moving to \"" + defendCountryName + "\"");
                dialog.setContentText("Choose a number: ");

                Optional<Integer> dialogInput = dialog.showAndWait();
                if (dialogInput.isPresent()) {
                    int deployArmyNbr = dialogInput.get();

                    System.out.println("input deploy number: " + deployArmyNbr);

                    if (deployArmyNbr > 0) {
                        attackingCountry.reduceFromCountryArmyNumber(deployArmyNbr);
                        defendingCountry.addToCountryArmyNumber(deployArmyNbr);
                    }
                }
            }

            if (AttackProcess.isContinentConquered(defenderIndex, continentName)) {
                Player defenderPlayer = Main.playersList.get(defenderIndex);

                int continentBonus = curContinent.getContinentBonusValue();

                defenderPlayer.reduceContinentBonus(continentBonus);
                curContinent.setContinentOwnerIndex(-1);

                AttackProcess.updateContinentOwner(attackerIndex, continentName);

            }
            if (AttackProcess.isContinentConquered(attackerIndex, continentName)) {
                curContinent.setContinentOwnerIndex(attackerIndex);
                Player attackerPlayer = Main.playersList.get(attackerIndex);

                int continentBonus = curContinent.getContinentBonusValue();

                attackerPlayer.addContinentBonus(continentBonus);

                AttackProcess.updateWorldOwner(attackerIndex);
            }

        }
    }

    private static void updateConqueredCountry(Country attackingCountry, Country defendingCountry, int remainingArmyNbr, Player attackPlayer, Player defendPlayer) {
        int attackerIndex = attackPlayer.getPlayerIndex();
//        attackPlayer.getOwnedCountryNameList().add(defendCountryName);
//        defendPlayer.getOwnedCountryNameList().remove(defendCountryName);
//        defendingCountry.setCountryOwnerIndex(attackerIndex);
//        defendingCountry.setCountryArmyNumber(remainingArmyNbr);
//        attackingCountry.reduceFromCountryArmyNumber(remainingArmyNbr);

        defendingCountry.setObservableOwner(attackerIndex);
        defendingCountry.setObservableArmy(remainingArmyNbr);
        defendingCountry.notifyObservers("from update conquered country");
        attackingCountry.reduceFromCountryArmyNumber(remainingArmyNbr);

    }


    /**
     * get the random dice result list
     *
     * @param diceTimes number of dicing rolls
     * @return a new arrayList of Integer
     */
    public static ArrayList<Integer> getDiceResultList(int diceTimes) {
        ArrayList<Integer> result;
        Dice dice = new Dice();
        result = dice.rollNDice(diceTimes);

        return result;
    }


    public static boolean isCountryConquered(Country country) {
        boolean result = false;
        int remainingArmyNbr = country.getCountryArmyNumber();
        if (remainingArmyNbr == 0) {
            result = true;
        }

        System.out.println("country conquered: " + result);

        return result;
    }

    public static boolean isContinentConquered(int playerIndex, String continentName) {
        boolean result = true;

        Continent curContinent = Main.worldContinentMap.get(continentName);
        LinkedHashMap<String, Country> continentCountryGraph = curContinent.getContinentCountryGraph();

        for (Map.Entry<String, Country> entry : continentCountryGraph.entrySet()) {
            int curOwnerIndex = entry.getValue().getCountryOwnerIndex();
            if (curOwnerIndex != playerIndex) {
                result = false;
                break;
            }
        }

        System.out.println("continent conquered: " + result);

        return result;
    }

    public static void updateContinentOwner(int attackerIndex, String continentName) {
        boolean result = true;
        Continent curContinent = Main.worldContinentMap.get(continentName);
        LinkedHashMap<String, Country> continentCountryGraph = curContinent.getContinentCountryGraph();

        for (Map.Entry<String, Country> entry : continentCountryGraph.entrySet()) {
            int curOwnerIndex = entry.getValue().getCountryOwnerIndex();
            if (curOwnerIndex != attackerIndex) {
                result = false;
                break;
            }
        }
        if (result == true) {
            curContinent.setContinentOwnerIndex(attackerIndex);
        }

    }

    public static void updateWorldOwner(int attackerIndex) {
        boolean result = true;

        for (Map.Entry<String, Continent> entry : Main.worldContinentMap.entrySet()) {
            int curOwnerIndex = entry.getValue().getContinentOwnerIndex();
            if (curOwnerIndex != attackerIndex) {
                result = false;
                break;
            }
        }
        if (result == true) {
            System.out.println(attackerIndex + "wins");
        }

    }
}
