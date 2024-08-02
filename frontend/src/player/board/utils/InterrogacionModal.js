import React from "react";
import { ModalHeader, ModalBody } from "reactstrap";
import { Modal } from "reactstrap";
import tokenService from "../../../services/token.service";
import "../../../static/css/player/board/Interrogation.css";

export default function InterrogacionModal({
  visible,
  onClose,
  dado1,
  dado2,
  dado3,
  dado4,
  idTablero,
}) {
  const jwt = tokenService.getLocalAccessToken();

  if (!visible) {
    return null;
  }

  const handleClick = (seccion, valor) => {
    const url =
      seccion === "A"
        ? `/api/v1/boards/${idTablero}/setPuntuacionInterrogacion/criteriosA/${valor}`
        : `/api/v1/boards/${idTablero}/setPuntuacionInterrogacion/criteriosB/${valor}`;

    fetch(url, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        "Content-Type": "application/json",
      },
    }).then((response) => {
      if (response.ok) {
        onClose();
      }
    });
  };

  return (
    <Modal isOpen={visible} toggle={onClose}>
      <ModalHeader toggle={onClose}>
        Elige con qué criterio quieres puntuar
      </ModalHeader>
      <ModalBody>
        <div className="crit-modal-button-container">
          <button
            className="crit-modal-button"
            onClick={() => handleClick("A", dado1)}
          >
            Criterio A1
          </button>
          <button
            className="crit-modal-button"
            onClick={() => handleClick("A", dado2)}
          >
            Criterio A2
          </button>
          <button
            className="crit-modal-button"
            onClick={() => handleClick("B", dado3)}
          >
            Criterio B1
          </button>
          <button
            className="crit-modal-button"
            onClick={() => handleClick("B", dado4)}
          >
            Criterio B2
          </button>
        </div>
      </ModalBody>

    </Modal>
  );
}
