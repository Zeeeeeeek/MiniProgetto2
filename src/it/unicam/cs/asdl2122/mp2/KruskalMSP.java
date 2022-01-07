package it.unicam.cs.asdl2122.mp2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * 
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 * 
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class KruskalMSP<L> {

    /*
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;


    List<GraphEdge<L>> graphEdges = new ArrayList<>();

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     * @throws NullPointerException se il grafo g è null
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        if(g == null) throw new NullPointerException("Grafo null");
        if(g.isDirected()) throw new IllegalArgumentException("Il grafo g NON deve essere orientato");
        //Svuoto il disjoinSet
        disjointSets.clear();
        //Creo un insieme singoletto per ogni nodo del grafo
        for(GraphNode<L> node : g.getNodes()) {
            disjointSets.makeSet(node);
        }

        //Controllo se gli archi non sono pesati oppure hanno peso negativo
        for(GraphEdge<L> edge : g.getEdges()) {
            if(!edge.hasWeight()) throw new IllegalArgumentException("Almeno un arco non è pesato");
            if(edge.getWeight() < 0) throw new IllegalArgumentException("Almeno un arco ha indice negativo");
            graphEdges.add(edge);
        }
        //Ordino gli archi in ordine crescente con un Quicksort
        quickSort(0, graphEdges.size()-1);

        //Avendo gli archi ordinati inizio l'algoritmo di Kruskal
        Set<GraphEdge<L>> result = new HashSet<>();

        //Uso un foreach dell'Arraylist dato che l'iterazione garantisce l'ordine di estrazione dei dati
        for(GraphEdge<L> edge : graphEdges) {
            if(!disjointSets.findSet(edge.getNode1()).equals(disjointSets.findSet(edge.getNode2()))) {
                result.add(edge);
                disjointSets.union(edge.getNode1(), edge.getNode2());
            }
        }
        return result;
    }

    private void quickSort(int begin, int end) {
        if(begin < end) {
            int pivot = partition(begin, end);
            quickSort(begin, pivot - 1);
            quickSort(pivot + 1, end);
        }
    }

    private int partition(int begin, int end) {
        //Arco pivot, scelgo l'arco più a destra
        GraphEdge<L> pivot = graphEdges.get(end);
        //Inizializzo un indice i
        int i = begin -1;

        for(int j = begin; j < end; j++) {
            //Se l'elemento in posizione j ha un peso <= al pivot incremento l'indice i di 1 e scambio l'elemento
            //In posizione i con quello in posizione j
            if(graphEdges.get(j).getWeight() <= pivot.getWeight()) {
                i++;
                //Scambio
                GraphEdge<L> temp = graphEdges.get(i);
                graphEdges.set(i, graphEdges.get(j));
                graphEdges.set(j, temp);
            }
        }
        //Al termine del ciclo l'elemento in posizione i+1 è il primo elemento più grande del pivot
        //lo scambio quindi con il pivot situato nella posizione end
        GraphEdge<L> temp = graphEdges.get(end);
        graphEdges.set(end, graphEdges.get(i+1));
        graphEdges.set(i+1, temp);

        //Ora restituisco l'indice del pivot
        return i+1;
    }
}
