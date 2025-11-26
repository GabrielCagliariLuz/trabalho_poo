package dominio;

/**
 * Cliente pessoa jurídica (CNPJ + razão social).
 */
public class ClientePJ extends Cliente {
    private String cnpj;
    private String razaoSocial;

    public ClientePJ(String nome, String email, Conta conta, String cnpj, String razaoSocial) {
        super(nome, email, conta);
        if (cnpj == null || cnpj.length() != 14) {
            throw new DocumentoInvalidoException("CNPJ inválido: Deve conter 14 digitos numéricos");
        }
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }

    @Override
    public String toLineString() {
        return String.format(java.util.Locale.US,"PJ;%s;%s;%s;%s;%d;%.2f",
                this.getIdentificador(),
                this.getNome(),
                this.getEmail(),
                this.razaoSocial,
                this.getConta().getNumero(),
                this.getConta().getSaldo());
    }

    public static ClientePJ fromString(String linha) {
        String[] partes = linha.split(";");
        int numConta = Integer.parseInt(partes[4]);
        double saldoConta = Double.parseDouble(partes[5].replace(",","."));
        Conta conta = new Conta(numConta);
        conta.depositar(saldoConta);
        return new ClientePJ(partes[1], partes[2], conta, partes[0], partes[3]);
    }

    @Override
    public String getIdentificador() {
        return this.cnpj;
    }
}
