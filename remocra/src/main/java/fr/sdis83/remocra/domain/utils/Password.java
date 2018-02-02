package fr.sdis83.remocra.domain.utils;


public class Password {

    private static final long serialVersionUID = 1L;

    String content;

    public Password(String str) {
        this.content = str;
    }

    @Override
    public String toString() {
        return content;
    }
}
