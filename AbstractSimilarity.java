package com.example.asa.gaitrecog;

public abstract class AbstractSimilarity implements DistanceMeasure {

	private static final long serialVersionUID = 8279234668623952242L;

	public boolean compare(double x, double y) {
        return x > y;
    }

}
