package tests.structures;

import de.bioviz.structures.Point;
import de.bioviz.structures.Rectangle;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by keszocze on 27.01.16.
 */
public class RectangleTest {


	final int[][] rectangleDefinitions = {
			// taken from the default file, initially the positions() method
			// fails
			// for this rectangle
			{57, 1, 57, 51},
			// this one also failed hard
			{17, 8, 76, 8}
	};


	Rectangle createRectangleUsingPointClass(int[] points) {

		return new Rectangle(new Point(points[0], points[1]),
							 new Point(points[2], points[3]));
	}

	Rectangle createRectangleUsingCoordinates(int[] points) {
		return new Rectangle(points[0], points[1], points[2], points[3]);
	}


	@Test
	public void containsShouldHaveTheRightAmountOfElements() throws Exception {

		for (int i = 0; i < rectangleDefinitions.length; i++) {
			int distX = Math.abs(
					rectangleDefinitions[i][0] - rectangleDefinitions[i][2]) +
						1;
			int distY = Math.abs(
					rectangleDefinitions[i][1] - rectangleDefinitions[i][3]) +
						1;
			int nPositions = distX * distY;

			Rectangle rect1 =
					createRectangleUsingCoordinates(rectangleDefinitions[i]);
			Rectangle rect2 =
					createRectangleUsingPointClass(rectangleDefinitions[i]);

			assertEquals("#positions should be correct", nPositions,
						 rect1.positions().size());

			assertEquals("#positions should be correct", nPositions,
						 rect2.positions().size());

		}


	}

	@Test
	public void positionsShouldBeIdenticalForBothConstructors() throws
			Exception {
		for (int i = 0; i < rectangleDefinitions.length; i++) {
			Rectangle rect1 =
					createRectangleUsingCoordinates(rectangleDefinitions[i]);
			Rectangle rect2 =
					createRectangleUsingPointClass(rectangleDefinitions[i]);

			assertEquals(
					"positions of rectangles should be identical when using " +
					"different constructors",
					rect1.positions(), rect2.positions());
		}
	}
}