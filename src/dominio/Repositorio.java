package dominio;

import java.io.*;

/**
 * Repositório genérico simples que mantém itens em memória e permite
 * salvar/carregar linhas em um arquivo de texto.
 */
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repositorio<T extends Persistivel> {
    private Map<String, T> itensMap;
    private String nomeArquivo;

    public Repositorio(String nomeArquivo) {
        this.itensMap = new HashMap<>();
        this.nomeArquivo = nomeArquivo;
    }

    public void salvarParaArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.nomeArquivo))) {
            for (T item : itensMap.values()) {
                writer.write(item.toLineString());
                writer.newLine();
            }
            System.out.println("Dados salvos com sucesso em: " + this.nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados no arquivo: " + this.nomeArquivo);
        }
    }

    public List<String> carregarLinhasDoArquivo() {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    linhas.add(linha);
                }
            }
            System.out.println("Dados lidos com sucesso de: " + this.nomeArquivo);
        } catch (IOException e) {
            System.err.println("Arquivo de dados não encontrado ou erro na leitura: " + this.nomeArquivo
                    + ". Inicializado com dados vazios.");
        }
        return linhas;
    }

    public boolean adicionar(String chave, T item) {
        if (!itensMap.containsKey(chave)) {
            itensMap.put(chave, item);
            return true;
        }
        return false;
    }

    public T buscar(String chave) {
        return itensMap.get(chave);
    }

    public List<T> listarTodos() {
        return new ArrayList<>(itensMap.values());
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }
}
