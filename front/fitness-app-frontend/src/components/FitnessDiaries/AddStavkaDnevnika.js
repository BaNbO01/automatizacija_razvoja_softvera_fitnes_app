import React, { useState } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../services/api';

const AddStavkaDnevnika = () => {
  const { id: dnevnikId } = useParams(); 
  const [datum, setDatum] = useState('');
  const [nazivAktivnosti, setNazivAktivnosti] = useState('');
  const [komentar, setKomentar] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess('');

    if (!datum || !nazivAktivnosti || !komentar) {
      setError('Molimo popunite sva polja.');
      return;
    }

    try {
      await api.post(`/dnevnici/${dnevnikId}/stavke`, { datum, nazivAktivnosti, komentar });
      setSuccess('Stavka dnevnika uspešno dodata!');
      setTimeout(() => navigate(`/my-diaries/${dnevnikId}`), 1400);
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju stavke dnevnika.');
      console.error('Error adding diary item:', err.response || err);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card className="p-4 shadow-lg" style={{ width: '100%', maxWidth: '500px' }}>
        <Card.Body>
          <h2 className="text-center mb-4 text-primary">Dodaj Stavku u Dnevnik</h2>
         
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="datum">
              <Form.Label>Datum</Form.Label>
              <Form.Control type="date" value={datum} onChange={(e) => setDatum(e.target.value)} required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="nazivAktivnosti">
              <Form.Label>Naziv Aktivnosti</Form.Label>
              <Form.Control type="text" value={nazivAktivnosti} onChange={(e) => setNazivAktivnosti(e.target.value)} required />
            </Form.Group>

            <Form.Group className="mb-3" controlId="komentar">
              <Form.Label>Komentar</Form.Label>
              <Form.Control as="textarea" rows={3} value={komentar} onChange={(e) => setKomentar(e.target.value)} required />
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100 mt-3">
              Dodaj Stavku
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AddStavkaDnevnika;
