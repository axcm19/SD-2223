/*
Classe que serve para representar as contas que existem na aplicação.
 */

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Contas {

    private final HashMap<String, String> contas;   // <username, password>
    public ReentrantReadWriteLock lock_Contas = new ReentrantReadWriteLock();

    public Contas() {
        this.contas = new HashMap<>();
    }

    public String getPassword(String username) {
        return contas.get(username);
    }


    public void addAccount(String username, String password) {
        contas.put(username, password);
    }

    public boolean conta_existe(String username) {
        return contas.containsKey(username);
    }

    //faltam metodos

}
