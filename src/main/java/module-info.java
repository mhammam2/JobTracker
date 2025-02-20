module com.example.jobtracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires jakarta.activation;
    requires jakarta.mail;
    requires com.microsoft.graph;
    requires okhttp3;
    requires com.azure.identity;
    requires com.microsoft.aad.msal4j;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires java.datatransfer;
    requires com.microsoft.graph.core;

    opens com.example.jobtracker to javafx.fxml;
    exports com.example.jobtracker;
    exports com.example.jobtracker.email;
    opens com.example.jobtracker.email to javafx.fxml;

}