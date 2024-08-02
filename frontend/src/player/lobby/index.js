import '../../static/css/player/lobby/lobby.css'
import tokenService from '../../services/token.service';
import React, { useEffect, useState } from 'react';
import getIdFromUrl from "../../util/getIdFromUrl";
import { useNavigate } from 'react-router-dom';
import Modal from './LobbyModal';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import FadeawayMessage from './FadeawayMessage';

export default function Lobby() {
    const user = tokenService.getUser();
    const navigation = useNavigate();
    const welcome = `¡Bienvenido a la sala, ${user.username}!`;
    const jwt = tokenService.getLocalAccessToken();
    const gameId = getIdFromUrl(2);
    const[length, setLength] = useState(0);
    const[players, setPlayers] = useState([]);
    const [game, setGame] = useState(null);
    const [playerId, setPlayerId] = useState(null);
    const [liderId, setLiderId] = useState(null);
    const[name, setName] = useState("");
    const [liderCondition, setLiderCondition] = useState(false);
    const [isLeaveModalOpen, setIsLeaveModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [selectedGame, setSelectedGame] = useState(null);

    const [messageStack, setMessageStack] = useState([]);
    const lastMessage = messageStack[messageStack.length - 1];




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
                setPlayerId(playerData.id);
            }
        }
        
        fetchPlayer();
    }, []);

    
    useEffect(() => {
        async function fetchGame() {
            
            const gameResponse = await fetch(`/api/v1/games/${gameId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
                },
            });
            const gameData = await gameResponse.json();
            if (gameData) {
                setGame(gameData);
                
            }
        }
        
        fetchGame();
    }, [gameId,messageStack]);

    useEffect(() => {
        if(game){
            setPlayers(game.players);
            setLength(game.players.length);
            setLiderId(game.lider.id);
            setName(game.name);
            setLiderCondition(liderId === playerId);
            console.log(liderCondition);
        }
    }, [game]);

    useEffect(() => {
        setLiderCondition(liderId === playerId);
    }, [liderId, playerId]);




    useEffect(() => {

    const url = 'http://localhost:8080/ws';
    const socket = new SockJS(url);
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/lobby/${gameId}`, (message) => {  
            console.log(message.body);
            setMessageStack([...messageStack, message.body]);
            
        });
        stompClient.subscribe(`/topic/gameDeleted/${gameId}`, (message) => {
            if(message.body === 'true'){
                console.log(message.body)
                navigation(`/`, {state: {gameDeleted:true}});
            }
        });

        stompClient.subscribe(`/topic/start/${gameId}`, () => {
            const playerId = tokenService.getUser().id;
            navigation(`/game/${gameId}/${playerId}`);
        });

        

    });

    return () => {
        if (stompClient) {
            stompClient.disconnect();
        }
    };

      

    }, [gameId]);

    // recarga periódica de la página, para nada eficiente pero de momento es lo que hay



    async function deleteGame(gameId){
        // Confirmación muy chapucera pero necesaria pa no liarla. Cambiar en el futuro.
        const jwt = tokenService.getLocalAccessToken();
        const deleteGameResponse = await fetch(`/api/v1/games/${gameId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json"
            },
        });
        if (deleteGameResponse) {
                navigation(`/`);
        }
    } 

    async function leaveGame(gameId){
        const jwt = tokenService.getLocalAccessToken();
        const leaveGameResponse = await fetch(`/api/v1/games/leave/${gameId}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json"
            },
        });
        const leaveGameData = await leaveGameResponse.json();
        if (leaveGameData) {
            navigation(`/gameList`);
        }
    }

    async function startGame(gameId){

        const jwt = tokenService.getLocalAccessToken();
        const startGameResponse = await fetch(`/api/v1/games/start/${gameId}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                "Content-Type": "application/json"
            },
        });

        const startGameData = await startGameResponse.json();
        if (startGameData) {
            navigation(`/game/${gameId}/${tokenService.getUser().id}`);
        }


    }

    const toggleDeleteModal = () => {
        setIsDeleteModalOpen(!isDeleteModalOpen);
    }

    const toggleLeaveModal = () => {
        setIsLeaveModalOpen(!isLeaveModalOpen);
    }

    function handleLeave(gameId){
        setSelectedGame(gameId);
        toggleLeaveModal();
    }

    function handleDelete(gameId){
        setSelectedGame(gameId);
        toggleDeleteModal();
    }

    return (
        <div className='lobby-page-container'>
            <FadeawayMessage    message={lastMessage} />
            <h1>{(game ?? {}).name}</h1>
            <h2>{length < 4 && welcome}</h2>
            {length < 4 && (
                <h2>
                    Esperando a que se unan más jugadores
                    <span className="dot dot1">.</span>
                    <span className="dot dot2">.</span>
                    <span className="dot dot3">.</span>
                </h2>
            )}
            {length === 4 && (
                <h2 style={{ fontSize: '40px' }}>
                    ¡Bienvenidos aventureros!
                </h2>
            
            )}
            <div className='hero-div'>
            {Array.isArray(players) && players.map((player, playerIndex) => (
                <div style={{ fontSize: '30px'}} key={playerIndex}>
                    {`${player.firstName} ${player.lastName} (${player.gremio.gremio}) ${player.id === liderId ? ' ✦' : ''}`}
                </div>
            ))}
            </div>
            {length > 1 && (
                <h2 className="blinking-text" style={{ fontSize: '35px', paddingTop: '20px' }}>
                    ¡Todo listo para comenzar!
                </h2>
            )}
            <div style={{ flexDirection: 'row' }}>
                {liderCondition && (
                    <button className='lobby-game-button' onClick={() => handleDelete(gameId)}>Eliminar partida</button>
                )}
                {liderCondition && length > 1 && (
                    <button className='lobby-game-button' onClick={ () => startGame(gameId)}>Comenzar partida</button>
                )}
                {length > 1 && (
                    <button className='lobby-game-button' onClick={() => handleLeave(gameId)}>Abandonar partida</button>
                )}
            </div>
            <Modal
                isModalOpen={isLeaveModalOpen}
                toggleModal={toggleLeaveModal}
                onConfirm={() => leaveGame(selectedGame)}
            />
            <Modal
                isModalOpen={isDeleteModalOpen}
                toggleModal={toggleDeleteModal}
                onConfirm={() => deleteGame(selectedGame)}
            />
        </div>
    )


    // Lider: boton de comenzar, boton de eliminar partida, boton de abandonar partida (pierdes status de lider)
    // Jugador: boton de abandonar partida
    // Si solo esta el lider y abandona la partida, se elimina la partida
    

}

