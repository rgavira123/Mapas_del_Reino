import React, { useState, useEffect } from "react";
import { HexGrid, Layout, Hexagon, Pattern, Text } from "react-hexgrid";
import "../../static/css/player/board/opponents.css"; // Asegúrate de importar el archivo CSS

const OpponentsBoards = ({ jwt, gameId, userId, name, tableroCreado }) => {
  const [showTablero, setShowTablero] = useState(false);
  const [tablero, setTablero] = useState(null);
  const [boardId, setBoardId] = useState(null);
  const [casillas, setCasillas] = useState([]);

  useEffect(() => {
    if (showTablero && tableroCreado) {
      fetchBoard();
    }
  }, [showTablero, tableroCreado]);

  async function fetchBoard() {
    const response = await fetch(
      `/api/v1/boards/game/${gameId}/player/${userId}`,
      {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      }
    );
    const data = await response.json();
    if (data) {
      setTablero(data);
      setBoardId(data.id);
      setCasillas(data.casillas);
    }
  }

  const handleClick = () => {
    setShowTablero(true);
  };

  const handleClose = () => {
    setShowTablero(false);
  };

  function obtenerImagen(ficha) {
    switch (ficha) {
      case "RIO":
        return "https://img.freepik.com/foto-gratis/fotografia-aerea-rio-rodeado-islas-cubiertas-vegetacion-luz-solar_181624-6504.jpg";
      case "PRADERA":
        return "https://cdn0.bioenciclopedia.com/es/posts/3/8/0/clima_83_0_600.jpg";
      case "PUEBLO":
        return "https://img.freepik.com/foto-gratis/vista-nocturna-albarracin_1398-3615.jpg?size=626&ext=jpg&ga=GA1.1.672697106.1718928000&semt=sph";
      case "CASTILLO":
        return "https://th.bing.com/th/id/R.73a50d982cf65718654b069ea2ef4fca?rik=udWqEY5lR7v52Q&pid=ImgRaw&r=0";
      case "MONTAÑA":
        return "https://images.ecestaticos.com/X6jKxwFNcSPcl3CEQuItXawaAc8=/0x0:2097x1430/1200x900/filters:fill(white):format(jpg)/f.elconfidencial.com%2Foriginal%2F614%2F40c%2Ff02%2F61440cf024b55a412a97e4c4c59fffbd.jpg";
      case "BOSQUE":
        return "https://www.deutschland.de/sites/default/files/styles/image_carousel_mobile/public/media/image/tdt_02102023_wald_wald_der_zukunft_mischwald.jpg?h=e3193a6a&itok=TY6U3ekv";
      default:
        return "";
    }
  }

  return (
    <div className="opp-boards-container">
      <div className="opp-board-icon" onClick={handleClick}>
        <p>{name}</p>
      </div>
      {showTablero && (
        <div className="opp-board-modal">
          <button className="close-modal" onClick={handleClose}>
            Cerrar
          </button>
          <HexGrid width={1200} height={700}>
            <Layout
              size={{ x: 6, y: 6 }}
              flat={false}
              spacing={1.1}
              origin={{ x: 0, y: 0 }}
            >
              {tablero &&
                tablero.casillas.map((casilla, i) => (
                  <Hexagon
                    key={i}
                    q={casilla.q}
                    s={casilla.s}
                    r={casilla.r}
                    fill={casilla.tipoCasilla}
                  >
                    <Pattern
                      id={casilla.tipoCasilla}
                      link={obtenerImagen(casilla.tipoCasilla)}
                      size={{ x: 8, y: 6.5 }}
                    />
                    {casilla.tipoCasilla === "PODER_MASMENOS" && (
                      <Text className="hexagon-text">+1 / -1</Text>
                    )}
                    {casilla.tipoCasilla === "PODER_INTERROGACION" && (
                      <Text className="hexagon-text">❓</Text>
                    )}
                    {casilla.tipoCasilla !== "PODER_INTERROGACION" &&
                      casilla.tipoCasilla !== "PODER_MASMENOS" && (
                        <Text className="hexagon-text-invisible">O</Text>
                      )}
                  </Hexagon>
                ))}
            </Layout>
          </HexGrid>
        </div>
      )}
    </div>
  );
};

export default OpponentsBoards;
