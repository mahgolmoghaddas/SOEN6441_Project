package riskgame.model.BasicClass;

import riskgame.model.BasicClass.ObserverPattern.PhaseViewObservable;
import riskgame.model.BasicClass.StrategyPattern.Strategy;
import riskgame.model.Utils.InitPlayers;
import riskgame.model.Utils.InitWorldMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author Wei Wang
 * @version 1.0
 * @since 2019/03/29
 **/

public class TournamentGame implements Runnable {

    private String mapFile;
    private ArrayList<Strategy> playerStrategyList;
    private int gameRoundValue;

    private LinkedHashMap<String, GraphNode> worldMapInstance;
    private LinkedHashMap<String, Continent> continentLinkedHashMap;
    private ArrayList<Player> robotPlayerList;

    private int gameWinner;
    private PhaseViewObservable tournamentObservable;
    private final int MAX_GAME_ROUND = 100;
    private Player winnerPlayer;

    private final int DEFAULTWINNERINDEX = 8;

    public TournamentGame(){}
    public TournamentGame(String mapFile, ArrayList<Strategy> playerStrategyList, int gameRoundValue) {
        this.mapFile = mapFile;
        this.playerStrategyList = playerStrategyList;
        this.gameRoundValue = gameRoundValue;
        this.worldMapInstance = new LinkedHashMap<>();
        //this.continentLinkedHashMap = new LinkedHashMap<>();
        this.gameWinner = DEFAULTWINNERINDEX;
        this.robotPlayerList = new ArrayList<>();
        this.tournamentObservable = new PhaseViewObservable();
        initDefaultWinner();
    }

    public TournamentGame(String mapFile, ArrayList<Strategy> playerStrategyList) {
        this.mapFile = mapFile;
        this.playerStrategyList = playerStrategyList;
        this.gameRoundValue = MAX_GAME_ROUND;
        this.robotPlayerList = new ArrayList<>();
        //this.continentLinkedHashMap = new LinkedHashMap<>();
        this.gameWinner = DEFAULTWINNERINDEX;
        this.tournamentObservable = new PhaseViewObservable();
        this.worldMapInstance = new LinkedHashMap<>();
        initDefaultWinner();
    }

    private void initDefaultWinner() {
        this.winnerPlayer = new Player(DEFAULTWINNERINDEX);
        this.winnerPlayer.setPlayerName("No Player");
        this.winnerPlayer.setWorldMapInstance(worldMapInstance);
        this.winnerPlayer.setContinentMapInstance(continentLinkedHashMap);
    }

    private void mainGamingLogic() throws IOException {
        initMapAndPlayers();

        doAllPlayerReinforcement(robotPlayerList, tournamentObservable);

        doAllPlayerAttackAndFortification(robotPlayerList);

        int gameRoundLeft = gameRoundValue - 1;

        doRegularGaming(gameRoundLeft);

        System.out.println("\n\n\nOne Tournament END:\n");

        InitWorldMap.printGraph(worldMapInstance, robotPlayerList);

        System.out.println("\n>>>>> Final winner in doRegular() method: " + gameWinner + ", map: " + mapFile + "\n\n");
    }

    private void initMapAndPlayers() throws IOException {
        GraphNormal worldGraphNormal = new GraphNormal();
        worldMapInstance = worldGraphNormal.getWorldHashMap();
        continentLinkedHashMap = new LinkedHashMap<>();

        InitWorldMap.buildWorldMapGraph(mapFile, worldMapInstance, continentLinkedHashMap);
        int numOfplayers = playerStrategyList.size();
        InitPlayers.initPlayers(numOfplayers, worldMapInstance, continentLinkedHashMap, playerStrategyList, robotPlayerList);

        System.out.println("\n\nmap: " + mapFile);
        System.out.println("gameRoundValue: " + gameRoundValue);
        System.out.println("playerlist: " + robotPlayerList + "\n\n");

        InitWorldMap.printGraph(worldMapInstance, robotPlayerList);
    }


    private void doRegularGaming(int gameRoundLeft) {
        while (gameRoundLeft > 0 && gameWinner == DEFAULTWINNERINDEX) {
            for (int playerIndex = 0; playerIndex < robotPlayerList.size(); playerIndex++) {
                Player curRobot = robotPlayerList.get(playerIndex);
                if (curRobot.getActiveStatus()) {
                    curRobot.executeReinforcement(tournamentObservable);
                    curRobot.executeAttack();

                    System.out.println("\n\n!!!!!!!robot " + playerIndex + ", " + curRobot.getPlayerName()
                            + ": regular gaming!  Round left: " + gameRoundLeft + "\n\n");

                    if (curRobot.isFinalWinner()) {
                        gameWinner = curRobot.getPlayerIndex();

                        System.out.println("Tournament game winner: " + gameWinner);

                    } else {
                        curRobot.executeFortification();
                    }
                }
            }
            gameRoundLeft--;
        }
    }

    public void doAllPlayerAttackAndFortification(ArrayList<Player> robotPlayerList) {
        for (int playerIndex = 0; playerIndex < robotPlayerList.size(); playerIndex++) {
            Player curRobot = robotPlayerList.get(playerIndex);
            if (curRobot.getActiveStatus()) {
                curRobot.executeAttack();
                System.out.println("\n\n\nrobot " + playerIndex + ": doAllPlayerAttackAndFortification\n\n");

                if (curRobot.isFinalWinner()) {
                    gameWinner = curRobot.getPlayerIndex();

                    System.out.println("Tournament game winner: " + gameWinner);

                } else {
                    curRobot.executeFortification();
                }

            }
        }
    }

    public void doAllPlayerReinforcement(ArrayList<Player> robotPlayerList, PhaseViewObservable tournamentObservable) {
        for (int playerIndex = 0; playerIndex < robotPlayerList.size(); playerIndex++) {
            Player curRobot = robotPlayerList.get(playerIndex);
            curRobot.executeReinforcement(tournamentObservable);

            System.out.println("\n\nrobot " + playerIndex + ": doAllPlayerReinforcement\n\n");
        }
    }

    @Override
    public void run() {
        try {

            System.out.println("\n\n!!!!!!!!tournamentGame instance start!!! map: " + mapFile + "\n\n");

            mainGamingLogic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getGameWinner() {
        String result = "Draw";

        if (gameWinner != DEFAULTWINNERINDEX) {
            result = robotPlayerList.get(gameWinner).getPlayerName();
        }
        return result;
    }

    public void singleModeRun() throws IOException {
        mainGamingLogic();

        if (gameWinner != DEFAULTWINNERINDEX) {
            this.winnerPlayer = robotPlayerList.get(gameWinner);
        }

    }

    public Player getWinnerPlayer() {
        return winnerPlayer;
    }

    public ArrayList<Player> getRobotPlayerList() {
        return robotPlayerList;
    }

    public int getMAX_GAME_ROUND() {
        return MAX_GAME_ROUND;
    }
}
