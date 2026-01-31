package org.matsim.scoring;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.network.WuerzburgerStrasse_Links;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BicycleRoadTrafficHandler
	implements LinkLeaveEventHandler, PersonEntersVehicleEventHandler {

	/** Vehicle → Person */
	private final Map<Id<Vehicle>, Id<Person>> vehicleToPerson = new HashMap<>();

	/** Personen, die Würzburger im MIV genutzt haben */
	private final Set<Id<Person>> violators = new HashSet<>();

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		vehicleToPerson.put(event.getVehicleId(), event.getPersonId());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		if (event.getVehicleId() == null) return;
		if (event.getVehicleId().toString().contains("bike")) return;
		if (!WuerzburgerStrasse_Links.LINKS.contains(event.getLinkId())) return;

		Id<Person> personId = vehicleToPerson.get(event.getVehicleId());
		if (personId != null) {
			violators.add(personId);
		}
	}

	public boolean usedWuerzburger(Id<Person> personId) {
		return violators.contains(personId);
	}

	@Override
	public void reset(int iteration) {
		vehicleToPerson.clear();
		violators.clear();
	}
}


/* Reset Safe
package org.matsim.scoring;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

import java.util.Set;

public class BicycleRoadTrafficHandler implements
	LinkLeaveEventHandler,
	PersonEntersVehicleEventHandler,
	ActivityStartEventHandler {


	private final Id<Person> personId;
	private final PersonScoring scoring;

	private boolean usedWuerzburger = false;
	private boolean isResident = false;
	private boolean penaltyApplied = false;

	private Id<Vehicle> currentVehicle = null;

	public BicycleRoadTrafficHandler(Id<Person> personId, PersonScoring scoring) {
		this.personId = personId;
		this.scoring = scoring;
	}


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

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		if(event.getPersonId().equals(personId)){
			currentVehicle = event.getVehicleId();
		}
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		if(currentVehicle == null) return;

		if(!event.getVehicleId().equals(currentVehicle)) return;

		if(wuerzburgerStrasse_Links.contains(event.getLinkId())
			&& !event.getVehicleId().toString().startsWith("bike")) {

			usedWuerzburger = true;
		}
	}

	@Override
	public void handleEvent (ActivityStartEvent event) {
		if(!event.getPersonId().equals(personId)) return;

		if(wuerzburgerStrasse_Links.contains(event.getLinkId())
			&& (event.getActType().startsWith("home")
			|| event.getActType().startsWith("work")
			|| event.getActType().startsWith("edu")
			|| event.getActType().startsWith("shopping")
			|| event.getActType().startsWith("leisure")
			|| event.getActType().startsWith("business")
			|| event.getActType().startsWith("other"))){

			isResident = true;
		}

		if(usedWuerzburger && !isResident && !penaltyApplied) {
			scoring.addPenalty(10000.0);
			penaltyApplied = true;
		}
	}


	@Override
	public void reset(int iteration) {
		usedWuerzburger = false;
		isResident = false;
		penaltyApplied = false;
		currentVehicle = null;
	}
}











Variante ganz alt

	// Fahrten auf Würzburger Straße
	Set<Id<Vehicle>> vehiclesOnWuerzburger = new HashSet<>();

	private double score = 0.0;

	// Map die Vehicle ID mit Person ID in Kombination darstellt
	HashMap<Id<Vehicle>, Id<Person>> personVehicleID = new HashMap<>();

	private final Id<Person> personId;
	private final PersonScoring scoring;

	// Personen der Fahrten der Würzburger bestimmen
	Set<Id<Person>> personsOnWuerzburger = new HashSet<Id<Person>>();

	// Anlieger der Würzburger bestimmen
	// nicht einfach ActivityEndEvent an sich, weil dann auch Activities wie car interaction etc...
	Set<Id<Person>> residents = new HashSet<>();





	@Override
	public void reset(int iteration) {
		// scoring.reset(...) is handled elsewhere
	}

	//--------------------------------------------

	public void handleEvent(PersonEntersVehicleEvent event) {
		Id<Vehicle> vehicleId = event.getVehicleId();
		Id<Person> personId = event.getPersonId();

		// TODO Die folgende Zeile macht keinen Sinn. Die Bedingung ist immer true, weil 2x das gleiche verglichen wird
		if (event.getPersonId().equals(personId)) {
			personVehicleID.put(vehicleId, personId);
		}
	}

	public void handleEvent(LinkLeaveEvent event) {
		Id<Link> linkId = event.getLinkId();

		// Alle vehicle IDs die Bike beinhalten werden nicht beachtet

		if (wuerzburgerStrasse_Links.contains(linkId)) {
			if (event.getVehicleId() != null
				&& !event.getVehicleId().toString().startsWith("bike")) {
				// TODO Könnte sein, dass ihr damit auch Busse bestraft, weil die ja auch nicht mit "bike" starten.
				// TODO Aber vllt gibt es dort gar keine Busse

				vehiclesOnWuerzburger.add(event.getVehicleId());
			}
		}
	}

	public void identifyPerson(){

		for (Map.Entry<Id<Vehicle>, Id<Person>> entry : personVehicleID.entrySet()) {

			Id<Person> personId = entry.getValue();
			Id<Vehicle> vehicleId = entry.getKey();

			if (vehiclesOnWuerzburger.contains(vehicleId)) {
				personsOnWuerzburger.add(personId);
			}
		}
	}

	public void handleEvent(ActivityStartEvent event) {
		if(wuerzburgerStrasse_Links.contains(event.getLinkId())
			&& (event.getActType().startsWith("home")
			|| event.getActType().startsWith("work")
			|| event.getActType().startsWith("edu")
			|| event.getActType().startsWith("shopping")
			|| event.getActType().startsWith("leisure")
			|| event.getActType().startsWith("business")
			|| event.getActType().startsWith("other"))){

			residents.add(event.getPersonId());
		}
	}

	public void handleEvent(PersonLeavesVehicleEvent event) {
		identifyPerson();

		for(Id<Person> personId : personsOnWuerzburger){
			if(!residents.contains(personId)){
				scoring.addPenalty(-1000.);
			}
		}
	}
}
*/
