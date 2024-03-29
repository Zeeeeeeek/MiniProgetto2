package it.unicam.cs.asdl2122.mp2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 * 
 * @author Luca Tesei (template) Enrico Ulissi enrico.ulissi@studenti.unicam.it(implementazione)
 *
 * @param <E>
 *                il tipo degli elementi degli insiemi disgiunti
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;
    
    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         * 
         * @param item
         *                 l'elemento conservato in questo nodo
         * 
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
        currentElements = new HashMap<>();
    }

    @Override
    public boolean isPresent(E e) {
        if(e == null) throw new NullPointerException("Elemento di isPresent null");
        //Un elemento è presente se l'hashmap contiene la key E
        return currentElements.containsKey(e);
    }

    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */
    @Override
    public void makeSet(E e) {
        if(e == null) throw new NullPointerException("elemento di makeset null");
        if(currentElements.containsKey(e)) throw new IllegalArgumentException("Elemento di makeset già presente");
        //Dato che il metodo containsKey e put dell'hashmap hanno una complessità O(1) allora viene garantita la
        //complessità richiesta
        currentElements.put(e, new Node<E>(e));
    }

    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */
    @Override
    public E findSet(E e) {
        if(e == null) throw new NullPointerException("Elemento di findSet null");
        //Se l'elemento non è in nessun nodo presente
        if(!currentElements.containsKey(e)) return null;
        //Sono sicuro ci sia, dunque salvo il nodo in una variabile locale e aggiorno il parent di ogni nodo ricorsivamente
        //fino  quando non trovo il nodo che ha come parent se stesso. Questo indicia che è la root dell'albero
        Node<E> node = currentElements.get(e);
        if(node != node.parent) {
            node.parent = currentElements.get(findSet(node.parent.item));
        }
        return node.parent.item;
    }

    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */
    @Override
    public void union(E e1, E e2) {
        if(e1 == null || e2 == null) throw new NullPointerException("E1 o E2 null");
        if(!isPresent(e1) || !isPresent(e2)) throw new IllegalArgumentException("e1 o e2 non presenti negli insiemi");
        //Risalgo alle radici tramite il findSet di ognuno, eseguo di conseguenza la "compressione del cammino"
        Node<E> node1 = currentElements.get(findSet(e1));
        Node<E> node2 = currentElements.get(findSet(e2));
        //Se le radici sono lo stesso nodo, ovvero fanno parte dello stesso insieme il metodo si ferma
        if(node1.equals(node2)) return;
        //Sono sicuro non facciano parte dello stesso insieme, decido quindi quale delle due sarà la radice del nuovo
        //albero
        if(node1.rank > node2.rank) {
            //Il nodo1 diventa la radice del nuovo albero
            node2.parent = node1;
            return;
        }
        node1.parent = node2;
        //Qualora i rank dei nodi fosse uguali allora il secondo(e2) deve aumentare
        if(node1.rank == node2.rank) node2.rank++;
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
        Set<E> result = new HashSet<>();
        for(Node<E> node : currentElements.values()) {
            //In questo metodo controllo solo se il nodo ha se stesso come parent. Non mi preoccupo di nodi che potrebbero
            //avere i parent non aggiornati poiché mi interessano solo le root degli alberi che so di certo essere aggiornate
            //ogni qualvolta venga eseguito il metodo union
            if(node.parent.equals(node)) result.add(node.item);
        }
        return result;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        if(e == null) throw new NullPointerException("element null");
        if(!currentElements.containsKey(e)) throw new IllegalArgumentException("Elemento non contenuto");
        Set<E> result = new HashSet<>();
        for(Node<E> node: currentElements.values()) {
            //Dopo un'unione di due insiemi potrebbe capitare che dei nodi non abbiano aggiornato i parent, poiché durante
            //l'union vengono cambiati solo i parent delle root. Di conseguenza eseguo il findSet di ogni nodo e se il
            //risultato è uguale secondo l'equals della classe E allora lo aggiungo al risultato
            if(findSet(node.item).equals(findSet(e))) result.add(node.item);
        }
        return result;
    }

    @Override
    public void clear() {
        currentElements.clear();
    }

}
