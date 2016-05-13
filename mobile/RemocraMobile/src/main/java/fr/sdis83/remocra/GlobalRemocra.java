package fr.sdis83.remocra;

import android.content.Context;

/**
 * Created by jpt on 22/10/13.
 */
public class GlobalRemocra {

    private static GlobalRemocra mInstance = null;
    private final Context mContext;

    private String login;
    private String password;
    private boolean canAddHydrant;
    private boolean canReconnaissance;
    private boolean canControl;
    private boolean canReception;
    private boolean canSetMco;

    public static GlobalRemocra getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GlobalRemocra(context.getApplicationContext());
        }
        return mInstance;
    }

    public static GlobalRemocra getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("GlobalRemocra.mInstance n'a pas été initialisée");
        }
        return mInstance;
    }

    private GlobalRemocra(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public Context getContext() {
        return mContext;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCanAddHydrant(boolean canAddHydrant) {
        this.canAddHydrant = canAddHydrant;
    }

    public void setCanReconnaissance(boolean canReconnaissance) {
        this.canReconnaissance = canReconnaissance;
    }

    public void setCanControl(boolean canControl) {
        this.canControl = canControl;
    }

    public void setCanReception(boolean canReception) {
        this.canReception = canReception;
    }

    public void setCanSetMco(boolean canSetMco) {
        this.canSetMco = canSetMco;
    }

    public String toString() {
        return login + " add:" + canAddHydrant + " reco:" + canReconnaissance
                + " ctrl:" + canControl + " recep:" + canReception + " mco:" + canSetMco;
    }

    public boolean getCanAddHydrant() {
        return canAddHydrant;
    }

    public boolean getCanReception() {
        return canReception;
    }

    public boolean getCanReconnaissance() {
        return canReconnaissance;
    }

    public boolean getCanControl() {
        return canControl;
    }

    public boolean getCanSetMco() {
        return canSetMco;
    }
}
