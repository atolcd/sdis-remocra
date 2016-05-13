package fr.sdis83.remocra.domain.remocra;


public interface ITypeReference {

    public Long getId();

    public void setId(Long id);

    public String getCode();

    public void setCode(String code);

    public ITypeReference merge();
}
