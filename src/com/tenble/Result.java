package com.tenble;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Ben on 31/05/2016.
 */
public class Result {

    final HashMap<Integer, Set<Integer>> choiceToPeople;
    final int error;
    final int choiceIgot;

    /**
     * DOES NOT CLONE
     * @param choiceToPeople
     * @param error
     */
    public Result(HashMap<Integer, Set<Integer>> choiceToPeople, int error, int choiceIGot) {
        this.choiceToPeople = choiceToPeople;
        this.error = error;
        this.choiceIgot = choiceIGot;
    }

}
