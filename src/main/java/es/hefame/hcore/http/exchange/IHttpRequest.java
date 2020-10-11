package es.hefame.hcore.http.exchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;


import com.sun.net.httpserver.Headers;

/**
 * Interfaz para interactuar con los datos de una petición HTTP entrante.
 * 
 * @author Alejandro_AC
 *
 */
public interface IHttpRequest
{

	//
	// CONEXION
	//

	/**
	 * Cierra la conexión subyacente, si la hubiera.
	 */
	public void close();

	/**
	 * Devuelve la dirección IP desde la que se originó la petición.
	 * 
	 * @return La dirección IP desde la que se originó la petición
	 */
	public String getIP();

	/**
	 * Devuelve el puerto desde el que el cliente ha realizado la conexión
	 * 
	 * @return El puerto desde el que el cliente ha realizado la conexión
	 */
	public int getPort();

	/**
	 * Indica si la petición se realizó con un protocolo seguro en la capa de transporte
	 * 
	 * @return true si la conexión se realizó de forma segura.
	 */
	public boolean isSSL();

	/**
	 * Obtiene el ID de la sessión SSL, si exsite.
	 * 
	 * @return El ID de la sessión SSL o null si la conexión no es SSL
	 */
	public byte[] getSSLSessId();

	/**
	 * Obtiene el ID de la sessión SSL, si existe, en formato de cadena hexadecimal.
	 * 
	 * @return El ID de la sessión SSL en formato hexadecimal o null si la conexión no es SSL
	 */
	public String getSSLSessIdString();

	/**
	 * Obtiene la suite de algoritmos utilizados en la conexión SSL o null si la conexión no es SSL.
	 * Por ejemplo: "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
	 * 
	 * @return La suite de algoritmos utilizados en la conexión SSL o null si la conexión no es SSL.
	 */
	public String getSSLChiperSuite();

	/**
	 * Obtiene el protocolo utilizado en la conexión SSL o null si la conexión no es SSL.
	 * Por ejemplo: "TLSv1.2"
	 * 
	 * @return El protocolo utilizado en la conexión SSL o null si la conexión no es SSL.
	 */
	public String getSSLProtocol();

	//
	// METODO
	//

	/**
	 * Devuelve el método HTTP de la petición en minúsculas (get, post, put, delete, ...)
	 * 
	 * @return El método HTTP de la petición
	 */
	public String getMethod();

	//
	// URL
	//

	/**
	 * Devuelve el objeto URI utilizado en la llamada.
	 * 
	 * @return El objeto URI utilizado en la llamada
	 */
	public URI getURI();

	/**
	 * Obtiene la lista de elementos de la URI. Los elementos se separan por
	 * barras '/'. El primer elemento se encuentra en la posición 0. Dada la URI "/bar/foo/hello":
	 * <ul>
	 * <li>list.get(0) devuelve "bar"</li>
	 * <li>list.get(1) devuelve "foo"</li>
	 * <li>list.get(2) devuelve "hello"</li>
	 * </ul>
	 * 
	 * @return El elemento en la posición index o null si no existe
	 */
	public List<String> getURIFields();

	/**
	 * Obtiene el elemento en la posición index de la URI. Los elementos se separan por
	 * barras '/'. El primer elemento se encuentra en la posición 0. Dada la URI "/bar/foo/hello":
	 * <ul>
	 * <li>get_uri_field(0) devuelve "bar"</li>
	 * <li>get_uri_field(1) devuelve "foo"</li>
	 * <li>get_uri_field(2) devuelve "hello"</li>
	 * </ul>
	 * 
	 * @param index El número de elemento que queremos
	 * @return El elemento en la posición index o null si no existe
	 */
	public String getURIField(int index);

	
	/**
	 * Devuelve una instancia de FormEncodedQuery que contiene la información parseada del query string de la petición.
	 * @see FormEncodedQuery
	 * @return El Query String parseado
	 */
	public FormEncodedQuery getQueryString();
	
	
	//
	// PROTOCOLO HTTP
	//

	/**
	 * El protocolo HTTP especificado (HTTP/1.1, HTTP/1.0, ...)
	 * 
	 * @return El protocolo HTTP de la petición
	 */
	public String getProtocol();

	//
	// CABECERAS
	//

