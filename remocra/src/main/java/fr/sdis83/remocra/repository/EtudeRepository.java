package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ETUDE_STATUT;
import static fr.sdis83.remocra.db.model.remocra.tables.Document.DOCUMENT;
import static org.jooq.impl.DSL.row;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Order;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtude;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeEtudeStatut;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.Etude;
import fr.sdis83.remocra.web.model.ProcessusEtlPlanification;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration

public class EtudeRepository {
  private final Logger logger = Logger.getLogger(getClass());
  private final  DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);

  @Autowired
  DSLContext context;

  @Autowired
  JpaTransactionManager transactionManager;

  @PersistenceContext
  private EntityManager entityManager;

  public EtudeRepository() {

  }

  @Bean
  public EtudeRepository EtudeRepository(DSLContext context) {
    return new EtudeRepository(context);
  }

  EtudeRepository(DSLContext context) {
    this.context = context;
  }

  public List<Etude> getAll(List<ItemFilter> itemFilters, Integer limit , Integer start, List<ItemSorting> itemSortings) {
    Condition condition = this.getFilters(itemFilters);
    if(limit == null) limit = 0;
    if(start == null) start = 0;

    String sortField = "date_maj";
    SortOrder sortOrder = SortOrder.DESC;
    for (ItemSorting itemSorting : itemSortings) {
          sortField = itemSorting.getFieldName();
          sortOrder = itemSorting.isDesc() ?  SortOrder.DESC : SortOrder.ASC;
    }

    List<Record> listRecords = context.select()
      .from(ETUDE)
      .join(TYPE_ETUDE).on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
      .join(TYPE_ETUDE_STATUT).on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
      .where(condition).orderBy(ETUDE.field(DSL.field(sortField)).sort(sortOrder)).limit(limit).offset(start)
      .fetch();

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);

    List<Etude> listeEtudes = new ArrayList<>();

    for (Record r : listRecords){
      Etude etude = modelMapper.map(r, Etude.class);

      // Type
      TypeEtude type = context.select()
        .from(TYPE_ETUDE)
        .where(TYPE_ETUDE.ID.eq(Long.valueOf(r.getValue("type").toString())))
        .fetchOneInto(TypeEtude.class);
      etude.setType(type);

      //Statut
      TypeEtudeStatut statut = context.select()
        .from(TYPE_ETUDE_STATUT)
        .where(TYPE_ETUDE_STATUT.ID.eq(Long.valueOf(r.getValue("statut").toString())))
        .fetchOneInto(TypeEtudeStatut.class);
      etude.setStatut(statut);

      listeEtudes.add(etude);
    }
    return listeEtudes;
  }

 @Transactional
  public int count(List<ItemFilter> itemFilters) {
   Condition condition = this.getFilters(itemFilters);
   return context.fetchCount(context.select().from(ETUDE)
     .join(TYPE_ETUDE).on(TYPE_ETUDE.ID.eq(ETUDE.TYPE))
     .join(TYPE_ETUDE_STATUT).on(TYPE_ETUDE_STATUT.ID.eq(ETUDE.STATUT))
     .where(condition));
  }


  public Condition getFilters(List<ItemFilter> itemFilters){
    ItemFilter type = ItemFilter.getFilter(itemFilters, "type");
    ItemFilter statut = ItemFilter.getFilter(itemFilters,"statut");

    Condition condition = DSL.trueCondition();
    if (type != null) {
      condition = condition.and(TYPE_ETUDE.CODE.eq(type.getValue()));
    }
    if (statut != null) {
      condition = condition.and(TYPE_ETUDE_STATUT.CODE.eq(statut.getValue()));
    }
    return condition;

  }

}
