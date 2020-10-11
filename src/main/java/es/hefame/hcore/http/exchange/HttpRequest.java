package es.hefame.hcore.http.exchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import es.hefame.hcore.converter.ByteArrayConverter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;


/**
 * Clase que representa los datos de una petición HTTP entrante.
 * Esta clase es una clase envolvente de la clase <i>com.sun.net.httpserver.HttpExchange</i>.
 * 
 * Permite acceder a los métodos del objeto envuelto referidos a la petición HTTP, de una manera más cómoda.
 * 
 * @author Alejandro_AC
 *
 */
public class HttpRequest implements IHttpRequest
{

	/**
	 * El objeto HttpExchange envuelto
	 */
	private HttpExchange			original		= null;

	/**
	 * Cuerpo de la petición HTTP
	 */
	protected ByteArrayOutputStream	requestBody	= null;
	
	/**
	 * Proxy-buffer para el FormEncodedQuery
	 */
	private FormEncodedQuery queryStringProxy		= null;

	/**
	 * Crea la clase envolviendo a un objeto de la clase HttpExchange.
	 * 
	 * @param original El objeto sobre el que instanciamos la clase.
	 */
	public HttpRequest(HttpExchange original)
	{
		this.original = original;
	}

	//
	// CONEXION
	//

	@Override
	public void close()
	{
		if (this.original != null) this.original.close();
	}

	@Override
	public String getIP()
	{
		return original.getRemoteAddress().getAddress().getHostAddress();
	}

	@Override
	public int getPort()
	{
		return original.getRemoteAddress().getPort();
	}

	@Override
	public boolean isSSL()
	{
		return (original instanceof HttpsExchange);
	}

	@Override
	public byte[] getSSLSessId()
	{
		if (original instanceof HttpsExchange)
		{
			HttpsExchange secureExchange = (HttpsExchange) this.original;
			return secureExchange.getSSLSession().getId();
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getSSLSessIdString()
	{
		return ByteArrayConverter.toHexString(this.getSSLSessId(), false);
	}

	@Override
	public String getSSLChiperSuite()
	{
		if (original instanceof HttpsExchange)
		{
			HttpsExchange secureExchange = (HttpsExchange) this.original;
			return secureExchange.getSSLSession().getCipherSuite();
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getSSLProtocol()
	{
		if (original instanceof HttpsExchange)
		{
			HttpsExchange secureExchange = (HttpsExchange) this.original;
			return secureExchange.getSSLSession().getProtocol();
		}
		else
		{
			return null;
		}
	}

	//
	// METODO
	//
	@Override
	public String getMethod()
	{
		return original.getRequestMethod().toLowerCase();
	}

	//
	// URL
	//
	@Override
	public URI getURI()
	{
		return original.getRequestURI();
	}

	@Override
	public List<String> getURIFields()
	{
		String uriPath = original.getRequestURI().getPath();
		String[] chunks = uriPath.split("/");
		List<String> chunksCleaned = new ArrayList<>(chunks.length);
		for (String chunk : chunks)
		{
			if (chunk.length() > 0)
			{
				chunksCleaned.add(chunk);
			}
		}
		return chunksCleaned;
	}

	@Override
	public String getURIField(int index)
	{
		List<String> chunks = this.getURIFields();
		if (chunks.size() > index) { return chunks.get(index); }
		return null;
	}
	
	@Override
	public FormEncodedQuery getQueryString()
	{
		if (queryStringProxy != null) return queryStringProxy;
		
		queryStringProxy = new FormEncodedQuery(original.getRequestURI().getQuery());
		return queryStringProxy;
	}

	//
	// PROTOCOLO HTTP
	//

	@Override
	public String getProtocol()
	{
		return original.getProtocol();
	}

	//
	// CABECERAS
	//

	@Override
	public Headers getHeaders()
	{
		return original.getRequestHeaders();
	}

	@Override
	public List<String> getHeaders(String name)
	{
		Headers hdrs = this.getHeaders();
		if (hdrs == null) return null;
		return hdrs.get(name);
	}

	@Override
	public String getHeader(String name, int index)
	{
		List<String> values = this.getHeaders(name);
		if (values == null) return null;
		if (index < 0 || index >= values.size()) return null;
		return values.get(index);
	}

	@Override
	public String getHeader(String name)
	{
		return this.getHeader(name, 0);
	}

	@Override
	public int getContentLengthHeader()
	{
		String size = this.getHeader("content-length");
		if (size != null && size.length() > 0)
		{
			try
			{
				int rt = Integer.parseInt(size);
				if (rt < 0)
				{
					return -1;
				}
				return rt;
			}
			catch (NumberFormatException e)
			{

			}
		}

		return -1;
	}

	@Override
	public String getContentTypeHeader()
	{
		String value = this.getHeader("content-type");
		if (value != null && value.length() > 0)
		{
			String[] chunks = value.split("\\;");
			if (chunks.length > 0) return chunks[0];
		}
		return null;
	}

	@Override
	public String getCharsetHeader()
	{
		String value = this.getHeader("content-type");
		if (value != null && value.length() > 0)
		{
			String[] chunks = value.split("\\;");
			if (chunks.length > 1)
			{
				chunks = chunks[1].split("\\=");
				if (chunks.length == 2 && chunks[0].trim().equalsIgnoreCase("charset")) { return chunks[1].trim(); }
			}
		}
		return null;
	}

	//
	// BODY
	//

	@Override
	public ByteArrayOutputStream getBody() throws IOException
	{
		if (this.requestBody != null) return this.requestBody;

		int clength = this.getContentLengthHeader();
		if (clength > 0)
		{
			this.requestBody = new ByteArrayOutputStream(Math.abs(clength));

			InputStream is = original.getRequestBody();

			int currentByte;
			while ((currentByte = is.read()) > -1)
			{
				this.requestBody.write(currentByte);
			}

		}
		else
		{
			this.requestBody = new ByteArrayOutputStream();
		}
		return this.requestBody;
	}

	@Override
	public byte[] getBodyAsByteArray() throws IOException
	{
		return this.getBody().toByteArray();
	}

	@Override
	public String getBodyAsString(String charset) throws IOException
	{
		if (charset != null) return this.getBody().toString(charset);
		return this.getBody().toString();
	}

	@Override
	public String getBodyAsString() throws IOException
	{
		String charset = this.getCharsetHeader();
		return this.getBodyAsString(charset);
	}

	//
	// ATRIBUTOS NO HTTP
	//

	@Override
	public void setInternalValue(String key, Object value)
	{
		this.original.setAttribute(key, value);
	}

	@Override
	public Object getInternalValue(String key)
	{
		return this.original.getAttribute(key);
	}

	@Override
	public <T> T getInternalValue(String key, Class<T> clazz)
	{
		Object o = this.getInternalValue(key);
		if (o == null) return null;
		if (o.getClass().isAssignableFrom(clazz)) { return clazz.cast(o); }
		return null;
	}

}
