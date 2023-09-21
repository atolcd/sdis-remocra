package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_INDISPO_STATUT;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.CommuneRecord;
import fr.sdis83.remocra.web.model.HydrantIndispoTemporaireRecord;
import fr.sdis83.remocra.web.model.HydrantRecord;
import fr.sdis83.remocra.web.model.TypeHydrantIndispoStatutRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HydrantIndispoTemporaireRepository {

  @Autowired DSLContext context;

  @Autowired UtilisateurService utilisateurService;

  public HydrantIndispoTemporaireRepository() {}

  @Bean
  public HydrantIndispoTemporaireRepository hydrantIndispoTemporaireRepository(DSLContext context) {
    return new HydrantIndispoTemporaireRepository(context);
  }

  HydrantIndispoTemporaireRepository(DSLContext context) {
    this.context = context;
  }

  public List<HydrantIndispoTemporaireRecord> getAll(
      List<ItemFilter> itemFilters, Integer limit, Integer start, List<ItemSorting> itemSortings) {
    String condition = this.getFilters(itemFilters);
    // Par défaut on tri par id  DESC
    String sortField = "id";
    SortOrder sortOrder = SortOrder.DESC;
    for (ItemSorting itemSorting : itemSortings) {
      sortField = itemSorting.getFieldName();
      sortOrder = itemSorting.isDesc() ? SortOrder.DESC : SortOrder.ASC;
    }

    Long zc = utilisateurService.getCurrentZoneCompetenceId();

    Result<Record> hydrantIndispoTemporaireRecord = null;
    List<HydrantIndispoTemporaireRecord> indispoTempRecordList =
        new ArrayList<HydrantIndispoTemporaireRecord>();
    StringBuffer sbReq =
        new StringBuffer(
                "SELECT hit.id, hit.date_debut as dateDebut, hit.date_fin as dateFin, hit.motif,")
            .append(
                " hit.date_rappel_debut as dateRappelDebut, hit.date_rappel_fin as dateRappelFin, hit.total_hydrants as totalHydrants,")
            .append(
                " hit.bascule_auto_indispo as basculeAutoIndispo, hit.bascule_auto_dispo as basculeAutoDispo, hit.mel_avant_indispo as melAvantIndispo,")
            .append(
                " hit.mel_avant_dispo as melAvantDispo, hit.observation, hit.statut, hit.observation, ")
            .append("(SELECT c.nom FROM remocra.hydrant_indispo_temporaire hit2")
            .append(
                " JOIN remocra.hydrant_indispo_temporaire_hydrant hith2 ON hith2.indisponibilite = hit2.id")
            .append(" JOIN remocra.hydrant h2 on h2.id = hith2.hydrant")
            .append(
                " JOIN remocra.commune c on c.id = h2.commune WHERE hit2.id = hit.id limit 1) as commune")
            .append(" FROM remocra.hydrant_indispo_temporaire hit")
            .append(" JOIN remocra.type_hydrant_indispo_statut this on this.id = hit.statut")
            .append(" WHERE")
            .append(" " + condition)

            // Filtrage par zone de compétence systématique
            .append(" AND hit.id in (")
            .append("SELECT indisponibilite FROM remocra.hydrant_indispo_temporaire_hydrant hith ")
            .append(" WHERE hith.hydrant in(")
            .append(" SELECT h.id FROM remocra.hydrant h WHERE h.commune in(")
            .append(
                "SELECT zcc.commune_id FROM remocra.zone_competence_commune zcc WHERE zcc.zone_competence_id ="
                    + zc)
            .append(" )))")
            .append(" ORDER BY ")
            .append(" " + sortField)
            .append(" " + sortOrder)
            .append(" LIMIT " + limit)
            .append(" OFFSET " + start);

    hydrantIndispoTemporaireRecord = context.fetch(sbReq.toString());

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    for (Record r : hydrantIndispoTemporaireRecord) {
      HydrantIndispoTemporaireRecord indispoTemp =
          modelMapper.map(r, HydrantIndispoTemporaireRecord.class);
      Long idIndispoTemp = Long.valueOf(r.getValue("id").toString());

      // Statut
      TypeHydrantIndispoStatutRecord statut =
          context
              .selectFrom(TYPE_HYDRANT_INDISPO_STATUT)
              .where(
                  TYPE_HYDRANT_INDISPO_STATUT.ID.eq(Long.valueOf(r.getValue("statut").toString())))
              .fetchOneInto(TypeHydrantIndispoStatutRecord.class);
      indispoTemp.setStatut(statut);

      // Hydrants
      StringBuffer reqHydrants =
          new StringBuffer(
                  "SELECT h.id, h.numero, ST_asGeoJSON(h.geometrie) as jsonGeometrie, ST_AsText(h.geometrie) as wktGeometrie")
              .append(" FROM remocra.hydrant h")
              .append(
                  " JOIN remocra.hydrant_indispo_temporaire_hydrant hits on hits.hydrant = h.id")
              .append(
                  " JOIN remocra.hydrant_indispo_temporaire hit on hit.id = hits.indisponibilite")
              .append(" WHERE hit.id = " + indispoTemp.getId());

      List<HydrantRecord> hydrantRecords = new ArrayList<HydrantRecord>();
      for (Record h : context.fetch(reqHydrants.toString())) {
        HydrantRecord hr = modelMapper.map(h, HydrantRecord.class);
        hr.setGeometrie(GeometryUtil.toGeometry(hr.getWktGeometrie(), GlobalConstants.SRID_2154));

        // Récupération de la commune + remontée dans l'indispo temp
        CommuneRecord c =
            context
                .select(COMMUNE.ID, COMMUNE.NOM)
                .from(COMMUNE)
                .join(HYDRANT)
                .on(HYDRANT.COMMUNE.eq(COMMUNE.ID))
                .where(HYDRANT.ID.eq(hr.getId()))
                .fetchOneInto(CommuneRecord.class);
        hr.setCommune(c);

        hydrantRecords.add(hr);
      }
      indispoTemp.setHydrants(hydrantRecords);

      // Génération du texte du tooltip à afficher dans la grid
      StringBuffer tooltip = new StringBuffer("<ul>");
      for (HydrantRecord h : indispoTemp.getHydrants()) {
        tooltip.append("<li>").append(h.getNumero()).append("</li>");
      }
      tooltip.append("</ul>");
      indispoTemp.setHydrantsTooltip(tooltip.toString());

      indispoTemp.setGeometrie(this.calculIndispoTempGeometrie(indispoTemp));

      indispoTempRecordList.add(indispoTemp);
    }
    return indispoTempRecordList;
  }

  private String getFilters(List<ItemFilter> itemFilters) {
    String condition = "1 = 1";
    for (ItemFilter itemFilter : itemFilters) {
      if ("statut".equals(itemFilter.getFieldName())) {
        String code = itemFilter.getValue();
        if (code.startsWith("-")) {
          condition += " AND hit.statut != " + code.replaceAll("-", "");
        } else {
          condition += " AND hit.statut = " + code;
        }
      } else if ("commune".equals(itemFilter.getFieldName())) {
        StringBuffer communeReq =
            new StringBuffer(
                    " AND "
                        + itemFilter.getValue()
                        + " IN (SELECT c.id FROM remocra.hydrant_indispo_temporaire hit2")
                .append(
                    " JOIN remocra.hydrant_indispo_temporaire_hydrant hith2 ON hith2.indisponibilite = hit2.id")
                .append(" JOIN remocra.hydrant h2 on h2.id = hith2.hydrant")
                .append(
                    " JOIN remocra.commune c on c.id = h2.commune WHERE hit2.id = hit.id LIMIT 1)");
        condition += communeReq.toString();
      } else if ("hydrantId".equals(itemFilter.getFieldName())) {
        StringBuffer hydrantReq =
            new StringBuffer(" AND " + itemFilter.getValue() + " IN (SELECT hydrant")
                .append(" FROM remocra.hydrant_indispo_temporaire_hydrant")
                .append(" WHERE indisponibilite = hit.id)");
        condition += hydrantReq.toString();
      }
    }
    return condition;
  }

  /**
   * Calcule la géométrie d'une indisponibilité temporaire La géométrie de l'IT est l'enveloppe de
   * la géométrie des points d'eau composant l'IT
   */
  public Geometry calculIndispoTempGeometrie(HydrantIndispoTemporaireRecord indispo) {
    Geometry geom = null;
    if (indispo.getHydrants() != null && indispo.getHydrants().size() != 0) {
      GeometryFactory geometryFactory = new GeometryFactory();
      ArrayList<Geometry> geoms = new ArrayList<Geometry>();
      Iterator<HydrantRecord> hydrants = indispo.getHydrants().iterator();
      while (hydrants.hasNext()) {
        Geometry g = hydrants.next().getGeometrie();
        geoms.add(g);
      }
      geom = geometryFactory.buildGeometry(geoms);
    }
    return geom;
  }

  public long countHydrantsIndispoTemp(List<ItemFilter> itemFilters) {
    Long zc = utilisateurService.getCurrentZoneCompetenceId();

    String condition = this.getFilters(itemFilters);
    StringBuffer sbReq =
        new StringBuffer("SELECT count(*) as total")
            .append(" FROM remocra.hydrant_indispo_temporaire hit")
            .append(" JOIN remocra.type_hydrant_indispo_statut this on this.id = hit.statut")
            .append(" WHERE")
            .append(" " + condition)

            // Filtrage par zone de compétence systématique
            .append(" AND hit.id in (")
            .append("SELECT indisponibilite FROM remocra.hydrant_indispo_temporaire_hydrant hith ")
            .append(" WHERE hith.hydrant in(")
            .append(" SELECT h.id FROM remocra.hydrant h WHERE h.commune in(")
            .append(
                "SELECT zcc.commune_id FROM remocra.zone_competence_commune zcc WHERE zcc.zone_competence_id ="
                    + zc)
            .append(" )))");

    return (Long) context.fetchOne(sbReq.toString()).getValue("total");
  }
}
