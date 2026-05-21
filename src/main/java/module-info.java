module org.example.reproductor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires jaudiotagger;
    requires java.desktop;


    opens org.example.reproductor to javafx.fxml;
    exports org.example.reproductor;
}