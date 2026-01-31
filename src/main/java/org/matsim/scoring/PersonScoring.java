package org.matsim.scoring;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.scoring.SumScoringFunction;

public class PersonScoring implements SumScoringFunction.BasicScoring {

	private final Id<Person> personId;
	private final boolean isResident;
	private final BicycleRoadTrafficHandler bicycleRoadTrafficHandler;


	private double score = 0.0;

	public PersonScoring(Id<Person> personId, boolean isResident, BicycleRoadTrafficHandler bicycleRoadTrafficHandler) {
		this.personId = personId;
		this.isResident = isResident;
		this.bicycleRoadTrafficHandler = bicycleRoadTrafficHandler;
	}

	@Override
	public void finish() {
		if (!isResident && bicycleRoadTrafficHandler.usedWuerzburger(personId)) {
			score -= 10000.0;
		}
	}

	@Override
	public double getScore() {
		return score;
	}
}



/* Reset Safe
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

 */
