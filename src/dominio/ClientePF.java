package dominio;

public class ClientePF extends Cliente{
    private String cpf;

    public ClientePF(String nome, String email, Conta conta, String cpf) {
        super(nome, email, conta);
        this.cpf = cpf;
    }

    @Override
    public String getIdentificador() {
        return this.cpf;
    }
}
