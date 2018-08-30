package application;

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Pair;
/**
* Moja glówna klasa, nazwana LogOnWindow.
* Zbudowana na podstawie wskazówek projektowych.
* Generuje okno logowania za pomocą klasy LogonDialog oraz alert, zarówno w razie powodzenia,
* kiedy to informuje o wybranym środowisku oraz o tym, jaki użytkownik sie zalogował,
* jak i nie, kiedy to informuje o błędzie logowania.
* 
* @author Karolina Bilewicz
* @version 0.9
*/
public class LogOnWindow extends Application {
	/** Metoda start. Glówny punkt wejściowy aplikacji JavaFX. 
	 * Jest wołana, kiedy metoda init skończy się wykonywać i po tym, kiedy system jest gotowy na to,
	 * aby aplikacja zaczęła działac.
	 * Tworzy ona obiekt klasy LogonDialog i wywołuje na nim metodę showAndWait.
	 *
	 * @param primaryStage można ustawić scene aplikacji na primaryStage.
	 * @exception Exception gdy będzie wyjątek do obsłużenia
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		Optional<Pair<application.Environment, String>> result = (new LogonDialog("Logowanie",
				"Logowanie do systemu STYLEman")).showAndWait();
		
		if (result.isPresent()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Poprawnie zalogowano do systemu");
			alert.setHeaderText(null);
			Pair<application.Environment, String> p = result.get();
			
			String message = "Wybrane środowisko: " + p.getKey().toString() + 
					". Zalogowano jako " + p.getValue();
			alert.setContentText(message);

			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Niepoprawne dane");
			alert.setHeaderText(null);
			alert.setContentText("Logowanie nieudane");
			alert.showAndWait();
		}
		
	}
	
	/** Metoda main. 
	 *
	 * @param args argumenty wiersza poleceń
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
