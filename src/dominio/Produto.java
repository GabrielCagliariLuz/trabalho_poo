package dominio;

public class Produto implements Persistivel{
    private int codigo;
    private String nome;
    private double preco;
    private TipoProduto tipo;

    public Produto(int codigo, String nome, double preco, TipoProduto tipo) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.tipo = tipo;
    }

    @Override
    public String toLineString() {
        return String.format("%d;%s;%.2f;%s",
                this.codigo,
                this.nome,
                this.preco,
                //name tipo string (%s)
                this.tipo.name());
    }

    public static Produto fromString(String linha){
        String[] partes = linha.split(";");
        int codigo = Integer.parseInt(partes[0]);
        String nome = partes[1];
        double preco = Double.parseDouble(partes[2]);
        TipoProduto tipoProduto = TipoProduto.valueOf(partes[3]);
        Produto produto = new Produto(codigo, nome, preco, tipoProduto);
        return produto;
    }

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public double getPreco() {
        return preco;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
