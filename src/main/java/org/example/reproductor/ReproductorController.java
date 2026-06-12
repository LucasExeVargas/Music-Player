package org.example.reproductor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.*;

import recursos.ListaDoble;
import recursos.ListaOrdenada;

public class ReproductorController {

    @FXML
    private TableView<Song> tablaCanciones;

    @FXML
    private TableColumn<Song, String> colNombre;

    @FXML
    private TableColumn<Song, String> colArtista;

    @FXML
    private TableColumn<Song, String> colAnio;

    @FXML
    private Label lblCancion;

    @FXML
    private Label lblArtista;

    @FXML
    private Label lblTiempoActual;

    @FXML
    private Label lblDuracion;

    @FXML
    private Slider sliderTiempo;

    @FXML
    private Button btnPlayPause;

    @FXML
    private CheckBox checkLoop;

    @FXML
    private CheckBox checkRandom;

    @FXML
    private Slider sliderVolumen;

    @FXML
    private TableColumn<Song, String> colAlbum;

    @FXML
    private TableColumn<Song, String> colDuracion;

    @FXML
    private ImageView imgCover;

    private ListaDoble canciones =
            new ListaDoble();

    private final ObservableList<Song> observableCanciones =
            FXCollections.observableArrayList();

    private MediaPlayer mediaPlayer;

    private int indiceActual = -1;

    private boolean pausado = false;

    private final ListaDoble randomPendientes =
            new ListaDoble();

    private String criterioActual = "nombre";

    @FXML
    public void ordenarPorNombre() {
        ordenar("nombre");
    }

    @FXML
    public void ordenarPorArtista() {
        ordenar("artista");
    }

    @FXML
    public void ordenarPorAnio() {
        ordenar("anio");
    }

    @FXML
    public void initialize() {

        tablaCanciones.setItems(observableCanciones);
        tablaCanciones.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colArtista.setCellValueFactory(
                new PropertyValueFactory<>("artista"));

        colAnio.setCellValueFactory(
                new PropertyValueFactory<>("anio"));
        colAlbum.setCellValueFactory(
                new PropertyValueFactory<>("album"));

        colDuracion.setCellValueFactory(
                new PropertyValueFactory<>("duracion"));

        sliderVolumen.valueProperty().addListener(
                (obs, oldVal, newVal) -> {

                    if (mediaPlayer != null) {

                        mediaPlayer.setVolume(
                                newVal.doubleValue() / 100.0
                        );
                    }
                }
        );

        // tablaCanciones.setItems(canciones);

        tablaCanciones.setOnMouseClicked(e -> {

            if (e.getClickCount() == 2) {

                Song song =
                        tablaCanciones.getSelectionModel()
                                .getSelectedItem();

                if (song != null) {

                    indiceActual =
                            canciones.indexOf(song);

                    reproducir(song);
                }
            }
        });
    }

