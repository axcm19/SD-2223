/*
Classe que serve para representar o mapa onde as trotinetes podem circular.
É representado como uma matriz NxN onde N é um numero inteiro.
O mapa tem uma lista de posições que são compostas por um par(x, y) onde x, y são inteiros.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Math.abs;

public class Mapa {

    //------------------------------------------------------------------------------------------------------------------

    public static class Posicao {

        public int coord_x;
        public int coord_y;

        //faltam métodos

    }

    //------------------------------------------------------------------------------------------------------------------

    // tamanho do mapa = N*N
    private int N = 20;
    private int numTroti = 36;
    private List<Posicao> locais;
    public ReentrantReadWriteLock lock_mapa = new ReentrantReadWriteLock();

    //inicia o mapa    nao sei se esta certo
    public Mapa iniciaMapa(){
        Posicao trotinete = new Posicao();
        int x = -1;
        int y = -1;
        this.N = 20;
        this.locais = new ArrayList<Posicao>();
        while(x < N){
            x = x + 3;
            trotinete.coord_x = x;
            while(y < N){
                y = y + 3;
                trotinete.coord_y = y;
                locais.add(trotinete);
            }
        }
        Mapa mapa = new Mapa();
        mapa.locais = this.locais;
        return mapa;
    }

    //calcula distancia entre dois pontos
    public int calculaDistancia(Posicao p1,Posicao p2){
        int dist = abs(p1.coord_x - p2.coord_x) + abs(p1.coord_y - p2.coord_y);
        return dist;
    }

    public boolean comparaPosicoes(Posicao p1,Posicao p2){
        if(p1.coord_x == p2.coord_x)
            if(p1.coord_y == p2.coord_y) return true;
        return false;
    }

    //pegar posiçao do user e tirar do mapa
    public void reservaTrotinete(Posicao p,Mapa m){
        Posicao temp = new Posicao();
        temp.coord_x = 0;
        temp.coord_y = 0;
        for(int i = 0;i<locais.size();i++){
            if(calculaDistancia(p,locais.get(i))<calculaDistancia(p,temp)){
                temp = locais.get(i);
            }
            //faltam coisas
        }
    }

    //tira posicao inicial da lista de posiçoes do mapa e poe posiçao final na lista
    public Mapa desloca(Posicao inicio,Posicao fim,Mapa m){
        for(int i = 0;i<m.numTroti;i++){
            if(comparaPosicoes(inicio,m.locais.get(i))==true){
                m.locais.remove(i);
            }
        }
        m.locais.add(fim);
        //faltam coisas
        return m;
    }
    //faltam métodos


}
