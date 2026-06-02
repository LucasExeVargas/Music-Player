package recursos;

import org.example.reproductor.Song;

public class ListaOrdenada extends ListaDoble {

    public ListaOrdenada() {
        super();
    }

    public void addOrderedPorNombre(Song song) {
        addOrdered(song, "nombre");
    }

    public void addOrderedPorArtista(Song song) {
        addOrdered(song, "artista");
    }

    public void addOrderedPorAnio(Song song) {
        addOrdered(song, "anio");
    }
    private void addOrdered(Song song, String criterio) {
        if (isEmpty()) {
            add(song);
            return;
        }

        NodoDoble actual = cabeza;
        int indice = 0;

        while (actual != null) {
            Song songActual = (Song) actual.getNodoInfo();
            if (comparar(song, songActual, criterio) < 0) {
                insert(indice, song);
                return;
            }

            actual = actual.getNextNodo();
            indice++;
        }
        add(song);
    }

    private int comparar(Song a, Song b, String criterio) {
        switch (criterio.toLowerCase()) {
            case "artista":
                int cmpArtista = valorSeguro(a.getArtista()).compareToIgnoreCase(valorSeguro(b.getArtista()));
                if (cmpArtista != 0) return cmpArtista;
                return normalizarNombre(a).compareToIgnoreCase(normalizarNombre(b)); //si el artista es el mismo decide segun el nombre
            case "anio":
                int cmpAnio = valorSeguro(a.getAnio()).compareToIgnoreCase(valorSeguro(b.getAnio()));
                if (cmpAnio != 0) return cmpAnio;
                return normalizarNombre(a).compareToIgnoreCase(normalizarNombre(b));
            case "nombre":
            default:
                return normalizarNombre(a).compareToIgnoreCase(normalizarNombre(b));
        }
    }

    private String normalizarNombre(Song song) {

        String nombre =
                valorSeguro(song.getNombre());

        if (nombre.equalsIgnoreCase("Desconocido")
                || nombre.equalsIgnoreCase("----")
                || nombre.isBlank()) {

            String ruta = song.getRuta();

            int slash =
                    Math.max(
                            ruta.lastIndexOf("/"),
                            ruta.lastIndexOf("\\")
                    );

            nombre = ruta.substring(slash + 1);

            nombre = nombre.replace(".mp3", "");
        }

        return nombre;
    }

    private String valorSeguro(String valor) {

        if (valor == null
                || valor.isBlank()) {

            return "";
        }

        return valor;
    }
}