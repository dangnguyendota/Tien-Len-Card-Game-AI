package com.ndn.algorithm;

public class MctsPlayerConfiguration {
    public int iterations = 1000000000; /* loop */
    public double C = Math.sqrt(2); /* factor */
    public boolean debug = false;
    public long minTime = 2000;
    public long maxTime = 7000;
    public boolean usingK = true;
    public double K = 500;
}
