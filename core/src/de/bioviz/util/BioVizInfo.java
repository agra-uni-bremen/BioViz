package de.bioviz.util;

/**
 * Class used to store static information.
 * @author Oliver Keszocze
 */
public class BioVizInfo {




	public static final String VERSION = "0.8";
	private static final String[] authors = {
			"Oliver Keszocze", "Jannis Stoppe", "Maximilian Luehnert"
	};

	public static String[] authors() {
		return authors.clone();
	}

	/**
	 * Prevent instantiation of this class
	 */
	private BioVizInfo() {}

}
