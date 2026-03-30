import { useState, useEffect, useRef } from 'react';
import { fetchAPI, getUser, getBackendUrl } from '../apiService';
import { useToast } from '../components/ToastProvider';

const HealthRecords = () => {
    const [records, setRecords] = useState([]);
    const [title, setTitle] = useState('');
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('Click to select file');
    const [isUploading, setIsUploading] = useState(false);
    
    const { showToast } = useToast();
    const user = getUser();
    const fileInputRef = useRef(null);

    const loadRecords = async () => {
        try {
            const data = await fetchAPI(`/records/list/${user.id}`);
            setRecords(data);
        } catch (e) {
            showToast('Error loading records: ' + e.message, 'error');
        }
    };

    useEffect(() => {
        loadRecords();
    }, []);

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files.length > 0) {
            setFile(e.target.files[0]);
            setFileName(e.target.files[0].name);
        }
    };

    const handleUpload = async (e) => {
        e.preventDefault();
        if(!title) {
            showToast('Title is required', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('userId', user.id);
        formData.append('title', title);
        if (file) formData.append('file', file);

        setIsUploading(true);
        try {
            await fetchAPI('/records/upload', 'POST', formData, true);
            showToast('Record uploaded successfully!', 'success');
            setTitle('');
            setFile(null);
            setFileName('Click to select file');
            loadRecords();
        } catch (err) {
            showToast('Failed: ' + err.message, 'error');
        } finally {
            setIsUploading(false);
        }
    };

    const deleteRecord = async (id) => {
        if (!window.confirm('Delete this record?')) return;
        try {
            await fetchAPI(`/records/delete/${id}`, 'DELETE');
            showToast('Record deleted.', 'success');
            loadRecords();
        } catch (e) {
            showToast(e.message, 'error');
        }
    };

    return (
        <div className="container">
            <div className="page-header">
                <h1>Health Records & Prescriptions</h1>
            </div>
            <div className="grid">
                <div className="card" style={{ height: 'fit-content' }}>
                    <h3 style={{ marginBottom: '20px' }}>Upload Record</h3>
                    <form onSubmit={handleUpload}>
                        <div className="form-group">
                            <label>Record Title</label>
                            <input type="text" value={title} onChange={e => setTitle(e.target.value)} required placeholder="e.g. Blood Test Result Jan 2024" />
                        </div>
                        <div className="form-group">
                            <label>File (PDF/Image)</label>
                            <div className="file-upload-wrapper">
                                <div className="file-upload-box" onClick={() => fileInputRef.current.click()}>
                                    <p>{fileName}</p>
                                </div>
                                <input type="file" ref={fileInputRef} onChange={handleFileChange} style={{display: 'none'}} />
                            </div>
                        </div>
                        <button type="submit" className="btn" disabled={isUploading}>
                            {isUploading ? 'Uploading...' : 'Upload Record'}
                        </button>
                    </form>
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                    <h3 style={{ marginBottom: '20px' }}>Your Records</h3>
                    <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))' }}>
                        {records.length === 0 ? (
                            <div className="empty-state">
                                <h3>No Records Found</h3>
                            </div>
                        ) : (
                            records.map(rec => {
                                const isPdf = rec.filePath?.toLowerCase().endsWith('.pdf');
                                return (
                                    <div className="card" key={rec.id}>
                                        {isPdf ? (
                                            <div style={{height:'180px', background:'rgba(0,0,0,0.2)', display:'flex', alignItems:'center', justifyContent:'center', fontSize:'1.5rem', borderRadius:'12px', marginBottom:'16px'}}>
                                                📄 PDF Document
                                            </div>
                                        ) : (
                                            <img src={getBackendUrl(rec.filePath)} alt={rec.title} onError={(e) => { e.target.src = 'https://via.placeholder.com/300x200?text=No+Image'; }} />
                                        )}
                                        <div className="card-title">{rec.title}</div>
                                        <div className="actions">
                                            <a href={getBackendUrl(rec.filePath)} target="_blank" rel="noreferrer" className="btn-small btn-outline">View File</a>
                                            <button className="btn-small btn-danger" onClick={() => deleteRecord(rec.id)}>Delete</button>
                                        </div>
                                    </div>
                                );
                            })
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HealthRecords;
