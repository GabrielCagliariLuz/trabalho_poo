# Documentação: pacotes `dominio` e `interfaceGrafica`

Este documento descreve, de forma concisa, cada arquivo presente nos pacotes `src/dominio` e `src/interfaceGrafica` do projeto.

**Organização**

- **Pacote:** `src/dominio` — modelos de domínio, exceções e repositório simples para persistência em arquivo.
- **Pacote:** `src/interfaceGrafica` — interface gráfica Swing que consome o `SistemaVendas`.

---

**`src/dominio/Cliente.java`**:

- **Propósito:** Classe abstrata que modela um cliente genérico (PF ou PJ). Define campos comuns e contrato de persistência.
- **Principais campos:** `nome` (String), `email` (String), `conta` (Conta).
- **Métodos importantes:**
  - `getIdentificador()` (abstract): obrigatório nas subclasses para retornar CPF/CNPJ.
  - getters/setters: `getNome()`, `getEmail()`, `getConta()`, `setEmail(...)`, `setConta(...)`.
- **Persistência:** implementa `Persistivel` (contrato `toLineString()` definido nas subclasses).

**`src/dominio/ClientePF.java`**:

- **Propósito:** Representa Pessoa Física (CPF).
- **Campos:** `cpf` (String).
- **Validação:** no construtor valida se `cpf` é não-nulo e possui 11 caracteres; caso contrário lança `DocumentoInvalidoException`.
- **Persistência / formato de arquivo:**
  - `toLineString()` grava linha com prefixo `PF;` e os campos: identificador, nome, email, número da conta e saldo.
  - `fromString(String linha)` reconstrói `ClientePF` a partir de uma linha (usa índices esperados após split `;`).
- **Identificador:** retorna `cpf` em `getIdentificador()`.

**`src/dominio/ClientePJ.java`**:

- **Propósito:** Representa Pessoa Jurídica (CNPJ + razão social).
- **Campos:** `cnpj` (String), `razaoSocial` (String).
- **Validação:** no construtor valida se `cnpj` tem 14 caracteres; caso inválido lança `DocumentoInvalidoException`.
- **Persistência / formato de arquivo:**
  - `toLineString()` grava com prefixo `PJ;` e inclui `razaoSocial`.
  - `fromString(String linha)` recria `ClientePJ` a partir da linha formatada.
- **Identificador:** retorna `cnpj` em `getIdentificador()`.

**`src/dominio/Conta.java`**:

- **Propósito:** Modela conta com `numero` e `saldo` e operações financeiras básicas.
- **Campos:** `numero` (int), `saldo` (double).
- **Principais métodos:**
  - `depositar(double valor)` — adiciona saldo se `valor > 0`.
  - `debitar(double valor)` — valida valor >0; se saldo insuficiente lança `SaldoInsuficienteException`; caso sucesso debita e retorna true.
  - `transferir(Conta destino, double valor)` — usa `debitar` + `depositar`.
  - getters: `getNumero()`, `getSaldo()`.
  - `toLineString()` — formato `numero;saldo` para persistência.

**`src/dominio/DocumentoInvalidoException.java`**:

- **Propósito:** `RuntimeException` usada para sinalizar CPF/CNPJ inválido.

**`src/dominio/ItemVenda.java`**:

- **Propósito:** Representa um item de uma `Venda` (produto + quantidade).
- **Campos:** `produto` (Produto), `quantidade` (int).
- **Métodos:** `calcularSubtotal()` = `produto.getPreco() * quantidade`.

**`src/dominio/Persistivel.java`**:

- **Propósito:** Interface simples para objetos que podem ser convertidos em linha de texto para gravação em arquivo.
- **Contrato:** `String toLineString()` (implementado por `Produto`, `Conta`, `ClientePF`, `ClientePJ`, etc.).

**`src/dominio/Produto.java`**:

- **Propósito:** Modela um produto com código, nome, preço e tipo.
- **Campos:** `codigo` (int), `nome` (String), `preco` (double), `tipo` (TipoProduto).
- **Métodos importantes:**
  - `toLineString()` — retorno no formato `codigo;nome;preco;tipo` (usa `tipo.name()`).
  - `fromString(String linha)` — reconstrói o objeto a partir da linha (faz parse dos índices esperados).
  - getters e `setPreco(...)`.

**`src/dominio/Repositorio.java`**:

- **Propósito:** Repositório genérico em memória com persistência simples por arquivo de texto.
- **Tipo genérico:** `Repositorio<T extends Persistivel>` — armazena itens em `Map<String,T>` onde a chave é fornecida externamente.
- **Construtor:** recebe `nomeArquivo` (String) — arquivo usado para salvar/carregar.
- **Operações:**
  - `salvarParaArquivo()` — escreve cada `toLineString()` em `nomeArquivo` (sobrescreve).
  - `carregarLinhasDoArquivo()` — lê linhas não-vazias e retorna `List<String>`.
  - `adicionar(String chave, T item)` — adiciona se não existir a chave.
  - `buscar(String chave)` — obtém item pelo id.
  - `listarTodos()` — retorna lista dos valores.
  - `getNomeArquivo()` — retorna o nome do arquivo.
