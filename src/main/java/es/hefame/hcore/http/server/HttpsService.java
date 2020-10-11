package es.hefame.hcore.http.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import es.hefame.hcore.http.HttpController;

/**
 * Implementación de un servidor HTTPS basada en la clase com.sun.net.httpserver.HttpsServer.
 * 
 * @author Alejandro_AC
 *
 */
public class HttpsService extends HttpService
{

	/**
	 * Alias interno del servidor, casteado a la versión segura del mismo.
	 */
	private HttpsServer		sserver;

	/**
	 * Inicializa el servicio HTTP con protección de capa SSL.
	 * 
	 * @see es.hefame.hcore.http.server.HttpService
	 * @see com.sun.net.httpserver.HttpsServer
	 * @param port Puerto en el que escuchará el servicio
	 * @param maxConnectionQueue Tamaño de la cola de conexiones entrantes
	 * @param jksPath Ruta al fichero de claves JKS
	 * @param password Clave de acceso al fichero JKS
	 * @param routes Mapa de rutas a los distintos controladores
	 * @throws IOException Si falla al iniciar el servicio.
	 * @throws KeyStoreException Si ocurre algún error al abrir el JKS.
	 * @throws NoSuchAlgorithmException Si las claves para securizar el servicio utilizan algoritmos que no se soportan.
	 * @throws CertificateException Si algún certificado del JKS no es legibles.
	 * @throws UnrecoverableKeyException Si alguna clave privada del JKS no es legibles.
	 * @throws KeyManagementException Si falla la creación del contexto SSL.
	 */
	
	public HttpsService(int port, int maxConnectionQueue, String jksPath, char[] password, Map<String, HttpController> routes) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
	{
		super(port, maxConnectionQueue, routes);
		generateSSLContext(jksPath, password);

	}

	/**
	 * Inicia la instancia interna de com.sun.net.httpserver.HttpsServer.
	 * 
	 * @throws IOException Si falla al crear el servicio
	 */
	@Override
	protected void initializeServer() throws IOException
	{
		
		this.server = HttpsServer.create(new InetSocketAddress(this.port), this.maxConnectionQueue);
		this.sserver = (HttpsServer) this.server;
		this.sserver.setExecutor(this);

		for (Entry<String, HttpController> route : routes.entrySet())
		{
			this.sserver.createContext(route.getKey(), route.getValue());
		}

	}

	/**
	 * Inicia el contexto SSL, cargarndo las claves necesarias de un Java KeyStore (JKS), y lo asigna al servicio
	 * para securizar el mismo.
	 * 
	 * @param jksPath Ruta al fichero JKS
	 * @param password Clave de acceso al fichero JKS
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	private void generateSSLContext(String jksPath, char[] password) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException
	{

		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(jksPath);
		ks.load(fis, password);


		String sslManagerType = System.getProperty("es.hefame.hcore.http.sslmanager");
		sslManagerType = (sslManagerType == null) ? "SunX509" : sslManagerType;
		
		String sslContextType = System.getProperty("es.hefame.hcore.http.sslcontext");
		sslContextType = (sslContextType == null) ? "TLS" : sslContextType;
		
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(sslManagerType);
		kmf.init(ks, password);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(sslManagerType);
		tmf.init(ks);

		SSLContext sslContext = SSLContext.getInstance(sslContextType);
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom(new Date().toString().getBytes()));

		sserver.setHttpsConfigurator(new HttpsConfigurator(sslContext)
		{
			@Override
			public void configure(HttpsParameters params)
			{
				// L.trace("Generando contexto de seguridad HTTP para la conexion entrante desde la IP [{}]", params.getClientAddress().getAddress().toString().split("\\/")[1]);
				SSLContext c = this.getSSLContext();// SSLContext.getDefault();
				SSLEngine engine = c.createSSLEngine();
				params.setNeedClientAuth(false);
				params.setCipherSuites(engine.getEnabledCipherSuites());
				params.setProtocols(engine.getEnabledProtocols());
				SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
				params.setSSLParameters(defaultSSLParameters);
			}
		});
	}

}
