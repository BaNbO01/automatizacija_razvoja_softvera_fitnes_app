import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Pagination } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const MyTrainingPlansList = () => {
  const [trainingPlans, setTrainingPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10; 

  useEffect(() => {
    const fetchTrainingPlans = async () => {
      try {
        const response = await api.get(`/plan-treninga?page=${currentPage}&size=${pageSize}`);
        setTrainingPlans(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju planova treninga.');
        console.error('Error fetching training plans:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchTrainingPlans();
  }, [currentPage]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

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
      <Row className="mb-4 align-items-center">
        <Col>
          <h2 className="text-primary">Moji Planovi Treninga</h2>
        </Col>
        <Col xs="auto">
          <Button as={Link} to="/my-training-plans/add" variant="success">
            Dodaj Novi Plan
          </Button>
        </Col>
      </Row>

      {trainingPlans.length === 0 ? (
        <Alert variant="info" className="text-center">Nemate kreiranih planova treninga.</Alert>
      ) : (
        <>
          <Row xs={1} md={2} lg={3} className="g-4">
            {trainingPlans.map(plan => (
              <Col key={plan.id}>
                <Card className="h-100 shadow-sm border-0">
                  <Card.Body className="d-flex flex-column">
                    <Card.Title className="text-secondary">{plan.naziv}</Card.Title>
                    <Card.Text className="text-muted">
                      Datum: {new Date(plan.datum).toLocaleDateString('sr-RS')}
                    </Card.Text>
                    <Button as={Link} to={`/my-training-plans/${plan.id}`} variant="primary" className="mt-auto">
                      Pogledaj Detalje
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
          {totalPages > 1 && (
            <Pagination className="justify-content-center mt-4">
              {[...Array(totalPages).keys()].map(page => (
                <Pagination.Item
                  key={page}
                  active={page === currentPage}
                  onClick={() => handlePageChange(page)}
                >
                  {page + 1}
                </Pagination.Item>
              ))}
            </Pagination>
          )}
        </>
      )}
    </Container>
  );
};

export default MyTrainingPlansList;
