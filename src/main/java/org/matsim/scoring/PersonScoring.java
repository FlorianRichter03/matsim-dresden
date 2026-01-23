package org.matsim.scoring;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonScoring implements
	SumScoringFunction.ArbitraryEventScoring,
	LinkLeaveEventHandler,
	PersonEntersVehicleEventHandler,
	ActivityEndEventHandler {


	// Alle LinkIDs der Würzburger Straße in einer Liste speichern, um immer nur auf ein Objekt zugreifen zu müssen
	// selbst eingefügter Link wird nicht benötigt, da eh nur für Rad freigegeben

	Set<Id<Link>> wuerzburgerStrasse_Links = Set.of(
		Id.createLinkId("-50442874#2"),
		Id.createLinkId("50442874#0"),
		Id.createLinkId("-10088757"),
		Id.createLinkId("1282136539"),
		Id.createLinkId("-1279746843"),
		Id.createLinkId("911252645"),
		Id.createLinkId("-118724108"),
		Id.createLinkId("118724108"),
		Id.createLinkId("-1075370971"),
		Id.createLinkId("102714706"),
		Id.createLinkId("-1321690631"),
		Id.createLinkId("1321690631"),
		Id.createLinkId("-72693818"),
		Id.createLinkId("72693818"),
		Id.createLinkId("-695869693#0"),
		Id.createLinkId("695869693#0"),
		Id.createLinkId("-695869693#1"),
		Id.createLinkId("695869693#1"),
		Id.createLinkId("-1221815305"),
		Id.createLinkId("73520768#0"),
		Id.createLinkId("-52872405#2"),
		Id.createLinkId("52872405#0"),
		Id.createLinkId("-30502271#0"),
		Id.createLinkId("30502271#0"),
		Id.createLinkId("-30502271#1"),
		Id.createLinkId("30502271#1"),
		Id.createLinkId("-1321689200"),
		Id.createLinkId("1223529892"),
		Id.createLinkId("-1321689198#1"),
		Id.createLinkId("24518829"),
		Id.createLinkId("-24518826#0"),
		Id.createLinkId("1321689199"),
		Id.createLinkId("-24518826#1"),
		Id.createLinkId("24518826#1"),
		Id.createLinkId("-216457072"),
		Id.createLinkId("216457072"),
		Id.createLinkId("4428653")
	);


	// Map die Vehicle ID mit Person ID in Kombination darstellt
	HashMap<Id<Vehicle>, Id<Person>> personVehicleID = new HashMap<>();

	public void handleEvent(PersonEntersVehicleEvent event) {
		Id<Vehicle> vehicleId = event.getVehicleId();
		Id<Person> personId = event.getPersonId();

		personVehicleID.put(vehicleId,personId);
	}

	// Fahrten auf Würzburger Straße
	Set<Id<Vehicle>> vehiclesOnWuerzburger = new HashSet<>();

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		Id<Link> linkId = event.getLinkId();

		if(wuerzburgerStrasse_Links.contains(linkId)) {
			vehiclesOnWuerzburger.add(event.getVehicleId());
		}
	}

	// Personen der Fahrten der Würzburger bestimmen
	Set<Id<Person>> personsOnWuerzburger = new HashSet<Id<Person>>();

	public void personenidentifikation(){

		for (Map.Entry<Id<Vehicle>, Id<Person>> entry : personVehicleID.entrySet()) {

			Id<Person> personId = entry.getValue();
			Id<Vehicle> vehicleId = entry.getKey();

			if (vehiclesOnWuerzburger.contains(vehicleId)) {
				personsOnWuerzburger.add(personId);
			}
		}
	}

	// Anlieger der Würzburger bestimmen
	Set<Id<Person>> residents = new HashSet<>();

	@Override
	public void handleEvent(ActivityEndEvent event) {
		if(wuerzburgerStrasse_Links.contains(event.getLinkId())){
			residents.add(event.getPersonId());
		}
	}


	public void printResult(){

		System.out.println(personsOnWuerzburger);
//		for (Id<Vehicle> vehicleId : person.keySet()) {
//			System.out.println("vehicleId = " + vehicleId);
//			System.out.println("personId = " + person.get(vehicleId));
//		}

	}


	// Scoring Funktion anpassen für Nicht-Anlieger
	private double score;


	@Override
	public void handleEvent(Event event) {
		for(Id<Person> personId : personsOnWuerzburger){
			if(!residents.contains(personId)){
				score -= 1000.0;
			}
		}
	}

	@Override public void finish() {}

	@Override
	public double getScore() {
		return score;
	}
}
