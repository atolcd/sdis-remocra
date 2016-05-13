package fr.sdis83.remocra.domain.utils;

import flexjson.transformer.DateTransformer;

public class RemocraDateHourTransformer extends DateTransformer {

    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public RemocraDateHourTransformer() {
        super(FORMAT);
    }

    protected static RemocraDateHourTransformer instance;

    public static RemocraDateHourTransformer getInstance() {
        if (instance == null) {
            instance = new RemocraDateHourTransformer();
        }
        return instance;
    }

}
