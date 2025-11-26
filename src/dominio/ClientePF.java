package dominio;

/**
 * Cliente pessoa física (CPF).
 * Contém validação simples do CPF (apenas tamanho) e métodos de serialização.
 */
public class ClientePF extends Cliente {
    private String cpf;

    /**
     * Construtor que valida formato básico do CPF (11 dígitos).
     */
    public ClientePF(String nome, String email, Conta conta, String cpf) {
        super(nome, email, conta);
        if (cpf == null || cpf.length() != 11) {
            throw new DocumentoInvalidoException("CPF inválido: Deve conter 11 digitos numéricos");
        }
        this.cpf = cpf;
    }

    /**
     * Serializa o cliente para uma linha usada no arquivo (prefixo PF).
     */
    @Override
    public String toLineString() {
        return String.format(java.util.Locale.US,"PF;%s;%s;%s;%d;%.2f",
                this.getIdentificador(),
                this.getNome(),
                this.getEmail(),
                this.getConta().getNumero(),
                this.getConta().getSaldo());
    }

    /**
     * Constrói um ClientePF a partir de uma linha lida do arquivo.
     */
    public static ClientePF fromString(String linha) {
        String[] partes = linha.split(";");
        int numConta = Integer.parseInt(partes[3]);
        double saldoConta = Double.parseDouble(partes[4].replace(",","."));
        Conta conta = new Conta(numConta);
        conta.depositar(saldoConta);
        return new ClientePF(partes[1], partes[2], conta, partes[0]);
    }

    /**
     * Retorna o CPF como identificador do cliente.
     */
    @Override
    public String getIdentificador() {
        return this.cpf;
    }
}
