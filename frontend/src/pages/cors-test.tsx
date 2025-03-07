import { useState } from 'react';
import axios from 'axios';

export default function CorsTest() {
  const [result, setResult] = useState('');
  const [error, setError] = useState('');

  const testGet = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/auth/test');
      setResult(JSON.stringify(response.data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  const testRegister = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123'
      });
      setResult(JSON.stringify(response.data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  const testWithFetch = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/test', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      const data = await response.json();
      setResult(JSON.stringify(data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  const testRegisterWithFetch = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: 'testuser',
          email: 'test@example.com',
          password: 'password123'
        }),
      });
      const data = await response.json();
      setResult(JSON.stringify(data, null, 2));
      setError('');
    } catch (err: any) {
      console.error('Error:', err);
      setError(`Error: ${err.message}`);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">CORS Test</h1>
      
      <div className="flex space-x-2 mb-4">
        <button 
          onClick={testGet}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Test GET with Axios
        </button>
        
        <button 
          onClick={testRegister}
          className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
        >
          Test Register with Axios
        </button>
        
        <button 
          onClick={testWithFetch}
          className="bg-purple-500 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded"
        >
          Test GET with Fetch
        </button>
        
        <button 
          onClick={testRegisterWithFetch}
          className="bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded"
        >
          Test Register with Fetch
        </button>
      </div>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          <pre>{error}</pre>
        </div>
      )}
      
      {result && (
        <div className="bg-gray-100 p-4 rounded">
          <h2 className="text-lg font-semibold mb-2">Result:</h2>
          <pre>{result}</pre>
        </div>
      )}
    </div>
  );
} 