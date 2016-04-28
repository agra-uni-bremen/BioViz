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
			= new Color(0.75f, 0.75f, 0.5f, 1);

	/**
	 * Base color for an interference region overlap. This color is used before
	 * any modifications are applied.
	 */
	public static final Color INTERFERENCE_REGION_OVERLAP_COLOR
			= new Color(1f, 0f, 0f, 1f);

	/**
	 * Diff color for hovered fields.
	 */
	public static final Color HOVER_DIFF_COLOR
			= new Color(0.2f, 0.2f, 0.2f, 0);

	/**
	 * Diff color for source target arrows.
	 */
	public static final Color LONG_NET_INDICATORS_ON_FIELD_COLOR
			= new Color(0, 0, 0, 0.5f);

	/**
	 * Diff color for indicators on droplets.
	 */
	public static final Color LONG_NET_INDICATORS_ON_DROPLET_DIFF =
			new Color(0.2f, 0.2f, 0.2f, 0);

	/**
	 * Overlay color used for fields that have adjacent activations.
	 */
	public static final Color ADJACENT_ACTIVATION_COLOR =
			new Color(0.5f, -0.5f, -0.5f, 0);

}
