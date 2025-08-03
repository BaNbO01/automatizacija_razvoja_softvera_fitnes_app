import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Spinner, Button } from 'react-bootstrap';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const BASE_MEDIA_URL = 'http://localhost:8080/api'; 

const ExerciseDetail = () => {
  const { id } = useParams();
  const [exercise, setExercise] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { userRole } = useAuth(); 

  useEffect(() => {
    const fetchExercise = async () => {
      try {
        const response = await api.get(`/vezbe/${id}`);
        setExercise(response.data);
        console.log(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju detalja vežbe.');
        console.error('Error fetching exercise detail:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchExercise();
  }, [id]);

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

  if (!exercise) {
    return (
      <Container className="my-5">
        <Alert variant="info" className="text-center">Vežba nije pronađena.</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <Card className="p-4 shadow-lg border-0">
        <Row>
          <Col md={6}>
            {exercise.slika && (
              <img
                src={`${BASE_MEDIA_URL}${exercise.slika}`} 
                alt={exercise.naziv}
                className="img-fluid rounded shadow-sm mb-4"
                style={{ maxHeight: '400px', objectFit: 'cover', width: '100%' }}
              />
            )}
            {exercise.videoUrl && (
              <div className="video-container">
                <video controls className="w-100 rounded shadow-sm">
                  <source src={`${BASE_MEDIA_URL}${exercise.videoUrl}`} type="video/mp4" /> 
                  Vaš pretraživač ne podržava video tag.
                </video>
              </div>
            )}
          </Col>
          <Col md={6}>
            <h2 className="text-primary mb-3">{exercise.naziv}</h2>
            <p className="text-muted"><strong>Opis:</strong> {exercise.opis}</p>
            <p className="text-muted"><strong>Mišići na koje utiče:</strong> {exercise.misiciNaKojeUtice}</p>
            <p className="text-muted"><strong>Savet:</strong> {exercise.savet}</p>
            <p className="text-muted"><strong>Preporučeni broj serija:</strong> {exercise.preporuceniBrojSerija}</p>
            <p className="text-muted"><strong>Preporučeni broj ponavljanja:</strong> {exercise.preporuceniBrojPonavljanja}</p>
            {exercise.kategorija && (
              <p className="text-muted"><strong>Kategorija:</strong> {exercise.kategorija.naziv}</p>
            )}
          
            {userRole === 'TRENER' && (
              <Button as={Link} to={`/exercises/edit/${exercise.id}`} variant="outline-primary" className="mt-3">
                Izmeni Vežbu
              </Button>
            )}
          </Col>
        </Row>
      </Card>
    </Container>
  );
};

export default ExerciseDetail;
