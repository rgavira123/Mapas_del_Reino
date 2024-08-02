import React from 'react';
import { Modal } from 'reactstrap';
import '../../static/css/player/lobby/modal.css';

export default function LobbyModal({isModalOpen, toggleModal, onConfirm}) {
    return (

        <Modal
            isOpen={isModalOpen}
            toggle={toggleModal}
            className='fade-in'
            style={{
                content: {
                width: '50px',
                height: '10px',
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
            <div className='modal-header' style={{ textAlign: 'center' }}>
                <h2>¿Estás seguro de querer realizar esta acción?</h2>
            </div>
            <div className='modal-footer button-container' style={{ justifyContent: 'center' }}>
                <div className='button-wrapper'>
                <button className='lobby-cancel-modal-button' onClick={toggleModal}>Cancelar</button>
                </div>
                <div className='button-wrapper'>
                <button className='lobby-confirm-modal-button' onClick={onConfirm}>Confirmar</button>
                </div>
            </div>
        </Modal>
    );
}
