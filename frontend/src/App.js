import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Register from "./auth/register";
import Login from "./auth/login";
import Logout from "./auth/logout";

import tokenService from "./services/token.service";
import SwaggerDocs from "./public/swagger";

import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";

import Perfil from "./player/perfil/perfilList";
import EditarPerfil from "./player/perfil/perfilEdit";
import GameList from "./player/game/GameList";
import MyGames from "./player/myGames";
import Lobby from "./player/lobby";
import FinishedGamesList from "./admin/games/FinishedGamesList";
import StartedGamesList from "./admin/games/StartedGamesList";
import GameBoard from "./player/board";
import PlayerCreateAdmin from "./admin/users/PlayerCreateAdmin";
import LeaderBoard from "./player/game/LeaderBoard";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  );
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = [];
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let userRoutes = <></>;
  let publicRoutes = <></>;
  let playerRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route
            path="/users"
            exact={true}
            element={
              <PrivateRoute>
                <UserListAdmin />
              </PrivateRoute>
            }
          />
          <Route
            path="/users/:username"
            exact={true}
            element={
              <PrivateRoute>
                <UserEditAdmin />
              </PrivateRoute>
            }
          />
          <Route
            path="/users/newPlayer"
            exact={true}
            element={
              <PrivateRoute>
                <PlayerCreateAdmin />
              </PrivateRoute>
            }
          />
          <Route
            path="/games/finishedGames"
            exact={true}
            element={
              <PrivateRoute>
                <FinishedGamesList />
              </PrivateRoute>
            }
          />
          <Route
            path="/games/startedGames"
            exact={true}
            element={
              <PrivateRoute>
                <StartedGamesList />
              </PrivateRoute>
            }
          />
        </>
      );
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route
            path="/mygames"
            exact={true}
            element={
              <PrivateRoute>
                <MyGames />
              </PrivateRoute>
            }
          />
          <Route
            path="/gameList"
            exact={true}
            element={
              <PrivateRoute>
                <GameList />
              </PrivateRoute>
            }
          />
          <Route
            path="/perfilList"
            exact={true}
            element={
              <PrivateRoute>
                <Perfil />
              </PrivateRoute>
            }
          />
          <Route
            path="/perfilEdit"
            exact={true}
            element={
              <PrivateRoute>
                <EditarPerfil />
              </PrivateRoute>
            }
          />
          <Route
            path="/lobby/:id"
            exact={true}
            element={
              <PrivateRoute>
                <Lobby />
              </PrivateRoute>
            }
          />
          <Route
            path="/game/:gameId/:userId"
            exact={true}
            element={
              <PrivateRoute>
                <GameBoard />
              </PrivateRoute>
            }
          />
          <Route
            path="/game/:gameId/leaderboard"
            exact={true}
            element={
              <PrivateRoute>
                <LeaderBoard />
              </PrivateRoute>
            }
          />
        </>
      );
    }
  });

  if (!jwt) {
    publicRoutes = (
      <>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </>
    );
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
      </>
    );
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback}>
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          <Route path="/docs" element={<SwaggerDocs />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
