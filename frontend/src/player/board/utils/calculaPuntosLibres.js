var Graph = require('graphlib').Graph;

function calculaAdyacencias(casilla) {
    const adyacentes = [];
    const directions = [
      [1, -1, 0], [1, 0, -1], [0, 1, -1],
      [-1, 1, 0], [-1, 0, 1], [0, -1, 1]
    ];
  
    directions.forEach(direction => {
      const adj = {
        q: casilla.q + direction[0],
        r: casilla.r + direction[1],
        s: casilla.s + direction[2]
      };
      adyacentes.push(adj);
    });
  
    return adyacentes;
}

function busquedaGruposVacios(graph, start, visitadas) {
  let count = 0;
  const stack = [start];

  while (stack.length > 0) {
    const current = stack.pop();
    const key = `${current.q},${current.r},${current.s}`;

    if (!visitadas.has(key)) {
      visitadas.add(key);
      count++;
      graph.successors(key).forEach(neighborKey => {
        const neighbor = graph.node(neighborKey);
        if (
          (neighbor.tipoCasilla === 'VACIA' || 
           neighbor.tipoCasilla === 'PODER_MASMENOS' || 
           neighbor.tipoCasilla === 'PODER_INTERROGACION') && 
          !visitadas.has(neighborKey)
        ) {
          stack.push(neighbor);
        }
      });
    }
  }

  return count;
}

export default function calculaGruposCasillasVacias(casillas) {
  const graph = new Graph();

  // Añadir todos los nodos al grafo
  casillas.forEach(c => graph.setNode(`${c.q},${c.r},${c.s}`, c));

  // Añadir las aristas entre nodos adyacentes
  casillas.forEach(c => {
    const adyacentes = calculaAdyacencias(c);
    adyacentes.forEach(adj => {
      casillas.forEach(c2 => {
        if (c2.q === adj.q && c2.r === adj.r && c2.s === adj.s) {
          graph.setEdge(`${c.q},${c.r},${c.s}`, `${c2.q},${c2.r},${c2.s}`);
        }
      });
    });
  });

  const visitadas = new Set();
  let tamañoGrupoMayor = 0;

  const vacias = casillas.filter(c => 
    c.tipoCasilla === 'VACIA' || c.tipoCasilla === 'PODER_MASMENOS' || c.tipoCasilla === 'PODER_INTERROGACION'
  );
  
  let cantidadGrupos = 0;
  
  vacias.forEach(casilla => {
    if (!visitadas.has(`${casilla.q},${casilla.r},${casilla.s}`)) {
      const tamañoGrupo = busquedaGruposVacios(graph, casilla, visitadas);
      if (tamañoGrupo >= 1) {
        tamañoGrupoMayor = Math.max(tamañoGrupoMayor, tamañoGrupo);
        cantidadGrupos++;
      }
    }
  });

  return cantidadGrupos >= 1 ? tamañoGrupoMayor : 0;
}