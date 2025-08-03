import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Container, Card, Alert, Spinner, Button, ListGroup, Row, Col } from 'react-bootstrap';
import api from '../../services/api';

const FitnessDiaryDetail = () => {
  const { id } = useParams();
  const [diary, setDiary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDiaryDetail = async () => {
      try {
        const response = await api.get(`/dnevnici/${id}`);
        setDiary(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju detalja dnevnika.');
        console.error('Error fetching diary detail:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDiaryDetail();
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

  if (!diary) {
    return (
      <Container className="my-5">
        <Alert variant="info" className="text-center">Dnevnik nije pronađen.</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <Card className="p-4 shadow-lg border-0 mb-5">
        <Card.Body>
          <h2 className="text-primary mb-3">{diary.naslov}</h2>
          <p className="text-muted"><strong>Kratak opis:</strong> {diary.kratakOpis}</p>
          <hr />
          <Row className="mb-4 align-items-center">
            <Col>
              <h3 className="text-secondary">Stavke Dnevnika</h3>
            </Col>
            <Col xs="auto">
              <Button as={Link} to={`/my-diaries/${diary.id}/add-item`} variant="success">
                Dodaj Stavku
              </Button>
            </Col>
          </Row>

          {diary.stavkeDnevnika && diary.stavkeDnevnika.length > 0 ? (
            <ListGroup variant="flush">
              {diary.stavkeDnevnika.map(item => (
                <ListGroup.Item key={item.id} className="d-flex justify-content-between align-items-start">
                  <div>
                    <div className="fw-bold">{item.nazivAktivnosti}</div>
                    <small className="text-muted">{new Date(item.datum).toLocaleDateString('sr-RS')}</small>
                    <p className="mb-0">{item.komentar}</p>
                  </div>
                </ListGroup.Item>
              ))}
            </ListGroup>
          ) : (
            <Alert variant="info" className="text-center mt-3">Nema stavki u ovom dnevniku.</Alert>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default FitnessDiaryDetail;
