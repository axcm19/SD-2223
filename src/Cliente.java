import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Connection c = new Connection(s);

        Scanner input = new Scanner(System.in);

        String username = input.nextLine();
        String password = input.nextLine();;

        // send requests
        c.send(username.getBytes());   //p1
        c.send(password.getBytes());   //p2

        // get replies
        byte[] b1 = c.receive();
        byte[] b2 = c.receive();
        System.out.println("Server Reply: " + new String(b1));
        System.out.println("Server Reply: " + new String(b2));

        // ordem dos requests: p1, p2, p3
        // não existe uma thread especifica para um pedido
        // os replies não vêm pela mesma ordem dos requests pois algumas threads executam mais rápido do que outras

        c.close();
    }

    //faltam métodos

}


