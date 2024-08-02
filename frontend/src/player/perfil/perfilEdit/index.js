import "../../../static/css/auth/authButton.css";
import "../../../static/css/auth/authPage.css";
import "../../../static/css/player/perfil/perfil.css";
import tokenService from "../../../services/token.service";
import { editarPerfilInputs } from "../form/editarPerfilInputs";
import FormGenerator from "../../../components/formGenerator/formGenerator";
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import { Alert } from "reactstrap";


//TODO: HACER ESTILOS
const jwt = tokenService.getLocalAccessToken();

export default function EditarPerfil() {
  const user = tokenService.getUser();

  const [player, setPlayer] = useState({});
  const [message, setMessage] = useState("");

  useEffect(()=>{ setUp();},
  []);

  async function setUp(){
    const myPlayer = await (
      await fetch(`/api/v1/players/user/${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      })
    ).json();
    if(myPlayer){
      setPlayer(myPlayer);
    }
  }

  console.log(player);

  const [dataLoaded, setDataLoaded] = useState(false);

  const navigate = useNavigate();

  const editPlayerFormRef = useRef(null);

  function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  function handleSubmit({ values }) {

    setMessage(null);

    if (!validateEmail(values.email)) {
      setMessage("Correo inválido");
      return;
    }

    console.log(values);
    if (!editPlayerFormRef.current.validate()) return;

    const body = {
      lastName: values.lastName,
      email: values.email,
      firstName: values.firstName,
    };

    const bodyJSON = JSON.stringify(body);
    console.log(bodyJSON); 

    fetch(`/api/v1/players/${player.id}`, {
      method: "PUT",
      headers: {
        'Content-Type': "application/json",
        'Authorization': `Bearer ${jwt}`,
      },
      body: bodyJSON,
    })
      .then((res) => {
        if (res.status === 200) {
          navigate("/perfilList/");
        }
      })
      .catch((err) => {
        setMessage(err.message);
      });;
  }


  useEffect(() => {
    editarPerfilInputs.forEach((input) => {
      if (player.id !== "") {
        const defaultValue = player[input.name];

        input.defaultValue = defaultValue;
        setDataLoaded(true);
      } else {
        input.defaultValue = "";
      }
    });

  }, [player]);

  return (
    <div className="perfil-page-container">
      {message ? (
      <Alert color="primary">{message}</Alert>
      ) : (
      <></>
      )}
      <h1>{"Editar perfil"}</h1>
      <div className="hero-div">
          <FormGenerator
            key="editForm"
            ref={editPlayerFormRef}
            inputs={editarPerfilInputs}
            onSubmit={handleSubmit}
            buttonText="Confirmar"
            buttonClassName="editar-perfil-button set-margin2"
          />
      </div>
      <Link to='/perfilList'>
        <button className='modal-button'>Volver</button>
      </Link>
    </div>
  );
}
