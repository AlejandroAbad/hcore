package es.hefame.hcore.http.exchange;

import java.io.IOException;
import java.io.OutputStream;

import es.hefame.hcore.HException;
import es.hefame.hcore.JsonEncodable;
import es.hefame.hcore.http.HttpException;

import com.sun.net.httpserver.HttpExchange;


/**
 * Clase que representa los datos de una respuesta a una petición HTTP.
 * Esta clase es una clase envolvente de la clase <i>com.sun.net.httpserver.HttpExchange</i>, pero no hereda de la misma.
 * 
 * Permite acceder a los métodos del objeto envuelto referidos a la respuesta HTTP, de una manera más cómoda.
 * 
 * @author Alejandro_AC
 *
 */
public class HttpResponse implements IHttpResponse
{

	/**
	 * El objeto envuelto
	 */
	private HttpExchange	original;

	/**
	 * Indicador de si se ha respondido a la petición o no
	 */
	protected boolean		responseSent	= false;

	/**
	 * Cuerpo de la respuesta HTTP
	 */
	protected byte[]		responseBody	= null;

	/**
	 * Crea la clase envolviendo a un objeto de la clase HttpExchange.
	 * 
	 * @param original El objeto sobre el que instanciamos la clase.
	 */
	public HttpResponse(HttpExchange original)
	{
		this.original = original;
	}

	//
	// CONEXIÓN
	//

	@Override
	public void close()
	{
		if (this.original != null) this.original.close();
	}

	//
	// CABECERAS
	//
	@Override
	public void setHeader(String key, String value)
	{
		this.original.getResponseHeaders().set(key, value);
	}

	@Override
	public void addHeader(String key, String value)
	{
		this.original.getResponseHeaders().add(key, value);
	}

	//
	// CUERPO DE LA RESPUESTA
	//

	@Override
	public boolean isResponseSent()
	{
		return this.responseSent;
	}

	@Override
	public void send(byte[] responseBody, int code, String contentType) throws IOException
	{
		
		if (responseBody != null && responseBody.length > 0)
		{
			this.setHeader("Content-Type", contentType);
			this.original.sendResponseHeaders(code, responseBody.length);
			OutputStream os = original.getResponseBody();
			os.write(responseBody);
			os.close();

			this.responseSent = true;
		}
		else
		{
			original.sendResponseHeaders(code, -1);
		}

	}

	@Override
	public void send(char[] responseBody, int code, String contentType) throws IOException
	{
		if (responseBody == null) responseBody = new char[0];
		this.send(new String(responseBody).getBytes(), code, contentType);
	}

	@Override
	public void send(String responseBody, int code, String contentType) throws IOException
	{
		if (responseBody == null) responseBody = "";
		this.send(responseBody.getBytes(), code, contentType);
	}

	@Override
	public void send(JsonEncodable message, int code) throws IOException
	{
		if (message != null)
		{
			try
			{
				this.responseBody = message.jsonEncode().toJSONString().getBytes();
			}
			catch (Exception e)
			{

				this.responseBody = (new HttpException(500, "Error fatal del servidor", e)).jsonEncode().toJSONString().getBytes();
			}
		}

		this.send(responseBody, code, "text/json");
	}

	@Override
	public void send(HException exception) throws IOException
	{

		int code = 500;
		if (exception instanceof HttpException)
		{
			code = ((HttpException) exception).getHttpStatusCode();
		}

		this.send(exception, code);
	}

	@Override
	public void send(int code) throws IOException
	{
		this.send(new byte[0], code, null);
	}

	//
	// DEBUG
	//

	/**
	 * Imprime en el logger los datos de la respuesta.
	 */
	/*
	@SuppressWarnings("unchecked")
	private void __log_response()
	{
		if (L.isEnabled(l))
		{
			JSONObject root = new JSONObject();
			root.put("code", original.getResponseCode());
			root.put("headers", new JSONObject(original.getResponseHeaders()));

			if (response_body != null)
			{
				if (response_body.length > 120)
				{
					root.put("body", new String(response_body).substring(0, 120).replaceAll("\n", "") + " ( ... ) [" + C.sizes.bytesToHuman(response_body.length) + ']');
				}
				else
				{
					root.put("body", new String(response_body).replaceAll("\n", ""));
				}
			}

			L.log(l, "Respuesta HTTP enviada: {}", root.toJSONString());
		}

	}
	*/

}
