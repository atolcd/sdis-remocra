package fr.sdis83.remocra.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.sdis83.remocra.web.model.pei.PeiDiffModel;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PeiDiffModelSerializer extends StdSerializer<PeiDiffModel> {

  public PeiDiffModelSerializer() {
    this(null);
  }

  public PeiDiffModelSerializer(Class<PeiDiffModel> t) {
    super(t);
  }

  @Override
  public void serialize(PeiDiffModel pei, JsonGenerator jsonGenerator, SerializerProvider serializer) throws IOException {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("idHydrant", pei.getIdHydrant());
    jsonGenerator.writeStringField("numero", pei.getNumero());
    jsonGenerator.writeStringField("dateModification", dateFormat.format(pei.getDateModification()));
    jsonGenerator.writeStringField("operation", pei.getOperation());
    jsonGenerator.writeStringField("type", pei.getType());
    jsonGenerator.writeStringField("utilisateurModification", pei.getUtilisateurModification());
    jsonGenerator.writeStringField("utilisateurModificationOrganisme", pei.getUtilisateurModificationOrganisme());
    jsonGenerator.writeStringField("organismeModification", pei.getOrganismeModification());
    jsonGenerator.writeStringField("auteurModificationFlag", pei.getAuteurModificationFlag());
    jsonGenerator.writeEndObject();
  }
}
