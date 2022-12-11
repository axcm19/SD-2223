/*
Classe que serve para representar as contas que existem na aplicação.
 */

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Contas implements Serializable {

    private final HashMap<String, String> map_contas;   // <username, password>
    public ReentrantReadWriteLock lock_Contas = new ReentrantReadWriteLock();

    public Contas() {
        this.map_contas = new HashMap<>();
    }

    public String getPassword(String username) {
        return map_contas.get(username);
    }


    public void addAccount(String username, String password) {
        map_contas.put(username, password);
    }

    public boolean conta_existe(String username) {
        return map_contas.containsKey(username);
    }

}
