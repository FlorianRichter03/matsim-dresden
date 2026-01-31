package org.matsim.scoring;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.population.Person;
import org.matsim.network.WuerzburgerStrasse_Links;
import org.matsim.vehicles.Vehicle;

public class BicycleRoadTrafficHandler implements
	LinkLeaveEventHandler,
	PersonEntersVehicleEventHandler,
	PersonLeavesVehicleEventHandler {


	private final Id<Person> personId;
	private final PersonScoring scoring;
	private boolean isResident;

	private boolean usedWuerzburger = false;

	private Id<Vehicle> currentVehicle = null;

	public BicycleRoadTrafficHandler(Id<Person> personId, PersonScoring scoring, boolean isResident) {
		this.personId = personId;
		this.scoring = scoring;
		this.isResident = isResident;
	}



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

		if(WuerzburgerStrasse_Links.Links.contains(event.getLinkId())
		 	&& !event.getVehicleId().toString().startsWith("bike")) {

			usedWuerzburger = true;
		}
	}

	@Override
	public void handleEvent(PersonLeavesVehicleEvent event){
		if(!event.getPersonId().equals(personId)) return;

		if(usedWuerzburger && !isResident) {
			scoring.addPenalty(-10000.0);
		}
	}


	@Override
	public void reset(int iteration) {
		usedWuerzburger = false;
		isResident = false;
		currentVehicle = null;
	}
}











		/*

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
