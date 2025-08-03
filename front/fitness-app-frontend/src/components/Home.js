import React from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home = () => {
  const { isAuthenticated, userRole } = useAuth();

  return (
    <Container className="my-5">
      <Row className="justify-content-center text-center mb-5">
        <Col md={8}>
          <h1 className="display-4 fw-bold text-primary">Dobrodošli u Fitness Aplikaciju!</h1>
          <p className="lead text-muted">
            Vaš partner za praćenje napretka, planiranje treninga i otkrivanje novih vežbi.
          </p>
          {!isAuthenticated && (
            <div className="mt-4">
              <Button as={Link} to="/register" variant="primary" size="lg" className="me-3 shadow-sm">
                Registruj se sada
              </Button>
              <Button as={Link} to="/login" variant="outline-primary" size="lg" className="shadow-sm">
                Prijavi se
              </Button>
            </div>
          )}
        </Col>
      </Row>

      <Row className="g-4">
        <Col md={4}>
          <Card className="h-100 shadow-sm border-0">
            <Card.Body className="d-flex flex-column">
              <Card.Title className="text-center text-secondary mb-3">Lični Dnevnici</Card.Title>
              <Card.Text className="text-muted flex-grow-1">
                Pratite svoje treninge, obroke i napredak. Zapišite svaku aktivnost i komentar.
              </Card.Text>
              <Button as={Link} to="/my-diaries" variant="outline-info" className="mt-auto">
                Pogledaj Dnevnike
              </Button>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm border-0">
            <Card.Body className="d-flex flex-column">
              <Card.Title className="text-center text-secondary mb-3">Baza Vežbi</Card.Title>
              <Card.Text className="text-muted flex-grow-1">
                Pronađite detaljne opise vežbi, slike i video zapise za svaku mišićnu grupu i kategoriju.
              </Card.Text>
              <Button as={Link} to="/exercises" variant="outline-info" className="mt-auto">
                Istraži Vežbe
              </Button>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 shadow-sm border-0">
            <Card.Body className="d-flex flex-column">
              <Card.Title className="text-center text-secondary mb-3">Planovi Treninga</Card.Title>
              <Card.Text className="text-muted flex-grow-1">
                Kreirajte i pratite personalizovane planove treninga prilagođene vašim ciljevima.
              </Card.Text>
              <Button as={Link} to="/my-training-plans" variant="outline-info" className="mt-auto">
                Kreiraj Plan
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {isAuthenticated && (
        <Row className="justify-content-center text-center mt-5">
          <Col md={8}>
            <h2 className="text-primary">Vaša Uloga: <span className="text-info">{userRole}</span></h2>
            {userRole === 'ADMIN' && (
              <p className="text-muted">Kao administrator, imate potpunu kontrolu nad korisnicima i podacima.</p>
            )}
            {userRole === 'TRENER' && (
              <p className="text-muted">Kao trener, možete kreirati i upravljati vežbama za svoje klijente.</p>
            )}
            {userRole === 'VEZBAC' && (
              <p className="text-muted">Kao vežbač, možete pratiti svoj napredak i planirati treninge.</p>
            )}
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default Home;
