import React, { useState, useEffect } from 'react';
import '../../App.css';
import '../../static/css/player/gameList/gameList.css';
import tokenService from '../../services/token.service';
import { Link } from 'react-router-dom'; 
import { useNavigate } from 'react-router-dom';

export default function StartedGamesList() {

    const [startedGames, setStartedGames] = useState([]);
    const navigation = useNavigate();
    
    useEffect(() => {
        async function fetchGames() {
            const jwt = tokenService.getLocalAccessToken();
            const gamesResponse = await fetch(`/api/v1/games/started`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const gamesData = await gamesResponse.json();
            if (gamesData) {
                setStartedGames(gamesData);
            }
        }
        fetchGames();
    }, []);

    console.log(startedGames);
    
    return (
        <div className="gameList-page-container">
            <h1>Partidas en curso</h1>
            {startedGames.length===0 && <h2>No hay partidas en curso</h2>}
            {startedGames.length>0 && (
                <div className="hero-div">
                <div className='scrollable-div'>
                    <table style={{
                        maxWidth: '100%',
                        margin: 'auto',
                        tableLayout: 'fixed',
                    }} >
                        <thead>
                            <tr>
                                <th style={{ paddingRight: '30px', paddingBottom: '5px', paddingTop: '5px', fontSize: '30px' }}>Nombre</th>
                                <th style={{ paddingRight: '30px', paddingBottom: '5px', paddingTop: '5px', fontSize: '30px' }}>Jugadores</th>
                                <th style={{ paddingRight: '30px', paddingBottom: '5px', paddingTop: '5px', fontSize: '30px' }}>Líder</th>
                                <th style={{ paddingRight: '30px', paddingBottom: '5px', paddingTop: '5px', fontSize: '30px' }}>Fecha</th>                                
                                
                            </tr>
                        </thead>
                        <tbody>
                            {Array.isArray(startedGames) && startedGames.map((game, index) => (
                                <React.Fragment key={index}>
                                    <tr style={{ borderTop: '2px solid gray', borderBottom: '2px solid gray'}}>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>{game.name}</td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>
                                            {Array.isArray(game.players) && game.players.map((player, playerIndex) => (
                                                `${player.firstName} ${player.lastName}${playerIndex < game.players.length - 1 ? ', ' : ''}`
                                            ))}
                                        </td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>{game.lider.firstName} {game.lider.lastName}</td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>
                                        
                                        </td>
                                    </tr>
                                </React.Fragment>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
            )}
            <Link to='/'>
                <button className='modal-button'>Volver</button>
            </Link>
        </div>
    );
}