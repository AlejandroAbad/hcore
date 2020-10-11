package es.hefame.hcore.prtg.sensors;

import java.util.ArrayList;
import java.util.List;

import es.hefame.hcore.prtg.DefinedUnit;
import es.hefame.hcore.prtg.PrtgChannelResult;
import es.hefame.hcore.prtg.PrtgQueryable;
import es.hefame.hcore.prtg.PrtgResult;
import es.hefame.hcore.prtg.PrtgSensor;


/**
 * Implementación de un sensor que incluye los datos de utilización de la JVM.
 * 
 * @author Alejandro_AC
 *
 */
public class JVMPrtgSensor extends PrtgSensor implements PrtgQueryable
{

	/**
	 * Instancia el sensor con los datos actuales de la JVM.
	 */
	public JVMPrtgSensor()
	{
		this.addChannel(this.getResults());
	}

	/**
	 * Obtiene la lista de canales del sensor.
	 */
	@Override
	public List<PrtgResult> getResults()
	{
		List<PrtgResult> channels = new ArrayList<>(4);
		channels.add(new PrtgChannelResult("Memoria usada", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), DefinedUnit.BYTES_MEMORY));
		channels.add(new PrtgChannelResult("Memoria tocada", Runtime.getRuntime().totalMemory(),  DefinedUnit.BYTES_MEMORY));
		channels.add(new PrtgChannelResult("Memoria libre", Runtime.getRuntime().freeMemory(),  DefinedUnit.BYTES_MEMORY));
		channels.add(new PrtgChannelResult("Memoria maxima", Runtime.getRuntime().maxMemory(),  DefinedUnit.BYTES_MEMORY));

		return channels;
	}

}
