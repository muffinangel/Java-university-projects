
package xo;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;

public class PTPProducer {
	
	public void sendMessageWithFilter( String id, String val ) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		try {
		// [hostName][:portNumber][/serviceName]
		// 7676 numer portu, na którym JMS Service nas³uchuje po³¹czeñ
		((com.sun.messaging.ConnectionFactory) connectionFactory)
		.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,
		"localhost:7676/jms");
		JMSContext jmsContext = connectionFactory.createContext();
		JMSProducer jmsProducer = jmsContext.createProducer();
		Queue queue = new com.sun.messaging.Queue("ATJQueue");

		String msg = "Message_FILTER";
		TextMessage txtMsg = jmsContext.createTextMessage();
		txtMsg.setStringProperty(id, val);
		txtMsg.setText(msg);
		jmsProducer.send(queue, txtMsg);
		System.out.printf("Wiadomoœæ '%s' zosta³a wys³ana.\n", msg);
		jmsContext.close();
		}
		catch (JMSException e) { e.printStackTrace(); }
	}
	
	public void sendMessageWithFilterAndPosition( String id, String val, String position, String result ) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		try {
		// [hostName][:portNumber][/serviceName]
		// 7676 numer portu, na którym JMS Service nas³uchuje po³¹czeñ
		((com.sun.messaging.ConnectionFactory) connectionFactory)
		.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,
		"localhost:7676/jms");
		JMSContext jmsContext = connectionFactory.createContext();
		JMSProducer jmsProducer = jmsContext.createProducer();
		Queue queue = new com.sun.messaging.Queue("ATJQueue");

		String msg = "Message_FILTER_POSITION";
		TextMessage txtMsg = jmsContext.createTextMessage();
		txtMsg.setStringProperty(id, val);
		txtMsg.setText(msg);
		txtMsg.setStringProperty("POSITION_VALUE", position);
		if(result != null) txtMsg.setStringProperty("RESULT", result);
		jmsProducer.send(queue, txtMsg);
		System.out.printf("Wiadomoœæ '%s' zosta³a wys³ana.\n", msg);
		
		jmsContext.close();
		}
		catch (JMSException e) { e.printStackTrace(); }
	}

}
