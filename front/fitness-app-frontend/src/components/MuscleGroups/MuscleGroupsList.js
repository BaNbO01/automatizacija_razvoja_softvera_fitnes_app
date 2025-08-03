import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const BASE_MEDIA_URL = 'http://localhost:8080/api'; 

const MuscleGroupsList = () => {
  const [muscleGroups, setMuscleGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMuscleGroups = async () => {
      try {
        const response = await api.get('/grupe-misica');
        setMuscleGroups(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju grupa mišića.');
        console.error('Error fetching muscle groups:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchMuscleGroups();
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
      <h2 className="text-center mb-4 text-primary">Sve Grupe Mišića</h2>
      {muscleGroups.length === 0 ? (
        <Alert variant="info" className="text-center">Nema dostupnih grupa mišića.</Alert>
      ) : (
        <Row xs={1} md={2} lg={3} className="g-4">
          {muscleGroups.map(group => (
            <Col key={group.id}>
              <Card className="h-100 shadow-sm border-0">
                {group.slika && (
                  <Card.Img
                    variant="top"
                    src={`${BASE_MEDIA_URL}${group.slika}`} 
                    alt={group.naziv}
                    className="card-img-top"
                  />
                )}
                <Card.Body className="d-flex flex-column">
                  <Card.Title className="text-secondary">{group.naziv}</Card.Title>
                  <Card.Text className="text-muted flex-grow-1">
                    {group.opis.length > 150 ? group.opis.substring(0, 150) + '...' : group.opis}
                  </Card.Text>
                  <Button as={Link} to={`/muscle-groups/${group.id}`} variant="primary" className="mt-auto">
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

export default MuscleGroupsList;
