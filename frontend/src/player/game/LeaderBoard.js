import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import tokenService from "../../services/token.service";
import "../../static/css/player/leaderboard/leaderboard.css";
import OpponentsBoards from "../board/opponentsBoards";


export default function LeaderBoard() {
  const { gameId } = useParams();
  const [game, setGame] = useState({});
  const [players, setPlayers] = useState([]);
  const [playerPoints, setPlayerPoints] = useState(null);
  const [playerIds, setPlayerIds] = useState([]);

  const jwt = tokenService.getLocalAccessToken();

  useEffect(() => {
    async function fetchGame() {
      const gameResponse = await fetch(`/api/v1/games/${gameId}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      const gameData = await gameResponse.json();
      if (gameData) {
        setGame(gameData);
        setPlayers(gameData.players);
      }
    }
    fetchGame();
  }, [gameId, jwt]);

  useEffect(() => {
    const fetchBoard = async (id) => {
      const response = await fetch(
        `/api/v1/boards/game/${gameId}/player/${id}`,
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        }
      );

      const boardData = await response.json();

      if (boardData) {
        setPlayerPoints((prevPoints) => {
          // Crear un nuevo Map a partir del Map anterior
          const updatedPoints = new Map(prevPoints);
          // Actualizar los puntos para el jugador actual
          updatedPoints.set(id, boardData.points);
          // Convertir el Map a un array, ordenarlo y luego convertirlo de nuevo a un Map
          return new Map(
            [...updatedPoints.entries()].sort((a, b) => b[1] - a[1])
          );
        });
      }
    };

    players.forEach((player) => {
      fetchBoard(player.user.id);
    });
  }, [gameId, players, jwt]);

  function calculaLogros(puntos) {
    if (puntos < 80) {
      return "Ninguno";
    } else if (puntos >= 80 && puntos < 100) {
      return "Explorador de Patio";
    } else if (puntos >= 100 && puntos < 120) {
      return "Explorador Normalito";
    } else if (puntos >= 120 && puntos < 150) {
      return "Explorador Profesional";
    } else if (puntos >= 150) {
      return "Marco Polo";
    }
  }

  useEffect(() => {
    if (playerPoints) {
      const playerIds = Array.from(playerPoints.keys());
      setPlayerIds(playerIds);
    }
  }, [playerPoints]);

  const playerList = playerIds.map((playerId, index) => (
    <tr
      key={index}
      style={{ borderTop: "2px solid gray", borderBottom: "2px solid gray" }}
    >
      <td style={{ padding: "10px", fontSize: "35px", textAlign: "center" }}>
        {index + 1}
      </td>
      <td style={{ padding: "10px", fontSize: "20px", textAlign: "center" }}>
        {players &&
          players.find((player) => player.user.id === playerId).firstName}{" "}
        {players &&
          players.find((player) => player.user.id === playerId).lastName}
      </td>
      <td style={{ padding: "10px", fontSize: "20px", textAlign: "center" }}>
        {playerPoints && playerPoints.get(playerId)}
      </td>
      <td style={{ padding: "10px", fontSize: "20px", textAlign: "center" }}>
        {playerPoints && calculaLogros(playerPoints.get(playerId))}
      </td>
    </tr>
  ));

  function checkTie() {
    if (players.length === 2) {
      return playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[1]);
    } else if (players.length === 3) {
      return (
        playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[1]) &&
        playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[2])
      );
    } else if (players.length === 4) {
      return (
        playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[1]) &&
        playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[2]) &&
        playerPoints.get(playerIds[0]) === playerPoints.get(playerIds[3])
      );
    } else {
      return false;
    }
  }

  return (
    <div className="leaderboard-container">
      <h1>Tabla de puntuaciones</h1>
      <div className="leaderboard-hero-div">
        <div className="scrollable-div-admin">
          <table
            style={{
              maxWidth: "100%", // Cambia esto a 100% para que la tabla ocupe todo el espacio disponible
              margin: "auto",
              tableLayout: "fixed",
              width: "100%", // Asegúrate de que la tabla tenga un ancho de 100%
              overflowX: "auto", // Añade desplazamiento horizontal si el contenido es demasiado ancho
            }}
          >
            <thead>
              <tr>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Posición
                </th>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Nombre
                </th>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Puntos
                </th>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Logros
                </th>
              </tr>
            </thead>
            <tbody>{playerList && playerList.map((player) => player)}</tbody>
          </table>
        </div>
      </div>
      <div style={{ display: "flex", justifyContent: "center", gap: "5px", marginTop: "20px" }}>
        {players
            .map((player) => (
              <OpponentsBoards
                key={player.user.id} // Añadir key para optimización de renderizado en React
                jwt={jwt}
                userId={player.user.id}
                gameId={gameId}
                name={player.firstName + " " + player.lastName}
                tableroCreado={true}
              />
            ))}
      </div>
      {/* Mensaje en el caso de empate. Los dos primeros jugadores  */}
      {playerPoints && players && playerIds.length >= 2 && checkTie() && (
        <div className="tie-div">
          <h2> ¡Empate! </h2>
          <h4>
            {" "}
            {players &&
              players.find((player) => player.user.id === playerIds[0])
                .firstName}{" "}
            {players &&
              players.find((player) => player.user.id === playerIds[0])
                .lastName}{" "}
            y{" "}
            {players &&
              players.find((player) => player.user.id === playerIds[1])
                .firstName}{" "}
            {players &&
              players.find((player) => player.user.id === playerIds[1])
                .lastName}{" "}
            son los ganadores{" "}
          </h4>
        </div>
      )}
      <Link to="/">
        <button className="modal-button">Volver</button>
      </Link>
    </div>
  );
}
