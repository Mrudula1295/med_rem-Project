import { Link, useNavigate, useLocation } from 'react-router-dom';
import { logout } from '../apiService';

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <Link to="/dashboard" className="nav-brand">MedReminder</Link>
            <div className="nav-links">
                <Link to="/dashboard" className={location.pathname === '/dashboard' ? 'active' : ''}>Dashboard</Link>
                <Link to="/add-medicine" className={location.pathname === '/add-medicine' ? 'active' : ''}>Add Medicine</Link>
                <Link to="/records" className={location.pathname === '/records' ? 'active' : ''}>Records</Link>
                <button onClick={handleLogout} className="logout-btn">Logout</button>
            </div>
        </nav>
    );
};

export default Navbar;
