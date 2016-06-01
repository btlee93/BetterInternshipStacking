package com.tenble;

/**
 * Created by Ben on 31/05/2016.
 */
public class PreferenceSheet {

    int preferences[]; // array of just preferences, eg [3, 5, 6, 7]

    /**
     *
     * @param preferences
     */
    public PreferenceSheet(int... preferences) {
        this.preferences = new int[preferences.length];
        for (int i = 0; i < preferences.length; i++) {
            this.preferences[i] = preferences[i];
        }
    }

    public PreferenceSheet(PreferenceSheet rhs) {
        this(rhs.preferences);
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("[");
        for (int i = 0; i < preferences.length; i++) {
            if (i != 0) {
                bldr.append(", ");
            }
            bldr.append(preferences[i]);
        }
        bldr.append("]");
        return bldr.toString();
    }
}
