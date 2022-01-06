package it.unicam.cs.asdl2122.mp2;

import java.util.HashSet;
import java.util.Set;

//TODO completare gli import necessari

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 * 
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 *
 * @param <L>
 *                il tipo delle etichette dei nodi del grafo
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     * 
     * @param g
     *              un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     * @throws NullPointerException
     *                                      se il grafo passato è nullo
     * @throws IllegalArgumentException
     *                                      se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        if(g == null) throw new NullPointerException("Grafo null");
        if(g.isDirected()) throw new IllegalArgumentException("Grafo orientato NON ammesso");
        //Pulisco il disjointset ad ogni chiamata
        f.clear();

        Set<Set<GraphNode<L>>> result = new HashSet<>();
        //Creo un insieme singoletto per tutti i nodi del grafo
        for(GraphNode<L> node: g.getNodes()) {
            f.makeSet(node);
        }
        //Per ogni arco tra un nodo x e un nodo y faccio l'unione, i findset di x ed y sono controllati dal metodo union
        for(GraphEdge<L> edge : g.getEdges()) {
            f.union(edge.getNode1(), edge.getNode2());
        }
        //Per ogni rappresentante del DisjoinSet aggiungo al risultato il set dei suoi nodi
        for(GraphNode<L> rapp : f.getCurrentRepresentatives()) {
            result.add(f.getCurrentElementsOfSetContaining(rapp));
        }
        return result;
    }
}
