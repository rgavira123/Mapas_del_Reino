import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../../services/token.service";
import { useNavigate } from "react-router-dom";
import "../../../static/css/player/perfil/perfil.css";

const user = tokenService.getUser();  
const jwt = tokenService.getLocalAccessToken();

export default function Perfil() {
  const [player, setPlayer] = useState({});

  const [message, setMessage] = useState([]);
  const [visible, setVisible] = useState(true);

  useEffect(()=>{ setUpPlayer();},[]);
    
  async function setUpPlayer(){
      const myplayer = await (
      await fetch(`/api/v1/players/user/${user.id}`, {
          headers: {
              Authorization: `Bearer ${jwt}`,
          },
      })
      ).json();
      if (myplayer) {
          setPlayer(myplayer);
      }
  }


  const navigator = useNavigate();

  const playerList = (
    <tr key={player.id}>
      <td className="text-center">{player.firstName}</td>
      <td className="text-center">{player.lastName}</td>
      <td className="text-center">{player.email}</td>
      <td className="text-center">
        <ButtonGroup>
          <Button
            size="sm"
            color="primary"
            aria-label={"edit-" + player.firstName}
            tag={Link}
            to={"/players/" + player.id}
          >
            Editar Perfil
          </Button>
        </ButtonGroup>
      </td>
    </tr>
  );

  return (
      <div className="perfil-page-container">
        <h1>Mi Perfil</h1>
        <div className="hero-div">
          <h2> Nombre: </h2>
          <h3> {player.firstName} </h3>
          <h2> Alias: </h2>
          <h3> {player.lastName} </h3>
          <h2> E-mail: </h2>
          <h3> {player.email} </h3>
          <Link to="/perfilEdit">
            <button className='modal-button'>Editar Perfil</button>
          </Link>
        </div>
        <Link to='/'>
                <button className='modal-button'>Volver</button>
            </Link>
      </div>
  );
}