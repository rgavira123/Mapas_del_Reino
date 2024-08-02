import React, { useState } from "react";
import "../../static/css/player/board/InfoIcon.css"; // Archivo CSS para estilos, opcional

const InfoIcon = ({ infoText }) => {
  const [showInfo, setShowInfo] = useState(false);

  const handleMouseEnter = () => {
    setShowInfo(true);
  };

  const handleMouseLeave = () => {
    setShowInfo(false);
  };

  return (
    <div
      className="info-icon-container"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <div className="info-icon">
        <span>?</span>
      </div>
      {showInfo && (
        <div className="info-tooltip">
          <div className="info-tooltip-content">{infoText}</div>
        </div>
      )}
    </div>
  );
};

export default InfoIcon;
