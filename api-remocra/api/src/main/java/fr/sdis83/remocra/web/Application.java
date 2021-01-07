package fr.sdis83.remocra.web;

import com.google.common.collect.ImmutableSet;
import fr.sdis83.remocra.resteasy.providers.InstantParamConverterProvider;
import fr.sdis83.remocra.resteasy.providers.JacksonJsonProvider;
import fr.sdis83.remocra.web.exceptions.ApplicationSecurityExceptionMapper;
import fr.sdis83.remocra.web.exceptions.ClientErrorExceptionMapper;
import fr.sdis83.remocra.web.exceptions.JsonMappingExceptionMapper;
import fr.sdis83.remocra.web.exceptions.UnhandledExceptionMapper;
import fr.sdis83.remocra.web.exceptions.ValidationExceptionMapper;
import fr.sdis83.remocra.web.exceptions.WebApplicationExceptionMapper;
import fr.sdis83.remocra.web.s.ApplicationEndpoint;
import fr.sdis83.remocra.web.s.OpenApiEndpoint;
import fr.sdis83.remocra.web.s.PeiEndpoint;
import fr.sdis83.remocra.web.s.ReferentielsCommunsEndpoint;
import fr.sdis83.remocra.web.s.ReferentielsDeciEndpoint;
import fr.sdis83.remocra.web.s.ReferentielsDeciPenaEndpoint;
import fr.sdis83.remocra.web.s.ReferentielsDeciPibiEndpoint;


import java.util.Set;

public class Application extends javax.ws.rs.core.Application {

public static final int READ_MAXLIMIT = 10000;
public static final String READ_DEFAULTLIMIT = "100";

@Override
public Set<Class<?> > getClasses() {
  return ImmutableSet.of(
    // ExceptionMapper
    JsonMappingExceptionMapper.class, ValidationExceptionMapper.class,
    ClientErrorExceptionMapper.class,
    WebApplicationExceptionMapper.class, ApplicationSecurityExceptionMapper.class,
    UnhandledExceptionMapper.class,
    // Providers
    JacksonJsonProvider.class, InstantParamConverterProvider.class,
    // Services
    OpenApiEndpoint.class,
    ApplicationEndpoint.class,
    PeiEndpoint.class,
    ReferentielsCommunsEndpoint.class,
    ReferentielsDeciEndpoint.class,
    ReferentielsDeciPenaEndpoint.class,
    ReferentielsDeciPibiEndpoint.class
  );
}
}
