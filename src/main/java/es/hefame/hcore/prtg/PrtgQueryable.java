package es.hefame.hcore.prtg;

import java.util.List;

/**
 * Objetos que implementen esta clase deben poder convertirse en una lista de resultados PRTG.
 * 
 * @author Alejandro_AC
 *
 */
public interface PrtgQueryable
{
	/**
	 * Este método debe devolver una lista de resultados PRTG.
	 * 
	 * @return Una lista de resultados PRTG.
	 */
	public List<PrtgResult> getResults();
}
