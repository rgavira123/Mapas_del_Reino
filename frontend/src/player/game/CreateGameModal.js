import React, { useState } from 'react';
import Modal from 'react-modal';
import tokenService from '../../services/token.service';
import axios from 'axios';
import '../../static/css/home/home.css';
import '../../App.css';
import '@fontsource/lakki-reddy';
import { useNavigate } from 'react-router-dom';

export default function CreateGameModal({ isOpen, onClose }) {
  const navigate = useNavigate();
  const user = tokenService.getUser();
  const defaultInputValue = user ? `Partida de ${user.username}` : "Partida";
  const [inputValue, setInputValue] = useState(defaultInputValue);


    
  const handleConfirmClick = async () => {
    const jwt = JSON.parse(window.localStorage.getItem("jwt"));
    const response = await axios.post('api/v1/games', inputValue, {
      headers: {
        'Authorization': `Bearer ${jwt}`,
        'Content-Type': 'text/plain'
      }
    });

    navigate(`/lobby/${response.data.id}`);
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Create Game Modal"
      className='modal-container fade-in'
      style={{
        content: {
          width: '50%',
          height: '50%',
          margin: 'auto',
          borderRadius: '20px',
          padding: '2rem',
          boxShadow: '0 0 10px rgba(0,0,0,0.1)',
          border: '3px solid black',
        },
        overlay: {
          backgroundColor: 'rgba(0,0,0,0.5)',
        },
      }}
    >
      <div className='modal-header'>
        <h2>Nombre de la partida</h2>
      </div>
      <div className='input-wrapper'>
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
        />
      </div>
      <div className='modal-footer button-container'>
        <div className='button-wrapper'>
          <button className='modal-button' onClick={onClose}>Cancelar</button>
        </div>
        <div className='button-wrapper'>
          <button className='modal-button' onClick={handleConfirmClick}>Confirmar</button>
        </div>
      </div>
    </Modal>
  );
}
