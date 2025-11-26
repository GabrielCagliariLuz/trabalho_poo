package dominio;

/**
 * Representa um item dentro de uma venda: produto + quantidade.
 */
public class ItemVenda {
    private Produto produto;
    private int quantidade;

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public double calcularSubtotal() {
        return produto.getPreco() * quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
