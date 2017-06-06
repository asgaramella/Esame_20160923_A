package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
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
		boolean first=true;
		
		scegli(parziale,0,best,first);
		
		return best;
		
		
	}

	private void scegli(List<Corso> parziale, int livello, List<Corso> best,boolean first) {
		System.out.println(this.getFrequentanti().size());
		
		
		if(this.getFrequentanti().size()==0){
			if(first){
				best.addAll(parziale);
				first=false;
			}
			else{
				if(parziale.size()<best.size()){
				best.clear();
				best.addAll(parziale);
			}
			}
			return;
			
		}else{
			for(Corso ctemp:this.getAllCorsi()){
				if(!parziale.contains(ctemp)){
					
					parziale.add(ctemp);
					this.getFrequentanti().removeAll(Graphs.neighborListOf(this.getGraph(),ctemp));
					
					scegli(parziale, livello+1,best,first);
					
					parziale.remove(ctemp);
					this.getFrequentanti().addAll(Graphs.neighborListOf(this.getGraph(), ctemp));
					
					
					
				}
			}
		}
		
	}
	
	
	
}
