package dominio;

public class ClientePJ extends Cliente{
    private String cnpj;
    private String razaoSocial;

    public ClientePJ(String nome, String email, Conta conta, String cnpj, String razaoSocial) {
        super(nome, email, conta);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }

    @Override
    public String getIdentificador() {
        return this.cnpj;
    }
}
