package dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaVendas {
    private Map<String, Cliente> clientesMap;
    private Map<Integer, Produto> produtosMap;
    private List<Venda> vendas;

    public SistemaVendas() {
        this.clientesMap = new HashMap<>();
        this.produtosMap = new HashMap<>();
        this.vendas = new ArrayList<>();
    }

    public boolean cadastrarCliente(Cliente cliente){
        if (!clientesMap.containsKey(cliente.getCpf())){
            clientesMap.put(cliente.getCpf(), cliente);
            return true;
        }
        return false;
    }

}
