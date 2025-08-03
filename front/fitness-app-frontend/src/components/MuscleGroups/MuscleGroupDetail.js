import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Spinner, Form,Button } from 'react-bootstrap';
import api from '../../services/api';

const BASE_MEDIA_URL = 'http://localhost:8080/api';

const MuscleGroupDetail = () => {
  const { id } = useParams();
  const [muscleGroup, setMuscleGroup] = useState(null);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const [groupRes, categoriesRes] = await Promise.all([
          api.get(`/grupe-misica/${id}${selectedCategory ? `?kategorija_id=${selectedCategory}` : ''}`),
          api.get('/kategorije-vezbe') 
        ]);
        setMuscleGroup(groupRes.data);
        setCategories(categoriesRes.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju detalja grupe mišića.');
        console.error('Error fetching muscle group detail:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [id, selectedCategory]);

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

  if (!muscleGroup) {
    return (
      <Container className="my-5">
        <Alert variant="info" className="text-center">Grupa mišića nije pronađena.</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <Card className="p-4 shadow-lg border-0 mb-5">
        <Row>
          <Col md={6}>
            {muscleGroup.slika && (
              <img
                src={`${BASE_MEDIA_URL}${muscleGroup.slika}`} 
                alt={muscleGroup.naziv}
                className="img-fluid rounded shadow-sm mb-4"
                style={{ maxHeight: '400px', objectFit: 'cover', width: '100%' }}
              />
            )}
          </Col>
          <Col md={6}>
            <h2 className="text-primary mb-3">{muscleGroup.naziv}</h2>
            <p className="text-muted"><strong>Opis:</strong> {muscleGroup.opis}</p>
          </Col>
        </Row>
      </Card>

      <h3 className="text-center mb-4 text-primary">Vežbe za {muscleGroup.naziv}</h3>
      <Row className="mb-4 justify-content-center">
        <Col md={4}>
          <Form.Group controlId="categoryFilter">
            <Form.Label>Filtriraj po kategoriji:</Form.Label>
            <Form.Control
              as="select"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              <option value="">Sve kategorije</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.naziv}</option>
              ))}
            </Form.Control>
          </Form.Group>
        </Col>
      </Row>

      {muscleGroup.vezbe && muscleGroup.vezbe.length > 0 ? (
        <Row xs={1} md={2} lg={3} className="g-4">
          {muscleGroup.vezbe.map(exercise => (
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
                    {exercise.opis.length > 100 ? exercise.opis.substring(0, 100) + '...' : exercise.opis}
                  </Card.Text>
                  <Button as={Link} to={`/exercises/${exercise.id}`} variant="outline-primary" className="mt-auto">
                    Detalji Vežbe
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      ) : (
        <Alert variant="info" className="text-center mt-4">Nema dostupnih vežbi za ovu grupu mišića {selectedCategory && `u odabranoj kategoriji`}.</Alert>
      )}
    </Container>
  );
};

export default MuscleGroupDetail;
