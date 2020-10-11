package es.hefame.hcore.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class HttpClientResponse
{
	
	private HttpURLConnection	con;
	private byte[]				requestBody	= null;

	public HttpClientResponse(HttpURLConnection con)
	{
		this.con = con;
	}

	public int getStatusCode() throws IOException
	{
		return this.con.getResponseCode();
	}

	public String getStatusCodeMessage() throws IOException
	{
		return this.con.getResponseMessage();
	}

	public String getHeader(String headerName)
	{
		return this.con.getHeaderField(headerName);
	}

	public int getContentLenght() 
	{
		return this.con.getContentLength();
	}

	public byte[] getBody() throws IOException
	{
		if (this.requestBody != null) return this.requestBody;
		if (this.getContentLenght() < 1) { return new byte[0]; }

		InputStream is = null;
		if (this.con.getResponseCode() > 399)
		{
			is = this.con.getErrorStream();
		}
		else
		{
			is = this.con.getInputStream();
		}

		requestBody = new byte[this.getContentLenght()];

		int b;
		int i = 0;
		while (((b = is.read()) > -1) && (i < requestBody.length))
		{
			this.requestBody[i++] = (byte) b;
		}

		// TODO: If (int b) is -1, an IO error might happent
		// TODO: If next call to is.read() is not -1 nor blocked

		return this.requestBody;
	}

}
