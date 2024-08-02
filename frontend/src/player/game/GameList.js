import React, { useState, useEffect } from 'react';
import '../../App.css';
import '../../static/css/player/gameList/gameList.css';
import tokenService from '../../services/token.service';
import { Link } from 'react-router-dom'; 
import { useNavigate } from 'react-router-dom';

import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';



export default function GameList() {

    const [messageStack, setMessageStack] = useState([]);

    const [games, setGames] = useState([]);
    const navigation = useNavigate();
    
    useEffect(() => {
        async function fetchGames() {
            const jwt = tokenService.getLocalAccessToken();
            const gamesResponse = await fetch(`/api/v1/games/created`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const gamesData = await gamesResponse.json();
            if (gamesData) {
                setGames(gamesData);
            }
        }
        fetchGames();
    }, [messageStack]);

    useEffect(() => {

        const url = 'http://localhost:8080/ws';
        const socket = new SockJS(url);
        const stompClient = Stomp.over(socket);
    
        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/gameList`, (message) => {
            
                console.log(message.body);
                setMessageStack([...messageStack, message.body]);
                
    
    
            });
    
    
        });
    
        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        };
    
          
    
        }, []);

    async function joinGame(gameId) {
        const jwt = tokenService.getLocalAccessToken();
        const joinGameResponse = await fetch(`/api/v1/games/join/${gameId}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json"
            },
        });
        const joinGameData = await joinGameResponse.json();
        if (joinGameData) {
            navigation(`/lobby/${gameId}`);
        }
    }
    
    return (
        <div className="gameList-page-container">
            <h1>Partidas disponibles</h1>
            {games.length===0 && <h2>No hay partidas disponibles</h2>}
            {games.length>0 && (
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
                                <th style={{ paddingRight: '30px', paddingBottom: '5px', paddingTop: '5px', fontSize: '30px' }}>Restantes</th>
                                
                            </tr>
                        </thead>
                        <tbody>
                            {Array.isArray(games) && games.map((game, index) => (
                                <React.Fragment key={index}>
                                    <tr style={{ borderTop: '2px solid gray', borderBottom: '2px solid gray'}}>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>{game.name}</td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>
                                            {Array.isArray(game.players) && game.players.map((player, playerIndex) => (
                                                `${player.firstName} ${player.lastName}${playerIndex < game.players.length - 1 ? ', ' : ''}`
                                            ))}
                                        </td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>{game.lider.firstName} {game.lider.lastName}</td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>{4 - game.players.length}</td>
                                        <td style={{ padding: '20px', fontSize: '25px' }}>
                                            <button className='start-game-button' onClick={ () => joinGame(game.id)}>Unirse a Partida</button>
                                    
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