    @FXML
    public void agregarCanciones() {

        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "MP3",
                        "*.mp3"
                )
        );

        List<File> archivos = chooser.showOpenMultipleDialog(null);

        if (archivos == null) {
            return;
        }
        for (File file : archivos) {
            try {

                AudioFile audioFile =
                        AudioFileIO.read(file);

                String titulo =
                        audioFile.getTag()
                                .getFirst(FieldKey.TITLE);

                String artista =
                        audioFile.getTag()
                                .getFirst(FieldKey.ARTIST);

                String album =
                        audioFile.getTag()
                                .getFirst(FieldKey.ALBUM);

                String anio =
                        audioFile.getTag()
                                .getFirst(FieldKey.YEAR);

                int segundos =
                        audioFile.getAudioHeader()
                                .getTrackLength();

                String duracion =
                        String.format(
                                "%02d:%02d",
                                segundos / 60,
                                segundos % 60
                        );

                if (titulo.isEmpty()) {
                    titulo = file.getName()
                            .replace(".mp3", "");
                }

                if (artista.isEmpty()) {
                    artista = "Desconocido";
                }

                if (album.isEmpty()) {
                    album = "Desconocido";
                }

                if (anio.isEmpty()) {
                    anio = "----";
                }

                Image portada;

                try {

                    if (audioFile.getTag().getFirstArtwork() != null) {

                        byte[] imagenBytes =
                                audioFile.getTag()
                                        .getFirstArtwork()
                                        .getBinaryData();

                        portada = new Image(
                                new java.io.ByteArrayInputStream(
                                        imagenBytes
                                )
                        );

                    } else {

                        portada = new Image(
                                getClass().getResourceAsStream(
                                        "/default_cover.png"
                                )
                        );
                    }

                } catch (Exception e) {

                    portada = new Image(
                            getClass().getResourceAsStream(
                                    "/default_cover.png"
                            )
                    );
                }

                canciones.add(
                        new Song(
                                titulo,
                                artista,
                                album,
                                anio,
                                duracion,
                                file.getAbsolutePath(),
                                portada
                        )
                );
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        randomPendientes.clear();
        //ordenar(criterioActual);
        observableCanciones.clear();

        for (Object obj : canciones) {
            observableCanciones.add((Song) obj);
        }
    }

    @FXML
    public void eliminarCancion() {

        Song song =
                tablaCanciones.getSelectionModel()
                        .getSelectedItem();

        if (song != null) {
            canciones.remove(song);
            observableCanciones.remove(song);
        }
        randomPendientes.clear();
        tablaCanciones.refresh();
    }

    @FXML
    public void togglePlayPause() {

        if (mediaPlayer == null) {

            if (!canciones.isEmpty()) {

                indiceActual = 0;

                reproducir((Song) canciones.get(0));
            }

            return;
        }

        if (pausado) {

            mediaPlayer.play();

            btnPlayPause.setText("⏸");

            pausado = false;

        } else {

            mediaPlayer.pause();

            btnPlayPause.setText("▶");

            pausado = true;
        }
    }

    private void reproducir(Song song) {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media =
                new Media(
                        new File(song.getRuta())
                                .toURI()
                                .toString()
                );

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.play();

        pausado = false;

        btnPlayPause.setText("⏸");

        lblCancion.setText(song.getNombre());

        lblArtista.setText(song.getArtista());

        imgCover.setImage(song.getPortada());

        mediaPlayer.currentTimeProperty().addListener(
                (obs, oldTime, newTime) -> {

                    sliderTiempo.setValue(newTime.toSeconds());

                    lblTiempoActual.setText(formatTime(newTime));
                }
        );

        mediaPlayer.setOnReady(() -> {

            Duration total = mediaPlayer.getTotalDuration();

            sliderTiempo.setMax(total.toSeconds());

            lblDuracion.setText(formatTime(total));
        });

        mediaPlayer.setVolume(
                sliderVolumen.getValue() / 100.0
        );

        sliderVolumen.setOnScroll(event -> {

            double delta = event.getDeltaY();

            sliderVolumen.setValue(sliderVolumen.getValue() + delta / 10);
        });

        sliderTiempo.setOnMousePressed(event -> {

            double porcentaje = event.getX() / sliderTiempo.getWidth();

            double tiempo = porcentaje * sliderTiempo.getMax();

            sliderTiempo.setValue(tiempo);

            mediaPlayer.seek(Duration.seconds(tiempo));
        });

        sliderTiempo.setOnMouseDragged(event -> {

            double porcentaje = event.getX() / sliderTiempo.getWidth();

            double tiempo = porcentaje * sliderTiempo.getMax();

            sliderTiempo.setValue(tiempo);

            mediaPlayer.seek(Duration.seconds(tiempo));
        });

        mediaPlayer.setOnEndOfMedia(this::manejarFinCancion);
    }

    private void manejarFinCancion() {

        if (checkRandom.isSelected()) {
            reproducirRandom();
            return;
        }
        indiceActual++;
        if (indiceActual >= canciones.size()) {
            if (checkLoop.isSelected()) {
                indiceActual = 0;
            } else {
                btnPlayPause.setText("▶");
                return;
            }
        }
        reproducir((Song) canciones.get(indiceActual));
    }

    @FXML
    public void siguiente() {

        if (canciones.isEmpty()) {
            return;
        }
        if (checkRandom.isSelected()) {
            reproducirRandom();
            return;
        }
        indiceActual++;
        if (indiceActual >= canciones.size()) {
            if (checkLoop.isSelected()) {
                indiceActual = 0;
            } else {
                indiceActual = canciones.size() - 1;
                return;
            }
        }
        reproducir((Song) canciones.get(indiceActual));
    }

    @FXML
    public void anterior() {

        if (canciones.isEmpty()) {
            return;
        }
        indiceActual--;
        if (indiceActual < 0) {
            if (checkLoop.isSelected()) {
                indiceActual = canciones.size() - 1;
            } else {
                indiceActual = 0;
                return;
            }
        }
        reproducir((Song) canciones.get(indiceActual));
    }


    private void reproducirRandom() {

        if (randomPendientes.isEmpty()) {
            int total = canciones.size();
            int[] indices = new int[total];
            for (int i = 0; i < total; i++) {
                indices[i] = i;
            }
            Random random = new Random();
            for (int i = total - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int temp = indices[i];
                indices[i] = indices[j];
                indices[j] = temp;
            }
            for (int indice : indices) {
                randomPendientes.add(indice);
            }
        }
        indiceActual = (Integer) randomPendientes.remove(0);
        reproducir((Song) canciones.get(indiceActual));
    }

    private String formatTime(Duration duration) {

        int minutos = (int) duration.toMinutes();

        int segundos = (int) duration.toSeconds() % 60;

        return String.format("%02d:%02d", minutos, segundos);
    }

    private void ordenar(String criterio) {
        this.criterioActual = criterio;
        ListaOrdenada ordenada = new ListaOrdenada();

        for (Object obj : canciones) { // paso los elementos de la lista a la nueva lista ordenada
            Song song = (Song) obj;
            if (criterio.equalsIgnoreCase("artista")) {
                ordenada.addOrderedPorArtista(song);
            } else if (criterio.equalsIgnoreCase("anio")) {
                ordenada.addOrderedPorAnio(song);
            } else {
                ordenada.addOrderedPorNombre(song);
            }
        }
        this.canciones = ordenada;  //actualizo la lista actual, a la lista ordenada
        observableCanciones.clear();
        for (Object obj : canciones) {
            observableCanciones.add((Song) obj);
        }
        // Si hay una canción reproduciéndose, recalculamos su índice en la nueva lista
        Song seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            indiceActual = canciones.indexOf(seleccionada);
        }
    }
    @FXML
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        pausado = false;
        btnPlayPause.setText("▶");
        indiceActual = -1; // Reseteamos el índice

        lblCancion.setText("No hay canción");
        lblArtista.setText("...");
        imgCover.setImage(null);
        sliderTiempo.setValue(0);
        lblTiempoActual.setText("00:00");

        tablaCanciones.refresh();
    }
}