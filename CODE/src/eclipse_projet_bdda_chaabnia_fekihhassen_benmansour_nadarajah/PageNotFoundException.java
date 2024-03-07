package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

public class PageNotFoundException extends Exception {

  public PageNotFoundException() {
    super("Page non trouvée dans le buffer");
  }

  public PageNotFoundException(String message) {
    super(message);
  }

  public PageNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public PageNotFoundException(Throwable cause) {
    super(cause);
  }
}