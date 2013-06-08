package dmk.websocket.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmk.websocket.endpoint.EchoEndpoint;

/**
 * taken and modified from, 
 * 	https://tyrus.java.net/documentation/1.0/user-guide.html
 * @author dmknopp
 */
public class EchoServer<T> {
	private static final Logger logger = LoggerFactory
			.getLogger(EchoServerTest.class);

	private String host;
	private int port;
	private String contextRoot;
	private Class<T> endpointClazz;
	private Server server;
	
	/**
	 * 
	 */
	public EchoServer(String host, int port, String contextRoot, Class<T> endpoint){
		super();
		this.host = host;
		this.port = port;
		this.contextRoot = contextRoot;
		this.endpointClazz = endpoint;
		this.server = new Server(this.host, this.port, this.contextRoot, this.endpointClazz);
	}
	
	public void start() throws Exception{
		if(this.server != null){
			if(logger.isDebugEnabled()){
				logger.debug("started server");
			}
			this.server.start();
		}
	}
	
	public void stop(){
		if(this.server != null){
			this.server.stop();
		}
	}
	
	/**
	 * 
	 */
	public void spinupCliDriver() {
		logger.debug("starting websocket " + this.host + ":" + this.port + this.contextRoot);
		Server server = new Server(this.host, this.port, this.contextRoot, this.endpointClazz);
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
			server.start();
			System.out.print("Please press a key to stop the server.");
			reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]){
		final EchoServer<EchoEndpoint> server = new EchoServer<>("localhost", 8080, "/tyrus-ws", EchoEndpoint.class);
		server.spinupCliDriver();
	}
}