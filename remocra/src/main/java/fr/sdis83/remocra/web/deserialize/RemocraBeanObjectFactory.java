package fr.sdis83.remocra.web.deserialize;

import java.lang.reflect.Type;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import flexjson.ObjectBinder;
import flexjson.factories.BeanObjectFactory;

public class RemocraBeanObjectFactory extends BeanObjectFactory {

    // private final Logger logger = Logger.getLogger(getClass());

    private EntityManager entityManager;

    public RemocraBeanObjectFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, @SuppressWarnings("rawtypes") Class targetClass) {
        if (value == null) {
            return null;
        }
        if (!targetClass.isAnnotationPresent(Entity.class)) {
            return super.instantiate(context, value, targetType, targetClass);
        }
        if (targetClass.isInstance(value)) {
            return value;
        }
        Long id = null;
        if (value instanceof Map<?, ?>) {
            if (((Map<?, ?>) value).containsKey("id")) {
                id = Long.valueOf(((Map<?, ?>) value).get("id").toString());
            }
        }
        if (id == null) {
            try {
                id = Long.valueOf(value.toString());
            } catch (Exception e) {
                // on ne fait rien de spécial
            }
        }
        if (id == null) {
            return null;
        }
        return this.entityManager.getReference(targetClass, id);
    }
}
