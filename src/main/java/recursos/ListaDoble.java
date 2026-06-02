package recursos;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaDoble implements Iterable<Object> {

    protected NodoDoble cabeza;
    protected NodoDoble cola;

    protected int tamanio;

    public ListaDoble() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public boolean isEmpty() {
        return tamanio == 0;
    }

    public int size() {
        return tamanio;
    }

    public void add(Object info) { //agrega al final//
        NodoDoble nuevo = new NodoDoble(info);

        if (isEmpty()) {
            cabeza = nuevo;
            cola = nuevo;

        } else {
            nuevo.setPrevNodo(cola);

            cola.setNextNodo(nuevo);
            cola = nuevo;
        }
        tamanio++;
    }

    public void addFirst(Object info) {
        NodoDoble nuevo = new NodoDoble(info);

        if (isEmpty()) {
            cabeza = nuevo;
            cola = nuevo;

        } else {
            nuevo.setNextNodo(cabeza);

            cabeza.setPrevNodo(nuevo);
            cabeza = nuevo;
        }
        tamanio++;
    }

    public void insert(int indice, Object info) {

        if (indice < 0 || indice > tamanio) {
            throw new IndexOutOfBoundsException();
        }
        if (indice == 0) {
            addFirst(info);
            return;
        }
        if (indice == tamanio) {
            add(info);
            return;
        }
        NodoDoble actual = getNodo(indice);

        NodoDoble anterior = actual.getPrevNodo();

        NodoDoble nuevo = new NodoDoble(info, anterior, actual);

        anterior.setNextNodo(nuevo);
        actual.setPrevNodo(nuevo);
        tamanio++;
    }

    public Object get(int indice) {
        verificarIndice(indice);
        return getNodo(indice).getNodoInfo();
    }

    public boolean remove(Object info) {
        NodoDoble actual = cabeza;

        while (actual != null) {
            if (actual.getNodoInfo().equals(info)) {
                desconectar(actual);
                tamanio--;
                return true;
            }
            actual = actual.getNextNodo();
        }
        return false;
    }

    public Object remove(int indice) {
        verificarIndice(indice);
        NodoDoble nodo = getNodo(indice);
        desconectar(nodo);
        tamanio--;
        return nodo.getNodoInfo();
    }

    public int indexOf(Object info) {
        NodoDoble actual = cabeza;
        int indice = 0;

        while (actual != null) {
            if (actual.getNodoInfo().equals(info)) {
                return indice;
            }
            actual = actual.getNextNodo();
            indice++;
        }
        return -1;
    }

    public void clear() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    private NodoDoble getNodo(int indice) {
        NodoDoble actual;

        if (indice < tamanio / 2) {
            actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.getNextNodo();
            }
        } else {
            actual = cola;
            for (int i = tamanio - 1; i > indice; i--) {
                actual = actual.getPrevNodo();
            }
        }
        return actual;
    }

    private void desconectar(NodoDoble nodo) {
        NodoDoble prev = nodo.getPrevNodo();
        NodoDoble next = nodo.getNextNodo();

        if (prev != null) {
            prev.setNextNodo(next);
        } else {
            cabeza = next;
        }
        if (next != null) {
            next.setPrevNodo(prev);
        } else {
            cola = prev;
        }
    }

    private void verificarIndice(int indice) {

        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Indice fuera de rango"
            );
        }
    }

    @Override
    public Iterator<Object> iterator() {

        return new Iterator<Object>() {

            private NodoDoble actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Object dato = actual.getNodoInfo();
                actual = actual.getNextNodo();
                return dato;
            }
        };
    }
}