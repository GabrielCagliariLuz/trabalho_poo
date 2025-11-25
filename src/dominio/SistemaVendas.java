package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaVendas {
    //private Map<String, Cliente> clientesMap;
    //private Map<Integer, Produto> produtosMap;
    private Repositorio<Cliente> clientesRepositorio;
    private Repositorio<Produto> produtoRepositorio;
    private List<Venda> vendas;
    private int countCodigoVendas = 1;

    public SistemaVendas() {
        this.clientesRepositorio = new Repositorio<>("clientes.txt");
        this.produtoRepositorio = new Repositorio<>("produtos.txt");
        this.vendas = new ArrayList<>();
    }

    private int countCodigoVendas() {
        return countCodigoVendas++;
    }

    public boolean cadastrarCliente(Cliente cliente){
        String id = cliente.getIdentificador();
        return clientesRepositorio.adicionar(id, cliente);
    }

    public boolean cadastrarProduto(Produto produto){
        String codigoStr = String.valueOf(produto.getCodigo());
        return produtoRepositorio.adicionar(codigoStr, produto);

    }
    public Cliente buscarClientePorIdentificador(String identificador){
        return clientesRepositorio.buscar(identificador);
    }

    public Produto buscarProdutoPorCodigo(int codigo){
        return produtoRepositorio.buscar(String.valueOf(codigo));
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
