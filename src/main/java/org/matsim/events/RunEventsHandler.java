package org.matsim.events;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;


public class RunEventsHandler {

	public static void main(String[] args) {
		String eventFile = "output/basis/dresden-1pct.output_events.xml.gz"; // nochmal mit Standardeinstellungen laufen lassen

		EventsManager eventsManager = EventsUtils.createEventsManager();

		LinkEventAnalyzer linkEventAnalyzer = new LinkEventAnalyzer();

		eventsManager.addHandler(linkEventAnalyzer);

		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);

		eventsReader.readFile(eventFile);

		linkEventAnalyzer.personenidentifikation();

		linkEventAnalyzer.printResult();


	}
}
