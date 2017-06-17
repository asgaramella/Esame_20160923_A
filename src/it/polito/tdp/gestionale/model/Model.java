package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.gestionale.db.DidatticaDAO;

public class Model {

	private List<Corso> corsi;
	private List<Studente> studenti;
	private DidatticaDAO dao;
	private UndirectedGraph<Nodo,DefaultEdge> graph;
	private Map<Integer,Studente> mappaStudenti;
	private Set<Nodo> frequentanti;

	public Model() {
		dao=new  DidatticaDAO();
		mappaStudenti=new HashMap();
		
	}
	
	public List<Corso> getAllCorsi(){
		if(corsi==null){
			corsi=dao.getTuttiICorsi();
			// mi assicuro popolazione della lista di studenti
			this.getAllStudenti();
			
			for(Corso ctemp: corsi){
				dao.setStudentiIscrittiAlCorso(ctemp, mappaStudenti);
			}
		}
		return corsi;
	}
	
	public List<Studente> getAllStudenti(){
		if(studenti==null)
			studenti=dao.getTuttiStudenti();
		
		for(Studente stemp:studenti)
			mappaStudenti.put(stemp.getMatricola(), stemp);
		
		return studenti;
	}
	
	public UndirectedGraph<Nodo,DefaultEdge> getGraph(){
		if(graph==null)
			this.creaGrafo();
		return graph;
	}

	private void creaGrafo() {
		graph=new SimpleGraph<Nodo,DefaultEdge>(DefaultEdge.class);
		
		Graphs.addAllVertices(graph, this.getAllStudenti());
		Graphs.addAllVertices(graph, this.getAllCorsi());
		
		for(Corso ctemp: this.getAllCorsi()){
			for(Studente stemp: ctemp.getStudenti()){
				graph.addEdge(ctemp, stemp)	;	
				}
		}
		
	}
	
	public List<Integer> getStatCorsi (){
		List<Integer> ltemp= new ArrayList<Integer>();
		//inizializzo la struttura dati 
		for(int i=0; i<=this.getAllCorsi().size();i++){
			ltemp.add(i, 0);
			}
		
		for(Studente stemp:this.getAllStudenti()){
			int ncorsi=Graphs.neighborListOf(this.getGraph(), stemp).size();
			int frequenze=ltemp.get(ncorsi);
			frequenze++;
			ltemp.set(ncorsi, frequenze);
		}
		
		
		
		return ltemp;
	}
	
	
	public Set<Nodo> getFrequentanti(){
		if(frequentanti==null){
			frequentanti=new LinkedHashSet<Nodo>();
			for(Studente stemp: this.getAllStudenti()){
				if(this.getGraph().degreeOf(stemp)!=0)
					frequentanti.add(stemp);
			}
				
		}
		return frequentanti;
	}
	
	public List<Corso> trovaSequenza(){
		List<Corso> parziale=new ArrayList<Corso>();
		List<Corso> best=new ArrayList<Corso>();
		
		
		//CONDIZIONE DI TERMINAZIONE: QUANDO HO ESPLORATO TUTTE LE POSSIBILI SOLUZIONI
		scegli(0,parziale,best);
		
		return best; 
		
		
	}

	private void scegli(int livello,List<Corso> parziale, List<Corso> best) {
		System.out.println(parziale);
		
		HashSet<Nodo> hashSetStudenti=new HashSet<Nodo>(this.getFrequentanti());
		for(Corso ctemp: parziale){
			hashSetStudenti.removeAll(ctemp.getStudenti());
		}	
		
		if(hashSetStudenti.isEmpty()){
			if(best.isEmpty()){
				best.addAll(parziale);
			}
			else
			{
				if(parziale.size()<best.size()){
					best.clear();
					best.addAll(parziale);
				}
			}
		}
		
		
		for(Corso ctemp:this.getAllCorsi()){
				if(parziale.isEmpty() || ctemp.compareTo(parziale.get(parziale.size()-1))>0){
					
					parziale.add(ctemp);
					
					scegli(livello+1,parziale,best);
					
					parziale.remove(ctemp);
				
			}
			
		}
		
	}
	
	
	
}
