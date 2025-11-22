package dominio;

public class ClientePF extends Cliente{
    private String cpf;

    public ClientePF(String nome, String email, Conta conta, String cpf) {
        super(nome, email, conta);
        if (cpf == null || cpf.length() != 11){
            throw new DocumentoInvalidoException("CPF inválido: Deve conter 11 digitos numéricos");
        }
        this.cpf = cpf;
    }

    @Override
    public String getIdentificador() {
        return this.cpf;
    }
}
