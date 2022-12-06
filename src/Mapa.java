/*
Classe que serve para representar o mapa onde as trotinetes podem circular.
É representado como uma matriz NxN onde N é um numero inteiro.
O mapa tem uma lista de posições que são compostas por um par(x, y) onde x, y são inteiros.
 */

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa {

    //------------------------------------------------------------------------------------------------------------------

    public static class Posicao {

        public int coord_x;
        public int coord_y;

        //faltam métodos

    }

    //------------------------------------------------------------------------------------------------------------------

    // tamanho do mapa = N*N
    private int N;
    private List<Posicao> locais;
    public ReentrantReadWriteLock lock_mapa = new ReentrantReadWriteLock();

    //faltam métodos

}
