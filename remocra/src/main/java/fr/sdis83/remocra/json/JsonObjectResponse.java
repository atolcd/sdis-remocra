package fr.sdis83.remocra.json;

/**
 * Class to be used for returning data back to the client.
 *
 * <p>Data should be set on this object then serialized into JSON format to be returned to the
 * client.
 *
 * @author nvujasin
 */
public class JsonObjectResponse {

  private long total;
  private boolean success;
  private Object data;
  private String message;

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
