import "../../static/css/player/board/board.css";
import "../../static/css/player/board/grid.css";
import "../../static/css/player/board/buttons.css";
import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import tokenService from "../../services/token.service";
import { useEffect, useState } from "react";
import Dice from "react-dice-roll";
import SockJS from "sockjs-client";
import { Stomp, Client } from "@stomp/stompjs";
import { HexGrid, Layout, Hexagon, Text, Pattern } from "react-hexgrid";
import getErrorModal from "../../util/getErrorModal";
import InfoIcon from "./InfoIcon";
import calculaGruposCasillasVacias from "./utils/calculaPuntosLibres";
import InterrogacionModal from "./utils/InterrogacionModal";
import InfoAnnouncements from "./InfoAnnouncements";
import {
  calculaPoderInterrogacion,
  calculaPoderMasMenos,
} from "./utils/casillasEspeciales";
import OpponentsBoards from "./opponentsBoards";

export default function GameBoard() {
  const navigation = useNavigate();
  const [visible, setVisible] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const modal = getErrorModal(setVisible, visible, errorMessage);
  const { gameId, userId } = useParams();
  const jwt = tokenService.getLocalAccessToken();
  const user = tokenService.getUser();
  const [game, setGame] = useState(null);
  const [liderCondition, setLiderCondition] = useState(null);
  const [playerId, setPlayerId] = useState(null);
  const [diceRolls, setDiceRolls] = useState(0);
  const [dado1, setDado1] = useState(null);
  const [dado2, setDado2] = useState(null);
  const [dado3, setDado3] = useState(null);
  const [dado4, setDado4] = useState(null);
  const [critA1, setCritA1] = useState(null);
  const [critA2, setCritA2] = useState(null);
  const [critB1, setCritB1] = useState(null);
  const [critB2, setCritB2] = useState(null);
  const [isMinimized, setIsMinimized] = useState(false);
  const [tableroCreado, setTableroCreado] = useState(false);
  const [jugadores, setJugadores] = useState(0);
  const [playersBody, setPlayersBody] = useState([]);
  const [dados, setDados] = useState([]);
  const [tablero, setTablero] = useState(null);
  const [fichaJugador, setFichaJugador] = useState(null);
  const [numeroCasillasSeleccionado, setNumeroCasillasSeleccionado] =
    useState(null);
  const [isActive, setIsActive] = useState(false);
  const [anunciosCastillo, setAnunciosCastillo] = useState(0);
  const [anunciosPradera, setAnunciosPradera] = useState(0);
  const [anunciosPueblo, setAnunciosPueblo] = useState(0);
  const [anunciosRio, setAnunciosRio] = useState(0);
  const [anunciosMontaña, setAnunciosMontaña] = useState(0);
  const [anunciosBosque, setAnunciosBosque] = useState(0);
  const [cuerpoTablero, setCuerpoTablero] = useState(null);
  const allDiceRolled = dado1 && dado2 && dado3 && dado4;
  const allCriteriosRolled =
    dado1 !== dado2 && dado3 !== dado4 && dado4 !== null;
  const anuncios = [
    { nombre: "CASTILLO", condicion: anunciosCastillo > 0 },
    { nombre: "PRADERA", condicion: anunciosPradera > 0 },
    { nombre: "PUEBLO", condicion: anunciosPueblo > 0 },
    { nombre: "BOSQUE", condicion: anunciosBosque > 0 },
    { nombre: "RIO", condicion: anunciosRio > 0 },
    { nombre: "MONTAÑA", condicion: anunciosMontaña > 0 },
  ];
  const [puedeElegir, setPuedeElegir] = useState(false);
  const [sigRonda, setSigRonda] = useState(false);
  const [jugadoresTerminados, setJugadoresTerminados] = useState([]);
  const [liderTerminado, setActivoTerminado] = useState(false);
  const [usersIdList, setUsersIdList] = useState([]);
  const [indiceActivo, setIndiceActivo] = useState(null);
  const [pilaColocaciones, setPilaColocaciones] = useState([]);
  const [anunciacion, setAnunciacion] = useState(false);
  const [activoInicial, setActivoInicial] = useState(false);
  const [jugadorTerminado, setJugadorTerminado] = useState(false);
  const [turnos, setTurnos] = useState(0);
  const [diceRolled, setDiceRolled] = useState(
    Array(jugadores + 1 - turnos).fill(false)
  );

  const [valor1, setValor1] = useState(jugadores + 1 - turnos);
  const [valor2, setValor2] = useState(jugadores - turnos);
  const [historialColocaciones, setHistorialColocaciones] = useState([]);
  const [adyacencias, setAdyacencias] = useState([]);
  const [casillasDisponibles, setCasillasDisponibles] = useState(61);
  const [tableroLleno, setTableroLleno] = useState(false);
  const [dicesToShow, setDicesToShow] = useState(1);
  const [partidaTerminada, setPartidaTerminada] = useState(false);
  const [boardId, setBoardId] = useState(null);
  const [casillas, setCasillas] = useState([]);
  const [poderInterrogacion, setPoderInterrogacion] = useState(false);
  const [poderMasMenos, setPoderMasMenos] = useState(0);
  const [tablerosCreados, setTablerosCreados] = useState(0);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/game/${gameId}`, (message) => {
        const result = JSON.parse(message.body);
        setDado1(result.dado1);
        setDado2(result.dado2);
        setDado3(result.dado3);
        setDado4(result.dado4);
        setCritA1(defineCriteriosA(result.dado1));
        setCritA2(defineCriteriosA(result.dado2));
        setCritB1(defineCriteriosB(result.dado3));
        setCritB2(defineCriteriosB(result.dado4));
      });

      stompClient.subscribe(`/topic/boards/create/${gameId}`, (message) => {
        const result = JSON.parse(message.body);
        setCuerpoTablero(result);
      });

      stompClient.subscribe(
        `/topic/boards/choosedTileType/${gameId}`,
        (message) => {
          const result = JSON.parse(message.body);
          setFichaJugador(result.fichaJugador);
          setDados(result.dados);
          if (isActive === false) {
            setPuedeElegir(true);
          }
        }
      );

      stompClient.subscribe(
        `/topic/game/${gameId}/turnoFinalizado`,
        (message) => {
          const result = JSON.parse(message.body);
          setJugadoresTerminados((prevJugadoresTerminados) => {
            let newJugadoresTerminados = [...prevJugadoresTerminados];
            newJugadoresTerminados.push(result.jugador);
            return newJugadoresTerminados;
          });

          if (result.tableroLleno === true) {
            setPartidaTerminada(result.tableroLleno);
            //setTableroLleno(result.tableroLleno);
          }
        }
      );

      stompClient.subscribe(
        `/topic/game/${gameId}/siguienteRonda`,
        (message) => {
          const result = JSON.parse(message.body);
          if (result.turnoSiguiente === jugadores) {
            setTurnos(0);
          } else {
            setTurnos(result.turnoSiguiente);
          }
          setIndiceActivo(result.nuevoIndice);
          setIsActive(usersIdList[result.nuevoIndice] == userId);
          setFichaJugador(null);
          setNumeroCasillasSeleccionado(null);
          setDados([]);
          setActivoTerminado(false);
          setJugadoresTerminados([]);
          setAnunciacion(false);
          setPuedeElegir(true);
          setJugadorTerminado(false);
          setSigRonda(true);
          setPilaColocaciones([]);
          setDicesToShow(1);
        }
      );

      stompClient.subscribe(`/topic/gameFinished/${gameId}`, async () => {
        const response = await fetch(
          `/api/v1/boards/${boardId}/setPuntuacionFinal`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (response.status === 200) {
          navigation(`/game/${gameId}/leaderboard`);
        }
      });

      stompClient.subscribe(`/topic/board/tableroCreado/${gameId}`, () => {
        setTablerosCreados(tablerosCreados + 1);
      });
    });

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [
    usersIdList,
    tablero,
    tableroLleno,
    partidaTerminada,
    tablerosCreados,
    tableroCreado,
  ]);

  useEffect(() => {
    setSigRonda(true);
    setDiceRolled(Array(jugadores + 1 - turnos).fill(false));
    setValor1(jugadores + 1 - turnos);
    setValor2(jugadores - turnos);
  }, [sigRonda]);

  useEffect(() => {
    async function fetchPlayerAndGame() {
      const playerResponse = await fetch(`/api/v1/players/user/${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      const playerData = await playerResponse.json();
      if (playerData) {
        setPlayerId(playerData.id);
        console.log(isActive);
      }

      const gameResponse = await fetch(`/api/v1/games/${gameId}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      const gameData = await gameResponse.json();
      if (gameData) {
        setGame(gameData);
        setPlayersBody(gameData.players);
        setJugadores(gameData.players.length);
        setValor1(gameData.players.length + 1 - turnos);
        setValor2(gameData.players.length - turnos);
        let usersId = gameData.players.map((player) => player.user.id);
        setUsersIdList(usersId);
        console.log(usersIdList);
      }

      setLiderCondition(gameData.lider.id == playerData.id);

      let anuncios;
      if (gameData.players.length === 1 || gameData.players.length === 2) {
        anuncios = 3;
      } else if (gameData.players.length === 3) {
        anuncios = 2;
      } else if (gameData.players.length === 4) {
        anuncios = 1;
      } else {
        return; // or handle the default case as needed
      }

      setAnunciosCastillo(anuncios);
      setAnunciosPradera(anuncios);
      setAnunciosPueblo(anuncios);
      setAnunciosRio(anuncios);
      setAnunciosMontaña(anuncios);
      setAnunciosBosque(anuncios);
    }

    fetchPlayerAndGame();
  }, []);

  useEffect(() => {
    if (game == null) return;
    let indiceLider = usersIdList.indexOf(game.lider.user.id);
    if (activoInicial === false) {
      setIndiceActivo(indiceLider);
    }
    setActivoInicial(true);
    setIsActive(usersIdList[indiceLider] == userId);
  }, [game]);

  useEffect(() => {
    async function fetchBoard() {
      // Activa el indicador de carga
      const response = await fetch(
        `/api/v1/boards/game/${gameId}/player/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        }
      );
      const data = await response.json();
      if (data) {
        setTablero(data);
        setBoardId(data.id);
        setCasillas(data.casillas);
      }
    }

    if (tableroCreado) {
      fetchBoard();
    }
  }, [tableroCreado, pilaColocaciones, poderInterrogacion]);

  useEffect(() => {
    const calculateAdyacencias = () => {
      const directions = [
        [1, -1, 0],
        [1, 0, -1],
        [0, 1, -1],
        [-1, 1, 0],
        [-1, 0, 1],
        [0, -1, 1],
      ];

      return pilaColocaciones.flatMap((casilla) => {
        if (
          !casilla.coordenada ||
          !Array.isArray(casilla.coordenada) ||
          casilla.coordenada.length !== 3
        ) {
          console.error("Invalid casilla in pilaColocaciones:", casilla);
          return [];
        }

        return directions.map((direction) => [
          casilla.coordenada[0] + direction[0],
          casilla.coordenada[1] + direction[1],
          casilla.coordenada[2] + direction[2],
        ]);
      });
    };

    setAdyacencias(calculateAdyacencias());
  }, [pilaColocaciones]);

  function anunciarTerminado(dados) {
    if (isActive === false) {
      const socket = new SockJS("http://localhost:8080/ws");
      const stompClient = Stomp.over(socket);
      const mensaje = {
        jugador: userId,
        tableroLleno: tableroLleno,
      };

      stompClient.connect({}, () => {
        stompClient.send(
          `/app/game/${gameId}/finalizarTurno`,
          {},
          JSON.stringify(mensaje)
        );
      });
      setJugadorTerminado(true);
    } else if (isActive === true) {
      setActivoTerminado(true);
      if (partidaTerminada === false) {
        setPartidaTerminada(tableroLleno);
      }
    }
    setHistorialColocaciones([...historialColocaciones, pilaColocaciones]);
    setPoderInterrogacion(calculaPoderInterrogacion(pilaColocaciones));
    setPoderMasMenos(poderMasMenos + calculaPoderMasMenos(pilaColocaciones));
  }

  useEffect(() => {
    setCasillasDisponibles(calculaGruposCasillasVacias(casillas));
  }, [dados]);

  useEffect(() => {
    if (
      tableroCreado &&
      ((isActive === true && dados.length === jugadores + 1 - turnos) ||
        (isActive === false &&
          (dados.length === jugadores + 1 - turnos ||
            dados.length === jugadores - turnos)))
    ) {
      console.log(casillasDisponibles);
      console.log(dados);
      const estadoTablero = dados.every((dado) => dado > casillasDisponibles);
      console.log(estadoTablero);
      setTableroLleno(estadoTablero);
    }
  }, [dados]);

  async function avanzarRonda() {
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      debug: (str) => {
        console.log(new Date(), str);
      },
    });

    let mensaje;
    if (indiceActivo === jugadores - 1) {
      mensaje = {
        nuevoIndice: 0,
        turnoSiguiente: turnos + 1,
      };
    } else {
      mensaje = {
        nuevoIndice: indiceActivo + 1,
        turnoSiguiente: turnos + 1,
      };
    }

    const nuevoIndice = mensaje.nuevoIndice;

    //ahora setea el nuevo indice a indiceActivo
    setIndiceActivo(nuevoIndice);

    stompClient.onConnect = () => {
      stompClient.publish({
        destination: `/app/game/${gameId}/siguienteRondaV2`,
        body: JSON.stringify(mensaje),
      });
    };

    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    };

    stompClient.onWebSocketClose = (event) => {
      console.log("WebSocket closed: ", event);
    };

    stompClient.activate();
  }

  function changeDiceValue(index, value) {
    setDados((prevDados) => {
      let newDados = [...prevDados];
      newDados[index] = value;
      return newDados;
    });
    setDiceRolled((prevDiceRolled) => {
      let newDiceRolled = [...prevDiceRolled];
      newDiceRolled[index] = true;
      return newDiceRolled;
    });
  }

  function dicesPerPlayers(jugadores) {
    let dices = [];
    for (let i = 0; i < jugadores + 1 - turnos; i++) {
      if (i < dicesToShow) {
        dices.push(
          <div className="dice-container" key={i}>
            <Dice
              onRoll={(value) => {
                changeDiceValue(i, value);
                if (dicesToShow < jugadores + 1 - turnos) {
                  setDicesToShow(dicesToShow + 1); // Paso 3
                }
              }}
              size={75}
              disabled={Number.isInteger(dados[i])}
            />
          </div>
        );
      }
    }
    return dices;
  }

  const handleIncrement = (index) => {
    if (poderMasMenos > 0) {
      const nuevosDados = [...dados];
      nuevosDados[index] += 1;
      setDados(nuevosDados);
      setPoderMasMenos(poderMasMenos - 1);
    }
  };

  const handleDecrement = (index) => {
    if (poderMasMenos > 0 && dados[index] > 1) {
      const nuevosDados = [...dados];
      nuevosDados[index] -= 1;
      setDados(nuevosDados);
      setPoderMasMenos(poderMasMenos - 1);
    }
  };

  function buttonsPerDices(dados) {
    if (tableroLleno === true && poderMasMenos === 0) {
      return (
        <button className="tile-button" onClick={() => modifyDices(0)}>
          No puedes colocar más casillas, anuncia que has terminado
        </button>
      );
    } else {
      return (
        <div
          style={{
            display: "flex",
            flexWrap: "wrap",
            justifyContent: "center",
          }}
        >
          {dados.map((valorDado, index) => (
            <div
              key={index}
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                margin: "10px",
              }}
            >
              <button
                className="tile-button"
                onClick={() => modifyDices(valorDado)}
                style={{ marginBottom: "10px" }}
              >
                {valorDado}
              </button>
              <div>
                {poderMasMenos > 0 && (
                  <button
                    className="increment-button "
                    onClick={() => handleIncrement(index)}
                    style={{ marginRight: "5px" }}
                  >
                    +
                  </button>
                )}
                {poderMasMenos > 0 && valorDado > 1 && (
                  <button
                    className="decrement-button"
                    onClick={() => handleDecrement(index)}
                  >
                    -
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      );
    }
  }

  function reduceAnnouncements(ficha) {
    switch (ficha) {
      case "CASTILLO":
        setAnunciosCastillo(anunciosCastillo - 1);
        break;
      case "PRADERA":
        setAnunciosPradera(anunciosPradera - 1);
        break;
      case "PUEBLO":
        setAnunciosPueblo(anunciosPueblo - 1);
        break;
      case "RIO":
        setAnunciosRio(anunciosRio - 1);
        break;
      case "MONTAÑA":
        setAnunciosMontaña(anunciosMontaña - 1);
        break;
      case "BOSQUE":
        setAnunciosBosque(anunciosBosque - 1);
        break;
      default:
        break;
    }
  }

  function modifyDices(dado) {
    if (casillasDisponibles < dado) {
      showError("No puedes colocar más casillas de las disponibles");
      setVisible(true);
    } else {
      setNumeroCasillasSeleccionado(dado);
      setPuedeElegir(false);
      // Encuentra el índice del primer dado que coincide con el valor dado

      // Si se encontró un dado que coincide, elimínalo
      if (isActive === true) {
        const index = dados.findIndex((valorDado) => valorDado === dado);
        if (index !== -1) {
          dados.splice(index, 1);
        }
      }
    }
  }

  function handleChooseTile() {
    if (isActive === true) {
      // Asegurarse de que 'dados' es una lista de enteros antes de esta llamada
      let cuerpo = {
        fichaJugador: fichaJugador,
        dados: dados,
      };

      // Crear una instancia de SockJS
      const socket = new SockJS("http://localhost:8080/ws");

      // Crear un cliente Stomp con configuración avanzada
      const stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000, // reconectar cada 5 segundos si se pierde la conexión
        debug: (str) => {
          console.log(new Date(), str);
        },
      });

      // Definir el comportamiento al conectarse
      stompClient.onConnect = (frame) => {
        console.log("Connected: " + frame);
        stompClient.publish({
          destination: `/app/boards/setTileType/${gameId}`,
          body: JSON.stringify(cuerpo),
        });
      };

      // Manejar errores de STOMP
      stompClient.onStompError = (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        console.error("Additional details: " + frame.body);
      };

      // Activar el cliente
      stompClient.activate();

      setAnunciacion(true);
    }
  }

  async function creaYEnviaTablero() {
    const cuerpo = {
      playerId: userId,
      gameId: gameId,
      criterioA1: numberToWord(dado1),
      criterioA2: numberToWord(dado2),
      criterioB1: numberToWord(dado3),
      criterioB2: numberToWord(dado4),
    };
    console.log(cuerpo);

    // Crear una instancia de SockJS
    const socket = new SockJS("http://localhost:8080/ws");

    // Crear un cliente Stomp con configuración avanzada
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000, // reconectar cada 5 segundos si se pierde la conexión
      debug: (str) => {
        console.log(new Date(), str);
      },
    });

    // Definir el comportamiento al conectarse
    stompClient.onConnect = (frame) => {
      console.log("Connected: " + frame);
      stompClient.publish({
        destination: `/app/boards/create/${gameId}`,
        body: JSON.stringify(cuerpo),
      });
    };

    // Manejar errores de STOMP
    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    };

    // Activar el cliente
    stompClient.activate();

    const response = await fetch(`/api/v1/boards`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cuerpo),
    });
    console.log(response);
    setTableroCreado(true);
  }

  async function crearTablero(cuerpoTablero) {
    cuerpoTablero.playerId = userId;
    const response = await fetch(`/api/v1/boards`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cuerpoTablero),
    });
    console.log(response);
    setTableroCreado(true);
  }

  function numberToWord(num) {
    switch (num) {
      case 1:
        return "UNO";
      case 2:
        return "DOS";
      case 3:
        return "TRES";
      case 4:
        return "CUATRO";
      case 5:
        return "CINCO";
      case 6:
        return "SEIS";
      default:
        return "Número no reconocido";
    }
  }

  function isAdyacente(q, r, s) {
    return adyacencias.some(
      (adyacente) =>
        adyacente[0] === q && adyacente[1] === r && adyacente[2] === s
    );
  }

  const showError = (message) => {
    setErrorMessage(message);
    setTimeout(() => setErrorMessage(null), 3000); // Clear the message after 3 seconds
  };

  function estaEnPila(q, r, s) {
    return pilaColocaciones.some(
      (casilla) =>
        casilla.coordenada[0] === q &&
        casilla.coordenada[1] === r &&
        casilla.coordenada[2] === s
    );
  }

  async function eliminaCasilla(q, r, s) {
    if (
      numeroCasillasSeleccionado != null &&
      fichaJugador != null &&
      estaEnPila(q, r, s)
    ) {
      setNumeroCasillasSeleccionado(
        numeroCasillasSeleccionado + pilaColocaciones.length
      );
      for (let i = 0; i < pilaColocaciones.length; i++) {
        let casillaRequest = {
          coordenada: [
            pilaColocaciones[i].coordenada[0],
            pilaColocaciones[i].coordenada[1],
            pilaColocaciones[i].coordenada[2],
          ],
          tipoCasilla: fichaJugador,
        };

        const response = await fetch(
          `/api/v1/boards/${tablero.id}/deleteCasilla`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify(casillaRequest),
          }
        );
      }
      setPilaColocaciones([]);
    }
  }

  async function finalizarPartida() {
    const response = await fetch(`/api/v1/games/finish/${gameId}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
    });

    if (response.status === 200) {
      console.log("Partida finalizada");
    }
  }

  async function colocarCasilla(q, r, s) {
    if (
      numeroCasillasSeleccionado != null &&
      numeroCasillasSeleccionado != 0 &&
      fichaJugador != null
    ) {
      if (pilaColocaciones.length === 0 || isAdyacente(q, r, s)) {
        let casillaRequest = {
          coordenada: [q, r, s],
          tipoCasilla: fichaJugador,
        };
        const response = await fetch(
          `/api/v1/boards/${tablero.id}/setCasilla`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify(casillaRequest),
          }
        );
        console.log(response);
        if (response.status === 200) {
          setNumeroCasillasSeleccionado(numeroCasillasSeleccionado - 1);
          setPilaColocaciones((prev) => [...prev, casillaRequest]);
        }
      } else {
        showError(
          "La casilla no es adyacente a las casillas colocadas en este turno."
        );
        setVisible(true);
      }
    } else {
      showError("No se pueden colocar mas casillas");
      setVisible(true);
    }
  }

  function obtenerImagen(fichaJugador) {
    switch (fichaJugador) {
      case "RIO":
        return "https://img.freepik.com/foto-gratis/fotografia-aerea-rio-rodeado-islas-cubiertas-vegetacion-luz-solar_181624-6504.jpg";
      case "PRADERA":
        return "https://cdn0.bioenciclopedia.com/es/posts/3/8/0/clima_83_0_600.jpg";
      case "PUEBLO":
        return "https://img.freepik.com/foto-gratis/vista-nocturna-albarracin_1398-3615.jpg?size=626&ext=jpg&ga=GA1.1.672697106.1718928000&semt=sph";
      case "CASTILLO":
        return "https://th.bing.com/th/id/R.73a50d982cf65718654b069ea2ef4fca?rik=udWqEY5lR7v52Q&pid=ImgRaw&r=0";
      case "MONTAÑA":
        return "https://images.ecestaticos.com/X6jKxwFNcSPcl3CEQuItXawaAc8=/0x0:2097x1430/1200x900/filters:fill(white):format(jpg)/f.elconfidencial.com%2Foriginal%2F614%2F40c%2Ff02%2F61440cf024b55a412a97e4c4c59fffbd.jpg";
      case "BOSQUE":
        return "https://www.deutschland.de/sites/default/files/styles/image_carousel_mobile/public/media/image/tdt_02102023_wald_wald_der_zukunft_mischwald.jpg?h=e3193a6a&itok=TY6U3ekv";
      default:
        return "";
    }
  }

  function handleSubmit() {
    let cuerpo = {
      dado1: dado1,
      dado2: dado2,
      dado3: dado3,
      dado4: dado4,
    };

    // Create a SockJS instance
    const socket = new SockJS("http://localhost:8080/ws");

    // Create a Stomp client
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000, // auto reconnect every 5 seconds
    });

    // Connect the client
    stompClient.onConnect = (frame) => {
      console.log("Connected: " + frame);
      stompClient.publish({
        destination: `/app/game/${gameId}/rollDice`,
        body: JSON.stringify(cuerpo),
      });
    };

    // Handle connection errors
    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    };

    // Activate the client
    stompClient.activate();
  }

  function changeCritA1(value) {
    setCritA1(defineCriteriosA(value));
    setDado1(value);
    setDiceRolls(diceRolls + 1);
  }

  function changeCritA2(value) {
    setCritA2(defineCriteriosA(value));
    setDado2(value);
    setDiceRolls(diceRolls + 1);
  }

  function changeCritB1(value) {
    setCritB1(defineCriteriosB(value));
    setDado3(value);
    setDiceRolls(diceRolls + 1);
  }

  function changeCritB2(value) {
    setCritB2(defineCriteriosB(value));
    setDado4(value);
    setDiceRolls(diceRolls + 1);
  }

  function defineCriteriosA(crit) {
    switch (crit) {
      case 1:
        return "2 PV por cada CASTILLO rodeado por 6 territorios cualquiera";
      case 2:
        return "3 PV por cada PRADERA que conecte con MONTAÑA y RÍO. 1 PV extra si conecta con BOSQUE";
      case 3:
        return "2 PV por cada BOSQUE en tu grupo más pequeño de BOSQUES (mínimo 2 grupos)";
      case 4:
        return "2 PV por cada línea donde aparezcan PRADERA y RÍO";
      case 5:
        return "5 PV por cada grupo de PUEBLOS. Un solo PUEBLO cuenta como grupo";
      case 6:
        return "1 PV por cada MONTAÑA en los bordes del mapa";
      default:
        break;
    }
  }

  function defineCriteriosB(crit) {
    switch (crit) {
      case 1:
        return "1 PV por cada MONTAÑA en tu grupo más grande de MONTAÑAS (mínimo 2 grupos)";
      case 2:
        return "1 PV por cada PRADERA que conecte con al menos un PUEBLO. 3 PV extra si conecta también con dos CASTILLOS";
      case 3:
        return "10 PV por conectar dos caras opuestas del mapa mediante BOSQUES";
      case 4:
        return "12 PV por cada CASTILLO que conecte con un territorio de cada tipo";
      case 5:
        return "2 PV por cada RÍO que conecte con al menos dos BOSQUES";
      case 6:
        return "8 PV por cada PUEBLO que conecte con RÍO, BOSQUE y MONTAÑA y ningún otro PUEBLO";
      default:
        break;
    }
  }

  function getGenderedQuestion(fichaJugador) {
    // Lista de palabras que son femeninas
    const feminineWords = ["Pradera", "Montaña"];

    // Comprueba si la palabra está en la lista de palabras femeninas
    if (feminineWords.includes(fichaJugador)) {
      return `¿Cuántas ${fichaJugador}S quieres colocar?`;
    } else {
      return `¿Cuántos ${fichaJugador}S quieres colocar?`;
    }
  }

  function anuncioConstruccion(casilla) {
    setFichaJugador(casilla);
    reduceAnnouncements(casilla);
  }

  function casillaConstruidaEnEsteTurno(casilla) {
    return pilaColocaciones.some(
      (casillaColocada) =>
        casillaColocada.q === casilla.q &&
        casillaColocada.r === casilla.r &&
        casillaColocada.s === casilla.s
    );
  }

  return (
    <div className="board-game-container">
      {modal}
      <div>
        <InterrogacionModal
          visible={poderInterrogacion}
          onClose={() => setPoderInterrogacion(false)}
          dado1={dado1}
          dado2={dado2}
          dado3={dado3}
          dado4={dado4}
          idTablero={boardId}
        />
      </div>
      {tableroCreado ? (
        <div className="game-container">
          <div className="left-container">
            <div
              className={`criteria-hero-div ${isMinimized ? "minimized" : ""}`}
              onClick={() => setIsMinimized(!isMinimized)} // Evento onClick para cambiar el estado
            >
              {isMinimized ? (
                <h3>Criterios de la partida</h3>
              ) : (
                <>
                  <div className="a-criteria">
                    {critA1 && <h3>Criterios A</h3>}
                    {critA1 && <p>{critA1}</p>}
                    {critA2 && <p>{critA2}</p>}
                    {critB1 && <h3>Criterios B</h3>}
                    {critB1 && <p>{critB1}</p>}
                    {critB2 && <p>{critB2}</p>}
                    {tablero ? (
                      <h5>
                        {" "}
                        💡 Gasta todos tus +/- para poder terminar la partida
                      </h5>
                    ) : null}
                  </div>
                </>
              )}
            </div>
            {isActive &&
              fichaJugador == null &&
              tablerosCreados === jugadores && (
                <div className="dice-container">
                  {dicesPerPlayers(jugadores)}
                </div>
              )}
            {console.log(tablerosCreados)}
            {tablerosCreados !== jugadores && (
              <div className="info">
                <h3>Esperando a que todos los jugadores tengan su tablero.</h3>
                <h3>
                  {tablerosCreados}/{jugadores} jugadores
                </h3>
              </div>
            )}
            {numeroCasillasSeleccionado !== null &&
              numeroCasillasSeleccionado !== 0 && (
                <h4>Casilla a colocar: {fichaJugador}</h4>
              )}
            {isActive === true &&
              numeroCasillasSeleccionado != null &&
              anunciacion === false &&
              handleChooseTile()}
            {fichaJugador &&
              isActive == true &&
              dados.length === jugadores + 1 - turnos &&
              liderTerminado == false && (
                <div className="tile-number-container">
                  <p>{getGenderedQuestion(fichaJugador)}</p>
                  <div style={{ marginTop: "-20px" }}>
                    {buttonsPerDices(dados)}
                  </div>
                </div>
              )}
            {fichaJugador && isActive === false && puedeElegir === true && (
              <div className="tile-number-container">
                <p>{getGenderedQuestion(fichaJugador)}</p>
                <div style={{ marginTop: "-20px" }}>
                  {buttonsPerDices(dados)}
                </div>
              </div>
            )}
            {isActive &&
              dados.length === jugadores + 1 - turnos &&
              fichaJugador == null &&
              diceRolled.every((element) => element === true) && (
                <div className="ann-container">
                  <InfoAnnouncements
                    castillo={anunciosCastillo}
                    pradera={anunciosPradera}
                    montaña={anunciosMontaña}
                    rio={anunciosRio}
                    bosque={anunciosBosque}
                    pueblo={anunciosPueblo}
                  ></InfoAnnouncements>
                  <div className="tile-container">
                    {anuncios.map((anuncio, index) =>
                      anuncio.condicion ? (
                        <button
                          key={index}
                          className="tile-button"
                          onClick={() => anuncioConstruccion(anuncio.nombre)}
                        >
                          {anuncio.nombre}
                        </button>
                      ) : null
                    )}
                  </div>
                </div>
              )}
            {isActive === false
              ? fichaJugador != null &&
                jugadoresTerminados.length < jugadores - 1 &&
                numeroCasillasSeleccionado !== null &&
                numeroCasillasSeleccionado == 0 &&
                jugadorTerminado === false && (
                  <button
                    className="game-button"
                    onClick={() => anunciarTerminado()}
                  >
                    {tableroLleno === true
                      ? "Anunciar que has terminado"
                      : "Terminar turno"}
                  </button>
                )
              : fichaJugador != null &&
                liderTerminado === false &&
                numeroCasillasSeleccionado !== null &&
                numeroCasillasSeleccionado == 0 && (
                  <button
                    className="game-button"
                    onClick={() => anunciarTerminado()}
                  >
                    {tableroLleno === true
                      ? "Anunciar que has terminado"
                      : "Terminar turno"}
                  </button>
                )}
            {isActive === true &&
              jugadoresTerminados.length === jugadores - 1 &&
              liderTerminado === true &&
              (tableroLleno === false && partidaTerminada === false ? (
                <button className="game-button" onClick={() => avanzarRonda()}>
                  {turnos !== jugadores - 1
                    ? "Siguiente Turno"
                    : "Siguiente Ronda"}
                </button>
              ) : (
                <button
                  className="game-button"
                  onClick={() => finalizarPartida()}
                >
                  Finaliza la partida y calcula los puntos de todos los
                  jugadores
                </button>
              ))}
          </div>
          <div className="right-container">
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                marginTop: "35px",
              }}
            >
              <div className="information-div">
                {tablero ? <h4>PV del poder ❓: {tablero.points}</h4> : null}
                {tablero ? (
                  <h4>Número de +/- acumulados: {poderMasMenos}</h4>
                ) : null}
                <InfoIcon
                  infoText={
                    "Click Izquierdo para colocar. \n Click Derecho para borrar todas las de este turno"
                  }
                />
              </div>
              <HexGrid width={750} height={700}>
                <Layout
                  size={{ x: 6, y: 6 }}
                  flat={false}
                  spacing={1.1}
                  origin={{ x: 0, y: 0 }}
                >
                  {tablero &&
                    tablero.casillas.map((casilla, i) => (
                      <Hexagon
                        key={i}
                        q={casilla.q}
                        s={casilla.s}
                        r={casilla.r}
                        fill={casilla.tipoCasilla}
                        onClick={() =>
                          colocarCasilla(casilla.q, casilla.r, casilla.s)
                        }
                        onContextMenu={(e) => {
                          e.preventDefault();

                          if (
                            casilla.tipoCasilla !== "VACIA" &&
                            casilla.tipoCasilla !== "PODER_MASMENOS" &&
                            casilla.tipoCasilla !== "PODER_INTERROGACIÓN" &&
                            casillaConstruidaEnEsteTurno(
                              casilla.q,
                              casilla.r,
                              casilla.s
                            )
                          ) {
                            eliminaCasilla(casilla.q, casilla.r, casilla.s);
                          }
                        }}
                      >
                        <Pattern
                          id={casilla.tipoCasilla}
                          link={obtenerImagen(casilla.tipoCasilla)}
                          size={{ x: 8, y: 6.5 }}
                        />
                        {casilla.tipoCasilla === "PODER_MASMENOS" && (
                          <Text className="hexagon-text">+1 / -1</Text>
                        )}
                        {casilla.tipoCasilla === "PODER_INTERROGACION" && (
                          <Text className="hexagon-text">❓</Text>
                        )}
                        {casilla.tipoCasilla !== "PODER_INTERROGACION" &&
                          casilla.tipoCasilla !== "PODER_MASMENOS" && (
                            <Text className="hexagon-text-invisible">O</Text>
                          )}
                      </Hexagon>
                    ))}
                </Layout>
              </HexGrid>
              <div className="opp-boards-container">
                {tablerosCreados === jugadores &&
                  playersBody
                    .filter((player) => `${player.user.id}` !== userId) // Filtrar jugadores que no sean el jugador actual
                    .map((player) => (
                      <OpponentsBoards
                        key={player.user.id} // Añadir key para optimización de renderizado en React
                        jwt={jwt}
                        userId={player.user.id}
                        gameId={gameId}
                        name={player.firstName + " " + player.lastName}
                        tableroCreado={tableroCreado}
                      />
                    ))}
              </div>
              <h4>
                {" "}
                Número de casillas por colocar:{" "}
                {numeroCasillasSeleccionado != null
                  ? numeroCasillasSeleccionado
                  : 0}{" "}
              </h4>
            </div>
          </div>
        </div>
      ) : (
        <>
          {liderCondition && !allDiceRolled && (
            <>
              <h1>El juego va a comenzar!</h1>
              <div className="text-center">
                <p>¡Eres el lider!</p>
                <p>
                  Lanza los dados para seleccionar los criterios de puntuación.
                </p>
              </div>
            </>
          )}
          {!liderCondition && !allDiceRolled && (
            <p>No eres el lider, esperando a que el lider lance los dados</p>
          )}
          {liderCondition && (
            <div className="dice-container">
              {
                <Dice
                  onRoll={(value) => changeCritA1(value)}
                  size={75}
                  disabled={dado1 !== null}
                />
              }
              {
                <Dice
                  onRoll={(value) => changeCritA2(value)}
                  size={75}
                  disabled={
                    (dado2 !== null && dado1 !== dado2) || dado1 === null
                  }
                />
              }
              {
                <Dice
                  onRoll={(value) => changeCritB1(value)}
                  size={75}
                  disabled={dado3 !== null || dado1 === null || dado2 === null}
                />
              }
              {
                <Dice
                  onRoll={(value) => changeCritB2(value)}
                  size={75}
                  disabled={
                    (dado4 !== null && dado3 !== dado4) ||
                    dado3 === null ||
                    dado2 === null ||
                    dado1 === null
                  }
                />
              }
            </div>
          )}
          {((dado2 === dado1 && dado2 !== null) ||
            (dado4 === dado3 && dado4 !== null)) && (
            <div>
              <p>
                {" "}
                No puede haber criterios repetidos. Por favor, vuelve a tirar el
                dado.{" "}
              </p>
            </div>
          )}
          {allDiceRolled && handleSubmit()}
          {(dado1 !== null ||
            dado2 !== null ||
            dado3 !== null ||
            dado4 !== null) && (
            <div
              className={`criteria-hero-div ${isMinimized ? "minimized" : ""}`}
              onClick={() => setIsMinimized(!isMinimized)} // Evento onClick para cambiar el estado
            >
              {isMinimized ? (
                <h3>Criterios de la partida</h3>
              ) : (
                <>
                  <div className="a-criteria">
                    {(critA1 || critA2) && <h3>Criterios A</h3>}
                    {critA1 && <p>{critA1}</p>}
                    {critA2 && <p>{critA2}</p>}
                  </div>
                  <div className="b-criteria">
                    {(critB1 || critB2) && <h3>Criterios B</h3>}
                    {critB1 && <p>{critB1}</p>}
                    {critB2 && <p>{critB2}</p>}
                  </div>
                </>
              )}
            </div>
          )}

          {liderCondition &&
            allDiceRolled &&
            allCriteriosRolled &&
            !tableroCreado && (
              <button
                className="game-button"
                onClick={() => creaYEnviaTablero()}
              >
                Crear tableros para todos los jugadores
              </button>
            )}
          {cuerpoTablero &&
            !liderCondition &&
            allDiceRolled &&
            !tableroCreado && (
              <button
                className="game-button"
                onClick={() => crearTablero(cuerpoTablero)}
              >
                Muestra tu tablero
              </button>
            )}
        </>
      )}
    </div>
  );
}
