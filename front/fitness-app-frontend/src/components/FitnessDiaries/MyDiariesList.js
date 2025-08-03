import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Pagination } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const MyDiariesList = () => {
  const [diaries, setDiaries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10; 

  useEffect(() => {
    const fetchDiaries = async () => {
      try {
        const response = await api.get(`/users/dnevnici?page=${currentPage}&size=${pageSize}`);
        setDiaries(response.data.content);
        setTotalPages(response.data.totalPages);
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri dohvaćanju dnevnika.');
        console.error('Error fetching diaries:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDiaries();
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
          <h2 className="text-primary">Moji Fitnes Dnevnici</h2>
        </Col>
        <Col xs="auto">
          <Button as={Link} to="/my-diaries/add" variant="success">
            Dodaj Novi Dnevnik
          </Button>
        </Col>
      </Row>

      {diaries.length === 0 ? (
        <Alert variant="info" className="text-center">Nemate kreiranih fitnes dnevnika.</Alert>
      ) : (
        <>
          <Row xs={1} md={2} lg={3} className="g-4">
            {diaries.map(diary => (
              <Col key={diary.id}>
                <Card className="h-100 shadow-sm border-0">
                  <Card.Body className="d-flex flex-column">
                    <Card.Title className="text-secondary">{diary.naslov}</Card.Title>
                    <Card.Text className="text-muted flex-grow-1">
                      {diary.kratakOpis.length > 150 ? diary.kratakOpis.substring(0, 150) + '...' : diary.kratakOpis}
                    </Card.Text>
                    <Button as={Link} to={`/my-diaries/${diary.id}`} variant="primary" className="mt-auto">
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

export default MyDiariesList;
