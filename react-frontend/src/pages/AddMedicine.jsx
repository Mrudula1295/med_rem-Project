import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchAPI, getUser } from '../apiService';
import { useToast } from '../components/ToastProvider';

const AddMedicine = () => {
    const [medName, setMedName] = useState('');
    const [reminderTime, setReminderTime] = useState('');
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('Click or drag image to upload');
    const [extractedText, setExtractedText] = useState('');
    
    // AI State
    const [isValidating, setIsValidating] = useState(false);
    const [aiResult, setAiResult] = useState(null);

    const navigate = useNavigate();
    const { showToast } = useToast();
    const user = getUser();
    const fileInputRef = useRef(null);

    const handleFileChange = async (e) => {
        if (e.target.files && e.target.files.length > 0) {
            const selectedFile = e.target.files[0];
            setFile(selectedFile);
            setFileName(selectedFile.name);

            // Attempt OCR
            const formData = new FormData();
            formData.append('image', selectedFile);
            try {
                const res = await fetchAPI('/ai/analyze-medicine', 'POST', formData, true);
                if (res.text && !medName) {
                    const autoName = res.text.split('\n')[0];
                    setMedName(autoName);
                }
                setExtractedText(res.text);
            } catch (e) {
                console.log("OCR skip", e);
            }
        }
    };

    const validateMedicine = async () => {
        if (!medName) {
            showToast("Please enter a medicine name first.", "error");
            return;
        }

        setIsValidating(true);
        try {
            const profile = await fetchAPI(`/user/profile/${user.id}`);
            const conditionsList = profile.healthConditions 
                ? profile.healthConditions.toLowerCase().split(',').map(s => s.trim()) 
                : [];
            
            const checkReq = {
                userId: user.id,
                medicineName: medName,
                conditions: conditionsList,
                imageText: extractedText || ""
            };
            
            const result = await fetchAPI('/ai/check-medicine', 'POST', checkReq);
            setAiResult(result);
        } catch (e) {
            showToast("Validation failed: " + e.message, "error");
        } finally {
            setIsValidating(false);
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        if (!aiResult) return;

        const formData = new FormData();
        formData.append('userId', user.id);
        formData.append('name', medName);
        if (file) formData.append('image', file);
        
        formData.append('aiSafe', aiResult.isSafe);
        formData.append('aiDosage', aiResult.dosage);
        formData.append('aiTiming', aiResult.timing);
        formData.append('aiWarning', aiResult.warnings);

        try {
            const newMed = await fetchAPI('/medicines/add', 'POST', formData, true);
            
            const rFormData = new URLSearchParams();
            rFormData.append('medicineId', newMed.id);
            rFormData.append('reminderTime', reminderTime);
            rFormData.append('repeatMode', 'DAILY');
            
            await fetchAPI('/reminders/set', 'POST', rFormData, true);
            
            showToast('Medicine and Reminder added successfully!', 'success');
            setTimeout(() => navigate('/dashboard'), 1000);
        } catch (err) {
            showToast('Failed: ' + err.message, 'error');
        }
    };

    return (
        <div className="container">
            <div className="page-header">
                <h1>Add New Medicine</h1>
            </div>
            <div className="glass-card" style={{ margin: '0 auto', maxWidth: '500px' }}>
                <form id="addMedicineForm" onSubmit={(e) => e.preventDefault()}>
                    <div className="form-group">
                        <label>Medicine Name</label>
                        <input type="text" value={medName} onChange={(e) => setMedName(e.target.value)} required placeholder="e.g. Paracetamol 500mg" />
                    </div>
                    <div className="form-group">
                        <label>Medicine Image</label>
                        <div className="file-upload-wrapper">
                            <div className="file-upload-box" onClick={() => fileInputRef.current.click()}>
                                <i className="icon" style={{ fontSize: '2rem' }}>📷</i>
                                <p style={{ marginTop: '10px' }}>{fileName}</p>
                            </div>
                            <input type="file" ref={fileInputRef} onChange={handleFileChange} accept="image/*" style={{display: 'none'}} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label>First Reminder Time</label>
                        <input type="datetime-local" value={reminderTime} onChange={(e) => setReminderTime(e.target.value)} required />
                    </div>
                    
                    {!aiResult && (
                        <button type="button" className="btn" onClick={validateMedicine} disabled={isValidating}>
                            {isValidating ? 'Validating...' : 'Validate Medicine with AI'}
                        </button>
                    )}
                    
                    {aiResult && (
                        <div className="card" style={{ marginTop: '20px', textAlign: 'left' }}>
                            <h3 style={{ marginTop: 0 }}>AI Recommendation</h3>
                            <p><strong>Status:</strong> <span className={`badge ${aiResult.isSafe ? 'bg-success' : 'bg-danger'}`} style={{ backgroundColor: aiResult.isSafe ? '#5cb85c' : '#d9534f' }}>
                                {aiResult.isSafe ? '✔ Safe' : '❌ Risky'}
                            </span></p>
                            <p><strong>Dosage:</strong> <span>{aiResult.dosage}</span></p>
                            <p><strong>Best Time:</strong> <span>{aiResult.timing}</span></p>
                            <p><strong>Warnings:</strong> <span style={{ color: '#ff8a8a' }}>{aiResult.warnings}</span></p>
                            
                            <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                                <button type="button" className="btn btn-success" onClick={handleSave}>Confirm & Save</button>
                                <button type="button" className="btn btn-danger" onClick={() => setAiResult(null)}>Cancel</button>
                            </div>
                        </div>
                    )}
                </form>
            </div>
        </div>
    );
};

export default AddMedicine;
