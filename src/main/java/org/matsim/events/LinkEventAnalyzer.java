package org.matsim.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

import java.util.*;


public class LinkEventAnalyzer implements LinkLeaveEventHandler, PersonEntersVehicleEventHandler {

	// Alle LinkIDs der Würzburger Straße in einer Liste speichern, um immer nur auf ein Objekt zugreifen zu müssen
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

	HashMap<Id<Vehicle>, Id<Person>> person = new HashMap<>();

	public void handleEvent(PersonEntersVehicleEvent event) {

		Id<Vehicle> vehicleId = event.getVehicleId();
		Id<Person> personId = event.getPersonId();

		person.put(vehicleId,personId);
	}


	Set<Id<Vehicle>> vehicles = new HashSet<>();

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		Id<Link> linkId = event.getLinkId();

		if(wuerzburgerStrasse_Links.contains(linkId)) {
			vehicles.add(event.getVehicleId());
		}
	}



	public void personenidentifikation(){

		Set<Id<Person>> personsonwuerzburger = new HashSet<Id<Person>>();

		for (Map.Entry<Id<Vehicle>, Id<Person>> entry : person.entrySet()) {

			Id<Person> personId = entry.getValue();
			Id<Vehicle> vehicleId = entry.getKey();

			if (vehicles.contains(vehicleId)) {
				personsonwuerzburger.add(personId);
			}
		}
		return personsonwuerzburger;
	}


	public void printResult(){

		System.out.println(personsonwuerzburger);
//		for (Id<Vehicle> vehicleId : person.keySet()) {
//			System.out.println("vehicleId = " + vehicleId);
//			System.out.println("personId = " + person.get(vehicleId));
//		}

	}



}
