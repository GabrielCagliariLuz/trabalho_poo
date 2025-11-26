package dominio;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe responsável por gerar relatórios de vendas e análises do sistema.
 * Fornece informações sobre produtos vendidos, compras por cliente,
 * clientes que mais compram e operações de monetização.
 */
public class RelatorioVendas {
    private List<Venda> vendas;
    private List<Cliente> clientes;
    private List<Produto> produtos;

    /**
     * Cria um relatório de vendas com base nas vendas e dados do sistema.
     *
     * @param vendas   lista de vendas realizadas
     * @param clientes lista de clientes cadastrados
     * @param produtos lista de produtos cadastrados
     */
    public RelatorioVendas(List<Venda> vendas, List<Cliente> clientes, List<Produto> produtos) {
        this.vendas = vendas != null ? vendas : new ArrayList<>();
        this.clientes = clientes != null ? clientes : new ArrayList<>();
        this.produtos = produtos != null ? produtos : new ArrayList<>();
    }

    /**
     * Retorna relatório de produtos vendidos com quantidade e valor total.
     *
     * @return mapa com código do produto e seus dados de venda
     */
    public Map<String, ProdutoVendidoInfo> relatorioProdurosVendidos() {
        Map<String, ProdutoVendidoInfo> produtosVendidos = new LinkedHashMap<>();

        for (Venda venda : vendas) {
            for (ItemVenda item : venda.getItens()) {
                Produto p = item.getProduto();
                String chave = p.getCodigo() + " - " + p.getNome();

                produtosVendidos.putIfAbsent(chave,
                        new ProdutoVendidoInfo(p.getCodigo(), p.getNome(), p.getPreco(), p.getTipo()));

                ProdutoVendidoInfo info = produtosVendidos.get(chave);
                info.adicionarVenda(item.getQuantidade(), item.calcularSubtotal());
            }
        }

        return produtosVendidos.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue().getValorTotal(), a.getValue().getValorTotal()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Retorna relatório de compras de um cliente específico.
     *
     * @param identificadorCliente CPF ou CNPJ do cliente
     * @return lista de vendas do cliente com detalhes
     */
    public List<VendaClienteInfo> relatorioComprasCliente(String identificadorCliente) {
        return vendas.stream()
                .filter(v -> v.getCliente().getIdentificador().equals(identificadorCliente))
                .map(v -> new VendaClienteInfo(v))
                .collect(Collectors.toList());
    }

    /**
     * Retorna ranking dos clientes que mais compram (por valor gasto).
     *
     * @return lista de clientes ordenada por gasto total (descendente)
     */
    public List<ClienteMaisCompraInfo> relatorioClientesMaisCompram() {
        Map<String, ClienteMaisCompraInfo> clientesMap = new LinkedHashMap<>();

        for (Venda venda : vendas) {
            Cliente cliente = venda.getCliente();
            String id = cliente.getIdentificador();

            clientesMap.putIfAbsent(id, new ClienteMaisCompraInfo(
                    cliente.getIdentificador(),
                    cliente.getNome(),
                    cliente instanceof ClientePF ? "PF" : "PJ"));

            ClienteMaisCompraInfo info = clientesMap.get(id);
            info.adicionarVenda(venda.calcularTotal(), venda.getItens().size());
        }

        return clientesMap.values()
                .stream()
                .sorted((a, b) -> Double.compare(b.getGastoTotal(), a.getGastoTotal()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna ranking dos clientes mais ativos em operações de monetização
     * (análise de movimentação de conta: depósitos, débitos).
     *
     * @return lista de clientes ordenada por movimentação (descendente)
     */
    public List<ClienteMovimentacaoInfo> relatorioClientesMovimentacao() {
        Map<String, ClienteMovimentacaoInfo> clientesMap = new LinkedHashMap<>();

        for (Cliente cliente : clientes) {
            String id = cliente.getIdentificador();
            double saldoAtual = cliente.getConta().getSaldo();

            // Conta número de vendas do cliente (cada venda = movimentação de débito)
            long quantidadeVendas = vendas.stream()
                    .filter(v -> v.getCliente().getIdentificador().equals(id))
                    .count();

            clientesMap.put(id, new ClienteMovimentacaoInfo(
                    id,
                    cliente.getNome(),
                    cliente instanceof ClientePF ? "PF" : "PJ",
                    saldoAtual,
                    quantidadeVendas));
        }

        return clientesMap.values()
                .stream()
                .sorted((a, b) -> Long.compare(b.getQuantidadeOperacoes(), a.getQuantidadeOperacoes()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna resumo geral do sistema de vendas.
     *
     * @return objeto com estatísticas gerais
     */
    public ResumoVendas gerarResumo() {
        double totalVendas = vendas.stream().mapToDouble(Venda::calcularTotal).sum();
        int quantidadeVendas = vendas.size();
        int quantidadeClientes = clientes.size();
        int quantidadeProdutos = produtos.size();
        double mediaPorVenda = quantidadeVendas > 0 ? totalVendas / quantidadeVendas : 0;

        return new ResumoVendas(totalVendas, quantidadeVendas, quantidadeClientes, quantidadeProdutos,
                mediaPorVenda);
    }

    /**
     * Retorna lista de produtos não vendidos.
     *
     * @return lista de produtos que não aparecem em nenhuma venda
     */
    public List<Produto> relatorioProdurosNaoVendidos() {
        Set<Integer> codigosProdutosVendidos = vendas.stream()
                .flatMap(v -> v.getItens().stream())
                .map(item -> item.getProduto().getCodigo())
                .collect(Collectors.toSet());

        return produtos.stream()
                .filter(p -> !codigosProdutosVendidos.contains(p.getCodigo()))
                .collect(Collectors.toList());
    }

    // ========== CLASSES INTERNAS PARA INFORMAÇÕES ==========

    /**
     * Informação sobre um produto vendido.
     */
    public static class ProdutoVendidoInfo {
        private int codigo;
        private String nome;
        private double preco;
        private TipoProduto tipo;
        private int quantidadeVendida;
        private double valorTotal;

        public ProdutoVendidoInfo(int codigo, String nome, double preco, TipoProduto tipo) {
            this.codigo = codigo;
            this.nome = nome;
            this.preco = preco;
            this.tipo = tipo;
            this.quantidadeVendida = 0;
            this.valorTotal = 0.0;
        }

        public void adicionarVenda(int quantidade, double valor) {
            this.quantidadeVendida += quantidade;
            this.valorTotal += valor;
        }

        public int getCodigo() {
            return codigo;
        }

        public String getNome() {
            return nome;
        }

        public double getPreco() {
            return preco;
        }

        public TipoProduto getTipo() {
            return tipo;
        }

        public int getQuantidadeVendida() {
            return quantidadeVendida;
        }

        public double getValorTotal() {
            return valorTotal;
        }

        @Override
        public String toString() {
            return String.format("Produto: %s (Cod: %d) | Qtd: %d | Valor Total: R$ %.2f",
                    nome, codigo, quantidadeVendida, valorTotal);
        }
    }

    /**
     * Informação sobre uma venda de um cliente.
     */
    public static class VendaClienteInfo {
        private int codigoVenda;
        private Date data;
        private double total;
        private int quantidadeItens;

        public VendaClienteInfo(Venda venda) {
            this.codigoVenda = venda.getCodigo();
            this.data = venda.getData();
            this.total = venda.calcularTotal();
            this.quantidadeItens = venda.getItens().size();
        }

        public int getCodigoVenda() {
            return codigoVenda;
        }

        public Date getData() {
            return data;
        }

        public double getTotal() {
            return total;
        }

        public int getQuantidadeItens() {
            return quantidadeItens;
        }

        @Override
        public String toString() {
            return String.format("Venda #%d | Data: %s | Total: R$ %.2f | Itens: %d",
                    codigoVenda, data, total, quantidadeItens);
        }
    }

    /**
     * Informação sobre cliente com histórico de compras.
     */
    public static class ClienteMaisCompraInfo {
        private String identificador;
        private String nome;
        private String tipo;
        private double gastoTotal;
        private int quantidadeCompras;

        public ClienteMaisCompraInfo(String identificador, String nome, String tipo) {
            this.identificador = identificador;
            this.nome = nome;
            this.tipo = tipo;
            this.gastoTotal = 0.0;
            this.quantidadeCompras = 0;
        }

        public void adicionarVenda(double valor, int quantidadeItens) {
            this.gastoTotal += valor;
            this.quantidadeCompras++;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getNome() {
            return nome;
        }

        public String getTipo() {
            return tipo;
        }

        public double getGastoTotal() {
            return gastoTotal;
        }

        public int getQuantidadeCompras() {
            return quantidadeCompras;
        }

        public double getTicketMedio() {
            return quantidadeCompras > 0 ? gastoTotal / quantidadeCompras : 0;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) | ID: %s | Gasto Total: R$ %.2f | Qtd Compras: %d | Ticket Médio: R$ %.2f",
                    nome, tipo, identificador, gastoTotal, quantidadeCompras, getTicketMedio());
        }
    }

    /**
     * Informação sobre movimentação de conta do cliente.
     */
    public static class ClienteMovimentacaoInfo {
        private String identificador;
        private String nome;
        private String tipo;
        private double saldoAtual;
        private long quantidadeOperacoes;

        public ClienteMovimentacaoInfo(String identificador, String nome, String tipo,
                double saldoAtual, long quantidadeOperacoes) {
            this.identificador = identificador;
            this.nome = nome;
            this.tipo = tipo;
            this.saldoAtual = saldoAtual;
            this.quantidadeOperacoes = quantidadeOperacoes;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getNome() {
            return nome;
        }

        public String getTipo() {
            return tipo;
        }

        public double getSaldoAtual() {
            return saldoAtual;
        }

        public long getQuantidadeOperacoes() {
            return quantidadeOperacoes;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) | ID: %s | Saldo: R$ %.2f | Operações: %d",
                    nome, tipo, identificador, saldoAtual, quantidadeOperacoes);
        }
    }

    /**
     * Resumo geral do sistema de vendas.
     */
    public static class ResumoVendas {
        private double totalVendas;
        private int quantidadeVendas;
        private int quantidadeClientes;
        private int quantidadeProdutos;
        private double mediaPorVenda;

        public ResumoVendas(double totalVendas, int quantidadeVendas, int quantidadeClientes,
                int quantidadeProdutos, double mediaPorVenda) {
            this.totalVendas = totalVendas;
            this.quantidadeVendas = quantidadeVendas;
            this.quantidadeClientes = quantidadeClientes;
            this.quantidadeProdutos = quantidadeProdutos;
            this.mediaPorVenda = mediaPorVenda;
        }

        public double getTotalVendas() {
            return totalVendas;
        }

        public int getQuantidadeVendas() {
            return quantidadeVendas;
        }

        public int getQuantidadeClientes() {
            return quantidadeClientes;
        }

        public int getQuantidadeProdutos() {
            return quantidadeProdutos;
        }

        public double getMediaPorVenda() {
            return mediaPorVenda;
        }

        @Override
        public String toString() {
            return String.format(
                    "=== RESUMO DE VENDAS ===\n" +
                            "Total de Vendas: R$ %.2f\n" +
                            "Quantidade de Vendas: %d\n" +
                            "Quantidade de Clientes: %d\n" +
                            "Quantidade de Produtos: %d\n" +
                            "Ticket Médio: R$ %.2f",
                    totalVendas, quantidadeVendas, quantidadeClientes, quantidadeProdutos, mediaPorVenda);
        }
    }
}
