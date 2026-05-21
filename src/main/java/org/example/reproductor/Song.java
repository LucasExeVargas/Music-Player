package org.example.reproductor;

import javafx.scene.image.Image;

public class Song {

    private String nombre;
    private String artista;
    private String album;
    private String anio;
    private String duracion;
    private String ruta;

    private Image portada;

    public Song(String nombre,
                String artista,
                String album,
                String anio,
                String duracion,
                String ruta,
                Image portada) {

        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.anio = anio;
        this.duracion = duracion;
        this.ruta = ruta;
        this.portada = portada;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getAnio() {
        return anio;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getRuta() {
        return ruta;
    }

    public Image getPortada() {
        return portada;
    }
}