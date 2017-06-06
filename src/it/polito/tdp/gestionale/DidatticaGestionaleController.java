package it.polito.tdp.gestionale;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.gestionale.model.Corso;
import it.polito.tdp.gestionale.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DidatticaGestionaleController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtMatricolaStudente;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCorsiFrequentati(ActionEvent event) {
		txtResult.clear();
		int counter=0;
			for(Integer i:model.getStatCorsi()){
				txtResult.appendText("Nr di studenti frequentanti "+Integer.toString(counter)+" corsi: "+Integer.toString(i)+"\n");
				counter++;
			}
		
	}
	
	@FXML
	void doVisualizzaCorsi(ActionEvent event) {
		txtResult.clear();
		txtResult.appendText("Nr di interventi da eseguire "+Integer.toString(model.trovaSequenza().size())+"nei corsi di:\n");
		for(Corso ctemp: model.trovaSequenza()){
			txtResult.appendText(ctemp.toString()+"\n");
		}
		
		
	}

	@FXML
	void initialize() {
		assert txtMatricolaStudente != null : "fx:id=\"txtMatricolaStudente\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}

}
