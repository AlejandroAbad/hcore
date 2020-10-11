package es.hefame.hcore.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.sun.net.httpserver.HttpServer;

import es.hefame.hcore.http.HttpController;

/**
 * Implementación de un servidor HTTP basada en la clase
 * com.sun.net.httpserver.HttpServer, que tambien implementa un Executor
 * implementado internamente con una CachedThreadPool que se encarga de
 * administrar los hilos de ejecución para ateneder peticiones HTTP entrantes.
 * 
 * @author Alejandro_AC
 *
 */
public class HttpService implements Executor {

	/**
	 * El servidor HTTP del API Java
	 */
	protected HttpServer server;

	/**
	 * El pool de threads donde se ejecutan los hilos para atender peticiones HTTP.
	 */
	protected ThreadPoolExecutor tpe;

	/**
	 * Puerto donde escucha el servicio HTTP
	 */
	protected int port;

	/**
	 * Número máximo de peticiones HTTP que se pueden encolar antes de poder ser
	 * atendidas por un hilo
	 */
	protected int maxConnectionQueue;

	/**
	 * Mapa de rutas a los distintos controladores HTTP
	 * 
	 */
	protected Map<String, HttpController> routes;

	/**
	 * Inicializa el servicio HTTP en el puerto y con el tamaño de cola
	 * especificados. Las rutas permiten indicar que controlador (HttpController) se
	 * debe lanzar cuando se reciban peticiones, en función del path URI.
	 * Información adicional se puede consultar en la documentación de la clase
	 * com.sun.net.httpserver.HttpServer.
	 * 
	 * Cabe a destacar que durante la creación del servicio este quedará a la
	 * escucha en el puerto indicado, pero no atenderá peticiones hasta que se llame
	 * al método start() del mismo.
	 * 
	 * @see com.sun.net.httpserver.HttpServer
	 * @param port               Puerto en el que escuchará el servicio
	 * @param maxConnectionQueue Tamaño de la cola de conexiones entrantes
	 * @param routes             Mapa de rutas a los distintos controladores
	 * @throws IOException Si falla al iniciar el servicio.
	 */

	public HttpService(int port, int maxConnectionQueue, Map<String, HttpController> routes) throws IOException {

		this.tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();

		this.maxConnectionQueue = maxConnectionQueue;
		this.port = port;
		this.routes = routes;

		this.initializeServer();

	}

	/**
	 * Inicia la instancia interna de com.sun.net.httpserver.HttpServer. Este método
	 * existe para permitir que subclases utilicen a su vez otras subclases de
	 * com.sun.net.httpserver.HttpServer, como por ejemplo
	 * com.sun.net.httpserver.HttpsServer.
	 * 
	 * @throws IOException Si falla al crear el servidor
	 */
	protected void initializeServer() throws IOException {

		server = HttpServer.create(new InetSocketAddress(this.port), this.maxConnectionQueue);
		server.setExecutor(this);

		for (Entry<String, HttpController> route : routes.entrySet()) {
			server.createContext(route.getKey(), route.getValue());
		}

	}

	/**
	 * Arranca el servicio.
	 */
	public void start() {
		this.server.start();
	}

	/**
	 * Para el servicio. Este deja de atender peticiones entrantes y cancela todas
	 * las peticiones que estuvieran siendo atendidas pasados 5 segundos.
	 */
	public void stop() {
		this.stop(5);
	}

	/**
	 * Para el servicio. Este deja de atender peticiones entrantes y cancela todas
	 * las peticiones que estuvieran siendo atendidas pasado el tiempo especidicado
	 * en el parámetro <i>grace_time</i> (en segundos).
	 * 
	 * @param graceTime Tiempo en segundos durante los que el servicio esperará a
	 *                  que las peticiones que se encuentran procesandose terminen
	 *                  de hacerlo.
	 */
	public void stop(int graceTime) {
		this.server.stop(graceTime);
	}

	public void restart() {
		stop();
		start();
	}

	/**
	 * @see java.util.concurrent.Executor
	 */
	@Override
	public void execute(Runnable command) {
		tpe.execute(command);
	}

}
