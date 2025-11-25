package dominio;

public abstract class Cliente implements Persistivel {
    private String nome;
    private String email;
    private Conta conta;

    public Cliente(String nome, String email, Conta conta) {
        this.nome = nome;
        this.email = email;
        this.conta = conta;
    }

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
