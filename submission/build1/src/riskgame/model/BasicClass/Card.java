package riskgame.model.BasicClass;

/**
 * Card class
 **/

public enum Card {
    INFANTRY("Infantry"),
    CAVALRY("Cavalry"),
    ARTILLERY("Artillery");

    private final String cardType;

    Card(String cardType) {
        this.cardType = cardType;
    }
}
