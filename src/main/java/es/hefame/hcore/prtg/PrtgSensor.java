package es.hefame.hcore.prtg;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import es.hefame.hcore.JsonEncodable;



/**
 * Representa un sensor PRTG.
 * https://prtg.paessler.com/api.htm?tabid=7&amp;username=demo&amp;password=demodemo
 * 
 * Según el API, un sensor es un conjunto de resultados. Un resultado puede ser un canal, o un error.
 * 
 * <pre>
 * {
 * 	"prtg": {
 * 		"error": "1",
 * 		"text": "Your error message",
 * 		"result": [
 * 			{
 * 				"channel": "First channel",
 *	 			"value": "10"
 * 			},
 * 			{
 * 				"channel": "Second channel",
 * 				"value": "20"
 * 			}
 * 		]
 * 	}
 * }
 * </pre>
 * 
 * Puesto que PRTG solo permite mostrar un error, únicamente se mostrará el error del último resultado de error
 * que se encuentre en el conjunto de resultados.
 * 
 * 
 * @author Alejandro_AC
 *
 */
public class PrtgSensor implements JsonEncodable
{

	/**
	 * Lista de resultados del sensor.
	 */
	private List<PrtgResult> channels = null;

	/**
	 * Instancia un sensor vacío.
	 */
	public PrtgSensor()
	{
		this.channels = new LinkedList<>();
	}

	/**
	 * Instancia el sensor con el resultado indicado.
	 * 
	 * @param channel El resultado inicial.
	 */
	public final void addChannel(PrtgResult channel)
	{
		if (channel != null)
		{
			this.channels.add(channel);
		}
	}

	/**
	 * Instancia el sensor con una colección inicial de resultados.
	 * 
	 * @param channels Una colección de resultados PRTG.
	 */
	public final void addChannel(Collection<? extends PrtgResult> channels)
	{
		if (channels != null)
		{
			for (PrtgResult channel : channels)
			{
				this.channels.add(channel);
			}

		}
	}

	/**
	 * Añade los canales de un sensor dentro de este.
	 * 
	 * @param sensor El sensor a "engullir".
	 */
	public void addSensor(PrtgSensor sensor)
	{
		this.addChannel(sensor.channels);
	}

	/**
	 * Devuelve una representación JSON del objeto tal y como se especifica en el API de PRTG:
	 * https://prtg.paessler.com/api.htm?tabid=7&amp;username=demo&amp;password=demodemo
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final JSONAware jsonEncode()
	{
		JSONObject root = new JSONObject();
		JSONObject prtg = new JSONObject();

		JSONArray resultChannels = new JSONArray();
		for (PrtgResult channel : this.channels)
		{
			if (channel instanceof PrtgErrorResult)
			{
				PrtgErrorResult errorChannel = (PrtgErrorResult) channel;
				prtg.put("error", "1");
				prtg.put("text", errorChannel.getErrorMessage());
			}
			else
			{
				resultChannels.add(channel.jsonEncode());
			}
		}

		prtg.put("result", resultChannels);
		root.put("prtg", prtg);

		return root;
	}

	/**
	 * Dada una colección de sensores, devuelve un único sensor que contiene todos los canales de todos los sensores de la colección.
	 * 
	 * @param sensors La colección de sensores a unir.
	 * @return El sensor resultante de la unión.
	 */
	public static PrtgSensor mergeSensors(PrtgSensor... sensors)
	{
		PrtgSensor merged = new PrtgSensor();
		for (PrtgSensor s : sensors)
		{
			merged.addChannel(s.channels);
		}
		return merged;
	}

}
