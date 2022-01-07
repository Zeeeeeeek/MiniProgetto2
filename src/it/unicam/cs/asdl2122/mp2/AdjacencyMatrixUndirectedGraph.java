/**
 *
 */
package it.unicam.cs.asdl2122.mp2;

import java.util.*;

// ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 *
 * I nodi sono indicizzati da 0 a nodeCoount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 *
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 *
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 *
 * Questa classe supporta i metodi di cancellazione di nodi e archi e
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 *
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 *
 *
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    /*
     * Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
     * matrice di adiacenza
     */
    protected Map<GraphNode<L>, Integer> nodesIndex;

    /*
     * Matrice di adiacenza, gli elementi sono null o oggetti della classe
     * GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
     * dimensione gradualmente ad ogni inserimento di un nuovo nodo e di
     * ridimensionarsi se un nodo viene cancellato.
     */
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<ArrayList<GraphEdge<L>>>();
        this.nodesIndex = new HashMap<GraphNode<L>, Integer>();
    }

    @Override
    public int nodeCount() {
        return nodesIndex.size();
    }

    @Override
    public int edgeCount() {
        /* Creo un hashset che conterrà gli archi, non essendo un grafo orientato un stesso arco tra due nodi x ed y
         viene inserito nella matrice tra x ed y e tra y ed x. Per evitare che uno stesso arco venga contato due volte
         li inserisco nell'hashset che non ammette doppioni, di conseguenza avrò il numero giusto di archi.
         */
        Set<GraphEdge<L>> counter = new HashSet<>();
        for (ArrayList<GraphEdge<L>> row : matrix) {
            for (GraphEdge<L> edge : row) {
                if (edge != null) counter.add(edge);
            }
        }
        return counter.size();
    }

    @Override
    public void clear() {
        matrix.clear();
        nodesIndex.clear();
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il nodo da aggiungere è null");

        if (nodesIndex.containsKey(node)) return false;
        //Il nodo non è presente quindi lo aggiungo in nodesIndex con l'indice aggiornato
        int index = nodesIndex.size();
        nodesIndex.put(node, index);
        //Aggiungo il nodo alla matrice, inizio aggiungendo l'ultima riga
        matrix.add(new ArrayList<GraphEdge<L>>());
        for (int i = 0; i < nodesIndex.size(); i++) {
            matrix.get(index).add(null);
        }
        //Aggiungo una colonna alle righe precedenti
        for (int i = 0; i < index; i++) {
            matrix.get(i).add(null);
        }
        return true;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(L label) {
        if (label == null) throw new NullPointerException("Label di addNode null");
        //Eventuali altre eccezioni saranno lanciate dall'addNode(node)
        return addNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il nodo del removeNode è null");
        if (!nodesIndex.containsKey(node)) throw new IllegalArgumentException("Il nodo del removeNode non fa " +
                "parte del grafo");

        //Prendo l'indice del nodo
        int index = nodesIndex.get(node);
        //rimuovo il nodo
        nodesIndex.remove(node);
        //Aggiorno gli indici
        for (Map.Entry<GraphNode<L>, Integer> entry : nodesIndex.entrySet()) {
            if (entry.getValue() > index) {
                nodesIndex.put(entry.getKey(), entry.getValue() - 1);
            }
        }
        //Rimuovo la riga del nodo
        matrix.remove(index);
        //Rimuovo la colonna del nodo ad ogni riga rimanente
        for (ArrayList<GraphEdge<L>> row : matrix) {
            row.remove(index);
        }
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(L label) {
        if (label == null) throw new NullPointerException("Il laber di removeNode è null");
        //Altre eccezioni vengono lanciate dal removeNode(node)
        removeNode(new GraphNode<>(label));
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(int i) {
        if (i < 0 || i >= nodeCount())
            throw new IndexOutOfBoundsException("Indice di remove node fuori dall'intervallo" +
                    "[0, this.nodeCount()");
        //Altre eccezioni vengono lanciate dal removeNode(node)
        removeNode(getNode(i));
    }

    @Override
    public GraphNode<L> getNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il nodo del getNode è null");
        //Scorro ogni elemento di nodesIndex e se trovo il nodo ricercato allora lo restituisco
        for (GraphNode<L> n : nodesIndex.keySet()) {
            if (node.equals(n)) return n;
        }
        //Il nodo non c'è allora restituisco null
        return null;
    }

    @Override
    public GraphNode<L> getNode(L label) {
        if (label == null) throw new NullPointerException("Etichetta di getNode null");
        return getNode(new GraphNode<L>(label));
    }

    @Override
    public GraphNode<L> getNode(int i) {
        if (i < 0 || i >= nodeCount())
            throw new IndexOutOfBoundsException("Indice di remove node fuori dall'intervallo" +
                    "[0, this.nodeCount()");

        for (Map.Entry<GraphNode<L>, Integer> entry : nodesIndex.entrySet()) {
            if (entry.getValue() == i) return entry.getKey();
        }
        return null;
    }

    @Override
    public int getNodeIndexOf(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Node del getNodeIndexOf null");
        if (!nodesIndex.containsKey(node)) throw new IllegalArgumentException("Il nodo non esiste in questo grafo");
        return nodesIndex.get(node);
    }

    @Override
    public int getNodeIndexOf(L label) {
        if (label == null) throw new NullPointerException("label di getNodeIndexOf null");
        GraphNode<L> node = new GraphNode<>(label);
        if (!nodesIndex.containsKey(node)) throw new IllegalArgumentException("Il nodo non esiste in questo grafo");
        return nodesIndex.get(node);
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
        return nodesIndex.keySet();
    }

    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("Arco del addEdge null");
        if (!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2())) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco in addEdge non esiste nel grafo");
        if (isDirected() != edge.isDirected()) throw new IllegalArgumentException("l'arco è orientato e questo grafo " +
                "non è orientato o viceversa");
        //In un grafo NON orientato i cappi, ossia archi che escono ed entrano nello stesso nodo NON sono ammessi,
        //quindi restituisco false.
        //if(edge.getNode1().equals(edge.getNode2())) return false;

        int indexNode1 = nodesIndex.get(edge.getNode1());
        int indexNode2 = nodesIndex.get(edge.getNode2());
        //Controllo se l'arco già esiste
        if (edge.equals(matrix.get(indexNode1).get(indexNode2)) || edge.equals(matrix.get(indexNode2).get(indexNode1))) {
            return false;
        }
        //Sono sicuro non esista quindi lo aggiungo nella matrice sia tra node1 e node2 che tra node2 e node1
        matrix.get(indexNode1).set(indexNode2, edge);
        matrix.get(indexNode2).set(indexNode1, edge);

        return true;
    }

    @Override
    public boolean addEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if (node1 == null || node2 == null) throw new NullPointerException("Uno dei due nodi dell'addEdge è null");
        if (!nodesIndex.containsKey(node1) || !nodesIndex.containsKey(node2)) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco in addEdge non esiste nel grafo");
        return addEdge(new GraphEdge<>(node1, node2, isDirected()));
    }

    @Override
    public boolean addWeightedEdge(GraphNode<L> node1, GraphNode<L> node2,
                                   double weight) {
        if (node1 == null || node2 == null) throw new NullPointerException("Uno dei due nodi dell'addEdge è null");
        if (!nodesIndex.containsKey(node1) || !nodesIndex.containsKey(node2)) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco in addEdge non esiste nel grafo");

        return addEdge(new GraphEdge<>(node1, node2, isDirected(), weight));
    }

    @Override
    public boolean addEdge(L label1, L label2) {
        if (label1 == null || label2 == null)
            throw new NullPointerException("Almeno una delle due etichette dell'addEdge" +
                    " è null");
        GraphNode<L> node1 = new GraphNode<>(label1);
        GraphNode<L> node2 = new GraphNode<>(label2);
        if (!nodesIndex.containsKey(node1) || !nodesIndex.containsKey(node2)) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco in addEdge non esiste nel grafo");
        return addEdge(new GraphEdge<>(node1, node2, isDirected()));
    }

    @Override
    public boolean addWeightedEdge(L label1, L label2, double weight) {
        if (label1 == null || label2 == null)
            throw new NullPointerException("Almeno una delle due etichette dell'addEdge" +
                    " è null");
        GraphNode<L> node1 = new GraphNode<>(label1);
        GraphNode<L> node2 = new GraphNode<>(label2);
        if (!nodesIndex.containsKey(node1) || !nodesIndex.containsKey(node2)) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco in addEdge non esiste nel grafo");
        return addEdge(new GraphEdge<>(node1, node2, isDirected(), weight));
    }

    @Override
    public boolean addEdge(int i, int j) {
        //Eventuali eccezioni vengono lanciate da getNode.
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);
        return addEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public boolean addWeightedEdge(int i, int j, double weight) {
        //Eventuali eccezioni vengono lanciate da getNode.
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);
        return addEdge(new GraphEdge<L>(node1, node2, isDirected(), weight));
    }

    @Override
    public void removeEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("L'arco di removeEdge è null");
        if (!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2())) throw new
                IllegalArgumentException("Almeno uno dei due nodi dell'arco non appartengono al grafo");
        if (getEdge(edge) == null) throw new IllegalArgumentException("l'arco non esiste nel grafo");
        int index1 = getNodeIndexOf(edge.getNode1());
        int index2 = getNodeIndexOf(edge.getNode2());
        matrix.get(index1).set(index2, null);
        matrix.get(index2).set(index1, null);
    }

    @Override
    public void removeEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if (node1 == null || node2 == null) throw new NullPointerException("Almeno uno dei nodi di removeEdge è null");
        //Le altre eccezioni vengono controllate dal primo removeEdge
        removeEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public void removeEdge(L label1, L label2) {
        if (label1 == null || label2 == null)
            throw new NullPointerException("Almeno una delle etichette di removeEdge è null");
        GraphNode<L> node1 = new GraphNode<>(label1);
        GraphNode<L> node2 = new GraphNode<>(label2);

        removeEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public void removeEdge(int i, int j) {
        //Eventuali eccezioni vengono lanciate da getNode.
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);

        removeEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public GraphEdge<L> getEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("Edge di getEdge null");
        if(!nodesIndex.containsKey(edge.getNode1()) || !nodesIndex.containsKey(edge.getNode2())) throw new
                IllegalArgumentException("Almeno uno dei due nodi non esiste nel grafo");
        int index1 = getNodeIndexOf(edge.getNode1());
        int index2 = getNodeIndexOf(edge.getNode2());
        //Se il nodo è contenuto nella matrice
        if (edge.equals(matrix.get(index1).get(index2)) || edge.equals(matrix.get(index2).get(index1))) {
            //Se l'arco è uguale ad uno degli archi tra i due nodi allora ne restituisco uno qualunqe poiché da
            //come è stato implementato l'addedge uno stesso arco viene inserito nella matrice nelle posizioni
            //(index1, index2) e (index2,index1)
            return matrix.get(index1).get(index2);
        }
        return null;
    }

    @Override
    public GraphEdge<L> getEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if (node1 == null || node2 == null) throw new NullPointerException("ALmeno uno dei nodi di getEdge è null");
        //Altre eccezioni controllate le getEdge(edge)
        return getEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public GraphEdge<L> getEdge(L label1, L label2) {
        if (label1 == null || label2 == null) throw new NullPointerException("Almeno una etichetta di getEdge è null");
        GraphNode<L> node1 = new GraphNode<L>(label1);
        GraphNode<L> node2 = new GraphNode<L>(label2);
        //Altre eccezioni sono controllate da getEdge(node1,node2)
        return getEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public GraphEdge<L> getEdge(int i, int j) {
        //Eventuali eccezioni vengono lanciate da getNode.
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);
        return getEdge(new GraphEdge<L>(node1, node2, isDirected()));
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il nodo di getAdjacentNodesOf è null");
        if (!nodesIndex.containsKey(node)) throw new IllegalArgumentException("Il nodo non esiste");

        Set<GraphNode<L>> result = new HashSet<GraphNode<L>>();
        int nodeIndex = getNodeIndexOf(node);
        for (int i = 0; i < matrix.size(); i++) {
            if (matrix.get(nodeIndex).get(i) != null) {
                if (nodeIndex == getNodeIndexOf(matrix.get(nodeIndex).get(i).getNode1())) {
                    result.add(matrix.get(nodeIndex).get(i).getNode2());
                } else {
                    result.add(matrix.get(nodeIndex).get(i).getNode1());
                }
            }
        }

        return result;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(L label) {
        if (label == null) throw new NullPointerException("Etichetta null");
        //Altre eccezioni controllate da getAdjacentNodesOf(GraphNode<L> node)
        return getAdjacentNodesOf(new GraphNode<L>(label));
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(int i) {
        if (i < 0 || i >= nodeCount()) throw new IndexOutOfBoundsException("Indice fuori dai limiti " +
                "dell'intervallo [0, this.nodeCount() - 1]");
        return getAdjacentNodesOf(getNode(i));
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("nodo null");
        if (!nodesIndex.containsKey(node)) throw new IllegalArgumentException("Nodo non presente");

        Set<GraphEdge<L>> result = new HashSet<GraphEdge<L>>();

        int nodeIndex = getNodeIndexOf(node);
        //Aggiungo gli archi non nulli presenti sulla riga con indice del nodo
        for (int i = 0; i < matrix.size(); i++) {
            if (matrix.get(nodeIndex).get(i) != null) result.add(matrix.get(nodeIndex).get(i));
        }
        return result;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(L label) {
        if (label == null) throw new NullPointerException("etichetta nulla");
        //Altre eccezioni coperte dal getEdgesOf(node)
        return getEdgesOf(new GraphNode<L>(label));
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(int i) {
        if (i < 0 || i >= nodeCount()) throw new IndexOutOfBoundsException("Indice minore di 0 o maggiore o uguale " +
                "a nodeCount()");
        //Altre eccezioni coperte dal getEdgesOf(node)
        return getEdgesOf(getNode(i));
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> result = new HashSet<GraphEdge<L>>();

        for (ArrayList<GraphEdge<L>> row : matrix) {
            for (GraphEdge<L> g : row) {
                if (g != null) {
                    result.add(g);
                }
            }
        }
        return result;
    }
}
