package fr.sdis83.remocra.web.serialize.transformer;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.transformer.AbstractTransformer;

public class GeometryTransformer extends AbstractTransformer {

  @Override
  public void transform(Object object) {
    // Cette transformation est obligatoire pour les GeometryCollections.
    // Pour les autres types de geom, on obtient du GeoJSON

    // Pour sérialiser uniquement les géométries GeometryCollection en WKT,
    // dans le champ description :
    // if (object instanceof GeometryCollection) {
    // GeometryCollection coll = (GeometryCollection) object;
    // TypeContext geomTypeContext = getContext().writeOpenObject();
    // if (!geomTypeContext.isFirst())
    // getContext().writeComma();
    // geomTypeContext.setFirst(false);
    // getContext().writeName("description");
    // getContext().writeQuoted(coll.toText());
    // getContext().writeCloseObject();
    // }

    // Pour tout sérialiser en WKT dans un champ propre :
    if (object instanceof Geometry) {
      Geometry geom = (Geometry) object;
      getContext().writeQuoted(geom.toText());
    }

    // Ne pas oublier d'utiliser le Transformer :
    // .transform(new GeometryTransformer(), GeometryCollection.class)
    // ou
    // .transform(new GeometryTransformer(), Geometry.class)
  }
}
