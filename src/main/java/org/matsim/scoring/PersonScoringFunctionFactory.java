package org.matsim.scoring;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.*;

public class PersonScoringFunctionFactory implements ScoringFunctionFactory {
	private final Scenario scenario;

	@Inject
	private EventsManager events;

	@Inject
	PersonScoringFunctionFactory(Scenario scenario) {
		this.scenario = scenario;
	}


	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		SumScoringFunction sumScoringFunction = new SumScoringFunction();

		// Score activities, legs, payments and being stuck
		// with the default MATSim scoring based on utility parameters in the config file.
		final ScoringParameters params = new ScoringParameters.Builder(scenario, person).build();
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params));
		sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, scenario.getNetwork()));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params));
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));

		PersonScoring personScoring = new PersonScoring();
		sumScoringFunction.addScoringFunction(personScoring);

		// Register per-person event handler
		events.addHandler(
			new BicycleRoadTrafficHandler(person.getId(), personScoring)
		);

		return sumScoringFunction;
	}
}
