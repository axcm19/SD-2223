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
