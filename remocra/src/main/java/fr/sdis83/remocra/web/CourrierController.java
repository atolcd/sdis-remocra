package fr.sdis83.remocra.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.service.TelechargementService;
import fr.sdis83.remocra.util.DocumentUtil;

@RequestMapping("/courrier")
@Controller
public class CourrierController {

    @Autowired
    TelechargementService telechargementsService;

    @RequestMapping(value = "/{code}")
    public void downloadCourrierDocument(@PathVariable("code") String code, HttpServletResponse response)
            throws IOException {
        String path = telechargementsService.getCourrierFilePathFromCode(code);
        DocumentUtil.getInstance().downloadDocument(path, code, response);
        // Accusé de réception
        telechargementsService.setCourrierAccuseFromCode(code);
    }

    @RequestMapping(value = "/show/{code}")
    public void showCourrierDocument(@PathVariable("code") String code, HttpServletResponse response)
            throws IOException {
        String path = telechargementsService.getCourrierFilePathFromCode(code);
        DocumentUtil.getInstance().showDocument(path, code, response);
        // Accusé de réception
        telechargementsService.setCourrierAccuseFromCode(code);
    }
}
