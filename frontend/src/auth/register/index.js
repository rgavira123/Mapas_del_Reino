import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { registerFormOwnerInputs } from "./form/registerFormOwnerInputs";
import { registerFormVetInputs } from "./form/registerFormVetInputs";
import { registerFormClinicOwnerInputs } from "./form/registerFormClinicOwnerInputs";
import { registerFormPlayerInputs } from "./form/registerFormPlayerInputs";
import { useEffect, useRef, useState } from "react";
import '@fontsource/lakki-reddy';
import { Alert } from "reactstrap";

export default function Register() {
  let [type, setType] = useState(null);
  let [authority, setAuthority] = useState(null);
  let [clinics, setClinics] = useState([]);
  let [gremio, setGremio] = useState(null);
  let [message, setMessage] = useState(null);

  const registerFormRef = useRef();

  function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  function handleButtonClick(event) {
    const target = event.target;
    let value = event.target.getAttribute('data-role');
    let gremio = event.target.getAttribute('data-gremio');
    if (value === "Back") value = null;
    else setAuthority(value);
    setType(value);
    setGremio(gremio);


    console.log(value)
    console.log(gremio)
  }

  function handleSubmit({ values }) {

    if (!registerFormRef.current.validate()) return;

    const request = values;
    // request.clinic = clinics.filter((clinic) => clinic.name === request.clinic)[0];
    request["authority"] = authority;
    request["gremio"] = gremio;
    let state = "";

    console.log(request)
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
          const loginRequest = {
            username: request.username,
            password: request.password,
          };

          fetch("/api/v1/auth/signin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(loginRequest),
          })
            .then(function (response) {
              if (response.status === 200) {
                state = "200";
                return response.json();
              } else {
                state = "";
                return response.json();
              }
            })
            .then(function (data) {
              if (state !== "200") alert(data.message);
              else {
                tokenService.setUser(data);
                tokenService.updateLocalAccessToken(data.token);
                window.location.href = "/";
              }
            })
            .catch((message) => {
              alert(message);
            });
        }
      })
      .catch((message) => {
        alert(message);
      });
  }

  useEffect(() => {
    if (type === "Owner" || type === "Vet") {
      if (registerFormOwnerInputs[5].values.length === 1) {
        fetch("/api/v1/clinics")
          .then(function (response) {
            if (response.status === 200) {
              return response.json();
            } else {
              return response.json();
            }
          })
          .then(function (data) {
            setClinics(data);
            if (data.length !== 0) {
              let clinicNames = data.map((clinic) => {
                return clinic.name;
              });

              registerFormOwnerInputs[5].values = ["None", ...clinicNames];
            }
          })
          .catch((message) => {
            alert(message);
          });
      }
    }
  }, [type]);

  if (type) {
    return (
      <div className="auth-page-container">
        {message ? (
        <Alert color="primary">{message}</Alert>
        ) : (
        <></>
        )}
        <h1>Registro</h1>
        <div className="hero-div">
          <FormGenerator
            ref={registerFormRef}
            inputs={
              type === "Owner" ? registerFormOwnerInputs
                : type === "Vet" ? registerFormVetInputs
                  : type === "Clinic Owner" ? registerFormClinicOwnerInputs
                    : registerFormPlayerInputs

            }
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
          <h1>Registro</h1>
          <h2 className="text-center text-md">
            Elige tu rol para esta aventura
          </h2>
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
              onClick={handleButtonClick}>
              LADRON
            </button>
            <button
              className="auth-player set-width"
              data-role="PLAYER"
              data-gremio="CURANDEROS"
              onClick={handleButtonClick}>
              CURANDERO
            </button>
          </div>
        </div>
      </div>
    );
  }
}
