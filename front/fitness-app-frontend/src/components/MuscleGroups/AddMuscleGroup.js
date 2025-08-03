import React, { useState } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const AddMuscleGroup = () => {
  const [naziv, setNaziv] = useState('');
  const [opis, setOpis] = useState('');
  const [slika, setSlika] = useState(null);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setSlika(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess('');

    if (!naziv || !opis || !slika) {
      setError('Molimo popunite sva polja i dodajte sliku.');
      return;
    }

    const data = new FormData();
    data.append('request', new Blob([JSON.stringify({ naziv, opis })], { type: 'application/json' }));
    data.append('slika', slika);

    try {
      await api.post('/grupe-misica', data, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      setSuccess('Grupa mišića uspešno dodata!');
      setTimeout(() => navigate('/muscle-groups'), 1400);
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju grupe mišića.');
      console.error('Error adding muscle group:', err.response || err);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card className="p-4 shadow-lg" style={{ width: '100%', maxWidth: '500px' }}>
        <Card.Body>
          <h2 className="text-center mb-4 text-primary">Dodaj Novu Grupu Mišića</h2>
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="naziv">
              <Form.Label>Naziv</Form.Label>
              <Form.Control type="text" value={naziv} onChange={(e) => setNaziv(e.target.value)} required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="opis">
              <Form.Label>Opis</Form.Label>
              <Form.Control as="textarea" rows={3} value={opis} onChange={(e) => setOpis(e.target.value)} required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="slika">
              <Form.Label>Slika</Form.Label>
              <Form.Control type="file" onChange={handleFileChange} accept="image/*" required />
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100 mt-3">
              Dodaj Grupu Mišića
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AddMuscleGroup;
