package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.util.GeometryUtil;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.CRISE_REPERTOIRE_LIEU;
import static fr.sdis83.remocra.db.model.remocra.Tables.REPERTOIRE_LIEU;
import static fr.sdis83.remocra.util.GeometryUtil.sridFromGeom;

@Configuration
public class RepertoireLieuRepository {

  @Autowired
  DSLContext context;


  public RepertoireLieuRepository() {

  }

  @Bean
  public RepertoireLieuRepository repertoireLieuRepository(DSLContext context) {
    return new RepertoireLieuRepository(context);
  }

  RepertoireLieuRepository(DSLContext context) {
    this.context = context;
  }

  public List<fr.sdis83.remocra.web.model.RepertoireLieu> getAll(String query) {

    List<RepertoireLieu> repertoires = context.selectFrom(REPERTOIRE_LIEU).fetchInto(RepertoireLieu.class);
    return getListRepertoireLieu(query, repertoires);
  }

  private List<fr.sdis83.remocra.web.model.RepertoireLieu> getListRepertoireLieu(String query, List<RepertoireLieu> repertoires) {

    //TODO  vérifier que query est protégé contre l'injection
    List<fr.sdis83.remocra.web.model.RepertoireLieu> listRepertoireLieu = new ArrayList<>();
    StringBuilder sql = new StringBuilder();

    for(int i =0; i<repertoires.size(); i++){
      RepertoireLieu r = repertoires.get(i);
      sql.append(" SELECT ")
              .append(r.getSourceSqlLibelle())
              .append(" AS libelle, St_asewkt(")
              .append(r.getSourceSqlValeur())
              .append(") AS geometrie, CAST('")
              .append(r.getLibelle().replaceAll("'", "\""))
              .append("' as varchar) AS origine")
              .append(" FROM(").append(r.getSourceSql())
              .append(") AS rep").append(r.getId())
              .append(" WHERE lower(")
              .append(r.getSourceSqlLibelle())
              .append(") like lower('%")
              .append(query).append("%')");
      if (i  < repertoires.size()-1){
        sql.append(" UNION ");
      }if(i == repertoires.size()-1){
        sql.append(" limit 10");
      }
    }

    Result<Record> result = context.fetch(sql.toString());
    for (Record r : result){
      fr.sdis83.remocra.web.model.RepertoireLieu rep = new fr.sdis83.remocra.web.model.RepertoireLieu();
      rep.setLibelle(String.valueOf(r.getValue("libelle")));
      String[] coord = String.valueOf(r.getValue("geometrie")).split(";");
      Integer srid = sridFromGeom(coord[0]);
      Geometry geom = GeometryUtil.toGeometry(coord[1], srid);
      rep.setGeometrie(geom);
      rep.setOrigine(String.valueOf(r.getValue("origine")));
      listRepertoireLieu.add(rep);

    }
    return listRepertoireLieu;
  }

  public List<fr.sdis83.remocra.web.model.RepertoireLieu> getAllById(Long id, String query) {

    List<RepertoireLieu> repertoires = context.selectFrom(REPERTOIRE_LIEU)
        .where(REPERTOIRE_LIEU.ID.in(DSL.select(CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
            .from(CRISE_REPERTOIRE_LIEU)
            .where(CRISE_REPERTOIRE_LIEU.CRISE.eq(id)))).fetchInto(RepertoireLieu.class);

    return getListRepertoireLieu(query, repertoires);
  }
  public int count() {
    return context.fetchCount(context.select().from(REPERTOIRE_LIEU));
  }
}
