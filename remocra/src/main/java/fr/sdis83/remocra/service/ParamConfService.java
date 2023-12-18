package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.ParamConf;
import fr.sdis83.remocra.domain.remocra.ParamConf.ParamConfParam;
import java.lang.reflect.Constructor;
import javax.persistence.NoResultException;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

@Configuration
public class ParamConfService {

  private final Logger log = Logger.getLogger(getClass());

  @Bean
  public ParamConfService paramConfService() {
    return new ParamConfService();
  }

  public ParamConfService() {}

  protected Object getValue(ParamConfParam pcp, Object defaultValue) {
    Object result = getValue(pcp);
    return result == null ? defaultValue : result;
  }

  protected Object getValue(String cle, Object defaultValue) {
    Object result = getValue(cle);
    return result == null ? defaultValue : result;
  }

  @SuppressWarnings("all")
  protected Object getValue(ParamConfParam pcp) {
    return getValue(pcp.getCle());
  }

  @SuppressWarnings("all")
  protected Object getValue(String cle) {
    try {
      ParamConf pc = ParamConf.findParamConfsByCleEquals(cle).getSingleResult();
      Class cl = ParamConfParam.getClassFromCle(cle);
      Constructor constr = cl.getConstructor(String.class);
      return constr.newInstance(pc.getValeur());
    } catch (NoResultException e) {
      log.warn(
          "Paramètre "
              + cle
              + " non présent (NoResultException), restitution de la valeur par défaut");
    } catch (EmptyResultDataAccessException e) {
      log.warn(
          "Paramètre "
              + cle
              + " non présent (EmptyResultDataAccessException), restitution de la valeur par défaut");
    } catch (Exception e) {
      log.error("Paramètre " + cle + ", restitution de la valeur par défaut", e);
    }
    return null;
  }
}
