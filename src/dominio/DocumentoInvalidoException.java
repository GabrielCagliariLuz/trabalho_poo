package dominio;

/**
 * Exceção lançada quando um documento (CPF/CNPJ) é considerado inválido.
 */
public class DocumentoInvalidoException extends RuntimeException {
    public DocumentoInvalidoException(String message) {
        super(message);
    }
}
