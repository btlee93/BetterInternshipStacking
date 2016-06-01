package com.tenble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        myPrefSheet = data.buildRandomPreferenceSheet(mustHaveChoices);

        // data.orderedChoices; ceebs modify this
        this.data.allPeoplePreferences.add(myPrefSheet);
        myIndex = this.data.allPeoplePreferences.size()-1;
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

            // TODO randomize this

            for (int j = 0; j < data.allPeoplePreferences.size(); j++) {
                PreferenceSheet ithSheet = data.allPeoplePreferences.get(j);

                failedSimulation = true;
                for (int choice : ithSheet.preferences) {
                    if (!choiceToPeople.containsKey(choice)) {
                        choiceToPeople.put(choice, new HashSet<Integer>());
                    }
                    if (choiceToPeople.get(choice).size() < spotsPerHospital) {
                        failedSimulation = false;
                    }
                }
                if (failedSimulation) {
                    break;
                }

                while (true) {
                    int randInd = random.nextInt(ithSheet.preferences.length);
                    int points = randInd+1;
                    int choice = ithSheet.preferences[randInd];
                    if (choiceToPeople.get(choice).size() < spotsPerHospital) {
                        choiceToPeople.get(choice).add(j);
                        error += points;

                        if (j == myIndex) {
                            choiceIGot = choice;
                        }

                        break;
                    }
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
