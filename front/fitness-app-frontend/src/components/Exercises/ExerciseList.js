import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const BASE_MEDIA_URL = 'http://localhost:8080/api';

const ExerciseList = () => {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchExercises = async () => {
      try {
        const response = await api.get('/vezbe');
        setExercises(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju vežbi.');
        console.error('Error fetching exercises:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchExercises();
  }, []);

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Učitavanje...</span>
        </Spinner>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="my-5">
        <Alert variant="danger" className="text-center">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <h2 className="text-center mb-4 text-primary">Sve Vežbe</h2>
      {exercises.length === 0 ? (
        <Alert variant="info" className="text-center">Nema dostupnih vežbi.</Alert>
      ) : (
        <Row xs={1} md={2} lg={3} className="g-4">
          {exercises.map(exercise => (
            <Col key={exercise.id}>
              <Card className="h-100 shadow-sm border-0">
                {exercise.slika && (
                  <Card.Img
                    variant="top"
                    src={`${BASE_MEDIA_URL}${exercise.slika}`} 
                    alt={exercise.naziv}
                    className="card-img-top"
                  />
                )}
                <Card.Body className="d-flex flex-column">
                  <Card.Title className="text-secondary">{exercise.naziv}</Card.Title>
                  <Card.Text className="text-muted flex-grow-1">
                    {exercise.opis.length > 150 ? exercise.opis.substring(0, 150) + '...' : exercise.opis}
                  </Card.Text>
                  <Button as={Link} to={`/exercises/${exercise.id}`} variant="primary" className="mt-auto">
                    Detalji
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default ExerciseList;
