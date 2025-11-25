package interfaceGrafica;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import dominio.*;

public class TelaPrincipal extends JFrame {

    private SistemaVendas sistema;

    public TelaPrincipal() {
        sistema = new SistemaVendas();
        sistema.carregarDados(); // Carrega os arquivos .txt ao abrir

        setTitle("Sistema de Vendas com Monetização - Trabalho POO");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Salvar dados ao fechar a janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sistema.salvarDados();
                JOptionPane.showMessageDialog(null, "Dados salvos com sucesso!");
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Clientes", criarPainelClientes());
        tabbedPane.add("Produtos", criarPainelProdutos());
        tabbedPane.add("Vendas", criarPainelVendas());
        tabbedPane.add("Monetização", criarPainelMonetizacao());

        add(tabbedPane);
    }

    // --- PAINEL DE CLIENTES ---
    private JPanel criarPainelClientes() {
        JPanel panel = new JPanel(new BorderLayout());

        // Formulário
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDocumento = new JTextField(); // CPF ou CNPJ
        JTextField txtRazaoSocial = new JTextField(); // Só para PJ

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Pessoa Física", "Pessoa Jurídica"});

        formPanel.add(new JLabel("Tipo de Cliente:"));
        formPanel.add(cbTipo);
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("CPF / CNPJ:"));
        formPanel.add(txtDocumento);

        JLabel lblRazao = new JLabel("Razão Social (PJ):");
        formPanel.add(lblRazao);
        formPanel.add(txtRazaoSocial);

        txtRazaoSocial.setEnabled(false); // Começa desabilitado pois PF é padrão

        // Evento para habilitar/desabilitar Razão Social
        cbTipo.addActionListener(e -> {
            boolean isPJ = cbTipo.getSelectedItem().equals("Pessoa Jurídica");
            txtRazaoSocial.setEnabled(isPJ);
            if (!isPJ) txtRazaoSocial.setText("");
        });

        JButton btnSalvar = new JButton("Cadastrar Cliente");
        formPanel.add(new JLabel("")); // Espaço vazio
        formPanel.add(btnSalvar);

