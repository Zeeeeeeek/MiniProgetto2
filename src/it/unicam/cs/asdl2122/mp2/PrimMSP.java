package it.unicam.cs.asdl2122.mp2;


//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE


import java.util.ArrayList;
import java.util.List;
/**
 * Classe singoletto che implementa l'algoritmo di Prim per trovare un Minimum
 * Spanning Tree di un grafo non orientato, pesato e con pesi non negativi.
 * 
 * L'algoritmo richiede l'uso di una coda di min priorità tra i nodi che può
 * essere realizzata con una semplice ArrayList (non c'è bisogno di ottimizzare
 * le operazioni di inserimento, di estrazione del minimo, o di decremento della
 * priorità).
 * 
 * Si possono usare i colori dei nodi per registrare la scoperta e la visita
 * effettuata dei nodi.
 * 
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class PrimMSP<L> {

    List<GraphNode<L>> priorityQueue;
    /*
     * In particolare: si deve usare una coda con priorità che può semplicemente
     * essere realizzata con una List<GraphNode<L>> e si deve mantenere un
     * insieme dei nodi già visitati
     */

    /**
     * Crea un nuovo algoritmo e inizializza la coda di priorità con una coda
     * vuota.
     */
    public PrimMSP() {
        priorityQueue = new ArrayList<>();
    }

    /**
     * Utilizza l'algoritmo goloso di Prim per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. Dopo l'esecuzione del metodo nei nodi del grafo il campo
     * previous deve contenere un puntatore a un nodo in accordo all'albero di
     * copertura minimo calcolato, la cui radice è il nodo sorgente passato.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @param s
     *              il nodo del grafo g sorgente, cioè da cui parte il calcolo
     *              dell'albero di copertura minimo. Tale nodo sarà la radice
     *              dell'albero di copertura trovato
     * 
     * @throws NullPointerException se il grafo g o il nodo sorgente s sono nulli
     * @throws IllegalArgumentException se il nodo sorgente s non esiste in g
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public void computeMSP(Graph<L> g, GraphNode<L> s) {
        if(g == null || s == null) throw new NullPointerException("Grafo o nodo null");
        if(g.getNode(s) == null) throw new IllegalArgumentException("Il nodo non appartiene al grafo");
        if(g.isDirected()) throw new IllegalArgumentException("Grafo orientato");
        //Controllo se ci sono archi con pesi negativi o non pesati
        for(GraphEdge<L> edge: g.getEdges()) {
            if(!edge.hasWeight()) throw new IllegalArgumentException();
            if(edge.getWeight() < 0) throw new IllegalArgumentException();
        }
        //Imposto floatingpointDistance ,previous e colore di ogni nodo
        for(GraphNode<L> node: g.getNodes()) {
            if(!node.equals(s)) {
                node.setFloatingPointDistance(Double.POSITIVE_INFINITY);
            } else {
                //il nodo corrisponde ad s
                node.setFloatingPointDistance(0);
            }
            priorityQueue.add(node);
            node.setColor(GraphNode.COLOR_WHITE);
            node.setPrevious(null);
        }

        while(!priorityQueue.isEmpty()) {
            GraphNode<L> u = extractMin();
            u.setColor(GraphNode.COLOR_BLACK);
            for(GraphNode<L> v : g.getAdjacentNodesOf(u)) {
                if(priorityQueue.contains(v) && g.getEdge(u, v).getWeight() < v.getFloatingPointDistance()) {
                    v.setFloatingPointDistance(g.getEdge(u, v).getWeight());
                    v.setPrevious(u);
                    v.setColor(GraphNode.COLOR_BLACK);
                }
            }
        }

    }

    private GraphNode<L> extractMin() {
        GraphNode<L> min = priorityQueue.get(0);
        for(int i = 1; i < priorityQueue.size(); i++) {
            if(priorityQueue.get(i).getFloatingPointDistance() < min.getFloatingPointDistance())
                min = priorityQueue.get(i);
        }
        priorityQueue.remove(min);
        return min;
    }
}
