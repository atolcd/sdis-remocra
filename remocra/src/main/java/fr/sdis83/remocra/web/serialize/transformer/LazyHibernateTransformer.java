package fr.sdis83.remocra.web.serialize.transformer;

import flexjson.BeanAnalyzer;
import flexjson.BeanProperty;
import flexjson.ChainedSet;
import flexjson.JSONContext;
import flexjson.JSONException;
import flexjson.Path;
import flexjson.TypeContext;
import flexjson.transformer.ObjectTransformer;
import flexjson.transformer.TransformerWrapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hibernate.Hibernate;

/**
 * Transforme une propriété Lazy loadée ET avec un héritage Parent -(lazy)> AbstractChild | |
 * AChildClass AnotherChildClass
 *
 * @author bpa
 */
public class LazyHibernateTransformer extends ObjectTransformer {

  @SuppressWarnings("rawtypes")
  protected Class resolveClass(Object obj) {
    return findBeanClass(obj);
  }

  public Class<?> findBeanClass(Object object) {
    /* START FIX */
    // La classe récupérée par introspection (HibernateTransformer.class)
    // n'est que la classe abstraite.
    return (Class<?>) Hibernate.getClass(object);
    /* END FIX */
  }

  private Object getRealInstance(Object object) {
    try {
      Method method = object.getClass().getMethod("getHibernateLazyInitializer");
      Object initializer = method.invoke(object);
      Method pmethod = initializer.getClass().getMethod("getImplementation");
      return pmethod.invoke(initializer);
    } catch (IllegalAccessException e) {
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    }
    return object;
  }

  public void transform(Object object) {
    JSONContext context = getContext();
    Path path = context.getPath();
    ChainedSet visits = context.getVisits();
    try {
      if (!visits.contains(object)) {
        context.setVisits(new ChainedSet(visits));
        context.getVisits().add(object);
        // traverse object
        BeanAnalyzer analyzer = BeanAnalyzer.analyze(resolveClass(object));
        TypeContext typeContext = context.writeOpenObject();
        for (BeanProperty prop : analyzer.getProperties()) {
          String name = prop.getName();
          path.enqueue(name);
          if (context.isIncluded(prop)) {
            /* START FIX */
            /*
             * object n'est que le proxy (qui ne contient pas
             * nécéssairement les methodes d'accès spécifiques au
             * classes concrètes), il faut récupérer la vrai
             * instance de la classe concrète.
             */
            Object value = prop.getValue(getRealInstance(object));
            /* END FIX */
            if (!context.getVisits().contains(value)) {

              TransformerWrapper transformer = (TransformerWrapper) context.getTransformer(value);

              if (!transformer.isInline()) {
                if (!typeContext.isFirst()) context.writeComma();
                typeContext.setFirst(false);
                context.writeName(name);
              }
              typeContext.setPropertyName(name);

              transformer.transform(value);
            }
          }
          path.pop();
        }
        context.writeCloseObject();
        context.setVisits((ChainedSet) context.getVisits().getParent());
      }
    } catch (JSONException e) {
      throw e;
    } catch (Exception e) {
      throw new JSONException("Error trying to deepSerialize", e);
    }
  }
}
