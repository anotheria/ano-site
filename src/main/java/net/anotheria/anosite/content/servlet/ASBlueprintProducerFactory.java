package net.anotheria.anosite.content.servlet;

import net.anotheria.anosite.util.AnositeConstants;
import net.anotheria.moskito.core.blueprint.BlueprintProducer;
import net.anotheria.moskito.core.blueprint.BlueprintProducersFactory;

public class ASBlueprintProducerFactory {
	public static BlueprintProducer getBlueprintProducer(String producerId){
		return BlueprintProducersFactory.getBlueprintProducer(producerId, "default", AnositeConstants.AS_MOSKITO_SUBSYSTEM);
	}

}
