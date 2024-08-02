package org.springframework.samples.petclinic.mapas_del_reino.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.Casilla;
import org.springframework.samples.petclinic.mapas_del_reino.board.casillas.TipoCasilla;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;



public class CalculadoraCriterios {

    // 8PV por cada PUEBLO que conecte con RIO, BOSQUE MONTAÑA Y NUNCA CON OTRO PUEBLO.
    public static int calculaCriterioB6(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> pueblos = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.PUEBLO).toList();
        for(Casilla pueblo : pueblos){
            int adyMontaña = 0;
            int adyRio = 0;
            int adyBosque = 0;
            int adyPueblo = 0;
            List<int[]> adyacentes = calculaAdyacencias(pueblo);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() == TipoCasilla.MONTAÑA){
                            adyMontaña++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.RIO){
                            adyRio++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.BOSQUE){
                            adyBosque++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.PUEBLO){
                            adyPueblo++;
                        }
                    }
                }
            }

            if(adyMontaña > 0 && adyRio > 0 && adyBosque > 0 && adyPueblo == 0){
                puntos += 8;
            }

        }



        return puntos;
    }

    // 2 PV por cada RIO que conecte con AL MENOS 2 BOSQUES O MÁS

    public static int calculaCriterioB5(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> rios = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.RIO).toList();
        for(Casilla rio : rios){
            int adyBosques = 0;
            List<int[]> adyacentes = calculaAdyacencias(rio);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() == TipoCasilla.BOSQUE){
                            adyBosques++;
                        }
                    }
                }
            }

            if(adyBosques >= 2){
                puntos += 2;
            }

        }



        return puntos;
    }

    // 12 PV POR UN CASTILLO QUE NO ESTÉ EN EL BORDE QUE TENGA ADYACENTE UNA CASILLA DE CADA TIPO (RIO, BOSQUE, MONTAÑA, PRADERA, PUEBLO, CASTILLO)
    public static int calculaCriterioB4(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> castillos = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.CASTILLO).toList();
        for(Casilla castillo : castillos){
            if(esBorde(castillo)){
                continue;
            }
            int adyMontaña = 0;
            int adyRio = 0;
            int adyBosque = 0;
            int adyPradera = 0;
            int adyPueblo = 0;
            int adyCastillo = 0;
            List<int[]> adyacentes = calculaAdyacencias(castillo);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() == TipoCasilla.MONTAÑA){
                            adyMontaña++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.RIO){
                            adyRio++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.BOSQUE){
                            adyBosque++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.PRADERA){
                            adyPradera++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.PUEBLO){
                            adyPueblo++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.CASTILLO){
                            adyCastillo++;
                        }
                    }
                }
            }

            if(adyMontaña > 0 && adyRio > 0 && adyBosque > 0 && adyPradera > 0 && adyPueblo > 0 && adyCastillo > 0){
                puntos += 12;
            }

        }

        return puntos;
    }

    //10 PV POR CONECTAR DOS CARAS OPUESTAS MEDIANTE BOSQUES (ES DECIR QUE HAYA UN CAMINO DE ADYACENCIAS DE BOSQES QUE CONECTE DOS CARAS OPUESTAS DEL TABLERO)
    /*
     * LAS CARAS OPUESTAS SON:
     *  Q = 4 y Q = -4
     * R = 4 y R = -4
     * S = 4 y S = -4
     */
    public static int calculaCriterioB3(List<Casilla> casillas) {
        int puntos = 0;

        Graph<Casilla, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for(Casilla c : casillas){
            graph.addVertex(c);
        }

        for(Casilla c : casillas){
            List<int[]> adyacentes = calculaAdyacencias(c);
            for(int[] ady : adyacentes){
                for(Casilla c2 : casillas){
                    if(c2.getQ() == ady[0] && c2.getR() == ady[1] && c2.getS() == ady[2]){
                        graph.addEdge(c, c2);
                    }
                }
            }
        }

        //MIRAR SI HAY UN CAMINO DESDE Q = 4 A Q= -4 CON BOSQUES
        // MIRAR SI HAY UN CAMINO DESDE R = 4 A R= -4 CON BOSQUES
        // MIRAR SI HAY UN CAMINO DESDE S = 4 A S= -4 CON BOSQUES

        List<Casilla> startQ4 = casillas.stream().filter(c -> c.getQ() == 4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Casilla> endQ4 = casillas.stream().filter(c -> c.getQ() == -4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Casilla> startR4 = casillas.stream().filter(c -> c.getR() == 4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Casilla> endR4 = casillas.stream().filter(c -> c.getR() == -4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Casilla> startS4 = casillas.stream().filter(c -> c.getS() == 4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Casilla> endS4 = casillas.stream().filter(c -> c.getS() == -4 && c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();

        
        for(Casilla c1: startQ4){
            boolean hayCamino = false;
            for(Casilla c2: endQ4){
                if(existeCaminoBosque(graph, c1, c2)){
                    puntos += 10;
                    hayCamino = true;
                    break; // Salir del bucle interior si se encuentra un camino
                }
            }
            if(hayCamino) break; // Salir del bucle exterior si se encuentra un camino
        }

        // Repetir para startR4 y endR4
        for(Casilla c1: startR4){
            boolean hayCamino = false;
            for(Casilla c2: endR4){
                if(existeCaminoBosque(graph, c1, c2)){
                    puntos += 10;
                    hayCamino = true;
                    break; // Salir del bucle interior si se encuentra un camino
                }
            }
            if(hayCamino) break; // Salir del bucle exterior si se encuentra un camino
        }

        // Repetir para startS4 y endS4
        for(Casilla c1: startS4){
            boolean hayCamino = false;
            for(Casilla c2: endS4){
                if(existeCaminoBosque(graph, c1, c2)){
                    puntos += 10;
                    hayCamino = true;
                    break; // Salir del bucle interior si se encuentra un camino
                }
            }
            if(hayCamino) break; // Salir del bucle exterior si se encuentra un camino
        }
        
        return puntos;
    }

    private static boolean existeCaminoBosque(Graph<Casilla, DefaultEdge> graph, Casilla startVertex, Casilla endVertex) {
        return busquedaCaminoBosque(graph, startVertex, endVertex, new HashSet<>());
    }

    private static boolean busquedaCaminoBosque(Graph<Casilla, DefaultEdge> graph, Casilla current, Casilla end, Set<Casilla> visited) {
        if (!current.getTipoCasilla().equals(TipoCasilla.BOSQUE)) {
            return false; // Si la casilla actual no es de tipo bosque, no seguir buscando por este camino
        }
        if (current.equals(end)) {
            return true; // Si hemos llegado a la casilla de destino, devolvemos verdadero
        }
        visited.add(current); // Marcar la casilla actual como visitada
        for (DefaultEdge edge : graph.edgesOf(current)) {
            Casilla neighbor = graph.getEdgeTarget(edge);
            if (neighbor.equals(current)) {
                neighbor = graph.getEdgeSource(edge);
            }
            if (!visited.contains(neighbor)) {
                if (busquedaCaminoBosque(graph, neighbor, end, visited)) {
                    return true;
                }
            }
        }
        visited.remove(current); // Desmarcar la casilla actual antes de retroceder
        return false;
    }


    //1 PV por cada PRADERA que conecte con AL MENOS UN PUEBLO, 4 PV (ES DECIR 3 EXTRA) SI CONECTA CON DOS CASTILLOS
    public static int calculaCriterioB2(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> praderas = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.PRADERA).toList();
        for(Casilla pradera : praderas){
            int adyPueblos = 0;
            int adyCastillos = 0;
            List<int[]> adyacentes = calculaAdyacencias(pradera);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() == TipoCasilla.PUEBLO){
                            adyPueblos++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.CASTILLO){
                            adyCastillos++;
                        }
                    }
                }
            }

            if(adyPueblos > 0){
                puntos += 1;
                if(adyCastillos >= 2){
                    puntos += 3;
                }
            }

        }



        return puntos;
    }

    // 1 PV por cada montaña en tu grupo mas grande de montañas (al menos dos grupos)

    public static int calculaCriterioB1(List<Casilla> casillas) {
        // Filtra las casillas que son de tipo montaña
        Graph<Casilla, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for(Casilla c : casillas){
            graph.addVertex(c);
        }

        for(Casilla c : casillas){
            List<int[]> adyacentes = calculaAdyacencias(c);
            for(int[] ady : adyacentes){
                for(Casilla c2 : casillas){
                    if(c2.getQ() == ady[0] && c2.getR() == ady[1] && c2.getS() == ady[2]){
                        graph.addEdge(c, c2);
                    }
                }
            }
        }

        Set<Casilla> visitadas = new HashSet<>();
        int puntos = 0;

        List<Casilla> montanas = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.MONTAÑA).toList();
        Integer cantidadGrupos = 0;
        // Encuentra todos los grupos de montañas
        for (Casilla casilla : montanas) {
            if (!visitadas.contains(casilla)) {
                int tamañoGrupo = busquedaGruposMontaña(graph, casilla, visitadas);
                if (tamañoGrupo > 1) { // Considerar solo grupos con al menos 2 montañas
                    puntos = Math.max(puntos, tamañoGrupo);
                    cantidadGrupos++;
                }
            }
        }

        return cantidadGrupos > 1 ? puntos : 0; // El tamaño del mayor grupo de montañas
    }

    private static int busquedaGruposMontaña(Graph<Casilla, DefaultEdge> graph, Casilla start, Set<Casilla> visitadas) {
        int count = 0;
        Stack<Casilla> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Casilla current = stack.pop();
            if (!visitadas.contains(current)) {
                visitadas.add(current);
                count++;
                for (DefaultEdge edge : graph.edgesOf(current)) {
                    Casilla neighbor = graph.getEdgeTarget(edge);
                    if (neighbor.equals(current)) {
                        neighbor = graph.getEdgeSource(edge);
                    }
                    if (neighbor.getTipoCasilla() == TipoCasilla.MONTAÑA && !visitadas.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return count;
    }

    // 1 PV por cada montaña en los bordes del mapa
    public static int calculaCriterioA6(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> montañas = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.MONTAÑA).toList();
        for(Casilla montaña : montañas){
            if(esBorde(montaña)){
                puntos++;
            }
        }

        return puntos;
    }

    // 5 PV por cada grupo de pueblos (1 pueblo se considera grupo)

    public static int calculaCriterioA5(List<Casilla> casillas) {
        Graph<Casilla, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for(Casilla c : casillas){
            graph.addVertex(c);
        }

        for(Casilla c : casillas){
            List<int[]> adyacentes = calculaAdyacencias(c);
            for(int[] ady : adyacentes){
                for(Casilla c2 : casillas){
                    if(c2.getQ() == ady[0] && c2.getR() == ady[1] && c2.getS() == ady[2]){
                        graph.addEdge(c, c2);
                    }
                }
            }
        }

        Set<Casilla> visitadas = new HashSet<>();
        int puntos = 0;

        List<Casilla> pueblos = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.PUEBLO).toList();
        for (Casilla casilla : pueblos) {
            if (!visitadas.contains(casilla)) {
                int tamañoGrupo = busquedaGruposPueblo(graph, casilla, visitadas);
                if (tamañoGrupo >= 1) {
                    puntos+=5;
                }
            }
        }

        return puntos;
    }

    private static int busquedaGruposPueblo(Graph<Casilla, DefaultEdge> graph, Casilla start, Set<Casilla> visitadas) {
        int count = 0;
        Stack<Casilla> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Casilla current = stack.pop();
            if (!visitadas.contains(current)) {
                visitadas.add(current);
                count++;
                for (DefaultEdge edge : graph.edgesOf(current)) {
                    Casilla neighbor = graph.getEdgeTarget(edge);
                    if (neighbor.equals(current)) {
                        neighbor = graph.getEdgeSource(edge);
                    }
                    if (neighbor.getTipoCasilla() == TipoCasilla.PUEBLO && !visitadas.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return count;
    }

    
    public static int calculaCriterioA4(List<Casilla> casillas) {
        int puntos = 0;
        for(int r = -4; r<5; r++){
            final int finalR = r;
            List<Casilla> casillasR = casillas.stream().filter(c -> c.getR() == finalR).toList();
            int praderasR = 0;
            int riosR = 0;
            for(Casilla c : casillasR){
                if(c.getTipoCasilla() == TipoCasilla.PRADERA){
                    praderasR++;
                }
                else if(c.getTipoCasilla() == TipoCasilla.RIO){
                    riosR++;
                }
            }

            if(praderasR > 0 && riosR > 0){
                puntos += 2;
            }
        }
        return puntos;
    }

    public static int calculaCriterioA3(List<Casilla> casillas) {
        Graph<Casilla, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for(Casilla c : casillas){
            graph.addVertex(c);
        }

        for(Casilla c : casillas){
            List<int[]> adyacentes = calculaAdyacencias(c);
            for(int[] ady : adyacentes){
                for(Casilla c2 : casillas){
                    if(c2.getQ() == ady[0] && c2.getR() == ady[1] && c2.getS() == ady[2]){
                        graph.addEdge(c, c2);
                    }
                }
            }
        }

        Set<Casilla> visitadas = new HashSet<>();
        int puntos = 0;

        List<Casilla> bosques = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.BOSQUE).toList();
        List<Integer> tamañosGrupos = new ArrayList<>();
        // Encuentra todos los grupos de bosques
        for (Casilla casilla : bosques) {
            if (!visitadas.contains(casilla)) {
                int tamañoGrupo = busquedaGruposBosque(graph, casilla, visitadas);
                if (tamañoGrupo > 1) { // Considerar solo grupos con al menos 2 bosques
                    tamañosGrupos.add(tamañoGrupo);
                }
            }
        }

        if(tamañosGrupos.size() > 1){
            puntos = tamañosGrupos.get(0);
            for(int i = 1; i <= tamañosGrupos.size()-1; i++){
                if(tamañosGrupos.get(i) < puntos){
                    puntos = tamañosGrupos.get(i);
                }
            }
        }
        return puntos*2;
    }

    private static int busquedaGruposBosque(Graph<Casilla, DefaultEdge> graph, Casilla start, Set<Casilla> visitadas) {
        int count = 0;
        Stack<Casilla> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Casilla current = stack.pop();
            if (!visitadas.contains(current)) {
                visitadas.add(current);
                count++;
                for (DefaultEdge edge : graph.edgesOf(current)) {
                    Casilla neighbor = graph.getEdgeTarget(edge);
                    if (neighbor.equals(current)) {
                        neighbor = graph.getEdgeSource(edge);
                    }
                    if (neighbor.getTipoCasilla() == TipoCasilla.BOSQUE && !visitadas.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return count;
    }

    // 3 PV por cada PRADERA que tenga adyacente una MONTAÑA y un RIO, si también conecta con un bosque,son 4 PV (es decir 1 extra)

    public static int calculaCriterioA2(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> praderas = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.PRADERA).toList();
        for(Casilla pradera : praderas){
            int adyMontaña = 0;
            int adyRio = 0;
            int adyBosque = 0;
            List<int[]> adyacentes = calculaAdyacencias(pradera);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() == TipoCasilla.MONTAÑA){
                            adyMontaña++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.RIO){
                            adyRio++;
                        }
                        else if(c.getTipoCasilla() == TipoCasilla.BOSQUE){
                            adyBosque++;
                        }
                    }
                }
            }

            if(adyMontaña > 0 && adyRio > 0){
                puntos += 3;
                if(adyBosque > 0){
                    puntos+=1;
                }
            }

        }



        return puntos;
    }

    // 2PV por cada CASTILLO rodeado por 6 casillas QUE NO ESTEN VACIAS

    public static int calculaCriterioA1(List<Casilla> casillas) {
        int puntos = 0;
        List<Casilla> castillos = casillas.stream().filter(c -> c.getTipoCasilla() == TipoCasilla.CASTILLO).toList();
        for(Casilla castillo : castillos){
            int adyacenciasNoVacias = 0;
            if(esBorde(castillo)){
                continue;
            }
            List<int[]> adyacentes = calculaAdyacencias(castillo);
            for(int[] ady : adyacentes){
                for(Casilla c : casillas){
                    if(c.getQ() == ady[0] && c.getR() == ady[1] && c.getS() == ady[2]){
                        if(c.getTipoCasilla() != TipoCasilla.VACIA && c.getTipoCasilla() != TipoCasilla.PODER_INTERROGACION && c.getTipoCasilla() != TipoCasilla.PODER_MASMENOS){
                            adyacenciasNoVacias++;
                        }
                    }
                }
            }

            if(adyacenciasNoVacias == 6){
                puntos += 2;
            }

        }

        return puntos;
          
    }

    public static boolean esBorde(Casilla casilla) {
        return Math.abs(casilla.getQ()) == 4 || Math.abs(casilla.getR()) == 4 || Math.abs(casilla.getS()) == 4;
    }

    public static List<int[]> calculaAdyacencias(Casilla casilla) {

        List<int[]> adyacentes = new ArrayList<>();
        int[][] directions = {
            {1, -1, 0}, {1, 0, -1}, {0, 1, -1},
            {-1, 1, 0}, {-1, 0, 1}, {0, -1, 1}
        };

        for (int[] direction : directions) {
            int[] adj = {casilla.getQ() + direction[0], casilla.getR() + direction[1], casilla.getS()
                 + direction[2]};
            adyacentes.add(adj);
        }

        return adyacentes;
    }



}
