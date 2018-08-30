package xo;

import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameStageController {
	@FXML Button gameBtn00;
	@FXML Button gameBtn01;
	@FXML Button gameBtn02;
	@FXML Button gameBtn10;
	@FXML Button gameBtn11;
	@FXML Button gameBtn12;
	@FXML Button gameBtn20;
	@FXML Button gameBtn21;
	@FXML Button gameBtn22;
	
	@FXML Button connectBtn;
	@FXML Button restartBtn;
	
	@FXML Label gameCharLbl;
	@FXML Label gameStatusLbl;
	
	private boolean turn;
	private PTPProducer producer = new PTPProducer();
	private PTPConsumer consumer = new PTPConsumer(this);
	
	private Service<Void> waitingForMessage;
	private String lastPosition = null;
	private String lastResult = null;
	
	private Player act;
	
	private boolean anotherPlayerOn = true;
	
	@FXML private void connectBtn_Click() {
		anotherPlayerOn = true;
		setGameButtonsDisable(false);
		connectBtn.setDisable(true);
		
		Random rand = new Random();
		consumer.receiveQueueMessagesAsynch("Oconnect", "yes");
		try { Thread.sleep(rand.nextInt(50) + 10); }
		catch (InterruptedException e) { e.printStackTrace(); }
		consumer.closeJmsContext();
		
		if(QueueAsynchronicConsumer.getOnePlayerAlreadyConnected()) {
			//System.out.println("Your character is O");
			Player newPlayer = new Player("O");
			gameCharLbl.setText("O");
			act = newPlayer;
			turn = true;
			consumer.receiveQueueMessagesAsynch("Omessage", "yes");
			gameStatusLbl.setText("Twoja tura");
		}
		else {
			//System.out.println("Your character is X");
			producer.sendMessageWithFilter("Oconnect", "yes");
			Player newPlayer = new Player("X");
			gameCharLbl.setText("X");
			act = newPlayer;
			turn = false;
			consumer.receiveQueueMessagesAsynch("Xmessage", "yes");
			//System.out.println("You're waiting for the second player to connect and make move");
			gameStatusLbl.setText("tura przeciwnika");
		} 
	}
	
	private void showEndGameStatus(String result) {
		if(result == null)
			return;
		if(result.equals("draw"))
			gameStatusLbl.setText("It's a draw");
		else
			gameStatusLbl.setText("Player " + result + " won");
		setGameButtonsDisable(true);
		restartBtn.setDisable(false);
		QueueAsynchronicConsumer.setOnePlayerAlreadyConnected(false);
	}
	
	 public void initialize() {
		 setGameButtonsDisable(true);
		 restartBtn.setDisable(true);
	 }
	
	private void setGameButtonsDisable(boolean a) {
		gameBtn00.setDisable(a);
		gameBtn01.setDisable(a);
		gameBtn02.setDisable(a);
		gameBtn10.setDisable(a);
		gameBtn11.setDisable(a);
		gameBtn12.setDisable(a);
		gameBtn20.setDisable(a);
		gameBtn21.setDisable(a);
		gameBtn22.setDisable(a);
	}
	
	private void setTextButton(String pos) {
		if(pos == null) {
			return;
		}
		if(pos.equals("00"))
			gameBtn00.setText(act.getOpponentChar());
		else if(pos.equals("01"))
			gameBtn01.setText(act.getOpponentChar());
		else if(pos.equals("02"))
			gameBtn02.setText(act.getOpponentChar());
		else if(pos.equals("10"))
			gameBtn10.setText(act.getOpponentChar());
		else if(pos.equals("11"))
			gameBtn11.setText(act.getOpponentChar());
		else if(pos.equals("12"))
			gameBtn12.setText(act.getOpponentChar());
		else if(pos.equals("20"))
			gameBtn20.setText(act.getOpponentChar());
		else if(pos.equals("21"))
			gameBtn21.setText(act.getOpponentChar());
		else if(pos.equals("22"))
			gameBtn22.setText(act.getOpponentChar());
	}
	
	private String checkGameState() {	
		//check rows
		if ( (gameBtn00.getText().equals("O") && gameBtn01.getText().equals("O") && gameBtn02.getText().equals("O")) ||
			 (gameBtn10.getText().equals("O") && gameBtn11.getText().equals("O") && gameBtn12.getText().equals("O")) ||
			 (gameBtn20.getText().equals("O") && gameBtn21.getText().equals("O") && gameBtn22.getText().equals("O")) )
			return "O";
		if ( (gameBtn00.getText().equals("X") && gameBtn01.getText().equals("X") && gameBtn02.getText().equals("X")) ||
			 (gameBtn10.getText().equals("X") && gameBtn11.getText().equals("X") && gameBtn12.getText().equals("X")) ||
			 (gameBtn20.getText().equals("X") && gameBtn21.getText().equals("X") && gameBtn22.getText().equals("X")) 
			)
			return "X";
		
		// check columns
		if ( (gameBtn00.getText().equals("O") && gameBtn10.getText().equals("O") && gameBtn20.getText().equals("O")) ||
			 (gameBtn01.getText().equals("O") && gameBtn11.getText().equals("O") && gameBtn21.getText().equals("O")) ||
			 (gameBtn02.getText().equals("O") && gameBtn12.getText().equals("O") && gameBtn22.getText().equals("O")) )
			return "O";
		if ( (gameBtn00.getText().equals("X") && gameBtn10.getText().equals("X") && gameBtn20.getText().equals("X")) ||
			 (gameBtn01.getText().equals("X") && gameBtn11.getText().equals("X") && gameBtn21.getText().equals("X")) ||
		     (gameBtn02.getText().equals("X") && gameBtn12.getText().equals("X") && gameBtn22.getText().equals("X")) )
			return "X";
		
		//check diagonals
		if ( (gameBtn00.getText().equals("O") && gameBtn11.getText().equals("O") && gameBtn22.getText().equals("O")) ||
			 (gameBtn02.getText().equals("O") && gameBtn11.getText().equals("O") && gameBtn20.getText().equals("O")) )
			return "O";
		if ( (gameBtn00.getText().equals("X") && gameBtn11.getText().equals("X") && gameBtn22.getText().equals("X")) ||
			 (gameBtn02.getText().equals("X") && gameBtn11.getText().equals("X") && gameBtn20.getText().equals("X")) )
			return "X";
		
		//check if it is a draw - no field is empty
		if(	( gameBtn00.getText().equals("O") || gameBtn00.getText().equals("X") )  &&
		  ( gameBtn01.getText().equals("O")  || gameBtn01.getText().equals("X")  ) &&
		  ( gameBtn02.getText().equals("O")  || gameBtn02.getText().equals("X")  ) &&
		  ( gameBtn10.getText().equals("O")  || gameBtn10.getText().equals("X")  ) &&
		  ( gameBtn11.getText().equals("O")  || gameBtn11.getText().equals("X")  ) &&
		  ( gameBtn12.getText().equals("O")  || gameBtn12.getText().equals("X")  ) &&
		  ( gameBtn20.getText().equals("O")  || gameBtn20.getText().equals("X")  ) &&
		  ( gameBtn21.getText().equals("O")  || gameBtn21.getText().equals("X")  ) &&
		  ( gameBtn22.getText().equals("O")  || gameBtn22.getText().equals("X")  )
				)
			return "draw";
		return null;
	}
	
	
	@FXML private void restartBtn_Click() {
		//System.out.println("restart");
		anotherPlayerOn = false;
		if(!QueueAsynchronicConsumer.getOnePlayerAlreadyConnected())
			doCleaning();
		lastResult = null;
		lastPosition = null;
		gameBtn00.setText("");
		gameBtn01.setText("");
		gameBtn02.setText("");
		gameBtn10.setText("");
		gameBtn11.setText("");
		gameBtn12.setText("");
		gameBtn20.setText("");
		gameBtn21.setText("");
		gameBtn22.setText("");
		setGameButtonsDisable(true);
		act = null;
		turn = false;
		connectBtn.setDisable(false);
		gameCharLbl.setText(null);
		gameStatusLbl.setText(null);
		restartBtn.setDisable(true);
	}
	
	@FXML private void gameBtn00_Click() {
		if(turn) {
			if(gameBtn00.getText().equals("O") || gameBtn00.getText().equals("X")) return;
			gameBtn00.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "00", r);	
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}			
	}
	
	@FXML private void gameBtn01_Click() {
		if(turn) {
			if(gameBtn01.getText().equals("O") || gameBtn01.getText().equals("X")) return;
			gameBtn01.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "01", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
	}
	
	@FXML private void gameBtn02_Click() {
		if(turn) {
			if(gameBtn02.getText().equals("O") || gameBtn02.getText().equals("X")) return;
			gameBtn02.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "02", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
		 
	}
	
	@FXML private void gameBtn10_Click() {
		if(turn) {
			if(gameBtn10.getText().equals("O") || gameBtn10.getText().equals("X")) return;
			gameBtn10.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "10", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}

	}
	
	@FXML private void gameBtn11_Click() {
		if(turn) {
			if(gameBtn11.getText().equals("O") || gameBtn11.getText().equals("X")) return;
			gameBtn11.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "11", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
	}
	
	@FXML private void gameBtn12_Click() {
		if(turn) {
			if(gameBtn12.getText().equals("O") || gameBtn12.getText().equals("X")) return;
			gameBtn12.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "12", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}

	}
	
	@FXML private void gameBtn20_Click() {
		if(turn) {
			if(gameBtn20.getText().equals("O") || gameBtn20.getText().equals("X")) return;
			gameBtn20.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "20", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
	}
	
	@FXML private void gameBtn21_Click() {
		if(turn) {
			if(gameBtn21.getText().equals("O") || gameBtn21.getText().equals("X")) return;
			gameBtn21.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "21", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
	}
	
	@FXML private void gameBtn22_Click() {
		if(turn) {
			if(gameBtn22.getText().equals("O") || gameBtn22.getText().equals("X")) return;
			gameBtn22.setText(act.getGameChar());
			turn = !turn;
			String r = checkGameState();
			lastResult = r;
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "22", r);
			gameStatusLbl.setText("tura przeciwnika");
			if(r != null) {
				showEndGameStatus(r);
				return;
			}
		}
	}
	
	private void doMessage() {
		
		waitingForMessage = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try { Thread.sleep(10); }
						catch (InterruptedException e) { e.printStackTrace(); }
						return null;
					}
				};
			}
			
		};
		
		
		waitingForMessage.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent arg0) {
				setTextButton(lastPosition);
				gameStatusLbl.setText("Twoja tura");
				showEndGameStatus(lastResult);
				lastPosition = null;
				turn = !turn;	
			}
			
		});
		
		
		waitingForMessage.restart(); 
	}
	
	private void doAlert() {
		waitingForMessage = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try { Thread.sleep(10); }
						catch (InterruptedException e) { e.printStackTrace(); }
						return null;
					}
				};
			}
			
		};
		
		
		waitingForMessage.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent arg0) {
				setGameButtonsDisable(true);
				restartBtn.setDisable(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Drugi gracz zrezygnowa³ z gry");
				alert.setHeaderText("Nie da siê kontynuowaæ");
				alert.setContentText("Mo¿na wy³¹czyæ i w³¹czyæ grê, czekaj¹c na nowego gracza lub zrobiæ restart");
				alert.showAndWait();
			}
			
		});
		
		
		waitingForMessage.restart(); 
	}
	
	private void doCleaning() {
        consumer.closeJmsContext();
        // receive messages if there are any left
        consumer.receiveQueueMessagesAsynchronously();
		try { Thread.sleep(10); }
		catch (InterruptedException e) { e.printStackTrace(); }
		consumer.closeJmsContext();
	}
	
	public void shutdown() {
		if(act != null && lastResult == null) { //leaving before end
			//System.out.println("leaving before end of the game...");
			producer.sendMessageWithFilterAndPosition(act.getOpponentChar() + "message", "yes", "LEAVING", null);
			consumer.closeJmsContext();
			
		}
		
		else if(act != null) {
			//doCleaning();
	        consumer.closeJmsContext();
	        // receive messages if there are any left
	        consumer.receiveQueueMessagesAsynchronously();
			try { Thread.sleep(10); }
			catch (InterruptedException e) { e.printStackTrace(); }
			consumer.closeJmsContext();
			QueueAsynchronicConsumer.setOnePlayerAlreadyConnected(false);
		}

        Platform.exit();
    }
	

	public static class QueueAsynchronicConsumer implements MessageListener {
		private static boolean onePlayerAlreadyConnected = false;
		private GameStageController gameController;
		
		public QueueAsynchronicConsumer(GameStageController a) {
			gameController = a;
		}
		
		@Override
		public void onMessage(Message message) {
			if(message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				try {

					String a = textMessage.getStringProperty("Oconnect");
					if( a != null ) 
						onePlayerAlreadyConnected = true;
					
					String b = textMessage.getStringProperty("Xmessage");
					String c = textMessage.getStringProperty("Omessage");
					if( b != null || c != null ) {

						String pos = textMessage.getStringProperty("POSITION_VALUE");
						String res = textMessage.getStringProperty("RESULT");
						if( pos != null && pos.equals("LEAVING") ) {
							//second player left
							gameController.anotherPlayerOn = false;
							gameController.lastResult = "none";
							gameController.doAlert();
							return;
						}

						gameController.lastPosition = pos;
						gameController.lastResult = res;
						if(gameController.anotherPlayerOn) gameController.doMessage(); // it's working - if not maybe there are threads from previous use
					}
					
					
				}
				catch (JMSException e) { e.printStackTrace(); }
			}
		
			
		}
			
		
		public static boolean getOnePlayerAlreadyConnected() {
			return onePlayerAlreadyConnected;
		}
		
		public static void setOnePlayerAlreadyConnected(boolean a) {
			onePlayerAlreadyConnected = a;
		}
	}
}
