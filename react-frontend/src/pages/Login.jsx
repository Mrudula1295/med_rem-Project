import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { fetchAPI } from '../apiService';
import { useToast } from '../components/ToastProvider';

const Login = () => {
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const navigate = useNavigate();
    const { showToast } = useToast();

    const handleChange = (e) => {
        setCredentials({...credentials, [e.target.name]: e.target.value});
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const user = await fetchAPI('/auth/login', 'POST', credentials);
            localStorage.setItem('user', JSON.stringify(user));
            showToast('Login successful!', 'success');
            navigate('/dashboard');
        } catch (err) {
            showToast(err.message, 'error');
        }
    };

    return (
        <div className="auth-container">
            <div className="glass-card">
                <h2>Welcome Back</h2>
                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <label>Username</label>
                        <input type="text" name="username" value={credentials.username} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label>Password</label>
                        <input type="password" name="password" value={credentials.password} onChange={handleChange} required />
                    </div>
                    <button type="submit" className="btn">Login</button>
                </form>
                <div className="auth-links">
                    Don't have an account? <Link to="/signup">Register here</Link>
                </div>
                <div style={{ marginTop: '20px', fontSize: '10px', opacity: 0.5 }}>
                    Connecting to: {API_BASE}
                </div>
            </div>
        </div>
    );
};

export default Login;
