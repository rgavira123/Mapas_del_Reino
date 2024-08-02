import { useState } from "react";
import { Link } from "react-router-dom";
import { ButtonGroup } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";
import { useNavigate } from "react-router-dom";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const navigation = useNavigate();
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );

  
  const [alerts, setAlerts] = useState([]);

  const userList = users.map((user, index) => (
    <tr key={index} style={{ borderTop: '2px solid gray', borderBottom: '2px solid gray' }}>
      <td style={{ padding: '10px', fontSize: '20px', textAlign: 'center' }}>{user.username}</td>
      <td style={{ padding: '10px', fontSize: '20px', textAlign: 'center' }}>{user.authority.authority}</td>
      <td style={{ padding: '10px', fontSize: '20px', textAlign: 'center' }}>
        <ButtonGroup>
          <button
            className = "edit-button"
            onClick={() => navigation(`/users/${user.id}`)}
          >
            Editar
          </button>
          <button
            onClick={() =>
              deleteFromList(
                `/api/v1/users/${user.id}`,
                user.id,
                [users, setUsers],
                [alerts, setAlerts],
                setMessage,
                setVisible
              )
            }
            className="delete-button"
          >
            Eliminar
          </button>
        </ButtonGroup>
      </td>
    </tr>
  ));
  const modal = getErrorModal(setVisible, visible, message);

  // Suponiendo que userList es tu lista de usuarios
  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage] = useState(5); // Cambia esto según cuántos elementos quieras por página

  // Índices para paginación
  const indexOfLastUser = currentPage * usersPerPage;
  const indexOfFirstUser = indexOfLastUser - usersPerPage;
  const currentUsers = userList.slice(indexOfFirstUser, indexOfLastUser);

  // Cambiar página
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const pageNumbers = [];
  for (let i = 1; i <= Math.ceil(userList.length / usersPerPage); i++) {
    pageNumbers.push(i);
  }

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Usuarios</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <div>
        <button style={{ marginRight: "10px" }}
          className="admin-button"
          onClick={() => navigation("/users/new")}
        >
          Crear admin
        </button>
        <button
          className="admin-button"
          onClick={() => navigation("/users/newPlayer")}
        >
          Crear jugador
        </button>
      </div>
      <div className="hero-div-admin">
        <div className="scrollable-div-admin">
          <table
            style={{
              maxWidth: "100%", // Cambia esto a 100% para que la tabla ocupe todo el espacio disponible
              margin: "auto",
              tableLayout: "fixed",
              width: "100%", // Asegúrate de que la tabla tenga un ancho de 100%
              overflowX: "auto", // Añade desplazamiento horizontal si el contenido es demasiado ancho
            }}
          >
            <thead>
              <tr>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Nombre de Usuario
                </th>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Autoridad
                </th>
                <th
                  style={{
                    paddingRight: "30px",
                    paddingBottom: "10px",
                    paddingTop: "5px",
                    fontSize: "25px",
                    textAlign: "center",
                  }}
                >
                  Acciones
                </th>
              </tr>
            </thead>
            <tbody>{currentUsers.map((user) => user)}</tbody>
          </table>
          <div
            className="pagination"
            style={{
              display: "flex",
              justifyContent: "center",
              marginBottom: "-25px",
            }}
          >
            {pageNumbers.map((number) => (
              <a
                key={number}
                onClick={() => paginate(number)}
                className={
                  number === currentPage
                    ? "pagination-button-current"
                    : "pagination-button"
                }
              >
                {number}
              </a>
            ))}
          </div>
        </div>
      </div>
      <Link to="/">
        <button className="modal-button">Volver</button>
      </Link>
    </div>
  );
}

