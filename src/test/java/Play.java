import com.ndn.algorithm.*;
import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.Game;
import com.ndn.base.Player;
import com.ndn.objects.Dub;
import com.ndn.objects.Pass;
import com.ndn.util.DangNguyenDota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Play {
    private static ArrayList<Card> player1Card;
    private static ArrayList<Card> player2Card;
    private static ArrayList<Card> player3Card;
    private static ArrayList<Card> player4Card;
    private static Player player1;
    private static Player player2;
    private static Player player3;
    private static Player player4;

    private static Player getPlayer(int index){
        switch (index){
            case 0:
                return player1;
            case 1:
                return player2;
            case 2:
                return player3;
            case 3:
                return player4;
        }
        return null;
    }

    private static ArrayList<Card> from(String str) {
        str = str.replace(" ", "");
        String[] split = str.split(",");
        ArrayList<Card> cards = new ArrayList<>();
        for (String i : split) {
            cards.add(new Card(i));
        }
        return cards;
    }

    private static boolean contains(Card[] container, Card card) {
        for (Card c : container) {
            if (c.equals(card)) return true;
        }
        return false;
    }

    private static Card[] getCards(Player player) {
        Card[] cards = player.getStartCards();
        ArrayList<BaseObject> objects = player.listAvailableMoves();
        ArrayList<Card> out = new ArrayList<>();
        for (BaseObject o : objects) {
            for (Card c : o.getCards()) {
                if (contains(cards, c) && out.indexOf(c) == -1) out.add(c);
            }
        }

        return out.toArray(new Card[0]);
    }

    public static Game createGame() {
        Card[] deck = Card.deckOfCards();
        Card[] tmp1 = Card.randomCards(deck, 13);
        deck = Card.removeFrom(deck, tmp1);
        Card[] tmp2 = Card.randomCards(deck, 13);
        deck = Card.removeFrom(deck, tmp2);
        Card[] tmp3 = Card.randomCards(deck, 13);
        deck = Card.removeFrom(deck, tmp3);
        Card[] tmp4 = Card.randomCards(deck, 13);

        player1Card = new ArrayList<>(Arrays.asList(tmp1));
        player2Card = new ArrayList<>(Arrays.asList(tmp2));
        player3Card = new ArrayList<>(Arrays.asList(tmp3));
        player4Card = new ArrayList<>(Arrays.asList(tmp4));

//        player1Card = from("2♥, 3♥, 2♣, 4♥, 5♥, 6♣, 7♣, 8♠, J♦, 9♦, 10♠, K♣, Q♥");
//        player2Card = from("10♥, 10♦, K♦, K♥, Q♠, A♦, 9♥");

//        player1Card = from("2♣, 3♣, 9♦, K♠, A♦");
//        player2Card = from("3♦, 3♥, 3♠, 2♦, 8♣, A♣, J♥, 10♣, 6♥");

//        player2Card = from("8♣, 8♠, 10♣, 10♠, A♣, A♠, A♥");
//        player1Card = from("K♣, K♠");
//        player1Card = from("3♣, 5♠, 5♦, 6♠, 6♣, 6♦, 7♣, 7♦, Q♠, Q♦, A♣, A♦, 2♦");
//        player2Card = from("3♠, 4♠, 5♣, 8♠, 9♠, 10♥, J♦, J♥, Q♣, K♣, A♠, A♥, 2♣");

//        player1Card = from("3♠, 3♦, 3♣, 3♥, 4♠, 4♦, 5♠, 5♦, 6♠, 6♦");
//        player2Card = from("2♠");

        player1Card = from("2♠, 8♦, 3♣");
        player2Card = from("4♠, 4♦, 5♠, 5♦, 6♠, 6♦, 7♦, Q♠");

        /* config game */
        GameConfiguration gameConfiguration = new GameConfiguration();
        gameConfiguration.maxPlayer = 2;
        gameConfiguration.passed = new boolean[gameConfiguration.maxPlayer];
        gameConfiguration.currentPlayer = 0;
        gameConfiguration.previousPlayer = gameConfiguration.currentPlayer;
        gameConfiguration.lastDealt = null;
        gameConfiguration.gang_beat = false;
        gameConfiguration.first_turn = false;
        /* create game */
        Game game = new TienLenGame(gameConfiguration);
        player1 = new TienLenPlayer();
        player2 = new TienLenPlayer();
        player3 = new TienLenPlayer();
        player4 = new TienLenPlayer();

        player1.setBot(false);
        player2.setBot(true);
        player3.setBot(true);
        player4.setBot(true);

        player1.setCards(player1Card.toArray(new Card[0]));
        player2.setCards(player2Card.toArray(new Card[0]));
        player3.setCards(player3Card.toArray(new Card[0]));
        player4.setCards(player4Card.toArray(new Card[0]));

        game.put(player1);
        game.put(player2);
        if(gameConfiguration.maxPlayer >= 3) game.put(player3);
        if(gameConfiguration.maxPlayer == 4) game.put(player4);
        return game;
    }

    private static void playSolo() {
        try {
            Scanner scanner = new Scanner(System.in);
            Game game = createGame();
            /* config bot */
            MctsPlayerConfiguration configuration = new MctsPlayerConfiguration();
//            configuration.iterations = 1000000000;
//            configuration.C = Math.sqrt(2);
            configuration.debug = true;
//            configuration.usingK = true;
            configuration.minTime = 500;
            configuration.maxTime = 1000;
            /* create bot */
            MonteCarloTreeSearchPlayer bot = new MonteCarloTreeSearchPlayer();
            /* search */
            while (!game.end()) {
                int index = game.getCurrentPlayerIndex();
                if (index == 0) {
                    System.out.println("Player " + Arrays.toString(getCards(player1)));
                    System.out.println("your turn, please choose one: ");
                    ArrayList<BaseObject> objects = new ArrayList<>(game.getAvailableMoves());
                    if (game.getCurrentPlayerIndex() != game.getPreviousPlayerIndex()) objects.add(new Pass());
                    for (int i = 0; i < objects.size(); i++) {
                        System.out.println(String.valueOf(i + 1) + ". " + objects.get(i));
                    }
                    System.out.print("Choose: ");
                    int choose = scanner.nextInt();
                    BaseObject o = objects.get(choose - 1);
                    System.out.println("[PLAYER] " + o);
                    game.move(o);
                } else {
                    assert getPlayer(index) != null;
                    String cardStr = Arrays.toString(getCards(getPlayer(index)));
                    game.getConfig().using_heuristic = true;
                    BaseObject object = bot.getMove(game, configuration);
                    game.move(object);
                    System.out.println("Bot " + index + " " + cardStr + " " + object);
                }
            }
            System.out.print("********* Winner " + (game.getWinner() == 0 ? "Player" : "Bot " + game.getWinner()) + " *********");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void botSolo() {
        Game game = createGame();
        /* config bot */
        MctsPlayerConfiguration config1 = new MctsPlayerConfiguration();
        config1.iterations = 1000000000;
        config1.C = Math.sqrt(2);
        config1.debug = false;
        MctsPlayerConfiguration config2 = new MctsPlayerConfiguration();
        config2.iterations = 1000000000;
        config2.C = Math.sqrt(2);
        config2.debug = false;
        /* create bot */
        MonteCarloTreeSearchPlayer bot1 = new MonteCarloTreeSearchPlayer();
        MonteCarloTreeSearchPlayer bot2 = new MonteCarloTreeSearchPlayer();
        /* search */
        while (!game.end()) {
            if (game.getCurrentPlayerIndex() == 0) {
                System.out.println("Bot1 cards: " + Arrays.toString(getCards(player1)));
                BaseObject object = bot1.getMove(game, config1);
                game.move(object);
                System.out.println("Bot1 moved: " + object);
            } else {
                System.out.println("Bot2 cards: " + Arrays.toString(getCards(player2)));
                BaseObject object = bot2.getMove(game, config2);
                game.move(object);
                System.out.println("Bot2 moved: " + object);
            }
        }

        System.out.print("********* Winner " + (game.getWinner() == 0 ? "Bot1" : "Bot2") + " *********");
    }

    static void calculateAverage(){
        int counter = 0;
        int lose = 0, heuristic = 0;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 120000){
            counter++;
            Player player = new TienLenPlayer();
            player.setCards(Card.randomCards(Card.deckOfCards(), 13));
            player.scan();
            lose += player.getLosingScore();
            heuristic += player.getHeuristicScore();
        }
        System.out.println("counter: " + counter + ", lose: " + Double.toString(1.0 * lose / counter) + ", heuristic: " + Double.toString(1.0 * heuristic / counter));
    }

    public static void main(String[] args) {
        playSolo();
    }
}
