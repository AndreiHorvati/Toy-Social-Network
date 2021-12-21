module com.example.toysocialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.toysocialnetworkgui to javafx.fxml;
    opens com.example.toysocialnetworkgui.model to java.base;
    exports com.example.toysocialnetworkgui;
    exports com.example.toysocialnetworkgui.model;
}