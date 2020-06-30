package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.TypeHydrantDiametre;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNatureDeci;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Etude;
import fr.sdis83.remocra.web.model.EtudeHydrantProjet;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE_HYDRANT_PROJET;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE_DECI;

@Configuration

public class EtudeHydrantProjetRepository {

  @Autowired
  DSLContext context;

  @Autowired
  JpaTransactionManager transactionManager;

  @PersistenceContext
  private EntityManager entityManager;

  public EtudeHydrantProjetRepository() {

  }

  @Bean
  public EtudeRepository EtudeRepository(DSLContext context) {
    return new EtudeRepository(context);
  }

  public List<EtudeHydrantProjet> getAll(List<ItemFilter> itemFilters, Integer limit , Integer start, List<ItemSorting> itemSortings) {
    if(limit == null) limit = 100;
    if(start == null) start = 0;

    Condition condition = this.getFilters(itemFilters);

    String sortField = "id";
    SortOrder sortOrder = SortOrder.ASC;
    for (ItemSorting itemSorting : itemSortings) {
          sortField = itemSorting.getFieldName();
          sortOrder = itemSorting.isDesc() ?  SortOrder.DESC : SortOrder.ASC;
    }

    List<Record> listRecords = context.select()
      .from(ETUDE_HYDRANT_PROJET)
      .join(ETUDE).on(ETUDE.ID.eq(ETUDE_HYDRANT_PROJET.ETUDE))
      .leftOuterJoin(TYPE_HYDRANT_DIAMETRE).on(TYPE_HYDRANT_DIAMETRE.ID.eq(ETUDE_HYDRANT_PROJET.DIAMETRE_NOMINAL))
      .leftOuterJoin(TYPE_HYDRANT_NATURE_DECI).on(TYPE_HYDRANT_NATURE_DECI.ID.eq(ETUDE_HYDRANT_PROJET.TYPE_DECI))
      .where(condition).orderBy(ETUDE_HYDRANT_PROJET.field(DSL.field(sortField)).sort(sortOrder)).limit(limit).offset(start)
      .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    List<EtudeHydrantProjet> listePeiProjet = new ArrayList<>();

    for (Record r : listRecords) {
      EtudeHydrantProjet peiProjet = modelMapper.map(r, EtudeHydrantProjet.class);

      // Etude
      Etude etude = context.select(ETUDE.ID)
              .from(ETUDE)
              .where(ETUDE.ID.eq(Long.valueOf(r.getValue("etude").toString())))
              .fetchOneInto(Etude.class);
      peiProjet.setEtude(etude);

      // Deci
      TypeHydrantNatureDeci type_deci = context.select()
              .from(TYPE_HYDRANT_NATURE_DECI)
              .where(TYPE_HYDRANT_NATURE_DECI.ID.eq(Long.valueOf(r.getValue("type_deci").toString())))
              .fetchOneInto(TypeHydrantNatureDeci.class);
      peiProjet.setType_deci(type_deci);

      if(r.getValue("diametre_nominal") != null) {
        TypeHydrantDiametre diametre_nominal = context.select()
              .from(TYPE_HYDRANT_DIAMETRE)
              .where(TYPE_HYDRANT_DIAMETRE.ID.eq(Long.valueOf(r.getValue("diametre_nominal").toString())))
              .fetchOneInto(TypeHydrantDiametre.class);
        peiProjet.setDiametre_nominal(diametre_nominal);
      }


      listePeiProjet.add(peiProjet);
    }
    return listePeiProjet;
  }

  @Transactional
  public int count(List<ItemFilter> itemFilters) {

    Condition condition = this.getFilters(itemFilters);
    return context.fetchCount(context.select().from(ETUDE_HYDRANT_PROJET)
      .join(ETUDE).on(ETUDE.ID.eq(ETUDE_HYDRANT_PROJET.ETUDE))
      .join(TYPE_HYDRANT_DIAMETRE).on(TYPE_HYDRANT_DIAMETRE.ID.eq(ETUDE_HYDRANT_PROJET.DIAMETRE_NOMINAL))
      .join(TYPE_HYDRANT_NATURE_DECI).on(TYPE_HYDRANT_NATURE_DECI.ID.eq(ETUDE_HYDRANT_PROJET.TYPE_DECI))
      .where(condition));
  }

  public Condition getFilters(List<ItemFilter> itemFilters){
    Condition condition = DSL.trueCondition();
    return condition;
  }

  public long addPeiProjet(String json) throws CRSException, IllegalCoordinateException {
    HashMap<String, Object> obj = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    Long diametreNominal = (obj.get("diametreNominal") != null) ? new Long(obj.get("diametreNominal").toString()) : null;
    Integer diametreCanalisation = (obj.get("diametreCanalisation") != null) ? Integer.valueOf(obj.get("diametreCanalisation").toString()) : null;
    Integer capacite = (obj.get("capacite") != null) ? Integer.valueOf(obj.get("capacite").toString()) : null;
    Integer debit = (obj.get("debit") != null) ? Integer.valueOf(obj.get("debit").toString()) : null;

    Double longitude = (obj.get("longitude") != null) ? Double.valueOf(obj.get("longitude").toString()) : null;
    Double latitude = (obj.get("latitude") != null) ? Double.valueOf(obj.get("latitude").toString()) : null;

    Geometry geom = GeometryUtil.createPoint(longitude, latitude, "2154", "2154");

    long idPeiProjet = context.insertInto(ETUDE_HYDRANT_PROJET)
      .set(ETUDE_HYDRANT_PROJET.TYPE_DECI, new Long(obj.get("deci").toString()))
      .set(ETUDE_HYDRANT_PROJET.TYPE, obj.get("type").toString())
      .set(ETUDE_HYDRANT_PROJET.DIAMETRE_NOMINAL, diametreNominal)
      .set(ETUDE_HYDRANT_PROJET.DIAMETRE_CANALISATION, diametreCanalisation)
      .set(ETUDE_HYDRANT_PROJET.CAPACITE, capacite)
      .set(ETUDE_HYDRANT_PROJET.DEBIT, debit)
      .set(ETUDE_HYDRANT_PROJET.GEOMETRIE, geom)
      .set(ETUDE_HYDRANT_PROJET.ETUDE, new Long(obj.get("idEtude").toString())).returning(ETUDE_HYDRANT_PROJET.ID).fetchOne().getValue(ETUDE.ID);
    return idPeiProjet;
  }
}
