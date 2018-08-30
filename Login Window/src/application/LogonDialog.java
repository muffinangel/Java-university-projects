package application;

import java.util.Optional;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
* LogonDialog - klasa, która tworzy okienko logowania, kiedy tworzony jest obiekt tej klasy
* 
* @author Karolina Bilewicz
* @version 0.9
*/
public class LogonDialog {
	
	private ChoiceBox<Environment> environmentChoiceBox;
	private ComboBox<String> userIdComboBox;
	private PasswordField passField;
	private ButtonType logOnButton;
	private Dialog<Pair<Environment, String>> dialog;
	
	private Label env;
	private Label userLabel;
	private Label passLabel;
	private GridPane grid;
	private ButtonType cancelButton;
	/** Konstruktor
	 * Tworzy okno logowania ze wszystkimi potrzebnymi obiektami.
	 * Lista wyboru środowiska jest na bazie ChoiceBox.
	 * Lista wyboru użytkownika jest na bazie ComboBox.
	 * Pole wpisywania hasła na bazie PasswordField.
	 * Przyciski- Logon i Anuluj -  na bazie ButtonType.
	 * Zarządca układu - GridPane.
	 * 
	 * Dodatkowo, lista wyboru środowiska, lista wyborów użytkownika
	 * i pole wpisywania hasła mają dodane listenery, aby móc sprawdzić, czy
	 * przycisk Logon powinien być już aktywowany oraz czy lista użytkowników powinna się
	 * zmienić, kiedy środowisko zostało zmienione.
	 * 
	 * Korzysta też z metody na obiekcie klasy Dialog - setResultConverter,
	 * aby przekonwertować ButtonType, klikniętego przez użytkownika w Pair<Environment, String>.
	 * Sprawdzane jest, czy została wybrana opcja ok oraz czy hasło jest prawidłowe 
	 * dla danego użytkownika w danym środowisku.
	 * 
	 *
	 * @param windowTitle ustawia tytuł okna logowania
	 * @param headerTitle ustawia tytuł nagłówka logowania
	 * 
	 */
	LogonDialog(String windowTitle, String headerTitle) {
		Users user = new Users();
		Environment test = new Environment("Testowe");
		Environment prod = new Environment("Produkcyjne");
		Environment dev = new Environment("Deweloperskie");
		
		dialog = new Dialog<>();
		dialog.setTitle(windowTitle);
		dialog.setHeaderText(headerTitle);
		Image i = new Image("login.png");
		ImageView loginImage = new ImageView();
		loginImage.setImage(i);
		loginImage.setPreserveRatio(true);
		loginImage.setFitHeight(100);
		dialog.setGraphic(loginImage);
			
		grid = new GridPane();
		
		env = new Label("Środowisko: ");
		userLabel = new Label("Użytkownik: ");
		passLabel = new Label("Hasło: ");
		
		
	    logOnButton = new ButtonType("Logon", ButtonData.OK_DONE);
	    cancelButton = new ButtonType("Anuluj", ButtonData.CANCEL_CLOSE);
	    
	    environmentChoiceBox = new ChoiceBox<Environment>();
		environmentChoiceBox.getItems().addAll(prod, test, dev);
	    environmentChoiceBox.setValue(prod);
	    
	    userIdComboBox = new ComboBox<String>();
	    userIdComboBox.getItems().addAll(user.getUserList(prod));
	    userIdComboBox.setEditable(true);    
	    
	    passField = new PasswordField();
	    passField.setPromptText("Hasło");
	    
		
		grid.add(env, 1, 1);
		grid.add(environmentChoiceBox, 2, 1);
		grid.add(userLabel, 1, 2);
		grid.add(userIdComboBox, 2, 2);
		grid.add(passLabel, 1, 3);
		grid.add(passField, 2, 3);
		
		grid.setHgap(10);
		grid.setVgap(10);
	    
	    dialog.getDialogPane().getButtonTypes().addAll(logOnButton, cancelButton);
	    dialog.getDialogPane().lookupButton(logOnButton).setDisable(true);
	    
	    environmentChoiceBox.valueProperty().addListener(
	    		(observable, oldVal, newVal) -> changeUserList(user, oldVal));
	    
	    userIdComboBox.valueProperty().addListener(
	    		(observable, oldVal, newVal) -> changeButton());
	    
	    passField.textProperty().addListener(
	    		(observable, oldVal, newVal) -> changeButton());
	    
	    
	    dialog.setResultConverter(dialogButton -> {
	    	if(dialogButton == logOnButton) {
	    		if(user.isPasswordValid(userIdComboBox.getValue(), passField.getText(),environmentChoiceBox.getValue()))
	    			return new Pair<Environment, String>(environmentChoiceBox.getValue(), userIdComboBox.getValue());
	    	}
	    	return null;
	    });	
		
		dialog.getDialogPane().setContent(grid);		
		
	}
	
	
	/** Metoda changeUserList. Zmienia listę użytkowników przy zmianach środowiska.
	 *
	 * @param user obiekt, zawierający informacje o użytkownikach dla danych środowisk
	 * @param oldVal stara wartość środowiska
	 * 
	 */
	private void changeUserList(Users user, Environment oldVal) {
		Environment env = environmentChoiceBox.getValue();
		userIdComboBox.getItems().removeAll(user.getUserList(oldVal));
    	userIdComboBox.getItems().addAll(user.getUserList(env));
    	changeButton();
	}
	
	/** Metoda changeButton.
	 * Sprawdza, czy wszystkie pola, wymagana do aktywacji przycisku Logon, zostały zapełnione.
	 */
	private void changeButton() {
		if(userIdComboBox.getValue() == null || environmentChoiceBox.getValue() == null
				|| passField.getText().trim().isEmpty()) {
			dialog.getDialogPane().lookupButton(logOnButton).setDisable(true);
		}
		else
			dialog.getDialogPane().lookupButton(logOnButton).setDisable(false);
	}
	
	/** Metoda showAndWait.
	 * 
	 * @return zwraca pare z wybranym środowiskiem oraz użytkownikiem, ktory się do niego poprawnie zalogował, a jeśli niepoprawnie to null
	 */
	public Optional<Pair<Environment, String>> showAndWait() {
		return dialog.showAndWait();
	}
}
