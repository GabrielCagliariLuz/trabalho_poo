package dominio;

public class Conta implements Persistivel{
    private int numero;
    private double saldo;

    public Conta(int numero) {
        this.numero = numero;
        this.saldo = 0.0;
    }

    public boolean depositar(double valor){
        if (valor > 0){
            this.saldo += valor;
            return true;
        }
        return false;
    }

    public boolean debitar(double valor) throws SaldoInsuficienteException{
        if (valor <= 0 ){
            throw new IllegalArgumentException("Valor de débito inválido.");
        }
        if (this.saldo >= valor){
            this.saldo -= valor;
            return true;
        }else {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
    }

    public boolean transferir(Conta destino, double valor) throws SaldoInsuficienteException{
        if (this.debitar(valor)){
            return destino.depositar(valor);
        }
        return false;
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toLineString() {
        return String.format("%d;%.2f",
                this.numero,
                this.saldo);
    }
}
