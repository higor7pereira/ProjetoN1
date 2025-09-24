import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class mackshop {

    static int[] idsProdutos = {101, 203, 401};
    static String[] nomesProdutos = {"Mouse Gamer", "Teclado Mecanico", "Headset 7.1"};
    static double[] precosProdutos = {150.0, 350.0, 420.5};
    static int[] estoquesProdutos = {10, 5, 8};

    static int[] vendaAtualIds = new int[100];
    static int[] vendaAtualQuantidades = new int[100];
    static int vendaAtualCount = 0;

    static int[] historicoIdsPedidos = new int[100];
    static double[] historicoValoresPedidos = new double[100];
    static int[][] historicoItensVendidos = new int[1000][3];
    static int historicoCount = 0;
    static int historicoItensCount = 0;
    static int proximoIdPedido = 1001;

    static boolean baseInicializada = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n====== MACKSHOP ======");
            System.out.println("1. Inicializar base");
            System.out.println("2. Exibir catalogo de produtos");
            System.out.println("3. Adicionar item a venda");
            System.out.println("4. Ver resumo da venda atual");
            System.out.println("5. Finalizar venda");
            System.out.println("6. Ver historico de vendas");
            System.out.println("7. Buscar venda especifica do historico");
            System.out.println("8. (Admin) Repor estoque");
            System.out.println("9. (Admin) Relatorio de estoque baixo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");
            opcao = sc.nextInt();

            if (!baseInicializada && opcao != 1 && opcao != 0) {
                System.out.println("Voce precisa inicializar a base antes de usar o sistema!");
                continue;
            }

            switch (opcao) {
                case 1: inicializarBase(); break;
                case 2: exibirCatalogo(); break;
                case 3: adicionarItemVenda(sc); break;
                case 4: verResumoVenda(); break;
                case 5: finalizarVenda(); break;
                case 6: verHistorico(); break;
                case 7: buscarVenda(sc); break;
                case 8: reporEstoque(sc); break;
                case 9: relatorioEstoqueBaixo(); break;
                case 0: System.out.println("Saindo..."); break;
                default: System.out.println("Opcao invalida!");
            }
        } while (opcao != 0);

        sc.close();
    }

    static void inicializarBase() {
        vendaAtualCount = 0;
        historicoCount = 0;
        historicoItensCount = 0;
        proximoIdPedido = 1001;
        baseInicializada = true;
        System.out.println("Base de dados inicializada com sucesso!");
    }

    static void exibirCatalogo() {
        System.out.println("\n--- Catalogo de Produtos ---");
        System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Produto", "Preco", "Estoque");
        for (int i = 0; i < idsProdutos.length; i++) {
            if (estoquesProdutos[i] > 0) {
                System.out.printf("%-5d %-20s R$ %-8.2f %-10d\n",
                        idsProdutos[i], nomesProdutos[i], precosProdutos[i], estoquesProdutos[i]);
            }
        }
    }

    static void adicionarItemVenda(Scanner sc) {
        System.out.print("Digite o ID do produto: ");
        int id = sc.nextInt();
        System.out.print("Digite a quantidade: ");
        int qtd = sc.nextInt();

        int index = buscarProdutoPorId(id);
        if (index == -1) {
            System.out.println("Produto nao encontrado!");
            return;
        }

        if (estoquesProdutos[index] < qtd) {
            System.out.println("Estoque insuficiente!");
            return;
        }

        vendaAtualIds[vendaAtualCount] = id;
        vendaAtualQuantidades[vendaAtualCount] = qtd;
        vendaAtualCount++;
        System.out.println("Item adicionado a venda!");
    }

    static void verResumoVenda() {
        System.out.println("\n--- Resumo da Venda Atual ---");
        double total = 0;
        for (int i = 0; i < vendaAtualCount; i++) {
            int index = buscarProdutoPorId(vendaAtualIds[i]);
            double subtotal = precosProdutos[index] * vendaAtualQuantidades[i];
            total += subtotal;
            System.out.printf("%s (x%d) - R$ %.2f\n",
                    nomesProdutos[index], vendaAtualQuantidades[i], subtotal);
        }
        System.out.printf("TOTAL: R$ %.2f\n", total);
    }

    static void finalizarVenda() {
        if (vendaAtualCount == 0) {
            System.out.println("Nenhum item na venda atual!");
            return;
        }

        double total = 0;
        for (int i = 0; i < vendaAtualCount; i++) {
            int index = buscarProdutoPorId(vendaAtualIds[i]);
            double subtotal = precosProdutos[index] * vendaAtualQuantidades[i];
            total += subtotal;

            estoquesProdutos[index] -= vendaAtualQuantidades[i];

            historicoItensVendidos[historicoItensCount][0] = proximoIdPedido;
            historicoItensVendidos[historicoItensCount][1] = vendaAtualIds[i];
            historicoItensVendidos[historicoItensCount][2] = vendaAtualQuantidades[i];
            historicoItensCount++;
        }

        historicoIdsPedidos[historicoCount] = proximoIdPedido;
        historicoValoresPedidos[historicoCount] = total;
        historicoCount++;

        imprimirNotaFiscal(proximoIdPedido, total);

        vendaAtualCount = 0;
        proximoIdPedido++;
    }

    static void verHistorico() {
        System.out.println("\n--- Historico de Vendas ---");
        for (int i = 0; i < historicoCount; i++) {
            System.out.printf("Pedido ID: %d - Valor Total: R$ %.2f\n",
                    historicoIdsPedidos[i], historicoValoresPedidos[i]);
        }
    }

    static void buscarVenda(Scanner sc) {
        System.out.print("Digite o ID do pedido: ");
        int idPedido = sc.nextInt();

        boolean encontrado = false;
        for (int i = 0; i < historicoCount; i++) {
            if (historicoIdsPedidos[i] == idPedido) {
                imprimirNotaFiscal(idPedido, historicoValoresPedidos[i]);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("Pedido nao encontrado!");
        }
    }

    static void reporEstoque(Scanner sc) {
        System.out.print("Digite o ID do produto: ");
        int id = sc.nextInt();
        System.out.print("Digite a quantidade a repor: ");
        int qtd = sc.nextInt();

        int index = buscarProdutoPorId(id);
        if (index == -1) {
            System.out.println("Produto nao encontrado!");
            return;
        }

        estoquesProdutos[index] += qtd;
        System.out.println("Estoque atualizado!");
    }

    static void relatorioEstoqueBaixo() {
        System.out.println("\n--- Relatorio de Estoque Baixo ---");
        int limite = 10;
        for (int i = 0; i < idsProdutos.length; i++) {
            if (estoquesProdutos[i] < limite) {
                System.out.printf("%s - Estoque: %d\n", nomesProdutos[i], estoquesProdutos[i]);
            }
        }
    }

    static int buscarProdutoPorId(int id) {
        for (int i = 0; i < idsProdutos.length; i++) {
            if (idsProdutos[i] == id) return i;
        }
        return -1;
    }

    static void imprimirNotaFiscal(int idPedido, double total) {
        System.out.println("*********************************************************************************************");
        System.out.println("* MACKSHOP                                                                                  *");
        System.out.println("* CNPJ: 12.345.678/0001-99                                                                  *");
        System.out.println("*********************************************************************************************");
        System.out.println("* NOTA FISCAL - VENDA AO CONSUMIDOR                                                         *");
        System.out.printf("* Pedido ID: %-10d Data de Emissao: %-25s *\n", 
                idPedido, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("*********************************************************************************************");
        System.out.printf("* %-3s | %-6s | %-20s | %-4s | %-10s | %-10s *\n", "#", "ID", "DESCRICAO", "QTD", "VL.UNIT.", "VL.TOTAL");
        System.out.println("---------------------------------------------------------------------------------------------");

        int contador = 1;
        for (int i = 0; i < historicoItensCount; i++) {
            if (historicoItensVendidos[i][0] == idPedido) {
                int idProduto = historicoItensVendidos[i][1];
                int qtd = historicoItensVendidos[i][2];
                int index = buscarProdutoPorId(idProduto);
                double subtotal = qtd * precosProdutos[index];
                System.out.printf("* %-3d | %-6d | %-20s | %-4d | R$ %-9.2f | R$ %-9.2f *\n",
                        contador, idProduto, nomesProdutos[index], qtd,
                        precosProdutos[index], subtotal);
                contador++;
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("* %-55s TOTAL: R$ %-10.2f *\n", "", total);
        System.out.println("*********************************************************************************************");
        System.out.println("* OBRIGADO PELA PREFERENCIA! VOLTE SEMPRE!                                                  *");
        System.out.println("*********************************************************************************************");
    }
}