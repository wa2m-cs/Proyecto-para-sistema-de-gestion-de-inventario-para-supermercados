module api.supermercado.apisupermercadoempleados {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;


    opens api.supermercado.apisupermercadoempleados.controller to javafx.fxml;
    opens api.supermercado.apisupermercadoempleados.model to com.google.gson;
    exports api.supermercado.apisupermercadoempleados;
}