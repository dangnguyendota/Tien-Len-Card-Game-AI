package com.ndn.algorithm;

import com.ndn.base.BaseObject;

public class GameConfiguration {
    // array of flags where true is that play passed
    public boolean[] passed = new boolean[4];
    public int maxPlayer = 4;
    // the previous player discard
    public int previousPlayer = 0;
    // lastest discarded cards
    public BaseObject lastDealt = null;
    public int currentPlayer = 0;
    // set default
    public boolean gang_beat = false;
    public boolean first_turn = false;
    public boolean using_heuristic = true;

}
