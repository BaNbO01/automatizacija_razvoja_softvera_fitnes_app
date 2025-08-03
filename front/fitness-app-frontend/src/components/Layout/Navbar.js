import React from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import logo from '../../assets/logo.png'; 

const AppNavbar = () => {
  const { isAuthenticated, userRole, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="shadow-sm">
      <Container>
        <Navbar.Brand as={Link} to="/">
          <img
            src={logo}
            width="30"
            height="30"
            className="d-inline-block align-top me-2"
            alt="Fitness App Logo"
          />
          Fitness App
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">Početna</Nav.Link>
         
            

            {isAuthenticated && userRole === 'VEZBAC' && (
              <>
                 <Nav.Link as={Link} to="/exercises">Vežbe</Nav.Link>
                <Nav.Link as={Link} to="/my-diaries">Moji Dnevnici</Nav.Link>
                <Nav.Link as={Link} to="/my-training-plans">Moji Planovi Treninga</Nav.Link>
              </>
            )}
            {isAuthenticated && userRole === 'TRENER' && (
              <>
               <Nav.Link as={Link} to="/exercises">Vežbe</Nav.Link>
                <Nav.Link as={Link} to="/exercises/add">Dodaj Vežbu</Nav.Link>
              </>
            )}
            {isAuthenticated && userRole === 'ADMIN' && (
              <>
                <Nav.Link as={Link} to="/users/trainers">Treneri</Nav.Link>
                <Nav.Link as={Link} to="/users/vezbaci">Vežbači</Nav.Link>
                <Nav.Link as={Link} to="/muscle-groups/add">Dodaj Grupu Mišića</Nav.Link>
              </>
            )}

            {isAuthenticated && (
              <>
              <Nav.Link as={Link} to="/muscle-groups">Grupe Mišića</Nav.Link>
            <Nav.Link as={Link} to="/exercise-categories">Kategorije Vežbi</Nav.Link>
              </>
            )}
          </Nav>
          <Nav>
            {isAuthenticated ? (
              
              
             <Button variant="outline-light" onClick={handleLogout}>Odjavi se ({userRole})</Button>
          
             
            ) : (
              <>
                <Button as={Link} to="/login" variant="outline-light" className="me-2">Prijavi se</Button>
                <Button as={Link} to="/register" variant="light">Registruj se</Button>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default AppNavbar;
