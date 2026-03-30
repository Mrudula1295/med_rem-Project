const API_BASE = 'https://med-rem-project.onrender.com/api';

export async function fetchAPI(endpoint, method = 'GET', body = null, isFormData = false) {
    const headers = {};
    if (!isFormData) {
        headers['Content-Type'] = 'application/json';
    }

    const options = {
        method,
        headers,
    };

    if (body) {
        options.body = isFormData ? body : JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'API request failed');
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        console.error("API Error:", error);
        throw error;
    }
}

export function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

export function logout() {
    localStorage.removeItem('user');
    window.location.href = '/login';
}

export function getBackendUrl(path) {
    if (!path) return 'https://via.placeholder.com/300x200?text=No+Image';
    return `http://localhost:8080${path}`;
}
