package dominio;

/**
 * Exceção verificada para indicar que não há saldo suficiente para uma
 * operação.
 */
public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
