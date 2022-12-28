/*
Classe que serve para representar o mapa onde as trotinetes podem circular.
É representado como uma matriz NxN onde N é um numero inteiro.
O mapa tem uma lista de posições que são compostas por um par(x, y) onde x, y são inteiros.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Math.abs;

public class Mapa {

    //------------------------------------------------------------------------------------------------------------------

    public static class Posicao {

        public int coord_x;
        public int coord_y;

        public  Posicao(){
            this.coord_x = 0;
            this.coord_y = 0;
        }

        public Posicao(int x,int y){
            this.coord_x = x;
            this.coord_y = y;
        }

        public Posicao(Posicao p){
            this.coord_x = p.coord_x;
            this.coord_y = p.coord_y;
        }
        //faltam métodos


        public int getCoord_x() {
            return coord_x;
        }

        public void setCoord_x(int coord_x) {
            this.coord_x = coord_x;
        }

        public int getCoord_y() {
            return coord_y;
        }

        public void setCoord_y(int coord_y) {
            this.coord_y = coord_y;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    // tamanho do mapa = N*N
    private int N = 20;
    private int nsector = 16;   // 16 sectores
    private int tamanhosector = 5;  // 5*5 posiçoes
    private int sector = 0;
    private int numTroti = 6; // por enquanto só 6 trotinetes

    private HashMap<Integer, Trotinete> trotinetes; // <ID, Trotinete>
    private HashMap<Integer, List<Posicao>> locais; // <Numero do sector, Lista de posiçoes do sector>    alterar o formato
    public ReentrantReadWriteLock lock_mapa = new ReentrantReadWriteLock();

    //inicia o mapa    nao sei se esta certo
    public Mapa iniciaMapa(){
        Posicao trotinete = new Posicao();
        int x = -1;
        int y = -1;
        int i = 1;
        List<Posicao> posicoes = new ArrayList<>();
        this.locais = new HashMap<>();
        /*while(i <= nsector){
            while(x<tamanhosector){
                x = x+2;
                trotinete.coord_x = x;
                while(y<tamanhosector){
                    y = y+2;
                    trotinete.coord_y = y;
                    posicoes.add(trotinete);
                }
            }
            locais.put(i,posicoes);
            i++;
        }*/


        // inicializa trotinetes

        Trotinete t1 = new Trotinete(1, true, 5,3 );
        Trotinete t2 = new Trotinete(2, true, 1,2 );
        Trotinete t3 = new Trotinete(3, true, 2,19 );
        Trotinete t4 = new Trotinete(4, false, 15,9 );
        Trotinete t5 = new Trotinete(5, true, 16,7 );
        Trotinete t6 = new Trotinete(6, true, 4,20 );


        this.trotinetes = new HashMap<>();
        trotinetes.put(t1.id, t1);
        trotinetes.put(t2.id, t2);
        trotinetes.put(t3.id, t3);
        trotinetes.put(t4.id, t4);
        trotinetes.put(t5.id, t5);
        trotinetes.put(t6.id, t6);

        Mapa mapa = new Mapa();
        mapa.locais = this.locais;
        mapa.trotinetes = this.trotinetes;
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
    //mudar para os hashmap
    public boolean reservaTrotinete(Posicao p){
        for(int ID : trotinetes.keySet()){
            Trotinete t = trotinetes.get(ID);
            if(comparaPosicoes(p,t.pos)){
                t.ocupada = false;
                return true;
            }
        }
        return false;
    }
    /*
    //tira posicao inicial da lista de posiçoes do mapa e poe posiçao final na lista
    //mudar para os hashmap
    public void desloca(Posicao inicio, Posicao fim, Mapa m){
        for(int i = 0; i < m.numTroti; i++){
            if(comparaPosicoes(inicio, m.locais.get(i)) == true){
                m.locais.remove(i);
            }
        }
        m.locais.add(fim);
        int custo = calculaDistancia(inicio,fim);
        //envia para o cliente o custo da viagem
    }
*/


    //faz a listagem de todas as trotinetes livres
    public String lista_trotinetes(Posicao pos, int distancia_procura){
        String livres;
        StringBuilder sb = new StringBuilder();
        sb.append("\nTrotinetes livres: ");

        for(int ID : trotinetes.keySet()){
            Trotinete t = trotinetes.get(ID);
            Posicao t_pos = new Posicao(t.pos.coord_x, t.pos.coord_y);  // REDUNDANTE!! Nao é necessario criar nova posição mas cria-se na mesma
            int dist = calculaDistancia(pos, t_pos);

            if(dist <= distancia_procura && t.ocupada == true){
                sb.append("\nID: " + t.id + " ; " + "(" + t.pos.coord_x + ", " + t.pos.coord_y + ");");
            }
        }
        livres = String.valueOf(sb);
        return livres;
    }

    //faltam métodos


}
