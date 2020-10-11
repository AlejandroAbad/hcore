package es.hefame.hcore.http.exchange;

import java.io.IOException;

import es.hefame.hcore.HException;
import es.hefame.hcore.JsonEncodable;

/**
 * Clase que representa los datos de una respuesta a una petición HTTP. Esta
 * clase es una clase envolvente de la clase
 * <i>com.sun.net.httpserver.HttpExchange</i>, pero no hereda de la misma.
 * 
 * Permite acceder a los métodos del objeto envuelto referidos a la respuesta
 * HTTP, de una manera más cómoda.
 * 
 * @author Alejandro_AC
 *
 */
public interface IHttpResponse {
	//
	// CONEXIÓN
	//

	/**
	 * Cierra la conexión subyacente, si la hubiera.
	 */
	public void close();

	//
	// CABECERAS
	//

	/**
	 * Establece un header en la respuesta HTTP. Elimina cualquier valor con la
	 * misma clave de header que hubiera.
	 * 
	 * @param key   El nombre del header.
	 * @param value El valor del header.
	 */
	public void setHeader(String key, String value);

	/**
	 * Añade un valor a un header en la respuesta HTTP. Si ya existe algún valor
	 * para el header, el nuevo se añade a la lista.
	 * 
	 * @param key   El nombre del header.
	 * @param value El valor del header.
	 */
	public void addHeader(String key, String value);

	//
	// CUERPO DE LA RESPUESTA
	//

	/**
	 * Indica si se ha enviado una respuesta a esta petición HTTP.
	 * 
	 * @return true si se han enviado datos por el socket, false de lo contrario.
	 */
	public boolean isResponseSent();

	/**
	 * Responde a la petición HTTP con el cuerpo del mensaje, el status code y el
	 * 'Content-Type' indicados. El valor de la cabecera Content-Length se calcula
	 * automáticamente.
	 * 
	 * @param responseBody Cuerpo de la respuesta HTTP
	 * @param code         Código de respuesta
	 * @param contentType  Valor de la cabecera 'Content-Type'
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(byte[] responseBody, int code, String contentType) throws IOException;

	/**
	 * Responde a la petición HTTP con el cuerpo del mensaje, el status code y el
	 * 'Content-Type' indicados. El valor de la cabecera Content-Length se calcula
	 * automáticamente. Este método hace la conversión del cuerpo de char[] a byte[]
	 * y llama al método <i>answer(byte[] r_body, int code, String contentType)
	 * throws IOException</i>
	 * 
	 * @param responseBody Cuerpo de la respuesta HTTP
	 * @param code         Código de respuesta
	 * @param contentType  Valor de la cabecera 'Content-Type'
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(char[] responseBody, int code, String contentType) throws IOException;

	/**
	 * Responde a la petición HTTP con el cuerpo del mensaje, el status code y el
	 * 'Content-Type' indicados. El valor de la cabecera Content-Length se calcula
	 * automáticamente. Este método hace la conversión del cuerpo de String a byte[]
	 * y llama al método <i>answer(byte[] r_body, int code, String contentType)
	 * throws IOException</i>
	 * 
	 * @param responseBody Cuerpo de la respuesta HTTP
	 * @param code         Código de respuesta
	 * @param contentType  Valor de la cabecera 'Content-Type'
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(String responseBody, int code, String contentType) throws IOException;

	/**
	 * Responde a la petición HTTP con el objeto JsonEncodable en el cuerpo del
	 * mensaje, el status code indicados. El valor de la cabecera Content-Type se
	 * establece a 'application/json'. El valor de la cabecera Content-Length se
	 * calcula automáticamente. Este método codifica el objeto en formato JSON, que
	 * pasa a byte[] y llama al método <i>answer(byte[] r_body, int code, String
	 * contentType) throws IOException</i>. Si ocurre una excepcion de tipo
	 * APIException en la conversión, se trata de enviar la excepción como
	 * respuesta. Si ocurre un error no tratable, se intenta enviar un mensaje de
	 * error interno del servidor con código 500.
	 * 
	 * @param message Objeto que puede ser codificado en JSON que será enviado como
	 *                respuesta
	 * @param code    Código de respuesta
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(JsonEncodable message, int code) throws IOException;

	/**
	 * Responde a la petición HTTP con el objeto APIExcepcion en el cuerpo del
	 * mensaje, el status code indicados. El valor de la cabecera Content-Type se
	 * establece a 'application/json'. El valor de la cabecera Content-Length se
	 * calcula automáticamente. Si la APIExcepcion es a su vez del subtipo
	 * APIHttpException, el código de respuesta HTTP se establece por el que esta
	 * especifique. En otro caso, se utiliza el código 500 (Error interno del
	 * servidor). Como una HException es JsonEncodable, se llama al método
	 * <i>answer(JsonEncodable exception, int code) throws IOException</i>.
	 * 
	 * @param exception Excepción que será codificada como JSON y enviada como
	 *                  respuesta
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(HException exception) throws IOException;

	/**
	 * Response a la petición con un cuerpo vacío.
	 * 
	 * @param code Código de respuesta
	 * @throws IOException Si ocurre algún error al enviar los datos por el socket
	 */
	public void send(int code) throws IOException;

}
