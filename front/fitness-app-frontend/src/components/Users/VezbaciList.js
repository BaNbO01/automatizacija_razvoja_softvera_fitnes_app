import React, { useEffect, useState } from 'react'; 
import { Container, Table, Button, Alert, Spinner, Pagination, Modal } from 'react-bootstrap';
import api from '../../services/api';

const VezbaciList = () => {
  const [vezbaci, setVezbaci] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5; 

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [vezbacToDelete, setVezbacToDelete] = useState(null);
  const [deleteSuccess, setDeleteSuccess] = useState('');
  const [deleteError, setDeleteError] = useState('');

  const fetchVezbaci = async () => {
    try {
      const response = await api.get(`/users/vezbaci?page=${currentPage}&size=${pageSize}`);
      setVezbaci(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dohvaćanju vežbača.');
      console.error('Error fetching vezbaci:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVezbaci();
  }, [currentPage]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleDeleteClick = (vezbac) => {
    setVezbacToDelete(vezbac);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    setDeleteError('');
    setDeleteSuccess('');
    try {
      await api.delete(`/users/vezbaci/${vezbacToDelete.id}`);
      setDeleteSuccess('Vežbač uspešno obrisan!');
      setShowDeleteModal(false);
      setVezbacToDelete(null);
      fetchVezbaci(); // Ponovo dohvati listu nakon brisanja
    } catch (err) {
      setDeleteError(err.response?.data?.message || 'Greška pri brisanju vežbača.');
      console.error('Error deleting vezbac:', err);
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

  if (error) {
    return (
      <Container className="my-5">
        <Alert variant="danger" className="text-center">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-5">
      <h2 className="text-center mb-4 text-primary">Lista Vežbača</h2>
      {deleteSuccess && <Alert variant="success" className="text-center">{deleteSuccess}</Alert>}
      {deleteError && <Alert variant="danger" className="text-center">{deleteError}</Alert>}

      {vezbaci.length === 0 ? (
        <Alert variant="info" className="text-center">Nema registrovanih vežbača.</Alert>
      ) : (
        <>
          <Table striped bordered hover responsive className="shadow-sm">
            <thead>
              <tr>
                <th>ID</th>
                <th>Email</th>
                <th>Uloga</th>
                <th>Datum Registracije</th>
                <th>Akcije</th>
              </tr>
            </thead>
            <tbody>
              {vezbaci.map(vezbac => (
                <tr key={vezbac.id}>
                  <td>{vezbac.id}</td>
                  <td>{vezbac.email}</td>
                    <td>{vezbac.role ? vezbac.role.role : 'N/A'}</td> {/* AŽURIRANO: pristup pojedinačnoj ulozi */}
                  <td>{new Date(vezbac.datumRegistracije).toLocaleDateString('sr-RS')}</td>
                  <td>
                    <Button variant="danger" size="sm" onClick={() => handleDeleteClick(vezbac)}>
                      Obriši
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
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

      {/* Modal za potvrdu brisanja */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Potvrda Brisanja</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Da li ste sigurni da želite da obrišete vežbača: <strong>{vezbacToDelete?.email}</strong>?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Odustani
          </Button>
          <Button variant="danger" onClick={confirmDelete}>
            Obriši
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default VezbaciList;
