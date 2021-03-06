package riskgame.model.BasicClass;

import java.util.Observable;

/**
 * This class includes attributes a country needs and required methods
 * as a observable, country is observed by player
 * @author WW
 * @since build1
 **/
public class Country extends Observable{
    private final String countryName;
    private String continentName;
    private String coordinateX;
    private String coordinateY;
//    private int countryOwnerIndex;
    private Player owner;
    private int countryArmyNumber;

    /**
     * constructor for class Country
     *
     * @param countryName string of a country name
     */
    public Country(String countryName) {
        this.countryName = countryName;
        this.continentName = null;
        this.coordinateX = "";
        this.coordinateY = "";
//        this.countryOwnerIndex = -1;
        this.owner = null;
        this.countryArmyNumber = 1;
    }

    /**
     * getter
     *
     * @return continent name of the country
     */
    public String getContinentName() {
        return continentName;
    }

    /**
     * setter
     *
     * @param continentName continent name of the country
     */
    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    /**
     * setter
     *
     * @param coordinateX coordinate x
     */
    public void setCoordinateX(String coordinateX) {
        this.coordinateX = coordinateX;
    }

    /**
     * setter
     *
     * @param coordinateY coordinate y
     */
    public void setCoordinateY(String coordinateY) {
        this.coordinateY = coordinateY;
    }

    /**
     * getter
     *
     * @return current country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * getter
     *
     * @return player index of the country owner
     */
    public int getOwnerIndex() {
        return owner.getPlayerIndex();
    }

    public Player getOwner() {
        return owner;
    }

    public void setCountryOwner(Player newOwner) {
        this.owner = newOwner;
    }

    /**
     * Call when owner changed
     * Call in attack process only, which needs to notify player
     * @param newOwner new player
     * @param army new move-in army(by attacker)
     */
    public void setObservableArmyWhenOwnerChanged(Player newOwner, int army){
        this.owner = newOwner;
        this.countryArmyNumber = army;
        setChanged();
    }

    /**
     * getter
     *
     * @return army number in the country
     */
    public int getCountryArmyNumber() {
        return countryArmyNumber;
    }

    /**
     * add certain army number to the country
     *
     * @param addedNumber amount of army to be added to the country
     * @return true for success, false for failure
     */
    public boolean addToCountryArmyNumber(int addedNumber) {
        boolean result = false;
        if (this.countryArmyNumber >= 0) {
            this.countryArmyNumber += addedNumber;
            result = true;
        }
        return result;
    }

    /**
     * reduce certain army number from the country
     *
     * @param reducedNumber amount of army to be reduced
     * @return true for success, false for failure
     */
    public boolean reduceFromCountryArmyNumber(int reducedNumber) {
        boolean result = false;
        if (this.countryArmyNumber >= reducedNumber) {
            this.countryArmyNumber -= reducedNumber;
            result = true;
        }
        return result;
    }


    /**
     * Setter. Only be called in testing
     * Do Not Call!
     * @param countryArmyNumber army number to be set to the country
     */
    public void setCountryArmyNumber(int countryArmyNumber) {
        this.countryArmyNumber = countryArmyNumber;
    }

    /**
     * Call in attack process, which needs to notify player
     * @param countryArmyNumber army
     */
    public void setObservableArmy(int countryArmyNumber) {
        this.countryArmyNumber = countryArmyNumber;
        setChanged();
    }

    /**
     * Call in load map
     * @param countryArmyNumber army
     */
    public void setArmy(int countryArmyNumber) {
        this.countryArmyNumber = countryArmyNumber;
    }

    /**
     * string of country name and its army number
     *
     * @return combination of country name and army number
     */
    @Override
    public String toString() {
        return getCountryName() + " : " + getCountryArmyNumber();
    }

}
