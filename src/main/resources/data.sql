-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);
-- mapas del reino

-- 4 players with password 4dm1n and authority player
INSERT INTO authorities(id,authority) VALUES (5,'PLAYER');
INSERT INTO appusers(id,username,password,authority) VALUES (20,'player1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',5);
INSERT INTO appusers(id,username,password,authority) VALUES (21,'player2','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',5);
INSERT INTO appusers(id,username,password,authority) VALUES (22,'player3','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',5);
INSERT INTO appusers(id,username,password,authority) VALUES (23,'player4','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',5);

-- añadimos gremios

INSERT INTO gremios(id,gremio) VALUES (1,'CABALLEROS');
INSERT INTO gremios(id,gremio) VALUES (2,'MAGOS');
INSERT INTO gremios(id,gremio) VALUES (3,'GUERREROS');
INSERT INTO gremios(id,gremio) VALUES (4,'LADRONES');
INSERT INTO gremios(id,gremio) VALUES (5,'CURANDEROS');

-- añadimos jugador de prueba

INSERT INTO	players(id, first_name, last_name, email, user_id, gremios) VALUES (1, 'MC', 'Teteu','teteu@gmail.com', 20, 1);
INSERT INTO	players(id, first_name, last_name, email, user_id, gremios) VALUES (2, 'Manito', 'Camela','tete@gmail.com', 21, 1);
INSERT INTO	players(id, first_name, last_name, email, user_id, gremios) VALUES (3, 'su', 'madre','tet@gmail.com', 22, 1);
INSERT INTO	players(id, first_name, last_name, email, user_id, gremios) VALUES (4, 'Manito', 'to','te@gmail.com', 23, 1);

-- añadimos partidas

INSERT INTO games(id, name, status, finish_date, lider_id) VALUES 
    (1, 'Partida 1','FINISHED', '2023-01-04 17:32', 1),
    (2, 'Partida 2','FINISHED', '2023-01-04 17:32',1),
    (3, 'Partida 3','FINISHED', '2023-01-04 17:32',1),
    (4, 'Partida 4','FINISHED', '2023-01-04 17:32',1),
    (5, 'Partida 5','FINISHED', '2023-01-04 17:32',1),
    (6, 'Partida 6','FINISHED', '2023-01-04 17:32',1),
    (7, 'Partida 7','FINISHED', '2023-01-04 17:32',1),
    (8, 'Partida 8','FINISHED', '2023-01-04 17:32',1),
    (9, 'Partida 9','FINISHED', '2023-01-04 17:32',1),
    (10, 'Partida 10','FINISHED', '2023-01-04 17:32',1),
    (11, 'Partida 11','CREATED',null, 1);

-- asociamos jugadores con partidas

INSERT INTO game_players(game_id, player_id) VALUES 
    (1, 1),
    (2, 1),
    (3, 1),
    (1, 2),
    (2, 2),
    (2, 3),
    (2, 4),
    (4, 1),
    (5, 1),
    (6, 1),
    (7, 1),
    (8, 1),
    (9, 1),
    (10, 1),
    (11, 1);