        // Tabela de Listagem
        String[] colunas = {"ID/Doc", "Nome", "Email", "Tipo", "Saldo"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(model);
        atualizarTabelaClientes(model); // Método auxiliar para preencher tabela

        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText();
                String email = txtEmail.getText();
                String doc = txtDocumento.getText();
                Conta conta = new Conta(sistema.listarClientes().size() + 1); // Gera numero conta simples

                Cliente novoCliente;
                if (cbTipo.getSelectedItem().equals("Pessoa Física")) {
                    novoCliente = new ClientePF(nome, email, conta, doc);
                } else {
                    String razao = txtRazaoSocial.getText();
                    novoCliente = new ClientePJ(nome, email, conta, doc, razao);
                }

                if (sistema.cadastrarCliente(novoCliente)) {
                    JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                    atualizarTabelaClientes(model);
                    // Limpar campos
                    txtNome.setText(""); txtEmail.setText(""); txtDocumento.setText(""); txtRazaoSocial.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: Cliente já existe.");
                }
            } catch (DocumentoInvalidoException ex) {
                JOptionPane.showMessageDialog(this, "Erro de Validação: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    // --- PAINEL DE PRODUTOS ---
    private JPanel criarPainelProdutos() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField txtCodigo = new JTextField();
        JTextField txtNome = new JTextField();
        JTextField txtPreco = new JTextField();
        JComboBox<TipoProduto> cbTipo = new JComboBox<>(TipoProduto.values());

        formPanel.add(new JLabel("Código (Numérico):"));
        formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Nome do Produto:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("Preço:"));
        formPanel.add(txtPreco);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(cbTipo);

        JButton btnSalvar = new JButton("Cadastrar Produto");
        formPanel.add(new JLabel(""));
        formPanel.add(btnSalvar);

        String[] colunas = {"Código", "Nome", "Preço", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(model);
        atualizarTabelaProdutos(model);

        btnSalvar.addActionListener(e -> {
            try {
                int codigo = Integer.parseInt(txtCodigo.getText());
                String nome = txtNome.getText();
                double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
                TipoProduto tipo = (TipoProduto) cbTipo.getSelectedItem();

                Produto p = new Produto(codigo, nome, preco, tipo);
                if (sistema.cadastrarProduto(p)) {
                    JOptionPane.showMessageDialog(this, "Produto cadastrado!");
                    atualizarTabelaProdutos(model);
                    txtCodigo.setText(""); txtNome.setText(""); txtPreco.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: Código de produto já existe.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Verifique se Código e Preço são números válidos.");
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- PAINEL DE VENDAS ---
    private JPanel criarPainelVendas() {
        JPanel panel = new JPanel(new BorderLayout());

        // Área superior: Iniciar Venda
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField txtIdCliente = new JTextField(15);
        JButton btnIniciar = new JButton("Iniciar Nova Venda (CPF/CNPJ)");
        topPanel.add(new JLabel("ID Cliente:"));
        topPanel.add(txtIdCliente);
        topPanel.add(btnIniciar);

        // Área central: Adicionar itens e ver lista
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel itemPanel = new JPanel(new FlowLayout());
        JTextField txtCodProduto = new JTextField(10);
        JTextField txtQtd = new JTextField(5);
        JButton btnAdicionar = new JButton("Adicionar Item");

        itemPanel.add(new JLabel("Cód. Produto:"));
        itemPanel.add(txtCodProduto);
        itemPanel.add(new JLabel("Qtd:"));
        itemPanel.add(txtQtd);
        itemPanel.add(btnAdicionar);

        // Estado da venda atual
        final Venda[] vendaAtual = {null};

        String[] colunas = {"Produto", "Preço Unit.", "Qtd", "Subtotal"};
        DefaultTableModel modelItens = new DefaultTableModel(colunas, 0);
        JTable tableItens = new JTable(modelItens);
        JLabel lblTotal = new JLabel("Total da Venda: R$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        centerPanel.add(itemPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tableItens), BorderLayout.CENTER);
        centerPanel.add(lblTotal, BorderLayout.SOUTH);

        // Área inferior: Finalizar
        JButton btnFinalizar = new JButton("FINALIZAR VENDA (Debitar Saldo)");
        btnFinalizar.setBackground(new Color(100, 200, 100)); // Verde

        // Lógica dos Botões
        itemPanel.setVisible(false); // Esconde adição de itens até iniciar venda
        btnFinalizar.setEnabled(false);

        btnIniciar.addActionListener(e -> {
            String id = txtIdCliente.getText();
            vendaAtual[0] = sistema.iniciarNovaVenda(id);
            if (vendaAtual[0] != null) {
                JOptionPane.showMessageDialog(this, "Venda iniciada para: " + vendaAtual[0].getCliente().getNome());
                itemPanel.setVisible(true);
                btnFinalizar.setEnabled(true);
                modelItens.setRowCount(0); // Limpa tabela visual
                lblTotal.setText("Total da Venda: R$ 0.00");
            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado!");
            }
        });

        btnAdicionar.addActionListener(e -> {
            try {
                if (vendaAtual[0] == null) return;
                int codProd = Integer.parseInt(txtCodProduto.getText());
                int qtd = Integer.parseInt(txtQtd.getText());

                // O método adicionarItemAVenda do backend pede o ID da venda.
                // Como temos o objeto venda, podemos pegar o ID dele.
                boolean sucesso = sistema.adicionarItemAVenda(vendaAtual[0].getCodigo(), codProd, qtd);

                if (sucesso) {
                    atualizarTabelaItens(modelItens, vendaAtual[0], lblTotal);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao adicionar item (Verifique produto/estoque)");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Números inválidos.");
            }
        });

        btnFinalizar.addActionListener(e -> {
            if (vendaAtual[0] == null) return;
            try {
                boolean sucesso = sistema.finalizarVenda(vendaAtual[0].getCodigo());
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Venda Finalizada! Saldo debitado.");
                    // Resetar tela
                    vendaAtual[0] = null;
                    modelItens.setRowCount(0);
                    itemPanel.setVisible(false);
                    btnFinalizar.setEnabled(false);
                    lblTotal.setText("Total da Venda: R$ 0.00");
                    txtIdCliente.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao finalizar (Venda já finalizada ou erro interno).");
                }
            } catch (SaldoInsuficienteException ex) {
                JOptionPane.showMessageDialog(this, "ERRO: Saldo Insuficiente na conta do cliente!");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(btnFinalizar, BorderLayout.SOUTH);

        return panel;
    }

    // --- PAINEL DE MONETIZAÇÃO ---
    private JPanel criarPainelMonetizacao() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtIdCliente = new JTextField(20);
        JTextField txtValor = new JTextField(10);
        JButton btnDepositar = new JButton("Realizar Depósito");
        JButton btnConsultar = new JButton("Consultar Saldo");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID do Cliente (CPF/CNPJ):"), gbc);
        gbc.gridx = 1;
        panel.add(txtIdCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Valor R$:"), gbc);
        gbc.gridx = 1;
        panel.add(txtValor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnDepositar, gbc);

        gbc.gridy = 3;
        panel.add(btnConsultar, gbc);

        btnDepositar.addActionListener(e -> {
            try {
                String id = txtIdCliente.getText();
                double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
                Cliente c = sistema.buscarClientePorIdentificador(id);

                if (c != null) {
                    c.getConta().depositar(valor);
                    JOptionPane.showMessageDialog(this, "Depósito realizado! Novo saldo: R$ " + c.getConta().getSaldo());
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido.");
            }
        });

        btnConsultar.addActionListener(e -> {
            String id = txtIdCliente.getText();
            Cliente c = sistema.buscarClientePorIdentificador(id);
            if (c != null) {
                JOptionPane.showMessageDialog(this, "Cliente: " + c.getNome() + "\nSaldo: R$ " + c.getConta().getSaldo());
            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
            }
        });

        return panel;
    }

    // --- MÉTODOS AUXILIARES ---
    private void atualizarTabelaClientes(DefaultTableModel model) {
        model.setRowCount(0); // Limpa
        // Requer que você adicione listarClientes() no SistemaVendas
        try {
            for (Cliente c : sistema.listarClientes()) {
                String tipo = (c instanceof ClientePF) ? "PF" : "PJ";
                model.addRow(new Object[]{c.getIdentificador(), c.getNome(), c.getEmail(), tipo, c.getConta().getSaldo()});
            }
        } catch (Exception e) {
            // Caso o método não exista ainda, evita quebrar a tela
            System.err.println("Método listarClientes não implementado no backend ainda.");
        }
    }

    private void atualizarTabelaProdutos(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            for (Produto p : sistema.listarProdutos()) {
                model.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getPreco(), p.getTipo()});
            }
        } catch (Exception e) {
            System.err.println("Método listarProdutos não implementado no backend ainda.");
        }
    }

    private void atualizarTabelaItens(DefaultTableModel model, Venda venda, JLabel lblTotal) {
        model.setRowCount(0);
        for(ItemVenda item : venda.getItens()){
            model.addRow(new Object[]{
                    item.getProduto().getNome(),
                    item.getProduto().getPreco(),
                    item.getQuantidade(),
                    item.calcularSubtotal()
            });
        }
        lblTotal.setText("Total da Venda: R$ " + String.format("%.2f", venda.calcularTotal()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
