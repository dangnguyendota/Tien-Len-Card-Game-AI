follow this code to create a game of tien len:

    GameConfiguration gameConfiguration = new GameConfiguration();
    gameConfiguration.maxPlayer = 2; // maximum number of players in the game (2, 3 or 4)
    // if passed[number] == true it means players at 'number' passed in current round
    gameConfiguration.passed = new boolean[gameConfiguration.maxPlayer]; 
    // number of current player
    gameConfiguration.currentPlayer = 1;
    // the previous player.
    gameConfiguration.previousPlayer = gameConfiguration.currentPlayer;
    // last cards dealt 
    gameConfiguration.lastDealt = null;
    // create player 
    for(int i = 0; i < numberOfPlayer; i++) {
        Player player = new TienLenPlayer();
        player.setCards('Array of cards');
        game.put(player);
     }
     
     // find best cards to deal
     MonteCarloTreeSearchPlayer bot = new MonteCarloTreeSearchPlayer();
     BaseObject object = bot.getMove(game, new MctsPlayerConfiguration());
     // print object
     System.out.println(object);
     // you can also get cards in object by using object.getCards()
     
 see `Play` in test folder for more information