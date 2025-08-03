import React, { useEffect, useState } from 'react';
import { Container, Table, Button, Alert, Spinner, Pagination, Modal } from 'react-bootstrap';
import api from '../../services/api';

const TrainersList = () => {
  const [trainers, setTrainers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5; 

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [trainerToDelete, setTrainerToDelete] = useState(null);
  const [deleteSuccess, setDeleteSuccess] = useState('');
  const [deleteError, setDeleteError] = useState('');

  const fetchTrainers = async () => {
    try {
      const response = await api.get(`/users/treneri?page=${currentPage}&size=${pageSize}`);
      setTrainers(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dohvaćanju trenera.');
      console.error('Error fetching trainers:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrainers();
  }, [currentPage]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleDeleteClick = (trainer) => {
    setTrainerToDelete(trainer);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    setDeleteError('');
    setDeleteSuccess('');
    try {
      await api.delete(`/users/treneri/${trainerToDelete.id}`);
      setDeleteSuccess('Trener uspešno obrisan!');
      setShowDeleteModal(false);
      setTrainerToDelete(null);
      fetchTrainers(); 
    } catch (err) {
      setDeleteError(err.response?.data?.message || 'Greška pri brisanju trenera.');
      console.error('Error deleting trainer:', err);
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
      <h2 className="text-center mb-4 text-primary">Lista Trenera</h2>
      {deleteSuccess && <Alert variant="success" className="text-center">{deleteSuccess}</Alert>}
      {deleteError && <Alert variant="danger" className="text-center">{deleteError}</Alert>}

      {trainers.length === 0 ? (
        <Alert variant="info" className="text-center">Nema registrovanih trenera.</Alert>
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
              {trainers.map(trainer => (
                <tr key={trainer.id}>
                  <td>{trainer.id}</td>
                  <td>{trainer.email}</td>
                   <td>{trainer.role ? trainer.role.role : 'N/A'}</td>
                  <td>{new Date(trainer.datumRegistracije).toLocaleDateString('sr-RS')}</td>
                  <td>
                    <Button variant="danger" size="sm" onClick={() => handleDeleteClick(trainer)}>
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

  
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Potvrda Brisanja</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Da li ste sigurni da želite da obrišete trenera: <strong>{trainerToDelete?.email}</strong>?
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

export default TrainersList;
