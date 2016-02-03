package de.bioviz.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by keszocze on 20.08.15.
 *
 * @author Oliver Keszocze
 */
public abstract class Colors {
	/**
	 * Base color for a field. This color is used when no droplet is present
	 * and
	 * no other indicator (e.g., for an interference region) is set,
	 */
	public static final Color FIELD_COLOR = new Color(0.5f, 0.5f, 0.75f, 1f);

	/**
	 * Base color used for 'emptyness'.
	 * <p>
	 * It basically is white with no transparency. It is used to initialize the
	 * color when drawing fields.
	 */
	public static final Color FIELD_EMPTY_COLOR = new Color(0, 0, 0, 1);


	/**
	 * Base color for a sink. This color is used before any modifications are
	 * applied.
	 */
	public static final Color SINK_COLOR = new Color(0.75f, 0.5f, 0.5f, 1f);

	/**
	 * Base color for a source. This color is used before any modifications are
	 * applied.
	 */
	public static final Color SOURCE_COLOR = new Color(0.5f, 0.75f, 0.5f, 1f);

	/**
	 * Base color for a mixer. This color is used before any modifications are
	 * applied.
	 */
	public static final Color MIXER_COLOR = new Color(0.45f, 0.33f, 0.25f, 1f);


	// TODO check whether we actually need 'fieldAdjacentActivationColor'
//	public static final Color fieldAdjacentActivationColor
//			= new Color(1f / 2f, 1f / 3f, 0, 1); //218-165-32

	/**
	 * Color used to indicate a blockage.
	 */
	public static final Color BLOCKED_COLOR = new Color(1f / 2f, 0, 0, 1);

	/**
	 * Color used to indicate that a cells has been activated.
	 */
	public static final Color ACTAUTED_COLOR = new Color(1, 1, 0, 1);


	/**
	 * Base color for an interference region. This color is used before any
	 * modifications are applied.
	 */
	public static final Color INTERFERENCE_REGION_COLOR
			= new Color(0.25f, 0.25f, 0f, 1);


}
