package eu.europa.ec.empl.edci.parsers;

import eu.europa.ec.empl.edci.constants.ControlledList;

import java.util.Queue;

public interface ControlledListParser<T> {

    Queue<T> unmarshallControlledList(ControlledList source);

}
