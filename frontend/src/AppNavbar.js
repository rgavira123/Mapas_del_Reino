import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import { useLocation } from 'react-router-dom';
import getIdFromUrl from './util/getIdFromUrl';

function AppNavbar() {
    const location = useLocation();
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);

    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt])

    let adminLinks = <></>;
    let ownerLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;
    let playerLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    {/* <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/owners">Owners</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/pets">Pets</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/vets">Vets</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/consultations">Consultations</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/clinicOwners">Clinic Owners</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/clinics">Clinics</NavLink>
                    </NavItem> */}
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/users">USUARIOS</NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "OWNER") {
            ownerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/myPets">My Pets</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/consultations">Consultations</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/plan">Plan</NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "VET") {
            ownerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/consultations">Consultations</NavLink>
                    </NavItem>
                </>
            )
        }

        if (role === "CLINIC_OWNER") {
            ownerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/clinics">Clinics</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/owners">Owners</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/consultations">Consultations</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black" }} tag={Link} to="/vets">Vets</NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "PLAYER") {
            playerLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "black", marginTop: '8px' }} tag={Link} to="/perfilList">PERFIL</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "black", marginTop: '8px' }} tag={Link} to="/mygames">MIS PARTIDAS</NavLink>
                    </NavItem>
                </>
            )
        }

        if (role === "PLAYER" && location.pathname.startsWith('/lobby/') ){
            playerLinks = (<></>)
        }
    })

    if (!jwt) {
        publicLinks = (
            <>

                <NavItem>
                    <NavLink style={{ color: "black", marginTop: '8px' }} id="register" tag={Link} to="/register">REGISTRARSE</NavLink>
                </NavItem>
                {/* <NavItem>
                    <NavLink style={{ color: "white" }} id="plans" tag={Link} to="/plans">Pricing Plans</NavLink>
                </NavItem> */}
                <NavItem>
                    <NavLink style={{ color: "black", marginTop: '8px' }} id="login" tag={Link} to="/login">INICIAR SESIÓN</NavLink>
                </NavItem>
            </>
        )
    } else if (location.pathname.startsWith('/lobby/')){
        userLogout = (<>
            <NavbarText style={{ color: "black", textTransform: 'uppercase' }} className="justify-content-end">{username}</NavbarText>
        </>)
    } else {
        // userLinks = (
        //     <>
        //         <NavItem>
        //             <NavLink style={{ color: "black" }} tag={Link} to="/dashboard">Dashboard</NavLink>
        //         </NavItem>
        //     </>
        // )
        userLogout = (
            <>
                <NavItem>
                    <NavLink style={{ color: "black" }} id="docs" tag={Link} to="/docs">DOCS</NavLink>
                </NavItem>
                {/* <NavItem>
                    <NavLink style={{ color: "white" }} id="plans" tag={Link} to="/plans">Pricing Plans</NavLink>
                </NavItem> */}
                <NavbarText style={{ color: "black", textTransform: 'uppercase' }} className="justify-content-end">{username}</NavbarText>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "black" }} id="logout" tag={Link} to="/logout">CERRAR SESIÓN</NavLink>
                </NavItem>
            </>
        )

    }

    function dontNavigate(){
        if(location.pathname.startsWith('/lobby/'));

    }

    return (
        <div>
            <Navbar expand="md">
                <NavbarBrand href="/" style={{ marginTop: '1px' }} onClick={
                    (event) => {
                        event.preventDefault();
                        if (window.location.pathname.startsWith('/lobby/')) {
                            const gameId = getIdFromUrl(2);
                            window.location.href = `/lobby/${gameId}`;
                        } else {
                            window.location.href = "/";
                        }
                    }
                }>

                    <img alt="logo" src="/logo-monstruo.png" style={{ height: 40, width: 40 }} />
                    LOS MAPAS DEL REINO
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                        {ownerLinks}
                        {playerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {userLogout}

                    </Nav>
                </Collapse>
            </Navbar>
        </div>
    );
}

export default AppNavbar;