package org.matsim.scoring;

import org.matsim.core.scoring.SumScoringFunction;

public class PersonScoring implements SumScoringFunction.BasicScoring {

	private double score = 0.0;

	public void addPenalty(double penalty) {
		score -= penalty;
	}

	@Override
	public void finish() {

	}

	@Override
	public double getScore() {
		return score;
	}
}
