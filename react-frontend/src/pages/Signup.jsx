import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { fetchAPI } from '../apiService';
import { useToast } from '../components/ToastProvider';

const Signup = () => {
    const [userData, setUserData] = useState({ 
        username: '', 
        password: '', 
        email: '', 
        mobile: '',
        role: 'PATIENT' 
    });
    const navigate = useNavigate();
    const { showToast } = useToast();

    const handleChange = (e) => {
        setUserData({...userData, [e.target.name]: e.target.value});
    };

    const handleSignup = async (e) => {
        e.preventDefault();
        try {
            const user = await fetchAPI('/auth/signup', 'POST', userData);
            localStorage.setItem('user', JSON.stringify(user));
            showToast('Registration successful!', 'success');
            navigate('/dashboard');
        } catch (err) {
            showToast(err.message, 'error');
        }
    };

    return (
        <div className="auth-container">
            <div className="glass-card">
                <h2>Join MedReminder</h2>
                <form onSubmit={handleSignup}>
                    <div className="form-group">
                        <label>Username</label>
                        <input type="text" name="username" value={userData.username} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label>Email</label>
                        <input type="email" name="email" value={userData.email} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label>Mobile Number</label>
                        <input type="tel" name="mobile" value={userData.mobile} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                        <label>Password</label>
                        <input type="password" name="password" value={userData.password} onChange={handleChange} required />
                    </div>
                    <button type="submit" className="btn">Sign Up</button>
                </form>
                <div className="auth-links">
                    Already have an account? <Link to="/login">Log in</Link>
                </div>
            </div>
        </div>
    );
};

export default Signup;
