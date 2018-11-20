package fr.sdis83.remocra.domain.utils;

import flexjson.transformer.AbstractTransformer;
import org.joda.time.Instant;

public class RemocraInstantTransformer extends AbstractTransformer {

  @Override
  public void transform(Object o) {
    if (o instanceof Instant) {
       Instant i = (Instant) o;
      getContext().writeQuoted(String.valueOf(i));
    }
  }
}
