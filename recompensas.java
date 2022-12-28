import javafx.util.Pair;

import java.util.List;

public class Recompensa {

    //private Mapa.Posicao posInicial;
    //private Mapa.Posicao posFinal;
    private Pair<Mapa.Posicao, Mapa.Posicao> coordenadas; // como representar "par origem-destino" ?
    private double valor;



    public Recompensa(Pair<Mapa.Posicao, Mapa.Posicao> coordenadas) {
        this.coordenadas = coordenadas;
        formulaValor(coordenadas);
    }

    /*
        Getters and Setters
     */
    public Pair<Mapa.Posicao, Mapa.Posicao> getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(Pair<Mapa.Posicao, Mapa.Posicao> coordenadas) {
        this.coordenadas = coordenadas;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    /*
        Metodos
     */

    public void formulaValor(Pair<Mapa.Posicao, Mapa.Posicao> coordenadas) {
        int distancia = calculaDistancia(coordenadas);
        double r;

        // formula inventada
        if(distancia > 10) this.valor = 100;
        else this.valor = 50;
    }

    public Integer calculaDistancia(Pair<Mapa.Posicao, Mapa.Posicao> coordenadas) {
        Mapa.Posicao A, B;

        A = coordenadas.getKey(); // inicio
        B = coordenadas.getValue(); // fim

        int distance;
        // distancia de Manhattan
        distance = Math.abs(B.getCoord_x() - A.getCoord_x()) + Math.abs(B.getCoord_y() - A.getCoord_y());

        return distance;
    }

    @Override
    public String toString() {
        return "Recompensa{" +
                "coordenadas=" + coordenadas +
                ", valor=" + valor +
                '}';
    }


    //faltam métodos

}