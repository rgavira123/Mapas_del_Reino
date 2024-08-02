import React, { useEffect, useState } from 'react';
import tokenService from '../../services/token.service';
import '../../static/css/player/myGames/myGames.css'
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';


export default function MyGames() {
    const [player, setPlayer] = useState({});
    const [myGames, setMyGames] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        async function fetchPlayer() {
            const user = tokenService.getUser();
            const jwt = tokenService.getLocalAccessToken();
            
            const playerResponse = await fetch(`/api/v1/players/user/${user.id}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const playerData = await playerResponse.json();
            if (playerData) {
                setPlayer(playerData);
            }
        }
        
        fetchPlayer();
    }, []);

    useEffect(() => {
        async function fetchGames() {
            if (player) {
                const jwt = tokenService.getLocalAccessToken();
                const myGamesResponse = await fetch(`/api/v1/games/player/${player.id}`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    },
                });
                const myGamesData = await myGamesResponse.json();
                if (myGamesData) {
                    setMyGames(myGamesData);
                }
            }
        }

        fetchGames();
    }, [player]); // This effect depends on the player state

    return (
      <div className="mygames-page-container">
        <h1>Mis Partidas</h1>
        {myGames.length === 0 && <h2>No tienes partidas todavía</h2>}
        {myGames.length > 0 && (
          <div className="hero-div">
            <div className="scrollable-div">
              <table
                style={{
                  maxWidth: "100%",
                  margin: "auto",
                  tableLayout: "fixed",
                }}
              >
                <thead>
                  <tr>
                    <th
                      style={{
                        paddingRight: "30px",
                        paddingBottom: "5px",
                        paddingTop: "5px",
                        fontSize: "30px",
                      }}
                    >
                      Nombre
                    </th>
                    <th
                      style={{
                        paddingRight: "30px",
                        paddingBottom: "5px",
                        paddingTop: "5px",
                        fontSize: "30px",
                      }}
                    >
                      Jugadores
                    </th>
                    <th
                      style={{
                        paddingRight: "30px",
                        paddingBottom: "5px",
                        paddingTop: "5px",
                        fontSize: "30px",
                      }}
                    >
                      Finalización
                    </th>
                    <th
                      style={{
                        paddingRight: "30px",
                        paddingBottom: "5px",
                        paddingTop: "5px",
                        fontSize: "30px",
                      }}
                    ></th>
                  </tr>
                </thead>
                <tbody>
                  {Array.isArray(myGames) &&
                    myGames.map((game, index) => (
                      <React.Fragment key={index}>
                        <tr
                          style={{
                            borderTop: "2px solid gray",
                            borderBottom: "2px solid gray",
                          }}
                        >
                          <td style={{ padding: "20px", fontSize: "25px" }}>
                            {game.name}
                          </td>
                          <td style={{ padding: "20px", fontSize: "25px" }}>
                            {Array.isArray(game.players) &&
                              game.players.map(
                                (player, playerIndex) =>
                                  `${player.firstName} ${player.lastName}${
                                    playerIndex < game.players.length - 1
                                      ? ", "
                                      : ""
                                  }`
                              )}
                          </td>
                          <td style={{ padding: "20px", fontSize: "25px" }}>
                            {new Date(game.finishDate).toLocaleDateString(
                              "es-Es",
                              {
                                day: "2-digit",
                                month: "2-digit",
                                year: "numeric",
                              }
                            )}
                          </td>
                          <td style={{ padding: "20px", fontSize: "25px" }}>
                            <button
                              className='points-button'
                              onClick={() =>
                                navigate(`/game/${game.id}/leaderboard`)
                              }
                            >
                              Puntuaciones
                            </button>
                          </td>
                        </tr>
                      </React.Fragment>
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
        <Link to="/">
          <button className="modal-button">Volver</button>
        </Link>
      </div>
    );
}
