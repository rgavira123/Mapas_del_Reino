import React, { useState, useEffect } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import CreateGameModal from '../player/game/CreateGameModal';
import tokenService from '../services/token.service';
import GameDeletedModal from './GameDeletedModal';

export default function Home() {
    const location = useLocation();
    const navigate = useNavigate();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [deletedModal, setDeletedModal] = useState(false);
    const jwt = JSON.parse(window.localStorage.getItem("jwt"));
    const user = tokenService.getUser();

    useEffect(() => {
        const jwt = JSON.parse(window.localStorage.getItem("jwt"));
        if (jwt) {
            setIsLoggedIn(true);
        }
    }, []);

    const openModal = () => {
        setModalIsOpen(true);
    };

    const closeModal = () => {
        setModalIsOpen(false);
    };


    useEffect(() => {
        if(isLoggedIn && user.roles[0] == "ADMIN"){
            setIsAdmin(true);
        }
    }, [user]);
    
    useEffect(() => { 
        if (location.state && location.state.gameDeleted === true) {
            setDeletedModal(true);
        }

        

    }, [location]);

    const toggleDeletedModal = () => {
        setDeletedModal(!deletedModal);
        navigate("/")
        
    };

    return (
        <div className="home-page-container">
            <GameDeletedModal isOpen={deletedModal} toggle={toggleDeletedModal} />
            <div className="hero-div">
                <img src="/mapas.png" alt="map" className="map-image" />

                {isLoggedIn ? (
                    isAdmin ?  (
                        <h3 style={{ margin: '1rem', fontSize: '25px' }}>Bienvenido, administrador</h3>
                    ) : (
                        <h3 style={{ margin: '1rem', fontSize: '25px' }}>Bienvenido, aventurero</h3>
                    )
                ) : (
                    <h3 style={{ margin: '1rem', fontSize: '25px' }}>Inicia sesión para comenzar la aventura</h3>
                )}


                {isLoggedIn && !isAdmin &&
                    <div>
                    <button className='start-game-button' onClick={openModal}>Crear Partida</button>
                    <Link to="/gameList">
                        <button className='start-game-button'>Unirse a Partida</button>
                    </Link>
                    </div>
                }
                {!isLoggedIn && (
                    <div>
                        <Link to="/login">
                            <button className='start-game-button'>Iniciar Sesión</button>
                        </Link>
                        <Link to="/register">
                            <button className='start-game-button'>Registrarse</button>
                        </Link>
                    </div>
                )}
                {isAdmin && (
                    <div>
                        <Link to="/games/startedGames">
                            <button className='start-game-button'>Partidas en curso</button>
                        </Link>
                        <Link to="/games/finishedGames">
                            <button className='start-game-button'>Partidas terminadas</button>
                        </Link>
                    </div>
                    )
                }
                
                <CreateGameModal isOpen={modalIsOpen} onClose={closeModal} />

                
            </div>
        </div >
    );
}