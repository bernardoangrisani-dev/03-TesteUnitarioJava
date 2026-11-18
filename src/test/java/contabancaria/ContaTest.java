package contabancaria;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    // --- Testes do Construtor (Já existentes no projeto) ---

    @Test
    void construtor_DadosValidos_CriaContaCorretamente() {
        var conta = new Conta("Maria", 100.0);
        assertEquals("Maria", conta.getTitular());
        assertEquals(100.0, conta.getSaldo(), 0.001);
        assertTrue(conta.isAtiva());
    }

    @Test
    void construtor_SemSaldoInicial_CriaContaComSaldoZero() {
        var conta = new Conta("João");
        assertEquals("João", conta.getTitular());
        assertEquals(0.0, conta.getSaldo(), 0.001);
        assertTrue(conta.isAtiva());
    }

    @Test
    void construtor_TitularNulo_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Conta(null, 100.0));
    }

    @Test
    void construtor_TitularVazio_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Conta("", 100.0));
        assertThrows(IllegalArgumentException.class, () -> new Conta("   ", 100.0));
    }

    @Test
    void construtor_SaldoNegativo_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Conta("Maria", -1.0));
    }

    @ParameterizedTest
    @CsvSource({
            "Ana, 0.0",
            "Carlos, 50.5",
            "Beatriz, 1000.0"
    })
    void construtor_VariosValoresValidos_CriaContaCorretamente(String titular, double saldo) {
        var conta = new Conta(titular, saldo);
        assertEquals(titular, conta.getTitular());
        assertEquals(saldo, conta.getSaldo(), 0.001);
        assertTrue(conta.isAtiva());
    }

    // --- Novos Testes (Parte 2 - TDD) ---

    @Test
    void depositar_ValorValido_AtualizaSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act
        conta.depositar(50);

        // Assert
        assertEquals(150, conta.getSaldo(), 0.001);
    }

    @Test
    void depositar_ValorZero_LancaIllegalArgumentException() {
        var conta = new Conta("Maria", 100);

        assertThrows(IllegalArgumentException.class, () -> conta.depositar(0));
        assertEquals(100, conta.getSaldo(), 0.001);
    }

    @Test
    void depositar_ValorNegativo_LancaIllegalArgumentException() {
        var conta = new Conta("Maria", 100);

        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-10));
        assertEquals(100, conta.getSaldo(), 0.001);
    }

    @Test
    void sacar_ValorValido_AtualizaSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act
        conta.sacar(40);

        // Assert
        assertEquals(60, conta.getSaldo(), 0.001);
    }

    @Test
    void sacar_ValorZeroOuNegativo_LancaIllegalArgumentException() {
        var conta = new Conta("Maria", 100);

        assertThrows(IllegalArgumentException.class, () -> conta.sacar(0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-10));
        assertEquals(100, conta.getSaldo(), 0.001); // Confirma que o saldo foi preservado
    }

    @Test
    void encerrar_ComSaldoZero_InativaConta() {
        // Arrange
        var conta = new Conta("Maria", 0);

        // Act
        conta.encerrar();

        // Assert
        assertFalse(conta.isAtiva());
    }

    @Test
    void encerrar_ComSaldoPositivo_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 10.0);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.encerrar());
        assertTrue(conta.isAtiva());
        assertEquals(10.0, conta.getSaldo(), 0.001);
    }

    @Test
    void encerrar_ContaJaInativa_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 0);
        conta.encerrar(); // Encerra a conta pela primeira vez (fica inativa)

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.encerrar()); // Tenta encerrar de novo
        assertFalse(conta.isAtiva());
    }

    @Test
    void depositar_ContaEncerrada_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 100);
        conta.sacar(100); // Zera o saldo para permitir o encerramento
        conta.encerrar(); // Encerra a conta (saldo fica 0 e ativa = false)

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.depositar(50));
        assertEquals(0, conta.getSaldo(), 0.001);
    }

    @Test
    void sacar_ContaEncerrada_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 100);
        conta.sacar(100); // Zera o saldo
        conta.encerrar(); // Encerra a conta

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.sacar(10));
        assertEquals(0, conta.getSaldo(), 0.001);
    }

    @Test
    void transferir_ValorValido_AtualizaSaldosDeAmbasContas() {
        // Arrange
        var contaOrigem = new Conta("Maria", 200.0);
        var contaDestino = new Conta("João", 50.0);

        // Act
        contaOrigem.transferir(50.0, contaDestino);

        // Assert
        assertEquals(150.0, contaOrigem.getSaldo(), 0.001);
        assertEquals(100.0, contaDestino.getSaldo(), 0.001);
    }

    @Test
    void transferir_SaldoInsuficiente_LancaIllegalStateException() {
        // Arrange
        var contaOrigem = new Conta("Maria", 50.0);
        var contaDestino = new Conta("João", 100.0);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> contaOrigem.transferir(100.0, contaDestino));
        assertEquals(50.0, contaOrigem.getSaldo(), 0.001); // Saldo preservado
        assertEquals(100.0, contaDestino.getSaldo(), 0.001); // Destino não alterado
    }
}