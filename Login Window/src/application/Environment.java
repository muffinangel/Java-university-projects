package application;

/**
* Environment - prosta klasa, zawierająca informacje o środowisku.
* 
* @author Karolina Bilewicz
* @version 0.9
*/
public class Environment {
	private String name;
	/** Konstruktor Environment
	 * @param n nazwa środowiska
	 */
	Environment(String n) {
		name = n;
	}
	/** Metoda getName()
	 * @return nazwa środowiska
	 */
	public String getName() {
		return name;
	}
	/** Metoda toString(), potrzebna, aby wyświetlać w ChoiceBox
	 * @return nazwa środowiska
	 */
	public String toString() {
		return name;
	}
}
