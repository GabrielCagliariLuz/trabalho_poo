package dominio;

/**
 * Contrato para objetos que podem ser serializados em uma linha de texto
 * para persistência simples em arquivo.
 */
public interface Persistivel {
    /** Retorna representação em linha de texto para gravação. */
    public String toLineString();
}
