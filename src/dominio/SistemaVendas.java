package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaVendas {
    private Map<String, Cliente> clientesMap;
    private Map<Integer, Produto> produtosMap;
    private List<Venda> vendas;
    private int countCodigoVendas = 1;

    public SistemaVendas() {
        this.clientesMap = new HashMap<>();
        this.produtosMap = new HashMap<>();
        this.vendas = new ArrayList<>();
    }

    private int countCodigoVendas() {
        return countCodigoVendas++;
    }

    public boolean cadastrarCliente(Cliente cliente){
        String id = cliente.getIdentificador();
        if (!clientesMap.containsKey(id)){
            clientesMap.put(id, cliente);
            return true;
        }
        return false;
    }

    public boolean cadastrarProduto(Produto produto){
        int codigo = produto.getCodigo();
        if (!produtosMap.containsKey(codigo)){
            produtosMap.put(codigo, produto);
            return true;
        }
        return false;
    }
    public Cliente buscarClientePorIdentificador(String identificador){
        if (clientesMap.containsKey(identificador)){
            return clientesMap.get(identificador);
        }
        return null;
    }

    public Produto buscarProdutoPorCodigo(int codigo){
        if (produtosMap.containsKey(codigo)){
            return produtosMap.get(codigo);
        }
        return null;
    }

    public Venda buscarVendaPorCodigo(int codigo){
        return vendas.stream()
                .filter(v-> v.getCodigo()==codigo)
                .findFirst()
                .orElse(null);
    }

    public Venda iniciarNovaVenda(String identificadorCliente){
        Cliente cliente = buscarClientePorIdentificador(identificadorCliente);
        if (cliente == null){
            System.out.println("Erro: Cliente não encontrado.");
            return null;
        }
        Venda novaVenda = new Venda(countCodigoVendas(), cliente);
        this.vendas.add(novaVenda);
        return novaVenda;
    }

    public boolean adicionarItemAVenda(int codigoVenda, int codigoProduto, int quantidade){
        Venda venda = buscarVendaPorCodigo(codigoVenda);
        Produto produto = buscarProdutoPorCodigo(codigoProduto);

        if (venda == null){
            System.out.println("Erro: Venda não encontrada.");
            return false;
        }
        if (produto == null){
            System.out.println("Erro: Produto não encontrado.");
            return false;
        }
        if (quantidade <= 0){
            System.out.println("Erro: Quantidade inválida.");
            return false;
        }
        venda.adicionarItem(produto, quantidade);
        return true;
    }

    public boolean finalizarVenda(int codigoVenda) throws SaldoInsuficienteException{
        Venda venda = buscarVendaPorCodigo(codigoVenda);
        if (venda == null) {
            System.out.println("Erro: Venda não encontrada ou já finalizada.");
            return false;
        }
        return venda.finalizarVenda();
    }

}