	/**
	 * Obtiene un objeto tipo <i>com.sun.net.httpserver.Headers</i>, que contiene todas las cabeceras de la petición HTTP.
	 * La clase <i>com.sun.net.httpserver.Headers</i> implementa internamente un <i>Map&lt;String, List&lt;String&gt;&gt;</i>
	 * y los métodos de acceso al mapa son públicos.
	 * 
	 * @see com.sun.net.httpserver.Headers
	 * @return Un objeto tipo <i>com.sun.net.httpserver.Headers</i>
	 */
	public Headers getHeaders();

	/**
	 * Obtiene una lista con los valores de la cabecera cuyo nombre se indique.
	 * Dadas las siguientes cabeceras:
	 * 
	 * <pre>
	 * X-Header1: Perro
	 * X-Header2: Gato
	 * X-Header2: Minino
	 * </pre>
	 * 
	 * <ul>
	 * <li>get_headers("X-Header1") -&gt; [ "Perro" ]</li>
	 * <li>get_headers("X-Header2") -&gt; [ "Gato", "Minino" ]</li>
	 * <li>get_headers("X-Header3") -&gt; null</li>
	 * </ul>
	 * 
	 * @param name El nombre de la cabecera. Insensible a mayúsculas y minúsculas
	 * @return La lista de valores de la cabecera o null si la cabecera no existe
	 */
	public List<String> getHeaders(String name);

	/**
	 * Obtiene el valor de la ocurrencia en la posición <i>index</i> para la cabecera cuyo nombre se indique.
	 * Dadas las siguientes cabeceras:
	 * 
	 * <pre>
	 * X-Header1: Perro
	 * X-Header2: Gato
	 * X-Header2: Minino
	 * </pre>
	 * 
	 * <ul>
	 * <li>get_header("X-Header1", 0) -&gt; "Perro"</li>
	 * <li>get_header("X-Header2", 0) -&gt; "Gato"</li>
	 * <li>get_header("X-Header2", 1) -&gt; "Minino"</li>
	 * <li>get_header("X-Header2", 2) -&gt; null</li>
	 * <li>get_header("X-Header2", -1) -&gt; null</li>
	 * </ul>
	 * 
	 * @param name El nombre de la cabecera. Insensible a mayúsculas y minúsculas
	 * @param index El índice del elemento que queremos dentro de la lista de valores de la cabecera
	 * @return El valor en la posición <i>index</i> de la cabecera especificada, o null si la cabecera no existe
	 */
	public String getHeader(String name, int index);

	/**
	 * Obtiene el valor de la primera ocurrencia para la cabecera cuyo nombre se indica.
	 * Equivale a <i>get_header(name, 0)</i>
	 * 
	 * <pre>
	 * X-Header1: Perro
	 * X-Header2: Gato
	 * X-Header2: Minino
	 * </pre>
	 * 
	 * <ul>
	 * <li>get_header("X-Header1") -&gt; "Perro" ]</li>
	 * <li>get_header("X-Header2") -&gt; "Gato"</li>
	 * <li>get_header("X-Header3") -&gt; null</li>
	 * </ul>
	 * 
	 * @param name El nombre de la cabecera. Insensible a mayúsculas y minúsculas
	 * @return El valor en la primera posición de la cabecera especificada, o null si la cabecera no existe
	 */
	public String getHeader(String name);

	/**
	 * Devuelve el valor de la cabecera 'Content-Length'.
	 * Si la cabecera no existe o esta no puede convertirse a un entero, se devuelve el valor -1.
	 * 
	 * @return El valor de la cabecera 'Content-Length' o -1 si n ose encuentra
	 */
	public int getContentLengthHeader();

	/**
	 * Devuelve el valor de la cabecera 'Content-Type'
	 * En ocasiones, esta cabecera puede estar compuesta por varias partes separadas por ';' (Ver especificación https://tools.ietf.org/html/rfc7231#section-3.1.1.1)
	 * Este método devuelve únicamente el valor del primer campo, que corresponde con el 'media-type'. Por ejemplo:
	 * 
	 * <pre>
	 * Content-Type: application/json
	 * 		hdr_content_type() -&gt; "application/json"
	 * 
	 * Content-Type: application/xml; charset=utf-8
	 * 		hdr_content_type() -&gt; "application/xml"
	 * </pre>
	 * 
	 * Para obtener el charset, utiliza el método <i>hdr_charset()</i>
	 * Para obtener la cabecera original, utiliza el método <i>get_header("Content-Type")</i>
	 * 
	 * 
	 * @return El valor de la cabecera 'Content-Type'
	 */
	public String getContentTypeHeader();

