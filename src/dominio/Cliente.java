package dominio;

/**
 * Representa um cliente genérico (pessoa física ou jurídica).
 * Contém informações básicas comuns como nome, email e conta.
 */
public abstract class Cliente implements Persistivel {
    private String nome;
    private String email;
    private Conta conta;

    /**
     * Cria um cliente com nome, email e conta associada.
     */
    public Cliente(String nome, String email, Conta conta) {
        this.nome = nome;
        this.email = email;
        this.conta = conta;
    }

    /**
     * Retorna o identificador do cliente (CPF para PF, CNPJ para PJ).
     */
    public abstract String getIdentificador();

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Conta getConta() {
        return conta;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }
}
