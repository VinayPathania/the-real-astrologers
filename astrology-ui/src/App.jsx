import { useState, useEffect } from 'react';
import './App.css';

function App() {
  // --- AUTHENTICATION STATE ---
  // Check if we already have a token in the browser's pocket
  const [token, setToken] = useState(localStorage.getItem('jwt_token') || null);
  const [authMode, setAuthMode] = useState('login'); // 'login' or 'register'
  const [authData, setAuthData] = useState({ email: '', password: '', fullName: '' });

  // --- ASTROLOGY STATE ---
  const [formData, setFormData] = useState({ personName: '', birthDate: '', birthTime: '', cityName: '' });
  const [reading, setReading] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // --- AUTHENTICATION LOGIC ---
  const handleAuthChange = (e) => {
    setAuthData({ ...authData, [e.target.name]: e.target.value });
  };

 const handleAuthSubmit = async (e) => {
    e.preventDefault();
    const endpoint = authMode === 'login' ? 'login' : 'register';
    
    try {
      const response = await fetch(`http://localhost:8080/api/auth/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(authData)
      });

      const data = await response.text();

      if (authMode === 'login' && !data.includes('Error')) {
        // Success!
        localStorage.setItem('jwt_token', data);
        setToken(data);
        
        // THE FIX: Clear the auth form!
        setAuthData({ email: '', password: '', fullName: '' });
        
      } else {
        alert(data); 
        if (authMode === 'register' && data.includes('Success')) {
            setAuthMode('login'); 
            // THE FIX: Clear the form after registering so they can type their login cleanly
            setAuthData({ email: '', password: '', fullName: '' });
        }
      }
    } catch (error) {
      alert("Failed to connect to server.");
    }
  };

const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    setToken(null);
    setReading(null);
    
   
    setFormData({ personName: '', birthDate: '', birthTime: '', cityName: '' });
    setAuthData({ email: '', password: '', fullName: '' });
  };

  // --- ASTROLOGY LOGIC ---
  const handleAstroChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAstroSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setReading(null);

    try {
      const response = await fetch('http://localhost:8080/api/profiles/add', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          
          'Authorization': `Bearer ${token}` 
        },
        body: JSON.stringify(formData)
      });

      if (response.status === 403 || response.status === 401) {
        alert("Session expired. Please log in again.");
        handleLogout();
        return;
      }

      const data = await response.json(); 
      setReading(data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  // --- UI SCREENS ---

  // SCREEN 1: If the user is NOT logged in, show the Auth screen
  if (!token) {
    return (
      <div className="app-container">
        <h1>{authMode === 'login' ? 'Login to Astro AI' : 'Create an Account'}</h1>
        <form onSubmit={handleAuthSubmit} className="astro-form">
          {authMode === 'register' && (
            <input type="text" name="fullName" placeholder="Full Name" value={authData.fullName} onChange={handleAuthChange} required />
          )}
          <input type="email" name="email" placeholder="Email" value={authData.email} onChange={handleAuthChange} required />
          <input type="password" name="password" placeholder="Password" value={authData.password} onChange={handleAuthChange} required />
          
          <button type="submit">{authMode === 'login' ? 'Login' : 'Register'}</button>
        </form>
        
        <p style={{textAlign: 'center', cursor: 'pointer', color: '#3498db'}} 
           onClick={() => setAuthMode(authMode === 'login' ? 'register' : 'login')}>
          {authMode === 'login' ? "Don't have an account? Register here" : "Already have an account? Login here"}
        </p>
      </div>
    );
  }

  // SCREEN 2: If they ARE logged in, show the main app
  return (
    <div className="app-container">
      <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <h1>The Real Astrologers</h1>
        <button onClick={handleLogout} style={{backgroundColor: '#e74c3c', padding: '8px', fontSize: '14px'}}>Logout</button>
      </div>
      
<form onSubmit={handleAstroSubmit} className="astro-form">
        <input type="text" name="personName" placeholder="Full Name" value={formData.personName} onChange={handleAstroChange} required />
        <input type="date" name="birthDate" value={formData.birthDate} onChange={handleAstroChange} required />
        <input type="time" name="birthTime" value={formData.birthTime} onChange={handleAstroChange} required />
        <input type="text" name="cityName" placeholder="City of Birth (e.g. Kangra)" value={formData.cityName} onChange={handleAstroChange} required />
        
        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Consulting the Stars...' : 'Generate Kundli Reading'}
        </button>
      </form>

      {reading && (
        <div className="reading-results">
          <h2>Your Celestial Reading</h2>
          <div className="card"><h3>Career</h3><p>{reading.career}</p></div>
          <div className="card"><h3>Future Outlook</h3><p>{reading.future}</p></div>
          <div className="card"><h3>Health</h3><p>{reading.health}</p></div>
          <div className="card"><h3>Marriage</h3><p>{reading.marriage}</p></div>
          <div className="card"><h3>Family</h3><p>{reading.family}</p></div>
          <div className="card"><h3>Obstacles</h3><p>{reading.obstacles}</p></div>
        </div>
      )}
    </div>
  );
}

export default App;