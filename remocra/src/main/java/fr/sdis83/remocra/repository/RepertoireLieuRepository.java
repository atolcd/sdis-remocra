package fr.sdis83.remocra.repository;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.RepertoireLieu;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.model.RepertoireLieuData;
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

  public List<RepertoireLieuData> getAll(String query) {
    List<RepertoireLieu> repertoires = context.selectFrom(REPERTOIRE_LIEU).fetchInto(RepertoireLieu.class);
    return getRepertoireLieuData(query, repertoires);
  }

  private List<RepertoireLieuData> getRepertoireLieuData(String query, List<RepertoireLieu> repertoires) {

    List<RepertoireLieuData> listRepertoireLieu = new ArrayList<>();
    StringBuilder sql = new StringBuilder();

    String queryReplace = query
            //Replace des ' par '' pour les echapper en postgres
            .replace("'", "''")
            //Replace des espaces pas des % pour permettre une recherche du type :
            // "aire gen voyag gorr" pour trouver "aire d'accueil des gens du voyage - GORRON  (Lieu-dit)"
            .replace(' ', '%');

    for(int i =0; i<repertoires.size(); i++){
      RepertoireLieu repertoireLieu = repertoires.get(i);

      sql.append(" SELECT ")
              .append(repertoireLieu.getSourceSqlLibelle())
              .append(" AS libelle, St_asewkt(")
              .append(repertoireLieu.getSourceSqlValeur())
              .append(") AS geometrie, CAST('")
              .append(repertoireLieu.getLibelle().replaceAll("'", "\""))
              .append("' as varchar) AS origine")
              .append(" FROM(").append(repertoireLieu.getSourceSql())
              .append(") AS rep").append(repertoireLieu.getId())
              .append(" WHERE ")
              .append(repertoireLieu.getSourceSqlLibelle())
              .append(" ILIKE '%")
              .append(queryReplace)
              .append("%'");
      if (i  < repertoires.size()-1){
        sql.append(" UNION ");
      }if(i == repertoires.size()-1){
        sql.append(" limit 10");
      }
    }

    Result<Record> result = context.fetch(sql.toString());
    for (Record r : result){
      RepertoireLieuData rep = new RepertoireLieuData();
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

  public List<RepertoireLieuData> getAllById(Long id, String query) {

    List<RepertoireLieu> repertoires = context.selectFrom(REPERTOIRE_LIEU)
        .where(REPERTOIRE_LIEU.ID.in(DSL.select(CRISE_REPERTOIRE_LIEU.REPERTOIRE_LIEU)
            .from(CRISE_REPERTOIRE_LIEU)
            .where(CRISE_REPERTOIRE_LIEU.CRISE.eq(id)))).fetchInto(RepertoireLieu.class);

    return getRepertoireLieuData(query, repertoires);
  }
  public int count() {
    return context.fetchCount(context.select().from(REPERTOIRE_LIEU));
  }
}
