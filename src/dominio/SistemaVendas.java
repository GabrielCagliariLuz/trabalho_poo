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
        String id = cliente.getIdentificador();
        if (!clientesMap.containsKey(id)){
            clientesMap.put(id, cliente);
            return true;
        }
        return false;
    }

}
