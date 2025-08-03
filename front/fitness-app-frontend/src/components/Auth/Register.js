import React, { useState } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('vezbac'); 
  const [error, setError] = useState('');
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!username || !email || !password || !role) {
      setError('Molimo popunite sva polja.');
      return;
    }

    if (password.length < 8) {
      setError('Lozinka mora imati najmanje 8 karaktera.');
      return;
    }

    const result = await register(username, email, password, role);
    if (result.success) {
      navigate('/'); 
    } else {
      setError(result.message || 'Neuspešna registracija. Pokušajte ponovo.');
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card className="p-4 shadow-lg" style={{ width: '100%', maxWidth: '400px' }}>
        <Card.Body>
          <h2 className="text-center mb-4 text-primary">Registruj se</h2>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicUsername">
              <Form.Label>Korisničko ime</Form.Label>
              <Form.Control
                type="text"
                placeholder="Unesite korisničko ime"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicEmail">
              <Form.Label>Email adresa</Form.Label>
              <Form.Control
                type="email"
                placeholder="Unesite email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicPassword">
              <Form.Label>Lozinka</Form.Label>
              <Form.Control
                type="password"
                placeholder="Lozinka (min. 8 karaktera)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicRole">
              <Form.Label>Uloga</Form.Label>
              <Form.Control
                as="select"
                value={role}
                onChange={(e) => setRole(e.target.value)}
                required
              >
                <option value="vezbac">Vežbač</option>
                <option value="trener">Trener</option>
              </Form.Control>
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100 mt-3">
              Registruj se
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Register;
