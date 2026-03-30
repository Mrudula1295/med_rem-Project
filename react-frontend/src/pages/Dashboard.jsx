import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { fetchAPI, getUser, getBackendUrl } from '../apiService';
import { useToast } from '../components/ToastProvider';

const Dashboard = () => {
    const [profile, setProfile] = useState({});
    const [medicines, setMedicines] = useState([]);
    const [reminders, setReminders] = useState([]);
    const [isEditingProfile, setIsEditingProfile] = useState(false);
    const { showToast } = useToast();
    const user = getUser();

    const loadData = useCallback(async () => {
        try {
            const [meds, rems, prof] = await Promise.all([
                fetchAPI(`/medicines/list/${user.id}`),
                fetchAPI(`/reminders/user/${user.id}`),
                fetchAPI(`/user/profile/${user.id}`)
            ]);
            setMedicines(meds);
            setReminders(rems);
            setProfile(prof || {});
            
            // Notification logic
            if (Notification.permission === "default") {
                Notification.requestPermission();
            }
        } catch (e) {
            showToast('Error loading dashboard: ' + e.message, 'error');
        }
    }, [user.id, showToast]);

    useEffect(() => {
        loadData();
        const interval = setInterval(loadData, 60000); // refresh every minute
        return () => clearInterval(interval);
    }, [loadData]);

    const handleProfileChange = (e) => {
        setProfile({...profile, [e.target.name]: e.target.value});
    };

    const saveProfile = async (e) => {
        e.preventDefault();
        try {
            const updated = await fetchAPI('/user/update-profile', 'POST', {
                userId: user.id,
                ...profile
            });
            setProfile(updated);
            setIsEditingProfile(false);
            showToast('Profile updated!', 'success');
        } catch (err) {
            showToast(err.message, 'error');
        }
    };

    const markTaken = async (id) => {
        try {
            await fetchAPI(`/reminders/mark-taken/${id}`, 'POST');
            showToast('Marked as taken!', 'success');
            loadData();
        } catch (e) { showToast(e.message, 'error'); }
    };

    const snooze = async (id) => {
        try {
            await fetchAPI(`/reminders/snooze/${id}`, 'POST');
            showToast('Snoozed for 10 minutes', 'success');
            loadData();
        } catch (e) { showToast(e.message, 'error'); }
    };

    const deleteMed = async (id) => {
        if (!window.confirm('Delete this medicine?')) return;
        try {
            await fetchAPI(`/medicines/delete/${id}`, 'DELETE');
            showToast('Medicine deleted', 'success');
            loadData();
        } catch (e) { showToast(e.message, 'error'); }
    };

    const deleteReminder = async (id) => {
        if (!window.confirm('Delete this reminder?')) return;
        try {
            await fetchAPI(`/reminders/delete/${id}`, 'DELETE');
            showToast('Reminder deleted', 'success');
            loadData();
        } catch (e) { showToast(e.message, 'error'); }
    };

    const pendingReminders = reminders.filter(r => r.status === 'PENDING' || r.status === 'SNOOZED');

    return (
        <div className="container">
            <div className="page-header">
                <h1>Today's Schedule</h1>
                <Link to="/add-medicine" className="btn" style={{ width: 'auto', padding: '10px 20px', textDecoration: 'none' }}>+ Add Medicine</Link>
            </div>

            {/* Health Profile */}
            <div className="card" style={{ marginBottom: '30px', padding: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h2 style={{ margin: 0 }}>My Health Profile</h2>
                    <button className="btn-small" onClick={() => setIsEditingProfile(!isEditingProfile)}>
                        {isEditingProfile ? 'Cancel' : '✏️ Edit'}
                    </button>
                </div>

                {isEditingProfile ? (
                    <form onSubmit={saveProfile}>
                        <div className="form-group">
                            <label>Age</label>
                            <input type="number" name="age" value={profile.age || ''} onChange={handleProfileChange} />
                        </div>
                        <div className="form-group">
                            <label>Gender</label>
                            <select name="gender" value={profile.gender || ''} onChange={handleProfileChange}>
                                <option value="">Select</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Weight (kg)</label>
                            <input type="number" step="0.1" name="weight" value={profile.weight || ''} onChange={handleProfileChange} />
                        </div>
                        <div className="form-group">
                            <label>Health Conditions</label>
                            <div style={{ marginBottom: '10px', fontSize: '0.9em', color: 'var(--text-muted)' }}>e.g., Diabetes, Blood Pressure</div>
                            <input type="text" name="healthConditions" value={profile.healthConditions || ''} onChange={handleProfileChange} />
                        </div>
                        <button type="submit" className="btn" style={{ marginTop: '15px' }}>Save Profile</button>
                    </form>
                ) : (
                    <div>
                        <p><strong>Age:</strong> {profile.age || 'Not set'}</p>
                        <p><strong>Gender:</strong> {profile.gender || 'Not set'}</p>
                        <p><strong>Weight:</strong> {profile.weight ? `${profile.weight} kg` : 'Not set'}</p>
                        <p><strong>Conditions:</strong> {profile.healthConditions || 'None'}</p>
                    </div>
                )}
            </div>

            <h2 style={{ marginBottom: '20px' }}>Due Reminders</h2>
            <div className="grid">
                {pendingReminders.length === 0 ? (
                    <div className="empty-state">
                        <h3>All Caught Up!</h3>
                        <p>You have no pending reminders.</p>
                    </div>
                ) : (
                    pendingReminders.map(rem => {
                        const med = typeof rem.medicine === 'object' ? rem.medicine : (medicines.find(m => m.id === rem.medicine) || {});
                        return (
                            <div className="card" key={rem.id}>
                                <img src={getBackendUrl(med.imagePath)} alt={med.name} onError={(e) => { e.target.src = 'https://via.placeholder.com/300x200?text=No+Image'; }} />
                                <div className="card-title">{med.name}</div>
                                {(med.aiDosage || med.aiTiming) && (
                                    <div style={{ fontSize: '0.8em', margin: '5px 0', color: '#94a3b8' }}>
                                        {med.aiDosage && <><strong style={{color:'white'}}>Dosage:</strong> {med.aiDosage}<br/></>}
                                        {med.aiTiming && <><strong style={{color:'white'}}>Time:</strong> {med.aiTiming}</>}
                                    </div>
                                )}
                                <div className="card-meta">
                                    <span className={`badge ${rem.status.toLowerCase()}`}>{rem.status}</span>
                                    <span>{new Date(rem.nextReminderTime).toLocaleString()}</span>
                                </div>
                                <div className="actions">
                                    <button className="btn-small btn-success" onClick={() => markTaken(rem.id)}>✓ Taken</button>
                                    <button className="btn-small btn-outline" onClick={() => snooze(rem.id)}>⏰ Snooze</button>
                                    <button className="btn-small btn-danger" onClick={() => deleteReminder(rem.id)}>Delete</button>
                                </div>
                            </div>
                        );
                    })
                )}
            </div>

            <h2 style={{ marginTop: '40px', marginBottom: '20px' }}>Your Medicines</h2>
            <div className="grid">
                {medicines.length === 0 ? (
                    <div className="empty-state">
                        <h3>No Medicines Added</h3>
                        <p>Click "+ Add Medicine" to get started.</p>
                    </div>
                ) : (
                    medicines.map(med => (
                        <div className="card" key={med.id} style={{ position: 'relative' }}>
                             {med.aiSafe !== null && med.aiSafe !== undefined && (
                                 <span className="badge" style={{
                                     backgroundColor: med.aiSafe ? 'rgba(0, 230, 118, 0.15)' : 'rgba(255, 8, 68, 0.15)',
                                     color: med.aiSafe ? 'var(--success-neon)' : 'var(--danger-neon)',
                                     border: `1px solid ${med.aiSafe ? 'rgba(0,230,118,0.3)' : 'rgba(255,8,68,0.3)'}`,
                                     position: 'absolute', top: '10px', right: '10px', zIndex: 10
                                 }}>
                                    {med.aiSafe ? '✔ Safe' : '❌ Risky'}
                                 </span>
                             )}
                            <img src={getBackendUrl(med.imagePath)} alt={med.name} onError={(e) => { e.target.src = 'https://via.placeholder.com/300x200?text=No+Image'; }} />
                            <div className="card-title">{med.name}</div>
                            <div className="actions">
                                <button className="btn-small btn-danger" onClick={() => deleteMed(med.id)}>Delete</button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default Dashboard;
