import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import AddMedicine from './pages/AddMedicine';
import HealthRecords from './pages/HealthRecords';
import Login from './pages/Login';
import Signup from './pages/Signup';
import { getUser } from './apiService';
import { ToastProvider } from './components/ToastProvider';

const ProtectedRoute = ({ children }) => {
    if (!getUser()) return <Navigate to="/login" replace />;
    return children;
};

function App() {
  return (
    <ToastProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/*" element={
            <ProtectedRoute>
              <>
                <Navbar />
                <Routes>
                  <Route path="/" element={<Navigate to="/dashboard" replace />} />
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/add-medicine" element={<AddMedicine />} />
                  <Route path="/records" element={<HealthRecords />} />
                </Routes>
              </>
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </ToastProvider>
  );
}

export default App;
