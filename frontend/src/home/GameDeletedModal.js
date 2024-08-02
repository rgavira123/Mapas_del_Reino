
import React from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';

const GameDeletedModal = ({ isOpen, toggle }) => {
    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>Juego Eliminado</ModalHeader>
            <ModalBody>
                El juego en el que estabas ha sido eliminado.
            </ModalBody>
            <ModalFooter>
                <Button color="primary" onClick={toggle}>OK</Button>
            </ModalFooter>
        </Modal>
    );
};

export default GameDeletedModal;

