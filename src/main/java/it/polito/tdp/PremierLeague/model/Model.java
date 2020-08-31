package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Team, DefaultWeightedEdge> grafo ;
	private List<Team> vertici ;
	private List<Match> matches;
	
	public Model() {
		this.dao = new PremierLeagueDAO() ;
		this.matches = this.dao.listAllMatches();
	}
	
	public void creaGrafo() {
		// creo il grafo
		this.grafo = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class) ;
		// aggiungo i vertici
		this.vertici = this.dao.listAllTeams();
		Graphs.addAllVertices(this.grafo, this.vertici);
		for(Team t1 : this.grafo.vertexSet()) {
			int punteggio1 = this.getPunteggioInClassifica(t1);
			for(Team t2 : this.grafo.vertexSet()) {
				int punteggio2 = this.getPunteggioInClassifica(t2);
				if(!t1.equals(t2)) {
					if(punteggio1 < punteggio2) {
						Graphs.addEdgeWithVertices(this.grafo, t2, t1, punteggio2-punteggio1);
					}else if(punteggio1 > punteggio2) {
						Graphs.addEdgeWithVertices(this.grafo, t1, t2, punteggio1-punteggio2);
					}
				}
			
			}
		
		}
	}		
	
	
	public int numVertici() {
		return this.grafo.vertexSet().size() ;
	}
	
	public int numArchi() {
		return this.grafo.edgeSet().size() ;
	}
	
	public Integer getRisultatoMatch(Match m) {
		return this.dao.getRisultato(m);
	}

	public Integer getPunteggioInClassifica(Team t) {
		Integer punteggio = 0;
		for(Match m : this.matches) {
			if((this.getRisultatoMatch(m) == 0) && ((t.teamID.equals(m.teamHomeID)) || (t.teamID.equals(m.teamAwayID)))) {
				punteggio++;
			} else if(this.getRisultatoMatch(m) == 1) {
				if(t.teamID.equals(m.teamHomeID)){
					punteggio += 3;
				}
			} else if(this.getRisultatoMatch(m) == -1) {
				if(t.teamID.equals(m.teamAwayID)) {
					punteggio += 3;
				}
			}
		}
		return punteggio ;		
	}

	public List<Team> getSquadre() {
		return this.dao.listAllTeams();
	}
	
	public String getBattuteBattenti(Team t1){
		List<Team> battute = new ArrayList<Team>();
		List<Team> battenti = new ArrayList<Team>();
		Integer punteggio1 = this.getPunteggioInClassifica(t1);
		for(Team t2 : this.getSquadre()) {
			if(!t1.equals(t2)) {
				Integer punteggio2 = this.getPunteggioInClassifica(t2);
				if(punteggio2 < punteggio1) {
					battute.add(t2);
				} else if(punteggio2 > punteggio1) {
					battenti.add(t2);
				}
			}
		}
		
		Collections.sort(battute);
		Collections.sort(battenti);
		return "Squadre battute da: " +t1+ "\n" + battute + "\nSquadre che hanno battuto: " +t1 +"\n" + battenti;
	}
	
	
		
	
	


















}
