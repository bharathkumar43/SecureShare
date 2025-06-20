import React, { useState } from 'react';
import Header from './components/Header';
import ShareForm from './components/ShareForm';
import GeneratedLink from './components/GeneratedLink';
import AuditTrail from './components/AuditTrail';
import Toast from './components/Toast';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

function App() {
  const [formData, setFormData] = useState({
    sourceCloud: '', destinationCloud: '', toEmail: '', fromEmail: '',
    watermark: false, password: '', expiry: '', downloadLimit: ''
  });

  const [link, setLink] = useState('');
  const [auditLogs, setAuditLogs] = useState([]);
  const [progress, setProgress] = useState(0);
  const [isGenerating, setIsGenerating] = useState(false);

  const validateForm = () => {
    const { sourceCloud, destinationCloud, toEmail, fromEmail, password, expiry, downloadLimit } = formData;
    if (!sourceCloud || !destinationCloud || !toEmail || !fromEmail || !password || !expiry || !downloadLimit) {
      toast.error('Please fill in all required fields.');
      return false;
    }
    // Simple email format check
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(toEmail) || !emailRegex.test(fromEmail)) {
      toast.error('Please enter valid email addresses.');
      return false;
    }
    return true;
  };

  const generateLink = () => {
    if (!validateForm()) return;

    setIsGenerating(true);
    setProgress(0);

    const interval = setInterval(() => {
      setProgress((oldProgress) => {
        if (oldProgress >= 100) {
          clearInterval(interval);
          actuallyGenerateLink();
          return 100;
        }
        return oldProgress + 10;
      });
    }, 300);
  };

  const actuallyGenerateLink = async () => {
    try {
      console.log("Sending this payload:", formData); // Debugging
      const res = await fetch('http://localhost:8085/api/share', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      const data = await res.json();
      if (res.ok) {
        setLink(data.link);
        setAuditLogs([...auditLogs, { action: 'Generated', status: 'Active', timestamp: new Date().toLocaleString() }]);
        toast.success("Secure link generated!");
      } else {
        toast.error("Server error: " + (data.message || "Unknown error"));
      }
    } catch (err) {
      toast.error("Error generating link: " + err.message);
    } finally {
      setIsGenerating(false);
    }
  };

  const revokeLink = async () => {
    try {
      await fetch('http://localhost:8085/api/revoke', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ link })
      });
      setLink('');
      setAuditLogs([...auditLogs, { action: 'Revoked', status: 'Revoked', timestamp: new Date().toLocaleString() }]);
      toast.info("Link revoked.");
    } catch (err) {
      toast.error("Error revoking link: " + err.message);
    }
  };

  return (
    <div>
      <Header />
      <div className="container mt-4">
        <ShareForm
          formData={formData}
          setFormData={setFormData}
          generateLink={generateLink}
        />

        {isGenerating && (
          <div className="progress my-3">
            <div
              className="progress-bar progress-bar-striped progress-bar-animated bg-success"
              role="progressbar"
              style={{ width: `${progress}%` }}
            >
              {progress}%
            </div>
          </div>
        )}

        <GeneratedLink link={link} revokeLink={revokeLink} />
        <AuditTrail logs={auditLogs} />
      </div>

      <Toast />
    </div>
  );
}

export default App;
