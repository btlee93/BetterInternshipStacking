package com.tenble;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int MAX_THREADS = 8;
    private static int NUM_SIMULATIONS = 235;
    private static int NUM_TRIALS_PER_SIMULATION = 5000000;
    private static int MIN_SPOTS_PER_CHOICE = 5;
    private static int MAX_SPOTS_PER_CHOICE = 12;
    private static int CSV_INDEX_OF_PREF = 3;
    private static int SLEEP_TIME = 5000;
    private static int[] MY_CHOICES = new int[] { 1, 3 };

    public static void main(String[] args) throws InterruptedException, IOException {
        Args inputs = new Args(args);
        Data data = parseDataFile(inputs.fileLoc);
        ExecutorService executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(MAX_THREADS));

        int maxMinusMinSpotsPerChoiceMod = MAX_SPOTS_PER_CHOICE - MIN_SPOTS_PER_CHOICE + 1;
        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            try {
                executor.execute(new Simulation(data, NUM_TRIALS_PER_SIMULATION, MIN_SPOTS_PER_CHOICE + (i % maxMinusMinSpotsPerChoiceMod), MY_CHOICES));
            } catch (RejectedExecutionException e) {
                Thread.sleep(SLEEP_TIME);
                i--;
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(5000);
        }
    }

    private static Data parseDataFile(String fileLoc) throws IOException {
        ArrayList<Integer> firstPrefs = new ArrayList<>();
        Files.lines(Paths.get(fileLoc)).forEach(line -> {
            // thanks stackoverflow http://stackoverflow.com/questions/15738918/splitting-a-csv-file-with-quotes-as-text-delimiter-using-string-split
            String cols[] = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Optional<Integer> firstPref = findFirstPrefInCols(cols);
            if (firstPref.isPresent()) {
                firstPrefs.add(firstPref.get());
            }
        });

        return new Data(firstPrefs);
    }

    private static Optional<Integer> findFirstPrefInCols(String cols[]) {
        Optional<Integer> intt = Optional.empty();
        if (cols.length > CSV_INDEX_OF_PREF) {
            // so hacky
            try {
                int pref = Integer.parseInt(cols[CSV_INDEX_OF_PREF + 1]);
                if (pref == 1) {
                    intt = Optional.of(Integer.parseInt(cols[CSV_INDEX_OF_PREF].substring(0, 2)));
                }
            } catch (Exception e) {}
        }

        return intt;
    }
}
