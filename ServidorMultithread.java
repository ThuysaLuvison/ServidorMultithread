import java.io.*;
import java.net.*;

public class ServidorMultithread {

    public static void main(String[] args) {
        int porta = 5000; // porta de escuta do servidor

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor iniciado na porta " + porta + ".");
            System.out.println("Aguardando conexões de clientes...\n");

            // Loop infinito para aceitar conexões de vários clientes
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Novo cliente conectado: " + cliente.getInetAddress().getHostAddress());

                
                new Thread(new Worker(cliente)).start();// Cria uma nova thread para tratar cada cliente simultaneamente
            }

        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}


class Worker implements Runnable {// Classe Worker: responsável por atender cada cliente em uma thread separada
    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
        ) {
            saida.println("Conexão estabelecida com o servidor. Digite 'sair' para encerrar.");

            String mensagem;
            while ((mensagem = entrada.readLine()) != null) {
                if (mensagem.equalsIgnoreCase("sair")) {
                    saida.println("Encerrando conexão. Até logo!");
                    break;
                }
                System.out.println("Cliente [" + socket.getInetAddress().getHostAddress() + "]: " + mensagem);
                saida.println("Servidor recebeu: " + mensagem.toUpperCase());
            }

        } catch (IOException e) {
            System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Conexão encerrada com o cliente: " + socket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.out.println("Erro ao fechar o socket: " + e.getMessage());
            }
        }
    }
}