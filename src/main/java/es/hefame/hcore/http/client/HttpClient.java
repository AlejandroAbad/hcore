package es.hefame.hcore.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient
{

	private URL					url;
	private HttpURLConnection	con;
	private HttpClientResponse	response;
	private int					timeout;

	public HttpClient(String url, int timeout) throws IOException
	{
		this.url = new URL(url);
		this.timeout = timeout;
		con = (HttpURLConnection) this.url.openConnection();
	}

	public HttpClient(String url) throws IOException
	{
		this(url, 5000);
	}

	public HttpClientResponse get() throws IOException
	{
		con.setRequestMethod("GET");
		con.setReadTimeout(this.timeout);

		//__log_request();

		con.connect();
		response = new HttpClientResponse(con);

		//__log_response();
		return response;
	}

	public HttpClientResponse post(byte[] body) throws IOException
	{
		con.setRequestMethod("POST");

		con.setReadTimeout(this.timeout);
		con.setDoOutput(true);

		//__log_request();

		OutputStream wr = con.getOutputStream();
		wr.write(body);
		wr.flush();
		wr.close();

		con.connect();

		response = new HttpClientResponse(con);
		//__log_response();
		return response;
	}

	public HttpClientResponse put(byte[] body) throws IOException
	{
		con.setRequestMethod("PUT");

		con.setReadTimeout(this.timeout);
		con.setDoOutput(true);

		//__log_request();

		OutputStream wr = con.getOutputStream();
		wr.write(body);
		wr.flush();
		wr.close();

		con.connect();

		response = new HttpClientResponse(con);
		//__log_response();
		return response;
	}

	public HttpClientResponse delete() throws IOException
	{
		con.setRequestMethod("DELETE");
		con.setReadTimeout(this.timeout);

		//__log_request();

		con.connect();
		response = new HttpClientResponse(con);

		//__log_response();
		return response;
	}

	public void setHeader(String key, String value)
	{
		con.setRequestProperty(key, value);
	}

	/*
	@SuppressWarnings("unchecked")
	private void __log_request()
	{
		if (L.isDebugEnabled())
		{
			JSONObject request = new JSONObject();
			request.put("method", con.getRequestMethod());
			request.put("uri", con.getURL().toExternalForm());
			request.put("timeout", this.timeout);

			JSONObject hdrs = new JSONObject();
			for (Entry<String, List<String>> header : con.getRequestProperties().entrySet())
			{
				if (header.getValue().size() > 1)
				{
					JSONArray arr = new JSONArray();
					arr.addAll(header.getValue());
					hdrs.put(header.getKey(), arr);
				}
				else
				{
					hdrs.put(header.getKey(), header.getValue().get(0));
				}
			}
			request.put("headers", hdrs);

			L.debug("Solicitud saliente: {}", request.toJSONString());
		}
	}

	@SuppressWarnings("unchecked")
	private void __log_response()
	{
		if (L.isDebugEnabled())
		{
			try
			{
				JSONObject response = new JSONObject();
				response.put("code", con.getResponseCode());

				JSONObject hdrs = new JSONObject();
				for (Entry<String, List<String>> header : con.getHeaderFields().entrySet())
				{
					if (header.getValue().size() > 1)
					{
						JSONArray arr = new JSONArray();
						arr.addAll(header.getValue());
						hdrs.put(header.getKey(), arr);
					}
					else
					{
						hdrs.put(header.getKey(), header.getValue().get(0));
					}
				}
				response.put("headers", hdrs);

				response.put("body", new String(this.response.get_body(), "iso-8859-1"));

				L.debug("Respuesta entrante: {}", response.toJSONString());
			}
			catch (Exception e)
			{
				L.error("Excepcion al pintar la respuesta HTTP");
				L.catching(e);
			}
		}
	}
	*/
}
