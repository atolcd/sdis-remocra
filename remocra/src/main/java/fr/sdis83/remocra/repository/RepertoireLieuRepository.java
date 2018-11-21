package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_REPERTOIRE_LIEU;
import static fr.sdis83.remocra.db.model.remocra.Tables.REPERTOIRE_LIEU;
import static fr.sdis83.remocra.util.GeometryUtil.sridFromGeom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepertoireLieuRepository {
  private final Logger logger = Logger.getLogger(getClass());


  @Autowired
  DSLContext context;


  public RepertoireLieuRepository() {

  }

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public RepertoireLieuRepository repertoireLieuRepository(DSLContext context) {
    return new RepertoireLieuRepository(context);
  }

  RepertoireLieuRepository(DSLContext context) {
    this.context = context;
  }

  public List<RepertoireLieu> getAll(List<ItemFilter> itemFilters) {
    List<RepertoireLieu> l = null;
    l = context.select().from(REPERTOIRE_LIEU).fetchInto(RepertoireLieu.class);
    return l;
  }

  public List<fr.sdis83.remocra.web.model.RepertoireLieu> getAllById(Long id, String query) {


    List<fr.sdis83.remocra.web.model.RepertoireLieu> l = new ArrayList<fr.sdis83.remocra.web.model.RepertoireLieu>();

    List<RepertoireLieu> repertoires = context.select().from(REPERTOIRE_LIEU)
        .where(REPERTOIRE_LIEU.ID.in(DSL.select(CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
            .from(CRISE_REPERTOIRE_LIEU)
            .where(CRISE_REPERTOIRE_LIEU.CRISE.eq(id)))).fetchInto(RepertoireLieu.class);

    String sql = "";
    for(int i =0; i<repertoires.size(); i++){
     RepertoireLieu r = repertoires.get(i);
      sql = sql +" SELECT "+ r.getSourceSqlLibelle()+" AS libelle, St_asewkt("+r.getSourceSqlValeur()+") AS geometrie, CAST('" +r.getLibelle().replaceAll("\'","\"")+"' as varchar) AS origine"+
          " FROM("+ r.getSourceSql()+") AS rep"+r.getId()+
          " WHERE lower("+r.getSourceSqlLibelle()+") like lower('%"+ query+"%')";
      if (i  < repertoires.size()-1){
        sql = sql+ " UNION ";
      }if(i == repertoires.size()-1){
        sql = sql + " limit 10";
      }
    }
    Result<Record>result = context.fetch(sql);
    for (Record r : result){
      fr.sdis83.remocra.web.model.RepertoireLieu rep = new fr.sdis83.remocra.web.model.RepertoireLieu();
      rep.setLibelle(String.valueOf(r.getValue("libelle")));
      Geometry geom = null;
      Integer srid = 2154;
      String[] coord = String.valueOf(r.getValue("geometrie")).split(";");
      srid = sridFromGeom(coord[0]);
      geom = GeometryUtil.toGeometry(coord[1],srid);
      rep.setGeometrie(geom);
      rep.setOrigine(String.valueOf(r.getValue("origine")));
      l.add(rep);

    }
    return l;
  }
  public int count() {
    return context.fetchCount(context.select().from(REPERTOIRE_LIEU));
  }
}
