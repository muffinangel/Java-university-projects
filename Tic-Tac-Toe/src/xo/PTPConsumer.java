package xo;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;

public class PTPConsumer {
	private ConnectionFactory connectionFactory; 
	private JMSContext jmsContext;
	private JMSConsumer jmsConsumer;
	
	private xo.GameStageController.QueueAsynchronicConsumer asynchListener;
	
	public PTPConsumer(GameStageController gc) {
		asynchListener = new xo.GameStageController.QueueAsynchronicConsumer(gc);
		connectionFactory = new com.sun.messaging.ConnectionFactory();
	}

	
	public void receiveQueueMessagesAsynchronously() {
	    jmsContext = connectionFactory.createContext();
		try {
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
			jmsConsumer.setMessageListener(asynchListener);
		}
		catch(JMSException e) { e.printStackTrace(); }
	}
	
	public void receiveQueueMessagesAsynch(String id, String val) {
		jmsContext = connectionFactory.createContext();
		try {
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			String sign = " = '";
			String test = id + sign + val + "'";
			jmsConsumer = jmsContext.createConsumer(queue, test);
			jmsConsumer.setMessageListener(asynchListener);
		}
		catch(JMSException e) { e.printStackTrace(); }
		
	}
	
	public void receiveQueueMessagesAsychExcept(String id, String val) {
		jmsContext = connectionFactory.createContext();
		try {
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			String sign = " <> '";
			String test = id + sign + val + "'";
			jmsConsumer = jmsContext.createConsumer(queue, test);
			jmsConsumer.setMessageListener(asynchListener);
		}
		catch(JMSException e) { e.printStackTrace(); }
	}
	
	public void closeJmsContext() {
		jmsConsumer.close();
		jmsContext.close();
	}

}
