module com.ecanteen.ecanteen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires log4j;
    requires jasperreports;
    requires commons.validator;
    requires org.controlsfx.controls;

    opens com.ecanteen.ecanteen to javafx.fxml;
    exports com.ecanteen.ecanteen;
    exports com.ecanteen.ecanteen.controllers;
    opens com.ecanteen.ecanteen.controllers to javafx.fxml;
    exports com.ecanteen.ecanteen.entities;
}