import React, { useState, useEffect } from 'react';
import { Form, Button, Container, Card, Alert, Spinner,Row,Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const AddExercise = () => {
  const [formData, setFormData] = useState({
    naziv: '',
    opis: '',
    misiciNaKojeUtice: '',
    savet: '',
    preporuceniBrojSerija: '',
    preporuceniBrojPonavljanja: '',
    grupaMisicaId: '',
    kategorijaId: ''
  });
  const [slika, setSlika] = useState(null);
  const [video, setVideo] = useState(null);
  const [grupeMisica, setGrupeMisica] = useState([]);
  const [kategorijeVezbi, setKategorijeVezbi] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDependencies = async () => {
      try {
        const [grupeRes, kategorijeRes] = await Promise.all([
          api.get('/grupe-misica'),
          api.get('/kategorije-vezbe')
        ]);
        setGrupeMisica(grupeRes.data);
        setKategorijeVezbi(kategorijeRes.data);
      } catch (err) {
        setError('Greška pri učitavanju grupa mišića ili kategorija vežbi.');
        console.error('Error fetching dependencies:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDependencies();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    if (e.target.name === 'slika') {
      setSlika(e.target.files[0]);
    } else if (e.target.name === 'video') {
      setVideo(e.target.files[0]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess('');

    if (!slika || !video) {
        setError('Molimo dodajte i sliku i video za vežbu.');
        return;
    }

    const data = new FormData();
    
    data.append('request', new Blob([JSON.stringify({
      ...formData,
      preporuceniBrojSerija: parseInt(formData.preporuceniBrojSerija),
      preporuceniBrojPonavljanja: parseInt(formData.preporuceniBrojPonavljanja),
      grupaMisicaId: parseInt(formData.grupaMisicaId),
      kategorijaId: parseInt(formData.kategorijaId)
    })], { type: 'application/json' }));
    
    data.append('slika', slika);
    data.append('video', video);

    try {
      await api.post('/vezbe', data, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      setSuccess('Vežba uspešno dodata!');
      setTimeout(() => navigate('/exercises'), 1400); 
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju vežbe.');
      console.error('Error adding exercise:', err.response || err);
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
      <h2 className="text-center mb-4 text-primary">Dodaj Novu Vežbu</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}
      <Card className="p-4 shadow-lg border-0">
        <Form onSubmit={handleSubmit}>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3" controlId="naziv">
                <Form.Label>Naziv</Form.Label>
                <Form.Control type="text" name="naziv" value={formData.naziv} onChange={handleChange} required />
              </Form.Group>

              <Form.Group className="mb-3" controlId="opis">
                <Form.Label>Opis</Form.Label>
                <Form.Control as="textarea" rows={3} name="opis" value={formData.opis} onChange={handleChange} required />
              </Form.Group>

              <Form.Group className="mb-3" controlId="misiciNaKojeUtice">
                <Form.Label>Mišići na koje utiče</Form.Label>
                <Form.Control type="text" name="misiciNaKojeUtice" value={formData.misiciNaKojeUtice} onChange={handleChange} required />
              </Form.Group>

              <Form.Group className="mb-3" controlId="savet">
                <Form.Label>Savet</Form.Label>
                <Form.Control as="textarea" rows={3} name="savet" value={formData.savet} onChange={handleChange} required />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3" controlId="preporuceniBrojSerija">
                <Form.Label>Preporučeni Broj Serija</Form.Label>
                <Form.Control type="number" name="preporuceniBrojSerija" value={formData.preporuceniBrojSerija} onChange={handleChange} required min="1" />
              </Form.Group>

              <Form.Group className="mb-3" controlId="preporuceniBrojPonavljanja">
                <Form.Label>Preporučeni Broj Ponavljanja</Form.Label>
                <Form.Control type="number" name="preporuceniBrojPonavljanja" value={formData.preporuceniBrojPonavljanja} onChange={handleChange} required min="1" />
              </Form.Group>

              <Form.Group className="mb-3" controlId="grupaMisicaId">
                <Form.Label>Grupa Mišića</Form.Label>
                <Form.Control as="select" name="grupaMisicaId" value={formData.grupaMisicaId} onChange={handleChange} required>
                  <option value="">Izaberite grupu mišića</option>
                  {grupeMisica.map(grupa => (
                    <option key={grupa.id} value={grupa.id}>{grupa.naziv}</option>
                  ))}
                </Form.Control>
              </Form.Group>

              <Form.Group className="mb-3" controlId="kategorijaId">
                <Form.Label>Kategorija Vežbe</Form.Label>
                <Form.Control as="select" name="kategorijaId" value={formData.kategorijaId} onChange={handleChange} required>
                  <option value="">Izaberite kategoriju vežbe</option>
                  {kategorijeVezbi.map(kategorija => (
                    <option key={kategorija.id} value={kategorija.id}>{kategorija.naziv}</option>
                  ))}
                </Form.Control>
              </Form.Group>

              <Form.Group className="mb-3" controlId="slika">
                <Form.Label>Slika</Form.Label>
                <Form.Control type="file" name="slika" onChange={handleFileChange} accept="image/*" required />
              </Form.Group>

              <Form.Group className="mb-3" controlId="video">
                <Form.Label>Video</Form.Label>
                <Form.Control type="file" name="video" onChange={handleFileChange} accept="video/mp4" required />
              </Form.Group>
            </Col>
          </Row>
          <Button variant="primary" type="submit" className="w-100 mt-3">
            Dodaj Vežbu
          </Button>
        </Form>
      </Card>
    </Container>
  );
};

export default AddExercise;
