package es.hefame.hcore.http.exchange;

import com.sun.net.httpserver.HttpExchange;

/**
 * Clase que permite la comunicación entre el cliente y el servidor.
 * Encapsula la petición del usuario porporcionada por el servicio a través de una instanca de HttpExchange (o HttpsExchange),
 * y añade una serie de métodos que facilitan el envío y recepción de datos en el intercambio HTTP.
 * 
 * @see com.sun.net.httpserver.HttpExchange
 * @author Alejandro_AC
 *
 */
public class HttpConnection
{
	// private static Logger L = LogManager.getLogger();

	/**
	 * El objeto que representa la petición del cliente
	 */
	public final IHttpRequest	request;

	/**
	 * El objeto que representa la respuesta enviada
	 */
	public final IHttpResponse	response;

	/**
	 * Instancia
	 * 
	 * @param request
	 * @param response
	 */
	public HttpConnection(IHttpRequest request, IHttpResponse response)
	{
		this.request = request;
		this.response = response;
	}

	/**
	 * Instancia envolviendo el objeto HttpExchange devuelto por el servidor.
	 * Inicializa los atributos 'request' y 'response' que permiten interactuar con el cliente.
	 * 
	 * @param exchange El objeto HttpExchange devuelto por el HttpServer
	 */
	public HttpConnection(HttpExchange exchange)
	{
		this.request = new HttpRequest(exchange);
		this.response = new HttpResponse(exchange);
	}

}
