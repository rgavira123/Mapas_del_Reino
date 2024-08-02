export function calculaPoderInterrogacion(casillas){
    return casillas.some(casilla => casilla.coordenada[0] === 0 && casilla.coordenada[1] === 0 && casilla.coordenada[2] === 0);
}

export function calculaPoderMasMenos(casillas){
    

    let contador = 0;
    casillas.forEach(casilla => {
        const coord = casilla.coordenada;
        if(coord[0] == 0 && coord[1] == -3 && coord[2] == 3 ||
            coord[0] == 3 && coord[1] == -3 && coord[2] == 0 ||
            coord[0] == -4 && coord[1] == 0 && coord[2] == 4 ||
            coord[0] == 4 && coord[1] == 0 && coord[2] == 4 ||
            coord[0] == -3 && coord[1] == 3 && coord[2] == 0 ||
            coord[0] == 0 && coord[1] == 3 &&coord[2] == -3 ||
            coord[0] == 4 && coord[1] == 0 && coord[2] == -4 
        ){
            contador++;
        }
    });
    return contador;
}