	/**
	 * Devuelve el valor del campo 'carset' dentro de la cabecera 'Content-Type'
	 * 
	 * <pre>
	 * Content-Type: application/xml; charset=iso-8859-1
	 * 		hdr_charset() -&gt; "iso-8859-1"
	 * 
	 * Content-Type: text/html; charset=utf-8
	 * 		hdr_charset() -&gt; "utf-8"
	 * 
	 * Content-Type: application/json
	 * 		hdr_charset() -&gt; null
	 * </pre>
	 * 
	 * Para obtener el 'media-type' de la cabecera, utiliza el método <i>hdr_content_type()</i>
	 * Para obtener la cabecera original, utiliza el método <i>get_header("Content-Type")</i>
	 * 
	 * @return El valor del campo 'charset', dentro de la cabecera 'Content-Type'
	 */
	public String getCharsetHeader();

	//
	// BODY
	//

	/**
	 * Obtiene el cuerpo de la petición HTTP en una clase ByteArrayOutputStream donde el
	 * método <i>toByteArray</i> devuelve una referencia al array de bytes y no una copia
	 * 
	 * @return El cuerpo de la petición
	 * @throws IOException Si falló la lectura del socket
	 */
	public ByteArrayOutputStream getBody() throws IOException;

	/**
	 * Obtiene el cuerpo de la petición HTTP en un array de bytes.
	 * Es importante tener en cuenta que el array es referenciado, no copiado, por lo que no
	 * debe modificarse.
	 * 
	 * @return El cuerpo de la petición en un array de bytes
	 * @throws IOException Si falló la lectura del socket
	 */
	public byte[] getBodyAsByteArray() throws IOException;

	/**
	 * Obtiene el cuerpo de la petición HTTP en un String.
	 * Se utilizará ese charset indicado para covertir el array a String.
	 * Si <i>charset</i> es null, se utilizará el 'charset' por defecto del sistema
	 * 
	 * @param charset El charset con el que interpretar el array de bytes
	 * @return El cuerpo de la petición en un String
	 * @throws IOException Si falló la lectura del socket
	 */
	public String getBodyAsString(String charset) throws IOException;

	/**
	 * Obtiene el cuerpo de la petición HTTP en un String.
	 * Si la petición especifica valor de 'charset' en la cabecera 'Content-Type', se utilizará ese charset para
	 * covertir el array a String. De lo contrario se utiliza el 'charset' por defecto del sistema.
	 * 
	 * @return El cuerpo de la petición en un String
	 * @throws IOException Si falló la lectura del socket
	 */
	public String getBodyAsString() throws IOException;

	//
	// ATRIBUTOS NO HTTP
	//
	/**
	 * Controladores, autenticadores y otros elementos pueden almacenar objetos de manera arbitraria
	 * dentro de este objeto HttpConnection, como un método de comunicación out-of-band, de modo que
	 * otros controladores o elementos que procesen la petición puedan acceder a los mismos.
	 * Este método permite almacenar un objeto arbitrario con la clave especificada
	 * 
	 * @param key La clave del objeto que vamos a almacenar
	 * @param value El valor a almacenar
	 */
	public void setInternalValue(String key, Object value);

	/**
	 * Controladores, autenticadores y otros elementos pueden almacenar objetos de manera arbitraria
	 * dentro de este objeto HttpConnection, como un método de comunicación out-of-band, de modo que
	 * otros controladores o elementos que procesen la petición puedan acceder a los mismos.
	 * Este método permite obtener un objeto que haya sido almacenado previamente con la clave especificada
	 * 
	 * @param key La clave del valor que queremos obtener.
	 * @return El valor almacenado en la clave indicada, o null si no existe
	 */
	public Object getInternalValue(String key);

	/**
	 * Controladores, autenticadores y otros elementos pueden almacenar objetos de manera arbitraria
	 * dentro de este objeto HttpConnection, como un método de comunicación out-of-band, de modo que
	 * otros controladores o elementos que procesen la petición puedan acceder a los mismos.
	 * Este método permite obtener un objeto que haya sido almacenado previamente con la clave especificada,
	 * previamente casteado a la clase que se indique. En caso de fallo en el casteo se devuelve null.
	 * 
	 * @param <T> La clase a la que castearemos el objeto obtenido
	 * @param key La clave del valor que queremos obtener.
	 * @param className La clase a la que castearemos el objeto obtenido
	 * @return El valor almacenado en la clave indicada, o null si no existe o no se puede castear a la clase especificada.
	 */
	public <T> T getInternalValue(String key, Class<T> className);

}
