package fr.sdis83.remocra.service;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

import fr.sdis83.remocra.domain.pdi.Telechargement;
import fr.sdis83.remocra.domain.remocra.Document;

@Configuration
public class TelechargementService {

    public String getPdiFilePathFromCode(String code) {
        try {
            Telechargement t = Telechargement.findTelechargementsByCodeEquals(code).getSingleResult();
            if (t == null) {
                return null;
            }
            return t.getRessource();
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getRemocraFilePathFromCode(String code) {
        try {
            Document d = Document.findDocumentsByCodeEquals(code).getSingleResult();
            if (d == null) {
                return null;
            }
            return d.getRepertoire() + File.separator + d.getFichier();
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}