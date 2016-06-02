package com.tenble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Created by Ben on 31/05/2016.
 */
public class Data {

    private static int MAX_NUM_CHOICES = 17;
    private static int NUM_PREFERENCES_ON_SHEET = 15;
    private static int[] SET_ORDERING = new int[] { 1, 5, 10, 9, 3, 8, 6, 13, 12, 4, 7, 11, 2, 14, 15 };
    //private static Optional<int[]> PRESET_SHEET = Optional.of(new int[] { 5,1,10,9,3,8,13,6,4,2,12,15,7,14,11 });
    private static Optional<int[]> PRESET_SHEET = Optional.of(new int[] { 5, 1, 10, 9, 3, 8, 6, 13, 12, 4, 7, 11, 2, 14, 15 });
    private static HashMap<Integer, Integer> choiceToOrder;

    int orderedChoices[]; // ordered choices by popularity, eg [0] = 3 means 3rd choice out of all choices is most popular
    HashMap<Integer, Set<Integer>> choiceToPeople;
    ArrayList<PreferenceSheet> allPeoplePreferences;

    static {
        choiceToOrder = new HashMap<>();
        for (int i = 0; i < SET_ORDERING.length; i++) {
            choiceToOrder.put(SET_ORDERING[i], i);
        }
    }

    public Data(List<Integer> numberOneChoices) {
        this.choiceToPeople = new HashMap<>();
        this.allPeoplePreferences = new ArrayList<>();

        // first computer most popular
        HashMap<Integer, Integer> rankingsCount = new HashMap<>();
        for (int i = 0; i < MAX_NUM_CHOICES; i++) {
            rankingsCount.put(i, 0);
        }

        numberOneChoices.forEach(intt -> {
            int currInt = intt-1;
            int points = rankingsCount.get(currInt);
            rankingsCount.put(currInt, points + 1); // pre sure array list constant time read write
        });

        ArrayList<Integer> rankings = new ArrayList<>();
        for (int i = 0; i < MAX_NUM_CHOICES; i++) {
            rankings.add(i+1);
        }
        Collections.sort(rankings, (o1, o2) -> {
            // TODO DO BETTER ODERING HERE
            Integer order1 = choiceToOrder.get(o1);
            Integer order2 = choiceToOrder.get(o2);
            if (order1 == null && order2 == null) return o1 - o2;
            if (order1 == null) return order2;
            if (order2 == null) return order1;
            return order1 - order2;
        });

        orderedChoices = new int[rankings.size()];
        for (int i = 0; i < rankings.size(); i++) {
            orderedChoices[i] = rankings.get(i);
        }

        // generate all preference sheets, and computer all choiceToPeople
        for (int i = 0; i < numberOneChoices.size(); i++) {
            int firstPref = numberOneChoices.get(i);
            int allPrefs[] = new int[NUM_PREFERENCES_ON_SHEET];
            boolean hasSkip = false;
            for (int j = 0; j < NUM_PREFERENCES_ON_SHEET; j++) {
                if (!hasSkip && firstPref == rankings.get(j)) {
                    j--;
                    hasSkip = true;
                    continue;
                }

                if (j == 0) {
                    allPrefs[j] = firstPref;
                } else {
                    allPrefs[j] = rankings.get(j);
                }

                if (!choiceToPeople.containsKey(allPrefs[j])) {
                    choiceToPeople.put(allPrefs[j], new HashSet<Integer>());
                }
                choiceToPeople.get(allPrefs[j]).add(i);
            }
            PreferenceSheet sheet = new PreferenceSheet(i, allPrefs);
            allPeoplePreferences.add(sheet);
        }

        System.out.println("built data");
    }

    /**
     *     public PreferenceSheet buildRandomPreferenceSheet(int... mustHaveChoices) {

    LinkedHashSet<Integer> choices = new LinkedHashSet<Integer>();
    for (int i = 0; i < SET_ORDERING.length; i++) {
        choices.add(SET_ORDERING[i]);
    }
    for (int i = 0; i < mustHaveChoices.length; i++) {
        choices.remove(mustHaveChoices[i]);
    }
    int[] copy = new int[NUM_PREFERENCES_ON_SHEET];
    int j = 0;
    for (Integer i : choices) {
        copy[j++] = i;
        if (j >= NUM_PREFERENCES_ON_SHEET) {
            break;
        }
    }

    // splice in
    int[] indexArr = new int[NUM_PREFERENCES_ON_SHEET];
    for (int i = 0; i < indexArr.length; i++) {
        indexArr[i] = i;
    }
    shuffleArray(indexArr);

    int[] finalCopy = new int[NUM_PREFERENCES_ON_SHEET];
    for (int i = 0; i < finalCopy.length; i++) {
        finalCopy[i] = -1;
    }
    for (int i = 0; i < mustHaveChoices.length; i++) {
        finalCopy[indexArr[i]] = mustHaveChoices[i];
    }
    int copyInd = 0;
    for (int i = 0; i < finalCopy.length; i++) {
        if (finalCopy[i] == -1) {
            finalCopy[i] = copy[copyInd++];
        }
    }
    return new PreferenceSheet(finalCopy);
}
     */

    /**
     * deepclone
     * @param rhs
     */
    public Data(Data rhs) {
        this.choiceToPeople = new HashMap<>();
        this.allPeoplePreferences = new ArrayList<>();

        for (Map.Entry<Integer, Set<Integer>> entry : rhs.choiceToPeople.entrySet()) {
            this.choiceToPeople.put(entry.getKey(), new HashSet<Integer>(entry.getValue()));
        }

        for (PreferenceSheet sheet : rhs.allPeoplePreferences) {
            allPeoplePreferences.add(new PreferenceSheet(sheet));
        }
    }

    public PreferenceSheet buildRandomPreferenceSheet(int idx, int... mustHaveChoices) {
        if (PRESET_SHEET.isPresent()) {
            return new PreferenceSheet(idx, Arrays.copyOf(PRESET_SHEET.get(), PRESET_SHEET.get().length));
        }

        HashSet<Integer> choices = new HashSet<Integer>();
        for (int i = 0; i < orderedChoices.length; i++) {
            choices.add(orderedChoices[i]);
        }
        for (int i = 0; i < mustHaveChoices.length; i++) {
            choices.remove(mustHaveChoices[i]);
        }
        int[] copy = new int[NUM_PREFERENCES_ON_SHEET];
        int j = 0;
        for (Integer i : choices) {
            copy[j++] = i;
            if (j >= NUM_PREFERENCES_ON_SHEET) {
                break;
            }
        }
        shuffleArray(copy);

        // splice in
        int[] indexArr = new int[NUM_PREFERENCES_ON_SHEET];
        for (int i = 0; i < indexArr.length; i++) {
            indexArr[i] = i;
        }
        shuffleArray(indexArr);

        for (int i = 0; i < mustHaveChoices.length; i++) {
            copy[indexArr[i]] = mustHaveChoices[i];
        }
        return new PreferenceSheet(idx, copy);
    }


    // thanks stack overflow http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    static void shuffleArray(int[] ar) {
        Random rnd= new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
