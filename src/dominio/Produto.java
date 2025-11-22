package dominio;

public class Produto {
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
