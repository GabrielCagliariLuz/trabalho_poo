package dominio;

public class ClientePF extends Cliente implements Persistivel{
    private String cpf;

    public ClientePF(String nome, String email, Conta conta, String cpf) {
        super(nome, email, conta);
        if (cpf == null || cpf.length() != 11){
            throw new DocumentoInvalidoException("CPF inválido: Deve conter 11 digitos numéricos");
        }
        this.cpf = cpf;
    }

    @Override
    public String toLineString() {
        return String.format("PF;%s;%s;%s;%d;%.2f",
                this.getIdentificador(),
                this.getNome(),
                this.getEmail(),
                this.getConta().getNumero(),
                this.getConta().getSaldo());
    }

    public static ClientePF fromString(String linha){
        String[] partes = linha.split(";");
        int numConta = Integer.parseInt(partes[3]);
        double saldoConta = Double.parseDouble(partes[4]);
        Conta conta = new Conta(numConta);
        conta.depositar(saldoConta);
        return new ClientePF(partes[1],partes[2],conta, partes[0]);
    }

    @Override
    public String getIdentificador() {
        return this.cpf;
    }
}
