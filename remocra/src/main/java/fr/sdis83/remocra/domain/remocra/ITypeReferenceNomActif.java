package fr.sdis83.remocra.domain.remocra;

public interface ITypeReferenceNomActif extends ITypeReference {

  public String getNom();

  public void setNom(String nom);

  public Boolean getActif();

  public void setActif(Boolean actif);
}
