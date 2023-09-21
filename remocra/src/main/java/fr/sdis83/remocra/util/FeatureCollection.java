package fr.sdis83.remocra.util;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeatureCollection {

  private List<Feature> features = new ArrayList<Feature>();
  private JSONSerializer serializer;

  public FeatureCollection() {

    this.serializer = new JSONSerializer();
    serializer.exclude("*.class").include("features");
    serializer.transform(RemocraDateHourTransformer.getInstance(), Date.class);
    serializer.transform(new GeometryTransformer(), Geometry.class);
    serializer.transform(
        new AbstractTransformer() {
          // les geometry sont déjà "formatées" en geojson, il ne faut donc
          // pas
          // les reformatter en tant que String
          @Override
          public void transform(Object object) {
            getContext().write(object.toString());
          }
        },
        "features.geometry");
  }

  public String getType() {
    return "FeatureCollection";
  }

  public List<Feature> getFeatures() {
    return features;
  }

  public void addFeatures(Feature feature) {
    this.features.add(feature);
  }

  public String serialize() {
    return serializer.serialize(this);
  }

  public JSONSerializer getSerializer() {
    return this.serializer;
  }
}
