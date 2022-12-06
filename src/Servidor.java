import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    final static int WORKERS_PER_CONNECTION = 3;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);

        final Contas lista_contas;
        final Mapa locais;

        // criação da lista de contas
        lista_contas = new Contas();

        // criação do mapa é feita aqui (em principio)
        //locais =

        lista_contas.addAccount("afonso_m","pass_afonso");
        lista_contas.addAccount("vicente_c","pass_vicente");
        lista_contas.addAccount("jose_f","pass_jose");
        lista_contas.addAccount("pedro_p","pass_pedro");

        while(true) {
            Socket s = ss.accept();
            Connection c = new Connection(s);

            Runnable worker = () -> {
                try (c) {
                    for (;;) {
                        byte[] b = c.receive();
                        String msg = new String(b);
                        System.out.println("Replying to: " + msg);
                        c.send(msg.getBytes());
                    }
                } catch (Exception ignored) { }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i) {
                new Thread(worker).start();
            }
        }
    }

}
