package dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representa uma venda composta por itens, data e cliente responsável.
 * Permite calcular o total e finalizar debitando a conta do cliente.
 */
public class Venda {
    private int codigo;
    private Date data;
    private Cliente cliente;
    private List<ItemVenda> itens;

    public Venda(int codigo, Cliente cliente) {
        this.codigo = codigo;
        this.data = new Date();
        this.cliente = cliente;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        ItemVenda item = new ItemVenda(produto, quantidade);
        this.itens.add(item);
    }

    public double calcularTotal() {
        double total = 0.0;
        for (ItemVenda item : itens) {
            total += item.calcularSubtotal();
        }
        return total;
    }

    public boolean finalizarVenda() throws SaldoInsuficienteException {
        double total = this.calcularTotal();
        return cliente.getConta().debitar(total);
    }

    public int getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Date getData() {
        return data;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }
}
