/*
 * BioViz, a visualization tool for digital microfluidic biochips (DMFB).
 *
 * Copyright (c) 2017 Oliver Keszocze, Jannis Stoppe, Maximilian Luenert
 *
 * This file is part of BioViz.
 *
 * BioViz is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option)
 * any later version.
 *
 * BioViz is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details. You should have
 * received a copy of the GNU
 * General Public License along with BioViz.
 * If not, see <http://www.gnu.org/licenses/>.
 */

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
     * The name that is displayed as the program name in the OS's UI.
     */
    public static final String PROGNAME = "BioViz";

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
