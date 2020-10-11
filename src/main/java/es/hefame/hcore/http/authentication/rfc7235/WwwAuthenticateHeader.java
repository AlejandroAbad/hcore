package es.hefame.hcore.http.authentication.rfc7235;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representa una cabecera 'WWW-Authenticate' según el rfc7235 sección 4.1.
 * La cabecera se compone de una serie de 'Challenges' concatenados:
 * 
 * <pre>
 * 		WWW-Authenticate = 1#Challenge
 * </pre>
 * 
 * @author Alejandro_AC
 *
 */
public class WwwAuthenticateHeader
{

	/**
	 * La lista de challenges que componen la cabecera
	 */
	List<Challenge> challenges;

	/**
	 * Crea el objeto con la lista de challenges especificada.
	 * 
	 * @param challenges Una lista de Challenges.
	 */
	public WwwAuthenticateHeader(Challenge... challenges)
	{
		if (challenges != null)
		{
			this.challenges = Arrays.asList(challenges);
		}
		else
		{
			this.challenges = new ArrayList<>();
		}
	}

	/**
	 * Añade el Challenge al final de la lista.
	 * 
	 * @param challenge El Challenge a añadir.
	 */
	public void addChallenge(Challenge challenge)
	{
		this.challenges.add(challenge);
	}

	/**
	 * Obtiene una representación del objeto válida para ser utilizada como valor de la cabecera WWW-Authenticate
	 * según el rfc7235.
	 * 
	 * @return El objeto representado como cabecera WWW-Authenticate según el rfc7235.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		for (Challenge ch : challenges)
		{
			if (ch == null) continue;

			if (first) first = false;
			else sb.append(',').append(' ');

			sb.append(ch.toString());
		}
		return sb.toString();
	}

}
