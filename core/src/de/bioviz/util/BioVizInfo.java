package de.bioviz.util;

/**
 * Class used to store static information.
 *
 * @author Oliver Keszocze
 */
public final class BioVizInfo {


    /**
     * BioViz version.
     */
    public static final String VERSION = "0.8";

    /**
     * Vector containing all authors of BioViz.
     *
     * The ordering is random.
     */
    private static final String[] AUTHORS = {
            "Oliver Keszocze", "Jannis Stoppe", "Maximilian Luenert",
    };


    /**
     * Prevent instantiation of this class.
     */
    private BioVizInfo() {
    }

    /**
     * @return Vector of authors of BioViz.
     */
    public static String[] authors() {
        return AUTHORS.clone();
    }


}
