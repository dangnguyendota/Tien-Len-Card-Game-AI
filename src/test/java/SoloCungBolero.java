import com.ndn.algorithm.*;
import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.Game;
import com.ndn.base.Player;
import com.ndn.exeption.TLException;
import com.ndn.objects.Pass;
import com.ndn.util.DangNguyenDota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/* SOLO VOI BOT */
public class SoloCungBolero {

    private static ArrayList<Card> playersCard1;
    private static ArrayList<Card> playersCard2;
    private static ArrayList<Card> playersCard3;
    private static ArrayList<Card> playersCard4;

    private static void init() {
        Card[] deck = Card.deckOfCards();
        Card[] p1 = Card.randomCards(deck, 13);
        playersCard1 = new ArrayList<>(Arrays.asList(p1));
        deck = Card.removeFrom(deck, p1);
        Card[] p2 = Card.randomCards(deck, 13);
        playersCard2 = new ArrayList<>(Arrays.asList(p2));
        deck = Card.removeFrom(deck, p2);
        Card[] p3 = Card.randomCards(deck, 13);
        playersCard3 = new ArrayList<>(Arrays.asList(p3));
        deck = Card.removeFrom(deck, p3);
        Card[] b = Card.randomCards(deck, 13);
        playersCard4 = new ArrayList<>(Arrays.asList(b));
    }

    private void tmp(){
//        System.out.println("Your cards: " + Arrays.toString(getCards(player2)));
//        System.out.println("your turn, please choose one: ");
//        ArrayList<BaseObject> objects = new ArrayList<>(game.getAvailableMoves());
//        if(game.getCurrentPlayerIndex() != game.getPreviousPlayerIndex()) objects.add(new Pass());
//        for(int i = 0; i < objects.size(); i++){
//            System.out.println(String.valueOf(i + 1) + ". " + objects.get(i));
//        }
//        System.out.print("Choose: ");
//        int choose = scanner.nextInt();
//        BaseObject o = objects.get(choose - 1);
//        System.out.println("Your chosen: " + o);
//        game.move(o);
//        if(game.end()) System.out.println("** END GAME YOU WON **");
    }

    public static void main(String[] args) {
        int[] winLog = {0, 0, 0, 0};
        int iteration = 10;
        /* config bot */
        MctsPlayerConfiguration config1 = new MctsPlayerConfiguration();
        config1.iterations = 1000000000;
        config1.C = Math.sqrt(2);
        MctsPlayerConfiguration config2 = new MctsPlayerConfiguration();
        config2.iterations = 1000000000;
        config2.C = Math.sqrt(2);
        MctsPlayerConfiguration config3 = new MctsPlayerConfiguration();
        config3.iterations = 1000000000;
        config3.C = Math.sqrt(2);
        MctsPlayerConfiguration config4 = new MctsPlayerConfiguration();
        config4.iterations = 1000000000;
        config4.C = Math.sqrt(2);
        /* create bot */
        MonteCarloTreeSearchPlayer bot1 = new MonteCarloTreeSearchPlayer();
        MonteCarloTreeSearchPlayer bot2 = new MonteCarloTreeSearchPlayer();
        MonteCarloTreeSearchPlayer bot3 = new MonteCarloTreeSearchPlayer();
        MonteCarloTreeSearchPlayer bot4 = new MonteCarloTreeSearchPlayer();
        while (iteration > 0) {
            iteration--;
            try {
                init();
                /* config game */
                GameConfiguration gameConfiguration = new GameConfiguration();
                gameConfiguration.maxPlayer = 4;
                gameConfiguration.passed = new boolean[gameConfiguration.maxPlayer];
                gameConfiguration.currentPlayer = 0;
                gameConfiguration.previousPlayer = 0;
                gameConfiguration.lastDealt = null;
                /* create game */
                Game game = new TienLenGame(gameConfiguration);
                Player player1 = new TienLenPlayer().withCards(playersCard1.toArray(new Card[0]));
                Player player2 = new TienLenPlayer().withCards(playersCard2.toArray(new Card[0]));
                Player player3 = new TienLenPlayer().withCards(playersCard3.toArray(new Card[0]));
                Player player4 = new TienLenPlayer().withCards(playersCard4.toArray(new Card[0]));
                game.put(player1);
                game.put(player2);
                game.put(player3);
                game.put(player4);
                /* search */
                while (!game.end()) {
                    BaseObject object = null;
                    if (game.getCurrentPlayerIndex() == 0) object = bot1.getMove(game, config1);
                    else if (game.getCurrentPlayerIndex() == 1) object = bot2.getMove(game, config2);
                    else if (game.getCurrentPlayerIndex() == 2) object = bot3.getMove(game, config3);
                    else if (game.getCurrentPlayerIndex() == 3) object = bot4.getMove(game, config4);
                    game.move(object);
                }
                winLog[game.getWinner()]++;
                System.out.println(Arrays.toString(winLog));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
