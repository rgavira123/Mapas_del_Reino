import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { registerFormPlayerInputs } from "./registerFormPlayerInputs";
import { useRef, useState } from "react";
import "@fontsource/lakki-reddy";
import { Alert } from "reactstrap";

export default function PlayerCreateAdmin() {
  let [type, setType] = useState(null);
  let [authority, setAuthority] = useState(null);
  let [gremio, setGremio] = useState(null);
  let [message, setMessage] = useState(null);

  const registerFormRef = useRef();

  function validateEmail(email) {
    var re =
      /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  function handleButtonClick(event) {
    const target = event.target;
    let value = event.target.getAttribute("data-role");
    let gremio = event.target.getAttribute("data-gremio");
    if (value === "Back") value = null;
    else setAuthority(value);
    setType(value);
    setGremio(gremio);

    console.log(value);
    console.log(gremio);
  }

  function handleSubmit({ values }) {
    if (!registerFormRef.current.validate()) return;

    const request = values;
    request["authority"] = authority;
    request["gremio"] = gremio;

    console.log(request);
    setMessage(null);

    if (!validateEmail(request.email)) {
      setMessage("Correo inválido");
      return;
    }

    fetch("/api/v1/auth/signup", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(request),
    })
      .then(function (response) {
        if (response.status === 200) {
          // Redirección a "/users" después del sign up exitoso
          window.location.href = "/users";
        } else {
          // Manejo de errores o respuesta no exitosa
          return response.json();
        }
      })
      .then(function (data) {
        if (data && data.message) {
          // Mostrar mensaje de error si existe
          alert(data.message);
        }
      })
      .catch((message) => {
        // Manejo de errores de la petición fetch
        alert(message);
      });
  }

  if (type) {
    return (
      <div className="auth-page-container">
        {message ? <Alert color="primary">{message}</Alert> : <></>}
        <h1>Creación</h1>
        <div className="hero-div">
          <FormGenerator
            ref={registerFormRef}
            inputs={registerFormPlayerInputs}
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Save"
            buttonClassName="auth-player set-margin"
          />
        </div>
      </div>
    );
  } else {
    return (
      <div className="auth-page-container">
        <div className="auth-form-container">
          <h2 className="text-center text-md">Elige un rol para el jugador</h2>
          <div className="options-column">
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="CABALLEROS"
              onClick={handleButtonClick}
            >
              CABALLERO
            </button>
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="MAGOS"
              onClick={handleButtonClick}
            >
              MAGO
            </button>
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="GUERREROS"
              onClick={handleButtonClick}
            >
              GUERRERO
            </button>
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="LADRONES"
              onClick={handleButtonClick}
            >
              LADRON
            </button>
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="CURANDEROS"
              onClick={handleButtonClick}
            >
              CURANDERO
            </button>
          </div>
        </div>
      </div>
    );
  }
}
