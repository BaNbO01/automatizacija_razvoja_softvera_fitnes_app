import React, { useState } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const AddFitnessDiary = () => {
  const [naslov, setNaslov] = useState('');
  const [kratakOpis, setKratakOpis] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess('');

    if (!naslov || !kratakOpis) {
      setError('Molimo popunite sva polja.');
      return;
    }

    try {
      const response = await api.post('/dnevnici', { naslov, kratakOpis });
      setSuccess('Dnevnik uspešno dodat!');
      setTimeout(() => navigate(`/my-diaries/${response.data.id}`), 2000); 
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju dnevnika.');
      console.error('Error adding fitness diary:', err.response || err);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card className="p-4 shadow-lg" style={{ width: '100%', maxWidth: '500px' }}>
        <Card.Body>
          <h2 className="text-center mb-4 text-primary">Dodaj Novi Fitnes Dnevnik</h2>
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="naslov">
              <Form.Label>Naslov</Form.Label>
              <Form.Control type="text" value={naslov} onChange={(e) => setNaslov(e.target.value)} required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="kratakOpis">
              <Form.Label>Kratak Opis</Form.Label>
              <Form.Control as="textarea" rows={3} value={kratakOpis} onChange={(e) => setKratakOpis(e.target.value)} required />
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100 mt-3">
              Dodaj Dnevnik
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AddFitnessDiary;
