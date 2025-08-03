import React, { useState, useEffect } from 'react';
import { Form, Button, Container, Card, Alert, Spinner, Row, Col, ListGroup } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const AddTrainingPlan = () => {
  const [nazivPlana, setNazivPlana] = useState('');
  const [selectedVezbe, setSelectedVezbe] = useState([]); 
  const [availableVezbe, setAvailableVezbe] = useState([]);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAvailableExercises = async () => {
      try {
        const response = await api.get('/vezbe'); 
        setAvailableVezbe(response.data);
      } catch (err) {
        setError('Greška pri učitavanju dostupnih vežbi.');
        console.error('Error fetching available exercises:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchAvailableExercises();
  }, []);

  const handleAddVezba = (vezbaId) => {
    const vezba = availableVezbe.find(v => v.id === parseInt(vezbaId));
    if (vezba && !selectedVezbe.some(sv => sv.id === vezba.id)) {
      setSelectedVezbe(prev => [...prev, {
        id: vezba.id,
        naziv: vezba.naziv,
        brojSerija: vezba.preporuceniBrojSerija || 1, 
        brojPonavljanja: vezba.preporuceniBrojPonavljanja || 1 
      }]);
    }
  };

  const handleRemoveVezba = (vezbaId) => {
    setSelectedVezbe(prev => prev.filter(v => v.id !== vezbaId));
  };

  const handleVezbaChange = (vezbaId, field, value) => {
    setSelectedVezbe(prev => prev.map(v =>
      v.id === vezbaId ? { ...v, [field]: parseInt(value) } : v
    ));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess('');

    if (!nazivPlana || selectedVezbe.length === 0) {
      setError('Molimo unesite naziv plana i dodajte barem jednu vežbu.');
      return;
    }

    const vezbePayload = selectedVezbe.map(v => ({
      id: v.id,
      brojSerija: v.brojSerija,
      brojPonavljanja: v.brojPonavljanja
    }));

    try {
      const response = await api.post('/plan-treninga', {
        naziv: nazivPlana,
        vezbe: vezbePayload
      });
      setSuccess('Plan treninga uspešno dodat!');
      setTimeout(() => navigate(`/my-training-plans/${response.data.id}`), 1400);
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju plana treninga.');
      console.error('Error adding training plan:', err.response || err);
    }
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

  return (
    <Container className="my-5">
      <h2 className="text-center mb-4 text-primary">Kreiraj Novi Plan Treninga</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}
      <Card className="p-4 shadow-lg border-0">
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="nazivPlana">
            <Form.Label>Naziv Plana Treninga</Form.Label>
            <Form.Control type="text" value={nazivPlana} onChange={(e) => setNazivPlana(e.target.value)} required />
          </Form.Group>

          <h4 className="mt-4 mb-3 text-secondary">Dodaj Vežbe</h4>
          <Row className="mb-3">
            <Col md={8}>
              <Form.Group controlId="selectVezba">
                <Form.Label>Izaberi Vežbu</Form.Label>
                <Form.Control as="select" onChange={(e) => handleAddVezba(e.target.value)} value="">
                  <option value="">-- Izaberi vežbu --</option>
                  {availableVezbe.map(vezba => (
                    <option key={vezba.id} value={vezba.id}>
                      {vezba.naziv}
                    </option>
                  ))}
                </Form.Control>
              </Form.Group>
            </Col>
          </Row>

          {selectedVezbe.length > 0 && (
            <ListGroup className="mb-4">
              {selectedVezbe.map(vezba => (
                <ListGroup.Item key={vezba.id}>
                  <Row className="align-items-center">
                    <Col md={4}>
                      <strong>{vezba.naziv}</strong>
                    </Col>
                    <Col md={3}>
                      <Form.Group controlId={`serije-${vezba.id}`}>
                        <Form.Label>Serije</Form.Label>
                        <Form.Control
                          type="number"
                          value={vezba.brojSerija}
                          onChange={(e) => handleVezbaChange(vezba.id, 'brojSerija', e.target.value)}
                          min="1"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={3}>
                      <Form.Group controlId={`ponavljanja-${vezba.id}`}>
                        <Form.Label>Ponavljanja</Form.Label>
                        <Form.Control
                          type="number"
                          value={vezba.brojPonavljanja}
                          onChange={(e) => handleVezbaChange(vezba.id, 'brojPonavljanja', e.target.value)}
                          min="1"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={2} className="text-end">
                      <Button variant="danger" size="sm" onClick={() => handleRemoveVezba(vezba.id)}>
                        Ukloni
                      </Button>
                    </Col>
                  </Row>
                </ListGroup.Item>
              ))}
            </ListGroup>
          )}

          <Button variant="primary" type="submit" className="w-100 mt-3">
            Kreiraj Plan Treninga
          </Button>
        </Form>
      </Card>
    </Container>
  );
};

export default AddTrainingPlan;
