package com.ndn.util;

import com.ndn.algorithm.MonteCarloTreeSearchPlayer;
import com.ndn.algorithm.MctsPlayerConfiguration;
import com.ndn.algorithm.TienLenPlayer;
import com.ndn.base.Player;

import java.util.Random;

public class DangNguyenDota {
    public static final Random random = new Random();

    public static Player getNew(){
        return new TienLenPlayer();
    }
}
