package org.matsim.network;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import java.util.Set;

public class NetworkModifier {

	private static final Id<Node> from_Node_ID = Id.createNodeId("2016386874");
	private static final Id<Node> to_Node_ID = Id.createNodeId("27164749");

	private static final Id<Link> Id_wuerzbuerger_verlaengerung = Id.createLinkId("wuerzburger_verlaengernung");

	private static final double length = 190.21;
	private static final double freespeed = 5.8309999999999995;
	private static final int lanes = 1;
	private static final double capacity = 600;

	public static void addLink(Scenario scenario){
		Network network = scenario.getNetwork();

		Node fromNode = network.getNodes().get(from_Node_ID);
		Node toNode   = network.getNodes().get(to_Node_ID);

		Link wuerzburger_verlaengerung = network.getFactory().createLink(
			Id_wuerzbuerger_verlaengerung,
			fromNode,
			toNode
		);

		wuerzburger_verlaengerung.setLength(length);
		wuerzburger_verlaengerung.setAllowedModes(Set.of("bike"));
		wuerzburger_verlaengerung.setFreespeed(freespeed);
		wuerzburger_verlaengerung.setCapacity(capacity);

		network.addLink(wuerzburger_verlaengerung);




	}

}
