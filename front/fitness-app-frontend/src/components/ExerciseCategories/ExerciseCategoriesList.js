import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import api from '../../services/api';

const ExerciseCategoriesList = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get('/kategorije-vezbe');
        setCategories(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju kategorija vežbi.');
        console.error('Error fetching exercise categories:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchCategories();
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
      <h2 className="text-center mb-4 text-primary">Sve Kategorije Vežbi</h2>
      {categories.length === 0 ? (
        <Alert variant="info" className="text-center">Nema dostupnih kategorija vežbi.</Alert>
      ) : (
        <Row xs={1} md={2} lg={3} className="g-4">
          {categories.map(category => (
            <Col key={category.id}>
              <Card className="h-100 shadow-sm border-0">
                <Card.Body className="d-flex flex-column justify-content-center align-items-center">
                  <Card.Title className="text-secondary text-center">{category.naziv}</Card.Title>
                  <Card.Text className="text-muted text-center">
                    Kategorija vežbi za različite tipove treninga.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default ExerciseCategoriesList;
