package fr.sdis83.remocra.fragment.components;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Item utilisé par la carte d'affichage des Hydrants
 *
 * Created by vde on 20/02/2017.
 */

public class HydrantItem extends OverlayItem {

    private Long idHydrant;

     public HydrantItem(final String aTitle, final String aSnippet, final IGeoPoint aGeoPoint) {
        super(null, aTitle, aSnippet, aGeoPoint);
    }

    public HydrantItem(final String aUid, final String aTitle, final String aDescription,
                       final IGeoPoint aGeoPoint) {
        super(aUid, aTitle, aDescription, aGeoPoint);
    }

    public Long getIdHydrant() {
        return idHydrant;
    }

    public void setIdHydrant(Long idHydrant) {
        this.idHydrant = idHydrant;
    }
}
