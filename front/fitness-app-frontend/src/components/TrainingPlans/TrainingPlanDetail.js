import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Container, Card, Alert, Spinner, ListGroup, Row, Col, Button } from 'react-bootstrap';
import api from '../../services/api';

const TrainingPlanDetail = () => {
  const { id } = useParams();
  const [trainingPlan, setTrainingPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTrainingPlanDetail = async () => {
      try {
        const response = await api.get(`/plan-treninga/${id}`);
        setTrainingPlan(response.data);
        console.log(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju detalja plana treninga.');
        console.error('Error fetching training plan detail:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchTrainingPlanDetail();
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

  if (!trainingPlan) {
    return (
      <Container className="my-5">
        <Alert variant="info" className="text-center">Plan treninga nije pronađen.</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <Card className="p-4 shadow-lg border-0 mb-5">
        <Card.Body>
          <h2 className="text-primary mb-3">{trainingPlan.naziv}</h2>
          <p className="text-muted"><strong>Datum kreiranja:</strong> {new Date(trainingPlan.datum).toLocaleDateString('sr-RS')}</p>
          <hr />
          <h3 className="text-secondary mb-3">Vežbe u Planu</h3>

          {trainingPlan.planoviVezbi && trainingPlan.planoviVezbi.length > 0 ? (
            <ListGroup variant="flush">
              {trainingPlan.planoviVezbi.map(planVezbe => (
                <ListGroup.Item key={planVezbe.id} className="d-flex justify-content-between align-items-center">
                  <div>
                    <div className="fw-bold">{planVezbe.vezba?.naziv || 'N/A'}</div>
                    <small className="text-muted">
                      {planVezbe.brojSerija} serija x {planVezbe.brojPonavljanja} ponavljanja
                    </small>
                  </div>
                  {planVezbe.vezba && (
                    <Button as={Link} to={`/exercises/${planVezbe.vezba.id}`} variant="outline-info" size="sm">
                      Detalji Vežbe
                    </Button>
                  )}
                </ListGroup.Item>
              ))}
            </ListGroup>
          ) : (
            <Alert variant="info" className="text-center mt-3">Nema vežbi u ovom planu treninga.</Alert>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default TrainingPlanDetail;
