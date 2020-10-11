package es.hefame.hcore.http.authentication;

import es.hefame.hcore.http.exchange.IHttpRequest;

/**
 * Autenticador que permite siempre el paso del usuario.
 * 
 * @author Alejandro_AC
 *
 */
public class NullAuthenticator extends Authenticator
{

	@Override
	public boolean authenticateRequest(IHttpRequest request)
	{
		return true;
	}

}
