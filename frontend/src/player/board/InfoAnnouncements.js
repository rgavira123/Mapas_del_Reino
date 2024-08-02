import React, { useState } from "react";
import "../../static/css/player/board/InfoAnnouncements.css"; // Archivo CSS para estilos, opcional

const InfoAnnouncements = ({
  castillo,
  pradera,
  montaña,
  rio,
  bosque,
  pueblo,
}) => {
  const [showInfo, setShowInfo] = useState(false);

  const handleMouseEnter = () => {
    setShowInfo(true);
  };

  const handleMouseLeave = () => {
    setShowInfo(false);
  };

  return (
    <div
      className="ann-icon-container"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <div className="ann-icon">
        <p>Anuncios restantes</p>
      </div>
      {showInfo && (
        <div className="ann-tooltip">
          <div className="ann-tooltip-content">
            Anuncios de CASTILLOS restantes: {castillo}
          </div>
          <div className="ann-tooltip-content">
            Anuncios de PRADERAS restantes: {pradera}
          </div>
          <div className="ann-tooltip-content">
            Anuncios de MONTAÑAS restantes: {montaña}
          </div>
          <div className="ann-tooltip-content">
            Anuncios de RÍOS restantes: {rio}
          </div>
          <div className="ann-tooltip-content">
            Anuncios de BOSQUES restantes: {bosque}
          </div>
          <div className="ann-tooltip-content">
            Anuncios de PUEBLOS restantes: {pueblo}
          </div>
        </div>
      )}
    </div>
  );
};

export default InfoAnnouncements;
