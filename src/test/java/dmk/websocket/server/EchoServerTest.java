package dmk.websocket.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmk.websocket.endpoint.EchoEndpoint;

/**
 * server test code taken and modified from, 
 * 	https://tyrus.java.net/documentation/1.0/user-guide.html
 * @author dmknopp
 *
 */
public class EchoServerTest {
	private static final Logger logger = LoggerFactory
			.getLogger(EchoServerTest.class);

	private CountDownLatch messageLatch;
	private static final String SENT_MESSAGE = "Hello World";
	private final int numMessages = 5;
	
	private EchoServer<EchoEndpoint> server;
	private final int port = 8989;
	
	@Before
	public void setup() throws Exception{
		messageLatch = new CountDownLatch(numMessages);
		logger.debug("setting up websocket server");
		if(logger.isDebugEnabled()){
		}
		this.server = new EchoServer<>("localhost", this.port, "/tyrus-ws", EchoEndpoint.class);
		this.server.start();
	}
	
	@After public void breakdown(){
		logger.debug("stopping websocket server");
		if(logger.isDebugEnabled()){
		}
		this.server.stop();
	}

	@Test
	public void sendMessages() throws Exception {
		final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create()
				.build();
		ClientManager client = ClientManager.createClient();
		client.connectToServer(new Endpoint() {

			@Override
			public void onOpen(Session session, EndpointConfig config) {
				session.addMessageHandler(new MessageHandler.Whole<String>() {

					@Override
					public void onMessage(String message) {
						assertNotNull(message);
						assertEquals(SENT_MESSAGE, message);
						messageLatch.countDown();
						if (logger.isDebugEnabled()) {
							logger.debug("client recieved message: " + message);
						}

					}
				});
				try{
					for(int i = 0; i < numMessages; i++){
						session.getBasicRemote().sendText(SENT_MESSAGE);
					}
				} catch(IOException ioe){
					throw new RuntimeException(ioe.getMessage());
				}
			}
		}, cec, new URI(String.format("ws://localhost:%d/tyrus-ws/echo", this.port)));
		messageLatch.await(30, TimeUnit.SECONDS);
	}
}
