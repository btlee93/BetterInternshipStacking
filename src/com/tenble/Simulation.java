package com.tenble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Ben on 31/05/2016.
 */
public class Simulation implements Runnable {

    final int myIndex;
    final Data data;
    final PreferenceSheet myPrefSheet;

    // simulation params
    final int numTrials;
    final int spotsPerHospital;

    public Simulation(Data data, int numTrials, int spotsPerHospital, int ...mustHaveChoices) {
        this.data = new Data(data);
        this.numTrials = numTrials;
        this.spotsPerHospital = spotsPerHospital;

        myIndex = this.data.allPeoplePreferences.size();
        myPrefSheet = data.buildRandomPreferenceSheet(myIndex, mustHaveChoices);
        this.data.allPeoplePreferences.add(myPrefSheet);
        for (int pref : myPrefSheet.preferences) {
            if (!this.data.choiceToPeople.containsKey(pref)) {
                this.data.choiceToPeople.put(pref, new HashSet<Integer>());
            }
            this.data.choiceToPeople.get(pref).add(myIndex);
        }
    }

    @Override
    public void run() {
        Result bestResult = null;
        Random random = new Random();
        for (int i = 0; i < numTrials; i++) {
            HashMap<Integer, Set<Integer>> choiceToPeople = new HashMap<>();
            int error = 0;
            boolean failedSimulation = false;
            Integer choiceIGot = null;

            int[] randomArr = new int[data.allPeoplePreferences.size()];
            for (int j = 0; j < randomArr.length; j++) {
                randomArr[j] = j;
            }
            Data.shuffleArray(randomArr);

            for (int j = 0; j < data.allPeoplePreferences.size(); j++) {
                PreferenceSheet ithSheet = data.allPeoplePreferences.get(randomArr[j]);

                // try to give first preference
                failedSimulation = true;
                for (int k = 0; k < ithSheet.preferences.length; k++) {
                    int choice = ithSheet.preferences[k];

                    if (!choiceToPeople.containsKey(choice)) {
                        choiceToPeople.put(choice, new HashSet<Integer>());
                    }

                    int sphrel = spotsPerHospital;
                    if (choice == 12 || choice == 13) {
                        sphrel *= 2;
                    }
                    if (choiceToPeople.get(choice).size() < sphrel) {
                        choiceToPeople.get(choice).add(ithSheet.idx);
                        error += k+1;

                        if (ithSheet.idx == myIndex) {
                            choiceIGot = choice;
                        }

                        failedSimulation = false;
                        break;
                    }
                }

                if (failedSimulation) {
                    break;
                }
            }

            if (failedSimulation) {
                continue;
            }

            if (choiceIGot == null) {
                throw new RuntimeException("didn't set choice i got");
            }

            Result result = new Result(choiceToPeople, error, choiceIGot);
            if (bestResult == null || result.error < bestResult.error) {
                bestResult = result;
            }
        }

        if (bestResult == null) {
            System.out.println("Failed simulation");
        } else {
            System.out.println("I got choice: " + bestResult.choiceIgot + ", with error " + bestResult.error + ", with sheet " +
                    myPrefSheet.toString() + ", with spotsPerHospital=" + spotsPerHospital);
        }
    }
}
