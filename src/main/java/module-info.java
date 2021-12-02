module com.uet.dbms {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires freetts;
    requires java.sql;
    requires sqlite.jdbc;

    opens com.uet.dbms to javafx.fxml;
    exports com.uet.dbms;
    exports com.uet.dbms.GUIControllers;
    opens com.uet.dbms.GUIControllers to javafx.fxml;
}