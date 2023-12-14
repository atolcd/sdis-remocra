package fr.sdis83.remocra.usecase.parametre.srid;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.repository.ParametreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SridParametre {

  @Autowired ParametreRepository parametreRepository;

  public SridParametre() {}

  public String getSRID() {
    return parametreRepository.getByCle(GlobalConstants.CLE_SRID).getValeurParametre();
  }
}
