package fr.sdis83.remocra.util;

import java.util.Collection;

public class ListWithCountData<T> {
  private Collection<T> list;
  private int count;

  public Collection<T> getList() {
    return list;
  }

  public void setList(Collection<T> list) {
    this.list = list;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public ListWithCountData(Collection<T> list, int count) {
    this.list = list;
    this.count = count;
  }
}
