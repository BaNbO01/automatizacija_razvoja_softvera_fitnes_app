import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import AppNavbar from './components/Layout/Navbar';
import Home from './components/Home';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import PrivateRoute from './utils/PrivateRoute'; 


import ExerciseList from './components/Exercises/ExerciseList';
import ExerciseDetail from './components/Exercises/ExerciseDetail';
import AddExercise from './components/Exercises/AddExercise';
import EditExercise from './components/Exercises/EditExercise';

import MuscleGroupsList from './components/MuscleGroups/MuscleGroupsList';
import MuscleGroupDetail from './components/MuscleGroups/MuscleGroupDetail';
import AddMuscleGroup from './components/MuscleGroups/AddMuscleGroup';

import ExerciseCategoriesList from './components/ExerciseCategories/ExerciseCategoriesList';

import MyDiariesList from './components/FitnessDiaries/MyDiariesList';
import FitnessDiaryDetail from './components/FitnessDiaries/FitnessDiaryDetail';
import AddFitnessDiary from './components/FitnessDiaries/AddFitnessDiary';
import AddStavkaDnevnika from './components/FitnessDiaries/AddStavkaDnevnika';

import MyTrainingPlansList from './components/TrainingPlans/MyTrainingPlansList';
import TrainingPlanDetail from './components/TrainingPlans/TrainingPlanDetail';
import AddTrainingPlan from './components/TrainingPlans/AddTrainingPlan';

import TrainersList from './components/Users/TrainersList';
import VezbaciList from './components/Users/VezbaciList';

import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppNavbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

      
          <Route path="/exercises" element={<ExerciseList />} />
          <Route path="/exercises/:id" element={<ExerciseDetail />} />
          <Route path="/muscle-groups" element={<MuscleGroupsList />} />
          <Route path="/muscle-groups/:id" element={<MuscleGroupDetail />} />
          <Route path="/exercise-categories" element={<ExerciseCategoriesList />} />

      
          <Route path="/my-diaries" element={<PrivateRoute allowedRoles={['VEZBAC']}><MyDiariesList /></PrivateRoute>} />
          <Route path="/my-diaries/add" element={<PrivateRoute allowedRoles={['VEZBAC']}><AddFitnessDiary /></PrivateRoute>} />
          <Route path="/my-diaries/:id" element={<PrivateRoute allowedRoles={['VEZBAC']}><FitnessDiaryDetail /></PrivateRoute>} />
          <Route path="/my-diaries/:id/add-item" element={<PrivateRoute allowedRoles={['VEZBAC']}><AddStavkaDnevnika /></PrivateRoute>} />
          <Route path="/my-training-plans" element={<PrivateRoute allowedRoles={['VEZBAC']}><MyTrainingPlansList /></PrivateRoute>} />
          <Route path="/my-training-plans/add" element={<PrivateRoute allowedRoles={['VEZBAC']}><AddTrainingPlan /></PrivateRoute>} />
          <Route path="/my-training-plans/:id" element={<PrivateRoute allowedRoles={['VEZBAC']}><TrainingPlanDetail /></PrivateRoute>} />


         
          <Route path="/exercises/add" element={<PrivateRoute allowedRoles={['TRENER']}><AddExercise /></PrivateRoute>} />
          <Route path="/exercises/edit/:id" element={<PrivateRoute allowedRoles={['TRENER']}><EditExercise /></PrivateRoute>} />

         
          <Route path="/users/trainers" element={<PrivateRoute allowedRoles={['ADMIN']}><TrainersList /></PrivateRoute>} />
          <Route path="/users/vezbaci" element={<PrivateRoute allowedRoles={['ADMIN']}><VezbaciList /></PrivateRoute>} />
          <Route path="/muscle-groups/add" element={<PrivateRoute allowedRoles={['ADMIN']}><AddMuscleGroup /></PrivateRoute>} />

       
          <Route path="*" element={<h1 className="text-center my-5 text-danger">Stranica nije pronađena!</h1>} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
