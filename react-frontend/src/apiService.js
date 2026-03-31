// Reads the URL from Vercel's Environment Variables, defaults to localhost if running locally
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

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
            throw new Error(errorText || `HTTP Error ${response.status}`);
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
            console.error("Network Error: Possible CORS issue or Backend is down.", error);
            throw new Error("Unable to connect to the server. Please check your internet or ensure the Backend URL is correct in Vercel settings.");
        }
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
    
    // Strip "/api" from API_BASE to get the root server URL
    const rootUrl = API_BASE.replace('/api', '');
    return `${rootUrl}${path}`;
}
