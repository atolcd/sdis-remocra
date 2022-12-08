package fr.sdis83.remocra.usecase.importdocument;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DataCsvImport {
    private String document;
    private String numeroHydrant;

    public DataCsvImport() {

    }

    public DataCsvImport(String document, String numeroHydrant) {
        this.document = document;
        this.numeroHydrant = numeroHydrant;
    }

    public String getDocument() {
        return document;
    }

    public String getNumeroHydrant() {
        return numeroHydrant;
    }
}
