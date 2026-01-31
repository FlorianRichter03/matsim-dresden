package org.matsim.scoring;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.*;
import org.matsim.network.WuerzburgerStrasse_Links;

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
		sumScoringFunction.addScoringFunction(new PersonScoring());


		boolean isResident = false;
		if(person.getSelectedPlan() != null) {
			isResident = person.getSelectedPlan().getPlanElements().stream()
				.filter(pe -> pe instanceof Activity)
				.map(pe -> (Activity) pe)
				.anyMatch(act ->
					act.getLinkId() != null &&
					WuerzburgerStrasse_Links.Links.contains(act.getLinkId())
						&& (
						act.getType().startsWith("home")
							|| act.getType().startsWith("work")
							|| act.getType().startsWith("edu")
							|| act.getType().startsWith("shopping")
							|| act.getType().startsWith("leisure")
							|| act.getType().startsWith("business")
							|| act.getType().startsWith("other")));
		}


		// Register per-person event handler
		events.addHandler(
			new BicycleRoadTrafficHandler(person.getId(), personScoring, isResident)
		);

		return sumScoringFunction;
	}
}