- **Observações:** A classe trata erros de IO apenas exibindo mensagens no console; a lógica de conversão das linhas em objetos ocorre fora do repositório (ex.: `SistemaVendas`).

**`src/dominio/SaldoInsuficienteException.java`**:

- **Propósito:** Exceção verificada (`Exception`) lançada quando tentativa de débito excede o saldo.

**`src/dominio/SistemaVendas.java`**:

- **Propósito:** Camada de negócio que coordena repositórios de clientes e produtos, gerencia vendas e persistência.
- **Campos principais:**
  - `Repositorio<Cliente> clientesRepositorio` — arquivo `clientes.txt`.
  - `Repositorio<Produto> produtoRepositorio` — arquivo `produtos.txt`.
  - `List<Venda> vendas` — vendas em memória.
  - contador de códigos de venda `countCodigoVendas`.
- **Funcionalidades:**
  - cadastrar cliente/produto (`cadastrarCliente`, `cadastrarProduto`) — usa `Repositorio.adicionar`.
  - buscar cliente/produto/venda por ID.
  - iniciar nova venda (`iniciarNovaVenda`) — cria `Venda` associada a cliente existente.
  - adicionar item à venda (`adicionarItemAVenda`) — valida existência e quantidade.
  - finalizar venda (`finalizarVenda`) — delega para `Venda.finalizarVenda()` e pode lançar `SaldoInsuficienteException`.
  - carregarDados(): lê linhas dos repositórios e reconstrói `Produto` e `Cliente` (identifica PF/PJ por prefixo `PF;` ou `PJ;`).
  - salvarDados(): delega a `Repositorio.salvarParaArquivo()`.
  - listarClientes/listarProdutos(): retornam todos os itens dos repositórios.
- **Observações:** A lógica de parsing depende do formato definido em `toLineString()` de cada tipo.

**`src/dominio/TipoProduto.java`**:

- **Propósito:** Enum com categorias: `FISICO`, `DIGITAL`.

**`src/dominio/Venda.java`**:

- **Propósito:** Agrega itens (`ItemVenda`) associados a um `Cliente` e calcula total.
- **Campos:** `codigo`, `data` (Date), `cliente` (Cliente), `itens` (List<ItemVenda>).
- **Métodos-chave:**
  - `adicionarItem(Produto produto, int quantidade)` — cria `ItemVenda` e adiciona na lista.
  - `calcularTotal()` — soma subtotais dos itens.
  - `finalizarVenda()` — tenta debitar o total da `Conta` do cliente, lançando `SaldoInsuficienteException` se necessário.

---

**`src/interfaceGrafica/TelaPrincipal.java`**:

- **Propósito:** UI Swing principal que permite cadastrar clientes/produtos, iniciar vendas, adicionar itens, finalizar vendas e efetuar depósitos (monetização).
- **Integração com backend:** utiliza `SistemaVendas` para carregar/salvar dados e operações de negócio.
- **Abas principais:**
  - `Clientes`: formulário para cadastrar `ClientePF` ou `ClientePJ`, tabela de listagem que usa `sistema.listarClientes()`.
  - `Produtos`: formulário para cadastrar `Produto`, tabela com `sistema.listarProdutos()`.
  - `Vendas`: iniciar nova venda por identificador (CPF/CNPJ), adicionar itens por código do produto, visualizar itens e finalizar (debitar conta).
  - `Monetização`: depositar valores em conta do cliente e consultar saldo.
- **Comportamentos notáveis:**
  - Ao abrir a aplicação, chama `sistema.carregarDados()`.
  - Ao fechar a janela, chama `sistema.salvarDados()` e mostra diálogo de confirmação.
  - Tratamento de erros com `JOptionPane` para feedback ao usuário (ex.: `DocumentoInvalidoException`, `NumberFormatException`, `SaldoInsuficienteException`).

---

**Observações gerais e recomendações rápidas**

- Os formatos de persistência (linha por objeto) são definidos em `toLineString()` de cada classe; manter consistência entre `toLineString()` e `fromString()` é crítico.
- `Repositorio` delega parsing ao consumidor — poderia ser estendido com `Function<String,T>` para maior reutilização/segurança.
- As validações de CPF/CNPJ são apenas checagens de comprimento — se desejar validação real, implementar algoritmo de verificação de dígitos.
- `Conta.debitar(...)` lança `SaldoInsuficienteException` (exceção verificada), enquanto `DocumentoInvalidoException` é runtime — considere uniformizar tipo de exceção conforme comportamento esperado.

Arquivo gerado automaticamente pelo assistente em resposta à solicitação de documentação.
