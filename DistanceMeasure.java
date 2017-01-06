
package com.example.asa.gaitrecog;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.asa.gaitrecog.Instance;

public interface DistanceMeasure extends Serializable {
	/**
	 * Calculates the distance between two instances.
	 * 
	 * @param //i
	 *            the first instance
	 * @param //j
	 *            the second instance
	 * @return the distance between the two instances
	 */
    public double measure(ArrayList x, ArrayList y);


	/**
	 * Returns whether the first distance, similarity or correlation is better
	 * than the second distance, similarity or correlation. 
	 * 
	 * Both values should be calculated using the same measure.
	 * 
	 * For similarity measures the higher the similarity the better the measure,
	 * for distance measures it is the lower the better and for correlation
	 * measure the absolute value must be higher.
	 * 
	 * @param x
	 *            the first distance, similarity or correlation
	 * @param y
	 *            the second distance, similarity or correlation
	 * @return true if the first distance is better than the second, false in
	 *         other cases.
	 */
	public boolean compare(double x, double y);
   
}
